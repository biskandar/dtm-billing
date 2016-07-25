package com.beepcast.billing.acc;

import com.beepcast.common.util.concurrent.ReadWriteLock;
import com.beepcast.common.util.concurrent.WriterPreferenceReadWriteLock;

public class AccountEngineFactory {

  public static AccountEngine generateAccountEngineImpl( ReadWriteLock rwLock ) {
    AccountEngineImpl accountEngineImpl = new AccountEngineImpl();
    if ( rwLock == null ) {
      rwLock = new WriterPreferenceReadWriteLock();
    }
    accountEngineImpl.setRwLock( rwLock );
    return accountEngineImpl;
  }

}
