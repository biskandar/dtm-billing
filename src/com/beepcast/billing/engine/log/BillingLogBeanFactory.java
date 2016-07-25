package com.beepcast.billing.engine.log;

public class BillingLogBeanFactory {

  public static BillingLogBean createBillingLogBean( String accountId ,
      String accountProfile , int clientId , int paymentType ,
      int paymentLevel , int eventId , double unit , double balance ,
      String description ) {
    BillingLogBean bean = new BillingLogBean();
    bean.setAccountId( accountId );
    bean.setAccountProfile( accountProfile );
    bean.setClientId( clientId );
    bean.setPaymentType( paymentType );
    bean.setPaymentLevel( paymentLevel );
    bean.setEventId( eventId );
    bean.setUnit( unit );
    bean.setBalance( balance );
    bean.setDescription( description );
    return bean;
  }

}
