package com.beepcast.billing.engine;

import java.util.Map;

import com.beepcast.billing.BillingApi;
import com.beepcast.billing.acc.Account;
import com.beepcast.billing.profile.AccountProfile;

public interface BillingEngine extends BillingApi {

  public String name();

  public AccountProfile getAccountProfile( String profileId );

  public Map listActiveAccounts();

  public Account queryAccount( String profileId , String accountId );

  public boolean registerAccount( Account account );

  public boolean removeAccount( Account account );

}
