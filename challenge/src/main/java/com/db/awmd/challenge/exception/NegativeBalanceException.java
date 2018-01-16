package com.db.awmd.challenge.exception;

/**
 * Exception Class representing Nagative Value Exception. 
 * It will be thrown when net account balance reaches negative
 *
 */
public class NegativeBalanceException extends RuntimeException {

  public NegativeBalanceException(String message) {
    super(message);
  }
}
