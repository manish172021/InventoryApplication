package com.example.InventoryApplication.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.InventoryApplication.entity.Item;
import com.example.InventoryApplication.exception.InsufficientStockException;
import com.example.InventoryApplication.exception.ItemNotFoundException;
import com.example.InventoryApplication.repository.InventoryRepository;
import com.example.InventoryApplication.service.InventoryService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;

	@CacheEvict(value = "inventory", key = "#itemId")
	@Override
	@Transactional
	public void supplyItem(Long itemId, int quantity) {
		Item item = inventoryRepository.findById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId));
		item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
		log.info("Supplied {} units to item {}", quantity, itemId);
	}

	@CacheEvict(value = "inventory", key = "#itemId")
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

	@Cacheable(value = "inventory", key = "#itemId")
	@Override
	@Transactional(readOnly = true)
	public int getAvailableQuantity(Long itemId) {
		return inventoryRepository.findById(itemId)
				.orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + itemId))
				.getAvailableQuantity();
	}
	
	@CacheEvict(value = "inventory", key = "#itemId")
	@Retryable(
			retryFor = ObjectOptimisticLockingFailureException.class,
			maxAttempts = 3,
			backoff = @Backoff(delay = 100, multiplier = 2)
			)
	@Transactional
	@Override
	public void reserveItem(Long itemId, int quantity) {
		// Try reservation using optimistic locking
        Item item = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (item.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Not enough stock");
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
        item.setReservedQuantity(item.getReservedQuantity() + quantity);
	}
	
	// If all retries fail, this method will be called
	@Recover
	public void recover(ObjectOptimisticLockingFailureException ex, Long itemId, int quantity) {
	    Item item = inventoryRepository.findByIdWithLock(itemId)
	            .orElseThrow(() -> new ItemNotFoundException("Item not found"));

	    if (item.getAvailableQuantity() < quantity) {
	        throw new InsufficientStockException("Not enough stock");
	    }

	    item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
	    item.setReservedQuantity(item.getReservedQuantity() + quantity);
	}
}
