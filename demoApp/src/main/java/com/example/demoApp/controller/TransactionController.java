package com.example.demoApp.controller;

import com.example.demoApp.exception.RequestBindingException;
import com.example.demoApp.service.TransactionService;
import com.example.demoApp.service.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.example.demoApp.DemoAppApplication.REST_API_PREFIX;

@RestController
@RequestMapping(value = REST_API_PREFIX + "/transactions",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/{id}")
    public TransactionDTO getTransaction(@PathVariable int id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionDTO> createUser(@Valid @RequestBody TransactionDTO transaction, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestBindingException(bindingResult);
        }
        TransactionDTO newTransaction = transactionService.addTransaction(transaction);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTransaction.id)
                .toUri();
        return ResponseEntity.created(location).body(newTransaction);
    }

}
