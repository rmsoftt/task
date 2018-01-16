package com.db.awmd.challenge;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeAmountException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.AccountsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest

public class AccountsServiceTest {

	@Autowired
	@InjectMocks
	private AccountsService accountsService;
	@Mock
	AccountsRepository accountsRepository;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test(expected = NegativeAmountException.class)
	public void transfer_amount_when_amount_is_nagative() throws Exception {
		// Given
		String fromAccountId = "Id-" + System.currentTimeMillis();
		String toAccountId = "Id-" + System.currentTimeMillis();
		Account fromAccount = new Account(fromAccountId, new BigDecimal(100));
		Account toAccount = new Account(toAccountId, new BigDecimal(100));
		// when
		this.accountsService.transferAmount(fromAccount, toAccount, new BigDecimal("-10.00"));
	}

	@Test(expected = NegativeBalanceException.class)
	public void transfer_amount_when_balance_is_nagative() {

		// Given
		MockitoAnnotations.initMocks(this);
		String fromAccountId = "Id-" + System.currentTimeMillis();
		String toAccountId = "Id-" + System.currentTimeMillis();
		Account fromAccount = new Account(fromAccountId, new BigDecimal(100));
		Account toAccount = new Account(toAccountId, new BigDecimal(100));

		// when
		this.accountsService.transferAmount(fromAccount, toAccount, new BigDecimal("101"));
		assertThat(fromAccount.getBalance()).isEqualByComparingTo(new BigDecimal(100));
	}

	@Test
	public void transfer_amount_for_Happy_Path() {

		// Given
		MockitoAnnotations.initMocks(this);
		String fromAccountId = "Id-" + System.currentTimeMillis();
		String toAccountId = "Id-" + System.currentTimeMillis();
		Account fromAccount = new Account(fromAccountId, new BigDecimal(100));
		Account toAccount = new Account(toAccountId, new BigDecimal(100));

		// when
		this.accountsService.transferAmount(fromAccount, toAccount, new BigDecimal("50"));
		assertThat(fromAccount.getBalance()).isEqualByComparingTo(new BigDecimal(50));
		assertThat(toAccount.getBalance()).isEqualByComparingTo(new BigDecimal(150));
	}
}
