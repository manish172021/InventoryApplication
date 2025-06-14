package com.example.InventoryApplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.InventoryApplication.entity.Item;
import com.example.InventoryApplication.exception.InsufficientStockException;
import com.example.InventoryApplication.exception.ItemNotFoundException;
import com.example.InventoryApplication.repository.InventoryRepository;

public class InventoryServiceImplTest {

	private InventoryServiceImpl inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryService = new InventoryServiceImpl(inventoryRepository);
    }

    @Test
    public void supplyItem_shouldIncreaseQuantity_whenItemExists() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setAvailableQuantity(50);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testItem));

        inventoryService.supplyItem(1L, 30);

        assertEquals(80, testItem.getAvailableQuantity());
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void supplyItem_shouldThrow_whenItemNotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            inventoryService.supplyItem(1L, 30);
        });
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void cancelReservation_shouldAdjustQuantities_whenValid() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setAvailableQuantity(50);
        testItem.setReservedQuantity(20);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testItem));

        inventoryService.cancelReservation(1L, 10);

        assertEquals(60, testItem.getAvailableQuantity());
        assertEquals(10, testItem.getReservedQuantity());
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void cancelReservation_shouldThrow_whenItemNotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            inventoryService.cancelReservation(1L, 10);
        });
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void cancelReservation_shouldThrow_whenCancellingMoreThanReserved() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setReservedQuantity(5);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testItem));

        assertThrows(IllegalStateException.class, () -> {
            inventoryService.cancelReservation(1L, 10);
        });
    }

    @Test
    public void getAvailableQuantity_shouldReturnQuantity_whenItemExists() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setAvailableQuantity(75);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testItem));

        int quantity = inventoryService.getAvailableQuantity(1L);

        assertEquals(75, quantity);
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void getAvailableQuantity_shouldThrow_whenItemNotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            inventoryService.getAvailableQuantity(1L);
        });
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void reserveItem_shouldAdjustQuantities_whenStockAvailable() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setAvailableQuantity(100);
        testItem.setReservedQuantity(0);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testItem));

        inventoryService.reserveItem(1L, 30);

        assertEquals(70, testItem.getAvailableQuantity());
        assertEquals(30, testItem.getReservedQuantity());
        verify(inventoryRepository).findById(1L);
    }

    @Test
    public void reserveItem_shouldThrow_whenInsufficientStock() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setAvailableQuantity(10);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testItem));

        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.reserveItem(1L, 20);
        });
    }

    @Test
    public void reserveItem_shouldThrow_whenItemNotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> {
            inventoryService.reserveItem(1L, 10);
        });
    }

    @Test
    public void recover_shouldUsePessimisticLock_whenRetriesExhausted() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setAvailableQuantity(100);
        testItem.setReservedQuantity(0);
        
        ObjectOptimisticLockingFailureException ex = 
            new ObjectOptimisticLockingFailureException("Simulated lock", new Object());
        
        when(inventoryRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testItem));

        inventoryService.recover(ex, 1L, 30);

        assertEquals(70, testItem.getAvailableQuantity());
        assertEquals(30, testItem.getReservedQuantity());
        verify(inventoryRepository).findByIdWithLock(1L);
    }
}
