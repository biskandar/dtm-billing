package com.beepcast.billing.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.BillingResult;
import com.beepcast.billing.BillingResultFactory;
import com.beepcast.billing.BillingStatus;
import com.beepcast.billing.acc.Account;
import com.beepcast.billing.acc.AccountCommon;
import com.beepcast.billing.acc.AccountEngine;
import com.beepcast.billing.common.AccountUtils;
import com.beepcast.billing.profile.AccountProfile;
import com.beepcast.billing.profile.AccountProfiles;
import com.beepcast.billing.profile.BillingCommand;
import com.beepcast.billing.profile.PaymentType;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BasicBillingEngine implements BillingEngine {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "BasicBillingEngine" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private Object lockObject;
  private boolean debug;
  private AccountProfiles accountProfiles;
  private AccountEngine accountEngine;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public BasicBillingEngine() {
    lockObject = new Object();
    this.debug = false;
  }

  public BasicBillingEngine( boolean debug ) {
    lockObject = new Object();
    this.debug = debug;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Set Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void setAccountProfiles( AccountProfiles accountProfiles ) {
    this.accountProfiles = accountProfiles;
  }

  public void setAccountEngine( AccountEngine accountEngine ) {
    this.accountEngine = accountEngine;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public String name() {
    return "BasicBillingEngine";
  }

  public AccountProfile getAccountProfile( String profileId ) {
    AccountProfile accountProfile = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to get account profile "
          + ", found invalid init" );
      return accountProfile;
    }
    if ( profileId != null ) {
      accountProfile = accountProfiles.getAccountProfile( profileId );
    }
    return accountProfile;
  }

  public Map listActiveAccounts() {
    Map map = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to get list of active account "
          + ", found invalid init" );
      return map;
    }

    List listAccountProfileIds = accountProfiles.getAccountProfilesIds();
    if ( ( listAccountProfileIds == null )
        || ( listAccountProfileIds.size() < 1 ) ) {
      DLog.warning( lctx , "Failed to get list of active account "
          + ", found empty list account profile ids" );
      return map;
    }

    map = new HashMap();

    String accountProfileId;
    AccountProfile accountProfile;
    List listAccountIds;
    Iterator iterAccountIds;
    String accountId;

    Iterator iterAccountProfileIds = listAccountProfileIds.iterator();
    while ( iterAccountProfileIds.hasNext() ) {
      accountProfileId = (String) iterAccountProfileIds.next();
      if ( ( accountProfileId == null ) || ( accountProfileId.equals( "" ) ) ) {
        continue;
      }
      accountProfile = (AccountProfile) accountProfiles
          .getAccountProfile( accountProfileId );
      if ( accountProfile == null ) {
        continue;
      }
      listAccountIds = accountEngine.listActiveAccountIds( accountProfile );
      if ( listAccountIds == null ) {
        continue;
      }
      iterAccountIds = listAccountIds.iterator();
      while ( iterAccountIds.hasNext() ) {
        accountId = (String) iterAccountIds.next();
        if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
          continue;
        }
        // put into map object
        map.put( accountId , accountProfileId );
      } // iterate all account id
    } // iterate all profile id

    return map;
  }

  public Account queryAccount( String profileId , String accountId ) {
    Account account = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to query account , found invalid init" );
      return account;
    }
    synchronized ( lockObject ) {
      AccountProfile accountProfile = getAccountProfile( profileId );
      if ( accountProfile != null ) {
        account = accountEngine
            .queryAccount( accountProfile , accountId , true );
      }
    }
    return account;
  }

  public boolean registerAccount( Account account ) {
    boolean result = false;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to register account "
          + ", found invalid init" );
      return result;
    }
    synchronized ( lockObject ) {
      result = accountEngine.storeAccount( account , true );
    }
    return result;
  }

  public boolean removeAccount( Account account ) {
    boolean result = false;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to remove  account "
          + ", found invalid init" );
      return result;
    }
    synchronized ( lockObject ) {
      result = accountEngine.cleanAccount( account , true );
    }
    return result;
  }

  public BillingResult reset( String profileId , Integer accId , Double unit ) {
    BillingResult billingResult = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to reset , found invalid init" );
      billingResult = BillingResultFactory.generateBillingResult(
          BillingCommand.RESET , BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL );
      return billingResult;
    }
    if ( unit == null ) {
      unit = new Double( 0.0 );
    }
    synchronized ( lockObject ) {
      billingResult = doTransaction( BillingCommand.RESET , profileId , accId ,
          unit );
    }
    return billingResult;
  }

  public BillingResult doCredit( String profileId , Integer accId , Double unit ) {
    BillingResult billingResult = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to do credit , found invalid init" );
      billingResult = BillingResultFactory.generateBillingResult(
          BillingCommand.DO_CREDIT ,
          BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL );
      return billingResult;
    }
    if ( unit == null ) {
      billingResult = BillingResultFactory.generateBillingResult(
          BillingCommand.DO_CREDIT ,
          BillingStatus.PAYMENT_RESULT_FAILED_NOCREDITUNIT );
      return billingResult;
    }
    synchronized ( lockObject ) {
      billingResult = doTransaction( BillingCommand.DO_CREDIT , profileId ,
          accId , unit );
    }
    return billingResult;
  }

  public BillingResult getBalance( String profileId , Integer accId ) {
    BillingResult billingResult = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to do balance , found invalid init" );
      billingResult = BillingResultFactory.generateBillingResult(
          BillingCommand.GET_BALANCE ,
          BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL );
      return billingResult;
    }
    synchronized ( lockObject ) {
      billingResult = doTransaction( BillingCommand.GET_BALANCE , profileId ,
          accId , null );
    }
    return billingResult;
  }

  public BillingResult doDebit( String profileId , Integer accId , Double unit ) {
    BillingResult billingResult = null;
    if ( !validInit() ) {
      DLog.warning( lctx , "Failed to do debit , found invalid init" );
      billingResult = BillingResultFactory
          .generateBillingResult( BillingCommand.DO_DEBIT ,
              BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL );
      return billingResult;
    }
    if ( unit == null ) {
      billingResult = BillingResultFactory.generateBillingResult(
          BillingCommand.DO_DEBIT ,
          BillingStatus.PAYMENT_RESULT_FAILED_NODEBITUNIT );
      return billingResult;
    }
    synchronized ( lockObject ) {
      billingResult = doTransaction( BillingCommand.DO_DEBIT , profileId ,
          accId , unit );
    }
    return billingResult;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private BillingResult doTransaction( int billingCmd , String profileId ,
      Integer accId , Double unit ) {

    // query and validate account profile
    AccountProfile accountProfile = getAccountProfile( profileId );
    if ( accountProfile == null ) {
      DLog.warning( lctx , "Failed to get account profile" );
      return BillingResultFactory.generateBillingResult( billingCmd ,
          BillingStatus.PAYMENT_RESULT_FAILED_NOPROFID );
    }

    // query account
    Account account = accountEngine
        .queryAccount( accountProfile , accId , true );
    if ( account == null ) {
      DLog.warning( lctx , "Failed to get account in the list" );
      return BillingResultFactory.generateBillingResult( billingCmd ,
          BillingStatus.PAYMENT_RESULT_FAILED_NOACCOUNT );
    }

    // compose headerLog
    String headerLog = AccountCommon.headerLog( account );

    // get account payment type
    int accPaymentType = AccountUtils.getPaymentType( account );

    // get balance before
    Double balanceBefore = roundDecimalPlaces( accountProfile , accountEngine
        .readBalance( account ) );
    if ( balanceBefore == null ) {
      DLog.warning( lctx , headerLog + "Failed to get balance "
          + "in the account object" );
      return BillingResultFactory.generateBillingResult( billingCmd ,
          BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL , account );
    }

    // provide balance amount for get balance api
    if ( billingCmd == BillingCommand.GET_BALANCE ) {
      if ( debug ) {
        DLog.debug( lctx , headerLog + "Performed get balance from account "
            + ": balance = " + balanceBefore );
      }
      return BillingResultFactory.generateBillingResult( billingCmd , account ,
          balanceBefore , balanceBefore );
    }

    // update the balance based on billing command
    Double balanceAfter = null;
    switch ( billingCmd ) {
    case BillingCommand.RESET :
      balanceAfter = new Double( unit.doubleValue() );
      balanceAfter = roundDecimalPlaces( accountProfile , balanceAfter );
      if ( debug ) {
        DLog.debug( lctx , headerLog + "Performed reset from account "
            + ": balanceBefore = " + balanceBefore + " , balanceAfter = "
            + balanceAfter );
      }
      break;
    case BillingCommand.DO_CREDIT :
      if ( accPaymentType == PaymentType.PREPAID ) {
        balanceAfter = new Double( balanceBefore.doubleValue()
            + unit.doubleValue() );
        balanceAfter = roundDecimalPlaces( accountProfile , balanceAfter );
      }
      if ( accPaymentType == PaymentType.POSTPAID ) {
        DLog.warning( lctx , headerLog + "Postpaid can not perform credit "
            + ", rejected ." );
        return BillingResultFactory.generateBillingResult( billingCmd ,
            BillingStatus.PAYMENT_RESULT_ERROR_CREDIT , account );
      }
      if ( debug ) {
        DLog.debug( lctx , headerLog + "Performed credit from account "
            + ": paymentType = "
            + PaymentType.paymentTypeToString( accPaymentType )
            + " , balanceBefore = " + balanceBefore + " , balanceAfter = "
            + balanceAfter );
      }
      break;
    case BillingCommand.DO_DEBIT :
      if ( accPaymentType == PaymentType.PREPAID ) {
        balanceAfter = new Double( balanceBefore.doubleValue()
            - unit.doubleValue() );
        balanceAfter = roundDecimalPlaces( accountProfile , balanceAfter );
        // validate is enough balance ?
        if ( balanceAfter.doubleValue() < accountProfile.getLowestUnit() ) {
          DLog.warning( lctx , headerLog + "Can not perform debit "
              + ", found not enough balance , the lowest unit is "
              + accountProfile.getLowestUnit() );
          return BillingResultFactory.generateBillingResult( billingCmd ,
              BillingStatus.PAYMENT_RESULT_FAILED_NOTENOUGHBALANCE , account ,
              balanceBefore , balanceBefore );
        }
      }
      if ( accPaymentType == PaymentType.POSTPAID ) {
        balanceAfter = new Double( balanceBefore.doubleValue()
            + unit.doubleValue() );
        balanceAfter = roundDecimalPlaces( accountProfile , balanceAfter );
      }
      if ( debug ) {
        DLog.debug( lctx , headerLog + "Performed debit from account "
            + ": paymentType = "
            + PaymentType.paymentTypeToString( accPaymentType )
            + " , balanceBefore = " + balanceBefore + " , balanceAfter = "
            + balanceAfter );
      }
      break;
    }

    // validate and update the new balance
    if ( balanceAfter == null ) {
      DLog.warning( lctx , headerLog + "Failed to generate a new balance" );
      return BillingResultFactory.generateBillingResult( billingCmd ,
          BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL , account );
    }
    boolean updatedAccountBalance = accountEngine.writeBalance( account ,
        balanceAfter );
    if ( !updatedAccountBalance ) {
      DLog.warning( lctx , headerLog + "Failed to write "
          + "a new balance into account" );
      return BillingResultFactory.generateBillingResult( billingCmd ,
          BillingStatus.PAYMENT_RESULT_ERROR_INTERNAL , account );
    }

    return BillingResultFactory.generateBillingResult( billingCmd , account ,
        balanceBefore , balanceAfter );
  }

  private Double roundDecimalPlaces( AccountProfile accountProfile ,
      Double unitAmountBefore ) {
    Double unitAmountAfter = unitAmountBefore;
    if ( accountProfile == null ) {
      return unitAmountAfter;
    }
    if ( unitAmountBefore == null ) {
      return unitAmountAfter;
    }
    if ( accountProfile.getRoundDecimalPlaces() < 1 ) {
      return unitAmountAfter;
    }
    int cropper = (int) Math.pow( (double) 10 , (double) accountProfile
        .getRoundDecimalPlaces() );
    double unitAmount = unitAmountBefore.doubleValue();
    unitAmount = (double) Math.round( unitAmount * cropper ) / cropper;
    unitAmountAfter = new Double( unitAmount );
    return unitAmountAfter;
  }

  private boolean validInit() {
    boolean result = false;
    if ( accountProfiles == null ) {
      DLog.warning( lctx , "Failed to verify initialized "
          + ", found null account profiles" );
      return result;
    }
    if ( accountEngine == null ) {
      DLog.warning( lctx , "Failed to verify initialized "
          + ", found null account engine" );
      return result;
    }
    result = true;
    return result;
  }

}
