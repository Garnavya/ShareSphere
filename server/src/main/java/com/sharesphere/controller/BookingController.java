package com.sharesphere.controller;

import com.sharesphere.dto.request.CreateBookingRequest;
import com.sharesphere.entity.Booking;
import com.sharesphere.security.CustomUserDetails;
import com.sharesphere.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Booking booking = bookingService.createBookingAtomic(request, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Booking reservation created successfully.");
        response.put("booking", booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Booking> bookings = bookingService.getUserBookings(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("bookings", bookings);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable String id) {
        Booking booking = bookingService.getBookingById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("booking", booking);
        return ResponseEntity.ok(response);
    }
}
