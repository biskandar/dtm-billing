package com.beepcast.billing.profile;

import com.beepcast.billing.storage.Storage;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class AccountProfileFactory {

  static final DLogContext lctx = new SimpleContext( "AccountProfileFactory" );

  public static AccountProfile generateAccountProfile( String id , int level ,
      int paymentType , String mapKeyPrefix , Storage storage ,
      double lowestUnit , int roundDecimalPlaces ) {
    AccountProfile accountProfile = null;

    if ( ( id == null ) || ( id.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to generate account profile "
          + ", found empty id" );
      return accountProfile;
    }

    if ( level == Level.UNKNOWN ) {
      DLog.warning( lctx , "Failed to generate account profile "
          + ", found unknown level" );
      return accountProfile;
    }

    if ( paymentType == PaymentType.UNKNOWN ) {
      DLog.warning( lctx , "Failed to generate account profile "
          + ", found unknown paymentType" );
      return accountProfile;
    }

    if ( ( mapKeyPrefix == null ) || ( mapKeyPrefix.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to generate account profile "
          + ", found empty mapKeyPrefix" );
      return accountProfile;
    }

    if ( storage == null ) {
      DLog.warning( lctx , "Failed to generate account profile "
          + ", found null storage" );
      return accountProfile;
    }

    accountProfile = new AccountProfile();
    accountProfile.setId( id );
    accountProfile.setLevel( level );
    accountProfile.setPaymentType( paymentType );
    accountProfile.setMapKeyPrefix( mapKeyPrefix );
    accountProfile.setStorage( storage );
    accountProfile.setLowestUnit( lowestUnit );
    accountProfile.setRoundDecimalPlaces( roundDecimalPlaces );

    return accountProfile;
  }

}
