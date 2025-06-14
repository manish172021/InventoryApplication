package com.example.InventoryApplication.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EndPointTest {
	
	@Autowired
    private TestRestTemplate restTemplate;
	
	
	@Test
    public void shouldSupplyStock() {
        Long itemId = 1L;
        int quantity = 10;

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/inventory/supply?itemId={itemId}&quantity={quantity}",
            null,
            String.class,
            itemId, quantity
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Added 10 units to item 1");
    }
	
	@Test
    public void shouldReserveStock() {
        Long itemId = 1L;
        int quantity = 5;

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/inventory/reserve?itemId={itemId}&quantity={quantity}",
            null,
            String.class,
            itemId, quantity
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Reservation successful");
    }
	
	@Test
    public void shouldCancelReservation() {
		Long itemId = 1L;
	    int quantity = 3;
	    
	    // add
	    restTemplate.postForEntity(
	        "/inventory/reserve?itemId={itemId}&quantity={quantity}",
	        null,
	        String.class,
	        itemId, quantity
	    );
	    
	    // now cancel
	    ResponseEntity<String> response = restTemplate.postForEntity(
	        "/inventory/cancel?itemId={itemId}&quantity={quantity}",
	        null,
	        String.class,
	        itemId, quantity
	    );
	    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertThat(response.getBody()).isEqualTo("Reservation cancelled");
    }

    @Test
    public void shouldCheckAvailability() {
        Long itemId = 1L;

        restTemplate.postForEntity(
            "/inventory/supply?itemId={itemId}&quantity={quantity}",
            null,
            String.class,
            itemId, 10
        );

        // Check availability
        ResponseEntity<Integer> response = restTemplate.getForEntity(
            "/inventory/availability/{itemId}",
            Integer.class,
            itemId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(110); // 100(startup data) + 10
    }

    @Test
    public void shouldReturnBadRequestForInvalidParameters() {
        // Test with invalid parameters
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/inventory/supply?itemId=0&quantity=0",
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
