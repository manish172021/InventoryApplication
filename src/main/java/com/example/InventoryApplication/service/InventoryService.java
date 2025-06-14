package com.example.InventoryApplication.service;

public interface InventoryService {
	
	void supplyItem(Long itemId, int quantity);
	
	boolean reserveItem(Long itemId, int quantity);
	
	void cancelReservation(Long itemId, int quantity);
	
	int getAvailability(Long itemId);
}
