package com.beepcast.billing.common;

import org.apache.commons.lang.StringUtils;

public class AccountIdUtils {

  public static final String DELIMITER = "-";

  public static String getAccountId( String prefix , Integer accId ) {
    StringBuffer accountId = new StringBuffer();
    accountId.append( prefix );
    accountId.append( DELIMITER );
    accountId.append( accId );
    return accountId.toString();
  }

  public static String getPrefix( String accountId ) {
    String prefix = null;
    if ( accountId != null ) {
      String[] arr = StringUtils.split( accountId , DELIMITER , 2 );
      if ( arr.length > 1 ) {
        prefix = arr[0];
      }
    }
    return prefix;
  }

  public static Integer getAccId( String accountId ) {
    Integer accId = null;
    if ( accountId != null ) {
      String[] arr = StringUtils.split( accountId , DELIMITER , 2 );
      if ( arr.length > 1 ) {
        try {
          int accid = Integer.parseInt( arr[1] );
          if ( accid > 0 ) {
            accId = new Integer( accid );
          }
        } catch ( NumberFormatException e ) {
        }
      }
    }
    return accId;
  }

}
