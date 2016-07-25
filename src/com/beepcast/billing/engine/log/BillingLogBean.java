package com.beepcast.billing.engine.log;

public class BillingLogBean {

  private String accountId;
  private String accountProfile;
  private int clientId;
  private int paymentType;
  private int paymentLevel;
  private int eventId;
  private double unit;
  private double balance;
  private String description;

  public BillingLogBean() {

  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId( String accountId ) {
    this.accountId = accountId;
  }

  public String getAccountProfile() {
    return accountProfile;
  }

  public void setAccountProfile( String accountProfile ) {
    this.accountProfile = accountProfile;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId( int clientId ) {
    this.clientId = clientId;
  }

  public int getPaymentType() {
    return paymentType;
  }

  public void setPaymentType( int paymentType ) {
    this.paymentType = paymentType;
  }

  public int getPaymentLevel() {
    return paymentLevel;
  }

  public void setPaymentLevel( int paymentLevel ) {
    this.paymentLevel = paymentLevel;
  }

  public int getEventId() {
    return eventId;
  }

  public void setEventId( int eventId ) {
    this.eventId = eventId;
  }

  public double getUnit() {
    return unit;
  }

  public void setUnit( double unit ) {
    this.unit = unit;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance( double balance ) {
    this.balance = balance;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

}
