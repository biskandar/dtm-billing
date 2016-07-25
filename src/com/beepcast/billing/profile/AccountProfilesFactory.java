package com.beepcast.billing.profile;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.BillingConf;
import com.beepcast.billing.storage.Storage;
import com.beepcast.billing.storage.Storages;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class AccountProfilesFactory {

  static final DLogContext lctx = new SimpleContext( "AccountProfilesFactory" );

  public static AccountProfiles generateAccountProfiles( List ids , Map map ,
      Storages storages ) {
    AccountProfiles profiles = null;

    if ( ids == null ) {
      return profiles;
    }

    if ( map == null ) {
      return profiles;
    }

    if ( storages == null ) {
      return profiles;
    }

    // create account profiles with no thread safe because
    // there will be no changes list inside
    profiles = new AccountProfiles( false );

    String id;
    AccountProfile profile;

    String headerLog;
    Iterator iterIds = ids.iterator();
    while ( iterIds.hasNext() ) {
      id = (String) iterIds.next();
      // validate id
      if ( ( id == null ) || ( id.equals( "" ) ) ) {
        continue;
      }

      headerLog = "[AccountProfile-" + id + "] ";

      // get level property
      int level = Level.UNKNOWN;
      {
        String strLevel = (String) map.get( id + BillingConf.FIELD_SEPARATOR
            + BillingConf.ACCOUNT_PROFILE_LEVEL );
        if ( strLevel.equalsIgnoreCase( "client" ) ) {
          level = Level.CLIENT;
        }
        if ( strLevel.equalsIgnoreCase( "event" ) ) {
          level = Level.EVENT;
        }
      }
      if ( level == Level.UNKNOWN ) {
        DLog.warning( lctx , headerLog + "Bypass read , found empty level" );
        continue;
      }

      // get payment type property
      int paymentType = PaymentType.UNKNOWN;
      {
        String strPaymentType = (String) map.get( id
            + BillingConf.FIELD_SEPARATOR
            + BillingConf.ACCOUNT_PROFILE_PAYMENTTYPE );
        if ( strPaymentType.equalsIgnoreCase( "prepaid" ) ) {
          paymentType = PaymentType.PREPAID;
        }
        if ( strPaymentType.equalsIgnoreCase( "postpaid" ) ) {
          paymentType = PaymentType.POSTPAID;
        }
      }
      if ( paymentType == PaymentType.UNKNOWN ) {
        DLog.warning( lctx , headerLog
            + "Bypass read , found empty paymentType" );
        continue;
      }

      // get map key prefix property
      String mapKeyPrefix = (String) map.get( id + BillingConf.FIELD_SEPARATOR
          + BillingConf.ACCOUNT_PROFILE_MAPKEYPREFIX );
      if ( ( mapKeyPrefix == null ) || ( mapKeyPrefix.equals( "" ) ) ) {
        DLog.warning( lctx , headerLog
            + "Bypass read , found empty mapKeyPrefix" );
        continue;
      }

      // get storage id property
      String storageId = (String) map.get( id + BillingConf.FIELD_SEPARATOR
          + BillingConf.ACCOUNT_PROFILE_STORAGEID );
      if ( ( storageId == null ) || ( storageId.equals( "" ) ) ) {
        DLog.warning( lctx , headerLog + "Bypass read , found empty storageId" );
        continue;
      }

      // validate storage id property
      Storage storage = storages.getStorage( storageId );
      if ( storage == null ) {
        DLog.warning( lctx , headerLog
            + "Bypass read , found invalid storageId" );
        continue;
      }

      // get lowest unit property
      double lowestUnit = 0;
      try {
        String str = (String) map.get( id + BillingConf.FIELD_SEPARATOR
            + BillingConf.ACCOUNT_PROFILE_LOWESTUNIT );
        if ( ( str != null ) && ( !str.equals( "" ) ) ) {
          lowestUnit = Double.parseDouble( str );
        }
      } catch ( Exception e ) {
      }

      // get round decimal places property
      int roundDecimalPlaces = 5;
      try {
        String str = (String) map.get( id + BillingConf.FIELD_SEPARATOR
            + BillingConf.ACCOUNT_PROFILE_ROUNDDECIMALPLACES );
        if ( ( str != null ) && ( !str.equals( "" ) ) ) {
          roundDecimalPlaces = Integer.parseInt( str );
        }
      } catch ( Exception e ) {
      }

      // generate accountProfile and put in the accountProfiles
      profile = AccountProfileFactory.generateAccountProfile( id , level ,
          paymentType , mapKeyPrefix , storage , lowestUnit ,
          roundDecimalPlaces );
      if ( !profiles.addAccountProfile( profile ) ) {
        DLog.warning( lctx , headerLog + "Failed to created a new "
            + "account profile , from : id = " + id + " , level = " + level
            + " , paymentType = " + paymentType + " , mapKeyPrefix = "
            + mapKeyPrefix + " , lowestUnit = " + lowestUnit
            + " , roundDecimalPlaces = " + roundDecimalPlaces );
        continue;
      }

      // log it
      DLog.debug( lctx , headerLog + "Successfully created a new "
          + "account profile : id = " + id + " , level = " + level
          + " , paymentType = " + paymentType + " , mapKeyPrefix = "
          + mapKeyPrefix + " , lowestUnit = " + lowestUnit
          + " , roundDecimalPlaces = " + roundDecimalPlaces );

    } // while ( iterIds.hasNext() )

    return profiles;
  }

}
