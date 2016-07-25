package com.beepcast.billing.acc;

import org.apache.commons.lang.StringUtils;

public class AccountCommon {

  public static String headerLog( Account account ) {
    String headerLog = "";
    if ( account == null ) {
      return headerLog;
    }
    headerLog = headerLog( account.getAccountId() );
    return headerLog;
  }

  public static String headerLog( String accountId ) {
    String headerLog = "";
    if ( StringUtils.isBlank( accountId ) ) {
      return headerLog;
    }
    headerLog = "[Account-" + accountId + "] ";
    return headerLog;
  }

}
