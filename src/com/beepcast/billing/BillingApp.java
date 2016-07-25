package com.beepcast.billing;

import java.util.Iterator;
import java.util.List;

import com.beepcast.billing.engine.BillingEngine;
import com.beepcast.billing.engine.BillingEngineFactory;
import com.beepcast.billing.engine.log.BillingLogWorker;
import com.beepcast.billing.management.ManagementTask;
import com.beepcast.billing.management.ManagementTasks;
import com.beepcast.billing.management.ManagementTasksFactory;
import com.beepcast.billing.profile.AccountProfiles;
import com.beepcast.billing.profile.AccountProfilesFactory;
import com.beepcast.billing.profile.BillingCommand;
import com.beepcast.billing.storage.Storages;
import com.beepcast.billing.storage.StoragesFactory;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingApp implements Module , BillingApi {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "BillingApp" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean initialized;

  private BillingConf billingConf;
  private Storages storages;
  private BillingLogWorker billingLogWorker;
  private AccountProfiles accountProfiles;
  private BillingEngine billingEngine;
  private ManagementTasks managementTasks;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void init( BillingConf billingConf ) {
    initialized = false;

    if ( billingConf == null ) {
      DLog.warning( lctx , "Failed to initialized billing "
          + ", found null conf" );
      return;
    }
    this.billingConf = billingConf;

    DLog.debug( lctx , "Debug mode = " + billingConf.isDebug() );

    // generate storages
    DLog.debug( lctx , "Trying to generate storages" );
    storages = StoragesFactory.generateStorages( billingConf
        .getAccountStorageIds() , billingConf.getAccountStorages() );
    if ( storages == null ) {
      DLog.warning( lctx , "Found empty storages" );
      return;
    }
    DLog.debug( lctx , "Successfully generated storages , total = "
        + storages.size() + " storage(s)" );

    // generate billing log workers
    billingLogWorker = new BillingLogWorker( billingConf
        .getBillingLogWorkerConf() );
    DLog.debug( lctx , "Created billing log worker" );

    // generate account profiles
    DLog.debug( lctx , "Trying to generate account profiles" );
    accountProfiles = AccountProfilesFactory.generateAccountProfiles(
        billingConf.getAccountProfileIds() , billingConf.getAccountProfiles() ,
        storages );
    if ( accountProfiles == null ) {
      DLog.warning( lctx , "Found empty account profiles" );
      return;
    }
    DLog.debug( lctx , "Successfully generated account profiles , total = "
        + accountProfiles.size() + " profile(s)" );

    // generate persistence billing engine
    DLog.debug( lctx , "Trying to generate billing engine" );
    billingEngine = BillingEngineFactory.generatePersistenceBillingEngine(
        billingConf.isDebug() , accountProfiles , billingLogWorker );
    if ( billingEngine == null ) {
      DLog.warning( lctx , "Found empty persistence billing engine" );
      return;
    }
    DLog.debug( lctx , "Successfully generated billing engine , named = "
        + billingEngine.name() );

    // generate management tasks
    DLog.debug( lctx , "Trying to generate management tasks" );
    managementTasks = ManagementTasksFactory.generateManagementTasks(
        billingConf.isDebug() , billingConf.getManagementTaskIds() ,
        billingConf.getManagementTasks() , billingEngine );
    if ( managementTasks == null ) {
      DLog.warning( lctx , "Found empty management tasks" );
      return;
    }
    DLog.debug( lctx , "Successfully generated management tasks , total = "
        + managementTasks.size() + " task(s)" );

    initialized = true;
    DLog.debug( lctx , "all module(s) are initialized" );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void moduleStart() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to start billing module "
          + ", found not yet initialized" );
      return;
    }
    billingLogWorker.moduleStart();
    // start all modules
    startManagementTasks();
    DLog.debug( lctx , "all module(s) are started" );
  }

  public void moduleStop() {
    if ( !initialized ) {
      DLog.warning( lctx , "Failed to stop billing module "
          + ", found not yet initialized" );
      return;
    }
    // stop all modules
    stopManagementTasks();
    billingLogWorker.moduleStop();
    DLog.debug( lctx , "all module(s) are stopped" );
  }

  public BillingResult reset( String profileId , Integer accId , Double unit ) {
    int paymentResult = validateAppInit();
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult( BillingCommand.RESET ,
          paymentResult );
    }
    paymentResult = validateApiParam( profileId , accId );
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult( BillingCommand.RESET ,
          paymentResult );
    }
    return billingEngine.reset( profileId , accId , unit );
  }

  public BillingResult doCredit( String profileId , Integer accId , Double unit ) {
    int paymentResult = validateAppInit();
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult(
          BillingCommand.DO_CREDIT , paymentResult );
    }
    paymentResult = validateApiParam( profileId , accId );
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult(
          BillingCommand.DO_CREDIT , paymentResult );
    }
    return billingEngine.doCredit( profileId , accId , unit );
  }

  public BillingResult getBalance( String profileId , Integer accId ) {
    int paymentResult = validateAppInit();
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult(
          BillingCommand.GET_BALANCE , paymentResult );
    }
    paymentResult = validateApiParam( profileId , accId );
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult(
          BillingCommand.GET_BALANCE , paymentResult );
    }
    return billingEngine.getBalance( profileId , accId );
  }

  public BillingResult doDebit( String profileId , Integer accId , Double unit ) {
    int paymentResult = validateAppInit();
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult(
          BillingCommand.DO_DEBIT , paymentResult );
    }
    paymentResult = validateApiParam( profileId , accId );
    if ( paymentResult != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      return BillingResultFactory.generateBillingResult(
          BillingCommand.DO_DEBIT , paymentResult );
    }
    return billingEngine.doDebit( profileId , accId , unit );
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public Storages getStorages() {
    return storages;
  }

  public AccountProfiles getAccountProfiles() {
    return accountProfiles;
  }

  public BillingEngine getBillingEngine() {
    return billingEngine;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private int validateAppInit() {
    int paymentResult = BillingStatus.PAYMENT_RESULT_SUCCEED;
    if ( !initialized ) {
      paymentResult = BillingStatus.PAYMENT_RESULT_ERROR_NOINIT;
    }
    return paymentResult;
  }

  private int validateApiParam( String profileId , Integer accountId ) {
    int paymentResult = BillingStatus.PAYMENT_RESULT_SUCCEED;
    if ( ( profileId == null ) || ( profileId.equals( "" ) ) ) {
      paymentResult = BillingStatus.PAYMENT_RESULT_FAILED_NOPROFID;
    }
    if ( ( accountId == null ) || ( accountId.intValue() < 1 ) ) {
      paymentResult = BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT;
    }
    return paymentResult;
  }

  private void startManagementTasks() {
    if ( managementTasks == null ) {
      return;
    }
    List ids = managementTasks.getManagementTaskIds();
    Iterator iter = ids.iterator();
    while ( iter.hasNext() ) {
      String id = (String) iter.next();
      String headerLog = "[ManagementTask-" + id + "] ";
      ManagementTask mt = (ManagementTask) managementTasks
          .getManagementTask( id );
      if ( mt != null ) {
        mt.moduleStart();
        DLog.debug( lctx , headerLog + "module is started" );
      }
    }
  }

  private void stopManagementTasks() {
    if ( managementTasks == null ) {
      return;
    }
    List ids = managementTasks.getManagementTaskIds();
    Iterator iter = ids.iterator();
    while ( iter.hasNext() ) {
      String id = (String) iter.next();
      String headerLog = "[ManagementTask-" + id + "] ";
      ManagementTask mt = (ManagementTask) managementTasks
          .getManagementTask( id );
      if ( mt != null ) {
        mt.moduleStop();
        DLog.debug( lctx , headerLog + "module is stopped" );
      }
    }
  }

  private void init() {
    // nothing to do
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Singleton Pattern
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static final BillingApp INSTANCE = new BillingApp();

  private BillingApp() {
    init();
  }

  public static final BillingApp getInstance() {
    return INSTANCE;
  }

}
