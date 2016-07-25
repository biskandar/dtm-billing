package com.beepcast.billing.profile;

public class PaymentType {

  public static final int UNKNOWN = -1;
  public static final int POSTPAID = 0;
  public static final int PREPAID = 1;

  public static String paymentTypeToString( int paymentType ) {
    String strPaymentType = "";
    switch ( paymentType ) {
    case UNKNOWN :
      strPaymentType = "UNKNOWN";
      break;
    case POSTPAID :
      strPaymentType = "POSTPAID";
      break;
    case PREPAID :
      strPaymentType = "PREPAID";
      break;
    }
    return strPaymentType;
  }

}
