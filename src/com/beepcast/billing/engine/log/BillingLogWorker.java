package com.beepcast.billing.engine.log;

import java.util.ArrayList;
import java.util.List;

import com.beepcast.billing.Module;
import com.beepcast.billing.BillingConf.BillingLogWorkerConf;
import com.beepcast.common.util.concurrent.BoundedLinkedQueue;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingLogWorker implements Module {

  // //////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // //////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "BillingLogWorker" );

  // //////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // //////////////////////////////////////////////////////////////////////////

  private boolean initialized;

  private BillingLogWorkerConf billingLogWorkerConf;

  private BillingLogService billingLogService;

  private BoundedLinkedQueue queueData;

  private boolean threadActive;
  private Thread[] threadWorkers;

  // //////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // //////////////////////////////////////////////////////////////////////////

  public BillingLogWorker( BillingLogWorkerConf billingLogWorkerConf ) {
    initialized = false;

    if ( billingLogWorkerConf == null ) {
      DLog.warning( lctx , "Failed to initialized "
          + ", found null billingLogWorkerConf" );
      return;
    }
    this.billingLogWorkerConf = billingLogWorkerConf;

    billingLogService = new BillingLogService();
    DLog.debug( lctx , "Created billing log service" );

    queueData = new BoundedLinkedQueue( billingLogWorkerConf.getQueueSize() );
    DLog.debug( lctx , "Created queue : capacity = " + queueData.capacity()
        + " msg(s) , timeout = " + billingLogWorkerConf.getQueueTimeout()
        + " ms" );

    threadActive = false;
    threadWorkers = new Thread[billingLogWorkerConf.getWorkerSize()];
    DLog.debug( lctx , "Created workers : size = " + threadWorkers.length );
    for ( int idx = 0 ; idx < threadWorkers.length ; idx++ ) {
      threadWorkers[idx] = new BillingLogWorkerThread( idx );
    }

    initialized = true;
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // //////////////////////////////////////////////////////////////////////////

  public void moduleStart() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to start , found not yet initialized" );
      return;
    }
    threadActive = true;
    for ( int idx = 0 ; idx < threadWorkers.length ; idx++ ) {
      threadWorkers[idx].start();
    }
  }

  public void moduleStop() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to stop , found not yet initialized" );
      return;
    }
    threadActive = false;
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // //////////////////////////////////////////////////////////////////////////

  public boolean log( BillingLogBean billingLogBean ) {
    boolean result = false;
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to log , found app is not yet initialized" );
      return result;
    }

    // validate must be params
    if ( billingLogBean == null ) {
      DLog.warning( lctx , "Failed to log , found null billing log bean" );
      return result;
    }

    // header log
    String headerLog = "[BillingAccount" + billingLogBean.getAccountId() + "] ";

    // validate if there is no threads active , then will
    // log straight away into table
    if ( !threadActive ) {
      DLog.debug( lctx , headerLog + "Found empty batchers running "
          + ", will try to log transaction into table : profile = "
          + billingLogBean.getAccountProfile() + " , clientId = "
          + billingLogBean.getClientId() + " , unit = "
          + billingLogBean.getUnit() );
      result = billingLogService.log( billingLogBean );
      return result;
    }

    // asynchronous process
    try {
      queueData.put( billingLogBean );
      result = true;
    } catch ( Exception e ) {
      DLog.warning( lctx , "Failed to log , " + e );
    }

    if ( !result ) {
      // when failed to do asynchronous process
      // will try log straightaway into table
      DLog.debug( lctx , headerLog + "Found failed to queue billing log bean "
          + ", will try to log transaction into table : profile = "
          + billingLogBean.getAccountProfile() + " , clientId = "
          + billingLogBean.getClientId() + " , unit = "
          + billingLogBean.getUnit() );
      result = billingLogService.log( billingLogBean );
    }

    return result;
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // //////////////////////////////////////////////////////////////////////////

  public boolean insertRecord( BillingLogBean billingLogBean ,
      List listBillingLogBeans ) {
    boolean result = false;

    if ( listBillingLogBeans == null ) {
      DLog.warning( lctx , "Failed to insert record "
          + ", found null listBillingLogBeans" );
      return result;
    }

    result = true;

    if ( billingLogBean != null ) {
      listBillingLogBeans.add( billingLogBean );
    }

    if ( listBillingLogBeans.size() > 0 ) {
      int totalInserted = 0;
      if ( listBillingLogBeans.size() > queueData.capacity() ) {
        totalInserted = billingLogService.log( listBillingLogBeans );
        listBillingLogBeans.clear();
        DLog.debug( lctx , "Successfully inserted billing log beans into "
            + "table , total effected : " + totalInserted + " record(s)" );
      } else {
        if ( billingLogBean == null ) {
          totalInserted = billingLogService.log( listBillingLogBeans );
          listBillingLogBeans.clear();
          DLog.debug( lctx , "Successfully inserted billing log beans into "
              + "table , total effected : " + totalInserted + " record(s)" );
        }
      }
    }

    return result;
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Inner Class
  //
  // //////////////////////////////////////////////////////////////////////////

  class BillingLogWorkerThread extends Thread {

    public BillingLogWorkerThread( int idx ) {
      super( "BillingLogWorkerThread-" + idx );
    }

    public void run() {
      List listBillingLogBeans = new ArrayList();
      DLog.debug( lctx , "Thread started" );
      while ( threadActive ) {
        try {
          BillingLogBean billingLogBean = (BillingLogBean) queueData
              .poll( billingLogWorkerConf.getQueueTimeout() );
          if ( billingLogBean == null ) {
            Thread.sleep( 1000 );
          }
          if ( !insertRecord( billingLogBean , listBillingLogBeans ) ) {
            DLog.warning( lctx , "Failed to insert billing log bean : "
                + billingLogBean );
            continue;
          }
          // ...
        } catch ( Exception e ) {
          DLog.warning( lctx , "Failed to process billing log , " + e );
        }
      }
      DLog.debug( lctx , "Thread stopped" );
    }

  }

}
