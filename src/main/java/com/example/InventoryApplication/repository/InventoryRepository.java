package com.example.InventoryApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.InventoryApplication.entity.Item;

public interface InventoryRepository extends JpaRepository<Item, Long> {

}
