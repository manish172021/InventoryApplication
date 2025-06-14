package com.example.InventoryApplication.service;

public interface InventoryService {
	
	void supplyItem(Long itemId, int quantity);
	
	void reserveItem(Long itemId, int quantity);
	
	void cancelReservation(Long itemId, int quantity);
	
	int getAvailableQuantity(Long itemId);
}
