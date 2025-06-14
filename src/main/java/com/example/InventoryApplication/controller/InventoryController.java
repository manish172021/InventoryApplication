package com.example.InventoryApplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.InventoryApplication.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
	
	private final InventoryService inventoryService;
	
	@PostMapping("/supply")
    public ResponseEntity<?> supply(@RequestParam Long itemId, @RequestParam int quantity) {
        inventoryService.supplyItem(itemId, quantity);
        return ResponseEntity.ok("Stock added.");
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestParam Long itemId, @RequestParam int quantity) {
        boolean success = inventoryService.reserveItem(itemId, quantity);
        return success ? ResponseEntity.ok("Reserved.") : ResponseEntity.badRequest().body("Insufficient stock.");
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestParam Long itemId, @RequestParam int quantity) {
        inventoryService.cancelReservation(itemId, quantity);
        return ResponseEntity.ok("Reservation cancelled.");
    }

    @GetMapping("/availability/{itemId}")
    public ResponseEntity<Integer> availability(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryService.getAvailability(itemId));
    }
}
