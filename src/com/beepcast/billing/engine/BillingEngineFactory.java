package com.beepcast.billing.engine;

import com.beepcast.billing.acc.AccountEngine;
import com.beepcast.billing.acc.AccountEngineFactory;
import com.beepcast.billing.engine.log.BillingLogWorker;
import com.beepcast.billing.profile.AccountProfiles;
import com.beepcast.common.util.concurrent.ReadWriteLock;
import com.beepcast.common.util.concurrent.WriterPreferenceReadWriteLock;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingEngineFactory {

  static final DLogContext lctx = new SimpleContext( "BillingEngineFactory" );

  public static BillingEngine generatePersistenceBillingEngine( boolean debug ,
      AccountProfiles accountProfiles , BillingLogWorker billingLogWorker ) {
    BillingEngine billingEngine = null;

    String headerLog = "[PersistenceBillingEngine] ";

    if ( accountProfiles == null ) {
      DLog.warning( lctx , headerLog + "Failed to generate billing engine "
          + ", found empty accountProfiles" );
      return billingEngine;
    }
    if ( billingLogWorker == null ) {
      DLog.warning( lctx , headerLog + "Failed to generate billing engine "
          + ", found null billing log worker" );
      return billingEngine;
    }

    // generate read write lock object
    ReadWriteLock rwLock = new WriterPreferenceReadWriteLock();
    DLog.debug( lctx , headerLog + "Generated rwLock with "
        + "WriterPreference lock class" );

    // generate account engine with lock object
    AccountEngine accountEngine = AccountEngineFactory
        .generateAccountEngineImpl( rwLock );
    DLog.debug( lctx , headerLog + "Generated account engine with "
        + "rwLock reference" );

    // create a basic billing engine
    BasicBillingEngine basicBillingEngine = new BasicBillingEngine( debug );
    basicBillingEngine.setAccountProfiles( accountProfiles );
    basicBillingEngine.setAccountEngine( accountEngine );
    DLog.debug( lctx , headerLog + "Generated basic billing engine , with : "
        + accountProfiles.size() + " account profile(s)" );

    // use a decorator pattern to create extended billing engine
    billingEngine = new PersistenceBillingEngine( debug , basicBillingEngine ,
        billingLogWorker );
    DLog.debug( lctx , headerLog + "Generated decorator billing engine" );

    return billingEngine;
  }

}
