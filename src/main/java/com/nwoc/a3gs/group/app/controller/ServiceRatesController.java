package com.nwoc.a3gs.group.app.controller;

import java.util.List;
import java.util.Optional;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nwoc.a3gs.group.app.dto.ServiceRatesDTO;
import com.nwoc.a3gs.group.app.model.ServiceRates;
import com.nwoc.a3gs.group.app.services.ServiceRatesServiceImpl;

import javassist.NotFoundException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ServiceRatesController {
	
	@Autowired
	ServiceRatesServiceImpl serviceRatesImpl;
	private static final Logger LOGGER = LogManager.getLogger(ServiceRatesController.class);
	
	@PostMapping(value="/service/rates",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createServiceRates(@RequestBody ServiceRatesDTO serviceRatesDTO) {
		try {
			ServiceRates serviceRates = serviceRatesImpl.create(serviceRatesDTO);
			return ResponseEntity.ok(serviceRates);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@GetMapping(value="/service/rates",produces=MediaType.APPLICATION_JSON_VALUE)
	public List<ServiceRates> getAllServices() {
		return serviceRatesImpl.findAll();
	}
	
	@GetMapping(value="/service/rates/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getServicesById(@PathVariable(value = "id") Long id) {
		Optional<ServiceRates> serviceRate = serviceRatesImpl.findOne(id);
		if (!serviceRate.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok().body(serviceRate.get());
	}

	
	@PutMapping(value="/service/rates/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateServices(@PathVariable(value = "id") Long id, @RequestBody ServiceRatesDTO serviceRatesDTO) {
		ServiceRates serviceRateUpdate = null;
		try {
			serviceRateUpdate = serviceRatesImpl.update(serviceRatesDTO, id);
		} catch (NotFoundException e) {
			LOGGER.error(e.getMessage(),e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok().body(serviceRateUpdate);
	}
	
	@DeleteMapping(value="/service/rates/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteServices(@PathVariable(value = "id") Long id) {
		Optional<ServiceRates> serviceRates = serviceRatesImpl.findOne(id);
		if (!serviceRates.isPresent()) {
			return ((BodyBuilder) ResponseEntity.notFound()).body("User Not Found");
		}

		serviceRatesImpl.delete(serviceRates.get());
		return ResponseEntity.ok().body(serviceRates.get().getRate() + "  Successfully Deleted");
	}
	
	@GetMapping(value="/service/rateslist",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ServiceRates>> listServiceRatesByPages(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {

			Page<ServiceRates> ratePages = serviceRatesImpl.findServiceRatesByPages(page, size);
			return ResponseEntity.ok(ratePages);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
}
