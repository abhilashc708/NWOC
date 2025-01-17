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

import com.nwoc.a3gs.group.app.dto.TransactionHistoryDTO;
import com.nwoc.a3gs.group.app.model.TransactionHistory;
import com.nwoc.a3gs.group.app.services.TransactionHistoryServiceImpl;

import javassist.NotFoundException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class TransactionHistoryController {
	
	@Autowired
	TransactionHistoryServiceImpl transactionHistoryServiceImpl;
	private static final Logger LOGGER = LogManager.getLogger(ServiceHistoryController.class);
	
	@PostMapping(value="/transaction/history",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createTransactionHistory(@RequestBody TransactionHistoryDTO transactionHistoryDTO) {
		try {
			TransactionHistory transactionHistory = transactionHistoryServiceImpl.create(transactionHistoryDTO);
			return ResponseEntity.ok(transactionHistory);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/transaction/history",produces=MediaType.APPLICATION_JSON_VALUE)
	public List<TransactionHistory> getAllTransationHistory() {
		try {
			return transactionHistoryServiceImpl.findAll();
		} catch(Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			return (List<TransactionHistory>) ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			
		}
		
	}

	@GetMapping(value="/transaction/history/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTransactionHistoryById(@PathVariable(value = "id") Long id) {
		try {
			Optional<TransactionHistory> transctionHistory = transactionHistoryServiceImpl.findOne(id);
			if (!transctionHistory.isPresent()) {
				return ((BodyBuilder) ResponseEntity.notFound()).body("Transation History Not Found");
			}
			return ResponseEntity.ok().body(transctionHistory.get());
			
		} catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
	}

	
	@PutMapping(value="/transaction/history/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateTransactionHistory(@PathVariable(value = "id") Long id, @RequestBody TransactionHistoryDTO transactionHistoryDTO) {
		TransactionHistory transactionHistory = null;
		try {
			transactionHistory = transactionHistoryServiceImpl.update(transactionHistoryDTO, id);
		} catch (NotFoundException e) {
			LOGGER.error(e.getMessage(),e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok().body(transactionHistory);
	}
	
	@DeleteMapping(value="/transaction/history/{id}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteTransactionHistory(@PathVariable(value = "id") Long id) {
		try {
			
			Optional<TransactionHistory> transactionHistory = transactionHistoryServiceImpl.findOne(id);
			if (!transactionHistory.isPresent()) {
				return ((BodyBuilder) ResponseEntity.notFound()).body("Transaction History Not Found");
			}

			transactionHistoryServiceImpl.delete(transactionHistory.get());
			return ResponseEntity.ok().body(transactionHistory.get().getTransactionId() + "  Successfully Deleted");
			
		} catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			
		}
		
	}
	
	@GetMapping(value="/transaction/transactionHistorylist",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<TransactionHistory>> listTransactionHistoryByPages(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {

			Page<TransactionHistory> transactionHistory = transactionHistoryServiceImpl.findTransactionHistoryByPages(page, size);
			return ResponseEntity.ok(transactionHistory);
		} catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	

}
