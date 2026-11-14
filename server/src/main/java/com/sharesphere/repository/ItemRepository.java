package com.sharesphere.repository;

import com.sharesphere.entity.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdWithLock(@Param("id") String id);

    List<Item> findByIsAvailableTrueOrderByCreatedAtDesc();

    @Query("SELECT i FROM Item i WHERE i.isAvailable = true " +
           "AND (:category IS NULL OR i.category = :category) " +
           "AND (:isFree IS NULL OR i.isFree = :isFree) " +
           "AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY i.createdAt DESC")
    List<Item> searchItems(
            @Param("category") String category,
            @Param("isFree") Boolean isFree,
            @Param("search") String search
    );

    List<Item> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
}
