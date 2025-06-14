package com.example.InventoryApplication.service.impl;

import org.springframework.stereotype.Service;

import com.example.InventoryApplication.service.InventoryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

	@Override
	public void supplyItem(Long itemId, int quantity) {
		// TODO Auto-generated method stub
		log.info("Supplied {} units to item {}", quantity, itemId);
	}

	@Override
	public boolean reserveItem(Long itemId, int quantity) {
		// TODO Auto-generated method stub
		log.info("Reserved {} units for item {}", quantity, itemId);
		return false;
	}

	@Override
	public void cancelReservation(Long itemId, int quantity) {
		// TODO Auto-generated method stub
		log.info("Cancelled reservation of {} units for item {}", quantity, itemId);
	}

	@Override
	public int getAvailability(Long itemId) {
		// TODO Auto-generated method stub
		 log.info("Get availability for item {} ", itemId);
		return 0;
	}

}
