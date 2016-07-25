package com.beepcast.billing.acc;

import java.util.List;

import com.beepcast.billing.profile.AccountProfile;

public interface AccountEngine {

  public List listActiveAccountIds( AccountProfile accountProfile );

  public Account queryAccount( AccountProfile accountProfile , Integer accId ,
      boolean silent );

  public Account queryAccount( AccountProfile accountProfile ,
      String accountId , boolean silent );

  public boolean storeAccount( Account account , boolean silent );

  public boolean cleanAccount( Account account , boolean silent );

  public Double readBalance( Account account );

  public boolean writeBalance( Account account , Double balance );

}
