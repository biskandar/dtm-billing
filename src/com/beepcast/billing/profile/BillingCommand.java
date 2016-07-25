package com.beepcast.billing.profile;

public class BillingCommand {

  public static final int RESET = -1;
  public static final int DO_CREDIT = 0;
  public static final int GET_BALANCE = 1;
  public static final int DO_DEBIT = 2;

  public static String billingCommandToString( int billingCommand ) {
    String strBillingCommand = "";
    switch ( billingCommand ) {
    case RESET :
      strBillingCommand = "RESET";
      break;
    case DO_CREDIT :
      strBillingCommand = "DO_CREDIT";
      break;
    case GET_BALANCE :
      strBillingCommand = "GET_BALANCE";
      break;
    case DO_DEBIT :
      strBillingCommand = "DO_DEBIT";
      break;
    }
    return strBillingCommand;
  }

}
