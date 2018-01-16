package com.db.awmd.challenge.exception;

/**
 * Exception Class is used when the amount to be transfered from one account to other and the 
 * amount is -ve
 *
 */
public class NegativeAmountException extends RuntimeException {

  public NegativeAmountException(String message) {
    super(message);
  }
}
