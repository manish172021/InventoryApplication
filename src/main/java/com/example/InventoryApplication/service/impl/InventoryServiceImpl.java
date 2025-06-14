package com.example.InventoryApplication.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.InventoryApplication.entity.Item;
import com.example.InventoryApplication.exception.InsufficientStockException;
import com.example.InventoryApplication.exception.ItemNotFoundException;
import com.example.InventoryApplication.repository.InventoryRepository;
import com.example.InventoryApplication.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;

	@Override
	@Transactional
	public void supplyItem(Long itemId, int quantity) {
		Item item = inventoryRepository.findById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));
		item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
		log.info("Supplied {} units to item {}", quantity, itemId);
	}

	@Override
	@Transactional
	public void reserveItem(Long itemId, int quantity) {
		Item item = inventoryRepository.findById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));

		if (item.getAvailableQuantity() < quantity) {
			throw new InsufficientStockException(
					"Requested: " + quantity + ", Available: " + item.getAvailableQuantity());
		}

		item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
		item.setReservedQuantity(item.getReservedQuantity() + quantity);
		log.info("Reserved {} units for item {}", quantity, itemId);
	}

	@Override
	@Transactional
	public void cancelReservation(Long itemId, int quantity) {
		Item item = inventoryRepository.findById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));

		if (item.getReservedQuantity() < quantity) {
			throw new IllegalStateException(
					"Cannot cancel more than reserved. Reserved: " + item.getReservedQuantity());
		}

		item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
		item.setReservedQuantity(item.getReservedQuantity() - quantity);
		log.info("Cancelled reservation of {} units for item {}", quantity, itemId);
	}

	@Override
	@Transactional(readOnly = true)
	public int getAvailableQuantity(Long itemId) {
		return inventoryRepository.findById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId))
				.getAvailableQuantity();
	}

}
