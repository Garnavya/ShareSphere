package com.sharesphere.repository;

import com.sharesphere.entity.DeliveryStatus;
import com.sharesphere.entity.DeliveryTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, String> {
    List<DeliveryTask> findByStatus(DeliveryStatus status);
    List<DeliveryTask> findByPartnerId(String partnerId);
    List<DeliveryTask> findByBookingId(String bookingId);
}
