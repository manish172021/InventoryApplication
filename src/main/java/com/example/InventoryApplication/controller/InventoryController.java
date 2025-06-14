package com.example.InventoryApplication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.InventoryApplication.service.InventoryService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryService inventoryService;

	@PostMapping("/supply")
	public ResponseEntity<String> supplyStock(@RequestParam @Min(1) Long itemId, @RequestParam @Min(1) int quantity) {
		inventoryService.supplyItem(itemId, quantity);
		return ResponseEntity.ok("Added %d units to item %d".formatted(quantity, itemId));
	}

	@PostMapping("/reserve")
	public ResponseEntity<String> reserveStock(@RequestParam @Min(1) Long itemId, @RequestParam @Min(1) int quantity) {
		inventoryService.reserveItem(itemId, quantity);
		return ResponseEntity.ok("Reservation successful");
	}

	@PostMapping("/cancel")
	public ResponseEntity<String> cancel(@RequestParam @Min(1) Long itemId, @RequestParam @Min(1) int quantity) {
		inventoryService.cancelReservation(itemId, quantity);
		return ResponseEntity.ok("Reservation cancelled");
	}

	@GetMapping("/availability/{itemId}")
	public ResponseEntity<Integer> checkAvailability(@PathVariable @Min(1) Long itemId) {
		return ResponseEntity.ok(inventoryService.getAvailableQuantity(itemId));
	}
}
