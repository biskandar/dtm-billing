package com.beepcast.billing.engine;

import java.util.Map;

import com.beepcast.billing.BillingResult;
import com.beepcast.billing.BillingResultFactory;
import com.beepcast.billing.BillingStatus;
import com.beepcast.billing.acc.Account;
import com.beepcast.billing.acc.AccountCommon;
import com.beepcast.billing.acc.AccountFactory;
import com.beepcast.billing.acc.AccountIdFactory;
import com.beepcast.billing.common.AccountIdUtils;
import com.beepcast.billing.engine.log.BillingLogBeanFactory;
import com.beepcast.billing.engine.log.BillingLogWorker;
import com.beepcast.billing.persistence.ClientAccountEntity;
import com.beepcast.billing.persistence.ClientAccountService;
import com.beepcast.billing.profile.AccountProfile;
import com.beepcast.billing.profile.BillingCommand;
import com.beepcast.billing.profile.Level;
import com.beepcast.billing.profile.PaymentType;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class PersistenceBillingEngine extends BillingEngineDecorator {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "PersistenceBillingEngine" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private Object lockObject;
  private boolean debug;
  private ClientAccountService clientAccountService;
  private BasicBillingEngine basicBillingEngine;
  private BillingLogWorker billingLogWorker;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public PersistenceBillingEngine( boolean debug ,
      BasicBillingEngine basicBillingEngine , BillingLogWorker billingLogWorker ) {
    lockObject = new Object();
    this.debug = debug;
    clientAccountService = new ClientAccountService();
    this.billingLogWorker = billingLogWorker;
    this.basicBillingEngine = basicBillingEngine;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public String name() {
    return "PersistenceBillingEngine";
  }

  public Map listActiveAccounts() {
    Map map = null;
    synchronized ( lockObject ) {
      map = basicBillingEngine.listActiveAccounts();
    }
    return map;
  }

  public Account queryAccount( String profileId , String accountId ) {
    Account account = null;
    synchronized ( lockObject ) {
      account = basicBillingEngine.queryAccount( profileId , accountId );
    }
    return account;
  }

  public boolean registerAccount( Account account ) {
    boolean result = false;
    synchronized ( lockObject ) {
      result = basicBillingEngine.registerAccount( account );
      if ( debug ) {
        String headerLog = AccountCommon.headerLog( account );
        DLog.debug( lctx , headerLog + "Registered an account "
            + "into memory list , result = " + result );
      }
    }
    return result;
  }

  public boolean removeAccount( Account account ) {
    boolean result = false;
    synchronized ( lockObject ) {
      result = basicBillingEngine.removeAccount( account );
      if ( debug ) {
        String headerLog = AccountCommon.headerLog( account );
        DLog.debug( lctx , headerLog + "Removed an account "
            + "from memory list , result = " + result );
      }
    }
    return result;
  }

  public BillingResult reset( String profileId , Integer accId , Double unit ) {
    BillingResult billingResult = null;
    synchronized ( lockObject ) {
      billingResult = basicBillingEngine.reset( profileId , accId , unit );
      // when account id not found in the storage
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT ) {
        billingResult = persistanceAccount( BillingCommand.RESET , profileId ,
            accId , unit );
      }
      // when account not yet persist from entity
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_RETRY ) {
        billingResult = basicBillingEngine.reset( profileId , accId , unit );
      }
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_SUCCEED ) {
        doBillingLog( BillingCommand.RESET , billingResult );
      }
    }
    return billingResult;
  }

  public BillingResult doCredit( String profileId , Integer accId , Double unit ) {
    BillingResult billingResult = null;
    synchronized ( lockObject ) {
      billingResult = basicBillingEngine.doCredit( profileId , accId , unit );
      // when account id not found in the storage
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT ) {
        billingResult = persistanceAccount( BillingCommand.DO_CREDIT ,
            profileId , accId , unit );
      }
      // when account not yet persist from entity
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_RETRY ) {
        billingResult = basicBillingEngine.doCredit( profileId , accId , unit );
      }
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_SUCCEED ) {
        doBillingLog( BillingCommand.DO_CREDIT , billingResult );
      }
    }
    return billingResult;
  }

  public BillingResult getBalance( String profileId , Integer accId ) {
    BillingResult billingResult = null;
    synchronized ( lockObject ) {
      billingResult = basicBillingEngine.getBalance( profileId , accId );
      // when account id not found in the storage
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT ) {
        billingResult = persistanceAccount( BillingCommand.GET_BALANCE ,
            profileId , accId , null );
      }
      // when account not yet persist from entity
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_RETRY ) {
        billingResult = basicBillingEngine.getBalance( profileId , accId );
      }
    }
    return billingResult;
  }

  public BillingResult doDebit( String profileId , Integer accId , Double unit ) {
    BillingResult billingResult = null;
    synchronized ( lockObject ) {
      billingResult = basicBillingEngine.doDebit( profileId , accId , unit );
      // when account id not found in the storage
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT ) {
        billingResult = persistanceAccount( BillingCommand.DO_DEBIT ,
            profileId , accId , unit );
      }
      // when account not yet persist from entity
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_FAILED_RETRY ) {
        billingResult = basicBillingEngine.doDebit( profileId , accId , unit );
      }
      if ( billingResult.getPaymentResult() == BillingStatus.PAYMENT_RESULT_SUCCEED ) {
        doBillingLog( BillingCommand.DO_DEBIT , billingResult );
      }
    }
    return billingResult;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean doBillingLog( int billingCmd , BillingResult billingResult ) {
    boolean result = false;

    if ( billingResult == null ) {
      DLog.warning( lctx , "Can not do the billing log "
          + ", found null billing status" );
      return result;
    }

    if ( billingResult.getPaymentResult() != BillingStatus.PAYMENT_RESULT_SUCCEED ) {
      DLog.warning( lctx , "Can not do the billing log "
          + ", found failed and/or error payment status" );
      return result;
    }

    String accountId , accountProfile;
    int paymentType , paymentLevel , clientId , eventId;
    double unit = 0 , balance = 0;
    String description = null;

    // get key ( accountId )
    accountId = billingResult.getAccountId();

    // generate headerLog
    String headerLog = AccountCommon.headerLog( accountId );

    // get all params need for billing log
    accountProfile = billingResult.getAccountProfileId();
    paymentType = billingResult.getPaymentType();
    paymentLevel = billingResult.getPaymentLevel();

    clientId = 0;
    if ( paymentLevel == Level.CLIENT ) {
      Integer accId = AccountIdUtils.getAccId( accountId );
      if ( accId != null ) {
        clientId = accId.intValue();
      }
      if ( clientId < 1 ) {
        DLog.warning( lctx , headerLog + "Failed to perform billing log "
            + ", found zero clientId" );
        return result;
      }
    }

    eventId = 0;
    if ( paymentLevel == Level.EVENT ) {
      Integer accId = AccountIdUtils.getAccId( accountId );
      if ( accId != null ) {
        eventId = accId.intValue();
      }
      if ( eventId < 1 ) {
        DLog.warning( lctx , headerLog + "Failed to perform billing log "
            + ", found zero eventId" );
        return result;
      }
    }

    Double balanceBefore = billingResult.getBalanceBefore();
    Double balanceAfter = billingResult.getBalanceAfter();
    if ( ( balanceBefore != null ) && ( balanceAfter != null ) ) {
      unit = balanceAfter.doubleValue() - balanceBefore.doubleValue();
      balance = balanceAfter.doubleValue();
    }

    if ( billingLogWorker != null ) {
      if ( !billingLogWorker.log( BillingLogBeanFactory.createBillingLogBean(
          accountId , accountProfile , clientId , paymentType , paymentLevel ,
          eventId , unit , balance , description ) ) ) {
        DLog.warning( lctx , headerLog + "Failed to perform billing log " );
        return result;
      }
    }

    result = true;
    return result;
  }

  private BillingResult persistanceAccount( int billingCmd , String profileId ,
      Integer accId , Double unit ) {
    BillingResult billingResult = BillingResultFactory.generateBillingResult(
        billingCmd , BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT );

    // get account profile
    AccountProfile accountProfile = basicBillingEngine
        .getAccountProfile( profileId );
    if ( accountProfile == null ) {
      DLog.warning( lctx , "Failed to persist account "
          + ", found failed to get account profile" );
      return billingResult;
    }

    // get accountId
    String accountId = AccountIdFactory.generateAccountId( accountProfile ,
        accId );
    if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to persist account "
          + ", found failed to generate account id" );
      return billingResult;
    }

    // generate headerLog
    String headerLog = AccountCommon.headerLog( accountId );

    // prepare account
    Account account = null;

    // find and query entity
    ClientAccountEntity entity = clientAccountService.queryEntity( accountId );
    if ( ( entity != null ) && ( entity.getId() > 0 ) ) {
      // found persistent entity , validate clientProfile
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Found client account entity from table "
            + ", trying to persist account with : id = " + entity.getId() );
      }
      if ( !entity.getAccountProf().equals( accountProfile.getId() ) ) {
        DLog.warning( lctx , headerLog + "Failed to persist account "
            + ", found profile id is diff : " + entity.getAccountProf() );
        return billingResult;
      }
      // create account based on entity
      account = AccountFactory.generateAccount( accId , accountProfile ,
          new Double( entity.getBalance() ) );
      if ( account == null ) {
        DLog.warning( lctx , headerLog + "Failed to persist account "
            + ", failed to generate a new account" );
        return billingResult;
      }
      if ( debug ) {
        DLog.debug( lctx , headerLog
            + "Defined an account object with : profileId = "
            + account.getAccountProfile().getId() + " , unit = "
            + account.getCurrentUnit() );
      }
      // register as a new account
      if ( registerAccount( account ) ) {
        billingResult = BillingResultFactory.generateBillingResult( billingCmd ,
            BillingStatus.PAYMENT_RESULT_FAILED_RETRY );
      } else {
        DLog.warning( lctx , headerLog + "Failed to persist account "
            + ", found failed to register account" );
      }
    } else {
      // create account based on payment type and level
      DLog.debug( lctx , headerLog
          + "Failed to found client account entity from table "
          + ", trying to create new one with : paymentType = "
          + PaymentType.paymentTypeToString( accountProfile.getPaymentType() )
          + " , billingCmd = "
          + BillingCommand.billingCommandToString( billingCmd ) + " , unit = "
          + unit );
      account = createFirstAccount( headerLog , accId , accountProfile ,
          billingCmd , unit );
      if ( account == null ) {
        DLog.warning( lctx , headerLog + "Failed to persist account "
            + ", failed to generate new account" );
        return billingResult;
      }
      DLog.debug( lctx , headerLog
          + "Generated an account object with : profileId = "
          + account.getAccountProfile().getId() + " , unit = "
          + account.getCurrentUnit() );
      // register as a new account
      if ( registerAccount( account ) ) {
        billingResult = BillingResultFactory.generateBillingResult( billingCmd ,
            account , null , account.getCurrentUnit() );
        DLog.debug( lctx , headerLog + "Successfully persisted a new account" );
      } else {
        DLog.warning( lctx , headerLog + "Failed to persist account "
            + ", found failed to register account" );
      }
    }

    return billingResult;
  }

  private Account createFirstAccount( String headerLog , Integer accId ,
      AccountProfile accountProfile , int billingCmd , Double unit ) {
    Account account = null;

    // prepare balance
    Double balance = unit;
    if ( balance == null ) {
      balance = new Double( 0 );
      DLog.debug( lctx , headerLog + "Found null balance "
          + ", set into default value = " + balance );
    }

    // create fresh new account
    int paymentType = accountProfile.getPaymentType();
    switch ( paymentType ) {

    case PaymentType.PREPAID :
      switch ( billingCmd ) {
      case BillingCommand.RESET :
        account = AccountFactory.generateAccount( accId , accountProfile ,
            balance );
        break;
      case BillingCommand.DO_CREDIT :
        account = AccountFactory.generateAccount( accId , accountProfile ,
            balance );
        break;
      default :
        DLog.warning( lctx , headerLog + "Failed to create first account "
            + ", found invalid billing command = "
            + BillingCommand.billingCommandToString( billingCmd ) );
        break;
      } // switch ( billingCmd )
      break;

    case PaymentType.POSTPAID :
      switch ( billingCmd ) {
      case BillingCommand.RESET :
        account = AccountFactory.generateAccount( accId , accountProfile ,
            balance );
        break;
      case BillingCommand.DO_DEBIT :
        account = AccountFactory.generateAccount( accId , accountProfile ,
            balance );
        break;
      default :
        DLog.warning( lctx , headerLog + "Failed to create first account "
            + ", found invalid billing command = "
            + BillingCommand.billingCommandToString( billingCmd ) );
        break;
      } // switch ( billingCmd )
      break;

    default :
      DLog.warning( lctx , headerLog + "Failed to create first account "
          + ", found invalid payment type = " + paymentType );
      break;

    } // switch ( accountProfile.getPaymentType() )

    return account;
  }

}
