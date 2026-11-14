package com.sharesphere.service;

import com.sharesphere.dto.request.CreateBookingRequest;
import com.sharesphere.entity.*;
import com.sharesphere.exception.BadRequestException;
import com.sharesphere.exception.ConflictException;
import com.sharesphere.exception.ResourceNotFoundException;
import com.sharesphere.repository.BookingRepository;
import com.sharesphere.repository.ItemRepository;
import com.sharesphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OtpService otpService;

    /**
     * Atomic Reservation Engine:
     * - Uses PESSIMISTIC_WRITE lock on Item and overlapping Bookings
     * - Runs inside a strict ACID transaction to prevent double bookings during concurrent attempts
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking createBookingAtomic(CreateBookingRequest request, String borrowerId) {
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new BadRequestException("End date must be strictly after start date.");
        }

        // 1. Fetch Item with PESSIMISTIC_WRITE lock
        Item item = itemRepository.findByIdWithLock(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        if (!Boolean.TRUE.equals(item.getIsAvailable())) {
            throw new BadRequestException("Item is currently not available for borrowing.");
        }

        if (item.getOwner().getId().equals(borrowerId)) {
            throw new BadRequestException("You cannot borrow your own item.");
        }

        User borrower = userRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower user not found."));

        // 2. Strict Overlap Concurrency Check
        List<BookingStatus> activeStatuses = List.of(
                BookingStatus.CONFIRMED,
                BookingStatus.AWAITING_HANDOVER,
                BookingStatus.ACTIVE
        );

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                item.getId(),
                request.getStartDate(),
                request.getEndDate(),
                activeStatuses
        );

        if (!conflicts.isEmpty()) {
            throw new ConflictException("Requested dates overlap with an existing confirmed reservation.");
        }

        // 3. Compute duration and total pricing
        long days = ChronoUnit.DAYS.between(request.getStartDate().toLocalDate(), request.getEndDate().toLocalDate());
        int totalDays = (int) Math.max(1, days);

        BigDecimal rate = Boolean.TRUE.equals(item.getIsFree()) ? BigDecimal.ZERO : item.getDailyRate();
        BigDecimal totalAmount = rate.multiply(BigDecimal.valueOf(totalDays));

        // 4. Generate security handover OTP
        String plainPickupOtp = otpService.generateNumericOtp(6);
        String hashedPickupOtp = otpService.hashOtp(plainPickupOtp);

        Booking booking = Booking.builder()
                .item(item)
                .borrower(borrower)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays(totalDays)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .fulfillmentType(request.getFulfillmentType() != null ? request.getFulfillmentType() : FulfillmentType.SELF_PICKUP)
                .pickupOtp(hashedPickupOtp)
                .build();

        // 5. Automatic dispatch queue task if crowdsourced delivery selected
        if (booking.getFulfillmentType() == FulfillmentType.DELIVERY_PARTNER) {
            DeliveryTask outboundTask = DeliveryTask.builder()
                    .booking(booking)
                    .phase(DeliveryPhase.OUTBOUND)
                    .status(DeliveryStatus.AVAILABLE)
                    .pickupAddress(item.getAddress())
                    .dropoffAddress(borrower.getAddress() != null ? borrower.getAddress() : "Borrower specified address")
                    .deliveryFee(BigDecimal.valueOf(50.00)) // Base campus delivery fee
                    .build();
            booking.getDeliveryTasks().add(outboundTask);
        }

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByBorrowerIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }
}
