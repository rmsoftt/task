package com.db.awmd.challenge.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
  @PostMapping(path="/transfer",consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> transferAmount(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException  {
	  
    log.info("Transfer Service is invoked");
    
    ObjectMapper mapper =new ObjectMapper();
    @SuppressWarnings("unchecked")
	Map<String,String> transferReqtMap=(Map) mapper.readValue(json, Map.class);
    
    Account fromAccount=this.accountsService.getAccount(transferReqtMap.get("fromAccountId"));
    Account toAccount=this.accountsService.getAccount(transferReqtMap.get("toAccountId"));
    this.accountsService.transferAmount(fromAccount,toAccount ,new BigDecimal( transferReqtMap.get("ammount")));
    
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
