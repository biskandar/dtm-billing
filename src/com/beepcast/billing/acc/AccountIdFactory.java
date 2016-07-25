package com.beepcast.billing.acc;

import com.beepcast.billing.common.AccountIdUtils;
import com.beepcast.billing.profile.AccountProfile;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class AccountIdFactory {

  static final DLogContext lctx = new SimpleContext( "AccountIdFactory" );

  public static String generateAccountId( AccountProfile accountProfile ,
      Integer accId ) {
    String accountId = null;
    if ( accountProfile == null ) {
      DLog.warning( lctx , "Failed to generate accountId "
          + ", found null account profile" );
      return accountId;
    }
    return generateAccountId( accountProfile.getMapKeyPrefix() , accId );
  }

  public static String generateAccountId( String prefix , Integer accId ) {
    String accountId = null;
    if ( accId == null ) {
      DLog.warning( lctx , "Failed to generate accountId "
          + ", found null accId" );
      return accountId;
    }
    return generateAccountId( prefix , accId.intValue() );
  }

  public static String generateAccountId( String prefix , int accid ) {
    String accountId = null;
    if ( ( prefix == null ) || ( prefix.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to generate accountId "
          + ", found empty prefix" );
      return accountId;
    }
    if ( accid < 1 ) {
      DLog.warning( lctx , "Failed to generate accountId "
          + ", found zero accid" );
      return accountId;
    }
    accountId = AccountIdUtils.getAccountId( prefix , new Integer( accid ) );
    return accountId;
  }

}
