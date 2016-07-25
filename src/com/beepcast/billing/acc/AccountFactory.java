package com.beepcast.billing.acc;

import java.util.Date;

import com.beepcast.billing.profile.AccountProfile;
import com.beepcast.common.util.concurrent.ReadWriteLock;
import com.beepcast.common.util.concurrent.WriterPreferenceReadWriteLock;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class AccountFactory {

  static final DLogContext lctx = new SimpleContext( "AccountFactory" );

  public static Account generateAccount( Integer accId ,
      AccountProfile accountProfile ) {
    return generateAccount( accId , accountProfile ,
        new WriterPreferenceReadWriteLock() , new Double( 0 ) );
  }

  public static Account generateAccount( Integer accId ,
      AccountProfile accountProfile , Double initUnit ) {
    return generateAccount( accId , accountProfile ,
        new WriterPreferenceReadWriteLock() , initUnit );
  }

  public static Account generateAccount( Integer accId ,
      AccountProfile accountProfile , ReadWriteLock rwLock ) {
    return generateAccount( accId , accountProfile , rwLock , new Double( 0 ) );
  }

  public static Account generateAccount( Integer accId ,
      AccountProfile accountProfile , ReadWriteLock rwLock , Double initUnit ) {
    Account account = null;

    // compose accountId
    String accountId = AccountIdFactory.generateAccountId( accountProfile ,
        accId );
    if ( accountId == null ) {
      DLog.warning( lctx , "Failed to generate Account , found null accountId" );
      return account;
    }

    // generate headerLog
    String headerLog = "[Account-" + accountId + "] ";

    if ( rwLock == null ) {
      DLog.warning( lctx , headerLog + "Failed to generate Account "
          + ", found null rwLock" );
      return account;
    }

    // generate date time now
    Date now = new Date();

    // compose into a new object of account
    account = new Account();
    account.setAccountId( accountId );
    account.setAccountProfile( accountProfile );
    account.setCurrentUnit( initUnit );
    account.setLastHit( now );
    account.setRwLock( rwLock );

    return account;
  }

}
