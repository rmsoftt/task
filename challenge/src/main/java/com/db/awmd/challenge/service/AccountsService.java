package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.NegativeAmountException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;
	@Getter
	private final EmailNotificationService emailNotificationService;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository, EmailNotificationService emailNotificationService) {
		this.accountsRepository = accountsRepository;
		this.emailNotificationService = emailNotificationService;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}
/**
 * Method Executing for  transferring amount {@code amount} from 
 * account having Id {@code fromAccountId} to {@codetoAccountId}
 * @param fromAccountId
 * @param toAccountId
 * @param amount
 */
	@Transactional
	public void transferAmount(Account fromAccount, Account toAccount, BigDecimal amount) {
		if (amount.intValue() < 0) {
			
			throw new NegativeAmountException("The amount to transfer should always be a positive number");
		}

		
		if ((fromAccount.getBalance().subtract(amount).intValue() < 0)) {
			fromAccount.getBalance().add(amount);
			throw new NegativeBalanceException("Overdrafts is not Supported");
		}
		//transfer triggering
		 this.accountsRepository.transfer(fromAccount, toAccount, amount);

		 //Email Notification to Both Account
		emailNotificationService.notifyAboutTransfer(fromAccount, amount + "has been successfully debited");
		emailNotificationService.notifyAboutTransfer(toAccount, amount + "has been successfully credited");
		
	}
}
