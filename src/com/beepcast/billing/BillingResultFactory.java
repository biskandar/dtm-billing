package com.beepcast.billing;

import com.beepcast.billing.acc.Account;
import com.beepcast.billing.common.AccountUtils;

public class BillingResultFactory {

  public static BillingResult generateBillingResult( int billingCommand ,
      Account account , Double balanceBefore , Double balanceAfter ) {
    return generateBillingResult( billingCommand ,
        BillingStatus.PAYMENT_RESULT_SUCCEED , account , balanceBefore ,
        balanceAfter );
  }

  public static BillingResult generateBillingResult( int billingCommand ,
      int paymentResult ) {
    return generateBillingResult( billingCommand , paymentResult , null , null ,
        null );
  }

  public static BillingResult generateBillingResult( int billingCommand ,
      int paymentResult , Account account ) {
    return generateBillingResult( billingCommand , paymentResult , account ,
        null , null );
  }

  public static BillingResult generateBillingResult( int billingCommand ,
      int paymentResult , Account account , Double balanceBefore ,
      Double balanceAfter ) {
    BillingResult br = new BillingResult();

    br.setBillingCommand( billingCommand );

    br.setPaymentResult( paymentResult );

    if ( account != null ) {
      br.setAccountId( AccountUtils.getAccountId( account ) );
      br.setAccountProfileId( AccountUtils.getProfileId( account ) );
      br.setPaymentType( AccountUtils.getPaymentType( account ) );
      br.setPaymentLevel( AccountUtils.getLevel( account ) );
    }

    if ( balanceBefore != null ) {
      br.setBalanceBefore( new Double( balanceBefore.doubleValue() ) );
    }

    if ( balanceAfter != null ) {
      br.setBalanceAfter( new Double( balanceAfter.doubleValue() ) );
    }

    return br;
  }

}
