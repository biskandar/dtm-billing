package com.beepcast.billing.engine;

import java.util.Map;

import com.beepcast.billing.BillingResult;
import com.beepcast.billing.acc.Account;

public abstract class BillingEngineDecorator extends BasicBillingEngine {

  public abstract String name();

  public abstract Map listActiveAccounts();

  public abstract Account queryAccount( String profileId , String accountId );

  public abstract boolean registerAccount( Account account );

  public abstract boolean removeAccount( Account account );

  public abstract BillingResult doCredit( String profileId , Integer accountId ,
      Double unit );

  public abstract BillingResult getBalance( String profileId , Integer accountId );

  public abstract BillingResult doDebit( String profileId , Integer accountId ,
      Double unit );

}
