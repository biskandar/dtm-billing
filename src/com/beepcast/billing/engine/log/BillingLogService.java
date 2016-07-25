package com.beepcast.billing.engine.log;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingLogService {

  static final DLogContext lctx = new SimpleContext( "BillingLogService" );

  private BillingLogDAO dao;

  public BillingLogService() {
    dao = new BillingLogDAO();
  }

  public boolean log( String accountId , String accountProfile , int clientId ,
      int paymentType , int paymentLevel , int eventId , double unit ,
      double balance , String description ) {
    return log( BillingLogBeanFactory.createBillingLogBean( accountId ,
        accountProfile , clientId , paymentType , paymentLevel , eventId ,
        unit , balance , description ) );
  }

  public boolean log( BillingLogBean billingLogBean ) {
    boolean logged = false;

    // validate params
    if ( billingLogBean == null ) {
      DLog.warning( lctx , "Failed to log , found null billing log bean" );
      return logged;
    }
    if ( StringUtils.isBlank( billingLogBean.getAccountId() ) ) {
      DLog.warning( lctx , "Failed to log , found blank account id" );
      return logged;
    }
    if ( StringUtils.isBlank( billingLogBean.getAccountProfile() ) ) {
      DLog.warning( lctx , "Failed to log , found blank account profile" );
      return logged;
    }

    // clean params
    String description = billingLogBean.getDescription();
    description = ( description == null ) ? "" : description;
    billingLogBean.setDescription( description );

    // log into table
    logged = dao.log( billingLogBean );

    return logged;
  }

  public int log( List listBillingLogBeans ) {
    return dao.log( listBillingLogBeans );
  }

}
