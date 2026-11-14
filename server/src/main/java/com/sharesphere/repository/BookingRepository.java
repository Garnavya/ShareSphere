package com.sharesphere.repository;

import com.sharesphere.entity.Booking;
import com.sharesphere.entity.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
           "AND b.status IN :statuses " +
           "AND b.startDate <= :endDate " +
           "AND b.endDate >= :startDate")
    List<Booking> findConflictingBookings(
            @Param("itemId") String itemId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") Collection<BookingStatus> statuses
    );

    List<Booking> findByBorrowerIdOrderByCreatedAtDesc(String borrowerId);

    List<Booking> findByItemIdOrderByCreatedAtDesc(String itemId);

    List<Booking> findByItemIdAndStatusIn(String itemId, Collection<BookingStatus> statuses);
}
