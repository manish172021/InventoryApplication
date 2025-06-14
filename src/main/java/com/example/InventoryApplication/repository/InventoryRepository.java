package com.example.InventoryApplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import com.example.InventoryApplication.entity.Item;

import feign.Param;
import jakarta.persistence.LockModeType;

public interface InventoryRepository extends JpaRepository<Item, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :itemId")
    Optional<Item> findByIdWithLock(@Param("itemId") Long itemId);
}
