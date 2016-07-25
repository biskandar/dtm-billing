package com.beepcast.billing;

public class BillingStatus {

  public static final int PAYMENT_RESULT_UNKNOWN = -1;
  public static final int PAYMENT_RESULT_SUCCEED = 0;

  public static final int PAYMENT_RESULT_FAILED_NOPROFID = 10;
  public static final int PAYMENT_RESULT_FAILED_DATABASE = 11;
  public static final int PAYMENT_RESULT_FAILED_NOCLIENT = 12;
  public static final int PAYMENT_RESULT_FAILED_NOEVENT = 13;
  public static final int PAYMENT_RESULT_FAILED_NOCREDIT = 14;
  public static final int PAYMENT_RESULT_FAILED_NOACCOUNT = 15;
  public static final int PAYMENT_RESULT_FAILED_NODEBITUNIT = 16;
  public static final int PAYMENT_RESULT_FAILED_NOCREDITUNIT = 17;
  public static final int PAYMENT_RESULT_FAILED_NOTENOUGHBALANCE = 18;
  public static final int PAYMENT_RESULT_FAILED_RETRY = 19;

  public static final int PAYMENT_RESULT_ERROR_NOINIT = 20;
  public static final int PAYMENT_RESULT_ERROR_CREDIT = 21;
  public static final int PAYMENT_RESULT_ERROR_DEBIT = 22;
  public static final int PAYMENT_RESULT_ERROR_INTERNAL = 23;

  public static String paymentResultToString( int paymentResult ) {
    String strPaymentResult = "";
    switch ( paymentResult ) {
    case PAYMENT_RESULT_UNKNOWN :
      strPaymentResult = "PAYMENT_RESULT_UNKNOWN";
      break;
    case PAYMENT_RESULT_SUCCEED :
      strPaymentResult = "PAYMENT_RESULT_SUCCEED";
      break;

    case PAYMENT_RESULT_FAILED_NOPROFID :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOPROFID";
      break;
    case PAYMENT_RESULT_FAILED_DATABASE :
      strPaymentResult = "PAYMENT_RESULT_FAILED_DATABASE";
      break;
    case PAYMENT_RESULT_FAILED_NOCLIENT :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOCLIENT";
      break;
    case PAYMENT_RESULT_FAILED_NOEVENT :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOEVENT";
      break;
    case PAYMENT_RESULT_FAILED_NOCREDIT :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOCREDIT";
      break;
    case PAYMENT_RESULT_FAILED_NOACCOUNT :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOACCOUNT";
      break;
    case PAYMENT_RESULT_FAILED_NODEBITUNIT :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NODEBITUNIT";
      break;
    case PAYMENT_RESULT_FAILED_NOCREDITUNIT :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOCREDITUNIT";
      break;
    case PAYMENT_RESULT_FAILED_NOTENOUGHBALANCE :
      strPaymentResult = "PAYMENT_RESULT_FAILED_NOTENOUGHBALANCE";
      break;
    case PAYMENT_RESULT_FAILED_RETRY :
      strPaymentResult = "PAYMENT_RESULT_FAILED_RETRY";
      break;

    case PAYMENT_RESULT_ERROR_CREDIT :
      strPaymentResult = "PAYMENT_RESULT_ERROR_CREDIT";
      break;
    case PAYMENT_RESULT_ERROR_DEBIT :
      strPaymentResult = "PAYMENT_RESULT_ERROR_DEBIT";
      break;
    case PAYMENT_RESULT_ERROR_INTERNAL :
      strPaymentResult = "PAYMENT_RESULT_ERROR_INTERNAL";
      break;
    }
    return strPaymentResult;
  }

}
