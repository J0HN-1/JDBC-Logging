package com.example.demoApp.controller;

import com.example.demoApp.DemoAppApplication;
import com.example.demoApp.exception.RequestBindingException;
import com.example.demoApp.service.AccountService;
import com.example.demoApp.service.dto.AccountDTO;
import com.fasterxml.jackson.annotation.JsonView;
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

import static com.example.demoApp.DemoAppApplication.*;
import static com.example.demoApp.service.dto.View.AccountStatusView;
import static com.example.demoApp.service.dto.View.AccountTypeView;

@RestController
@RequestMapping(value = REST_API_PREFIX + "/accounts",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "/{id}")
    public AccountDTO getAccount(@PathVariable int id) {
        return accountService.getAccount(id);
    }

    @GetMapping
    public List<AccountDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO account, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestBindingException(bindingResult);
        }
        AccountDTO newAccount = accountService.createAccount(account);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newAccount.id)
                .toUri();
        return ResponseEntity.created(location).body(newAccount);
    }

    @JsonView(AccountTypeView.class)
    @PutMapping(value = "/{id}/type")
    public ResponseEntity<AccountDTO> changeAccountType(@PathVariable int id, @Valid @RequestBody AccountDTO account, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestBindingException(bindingResult);
        }
        accountService.setAccountType(id, account.type);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(REST_API_PREFIX + "/accounts/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
    }

    @JsonView(AccountStatusView.class)
    @PutMapping(value = "/{id}/status")
    public ResponseEntity<AccountDTO> changeAccountStatus(@PathVariable int id, @Valid @RequestBody AccountDTO account, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestBindingException(bindingResult);
        }
        accountService.setAccountStatus(id, account.status);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(REST_API_PREFIX + "/accounts/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(location).build();
    }
}
