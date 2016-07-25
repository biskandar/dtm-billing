package com.beepcast.billing;

public interface BillingApi {

  public BillingResult reset( String profileId , Integer accId , Double unit );

  public BillingResult doCredit( String profileId , Integer accId , Double unit );

  public BillingResult getBalance( String profileId , Integer accId );

  public BillingResult doDebit( String profileId , Integer accId , Double unit );

}
