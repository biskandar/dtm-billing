package com.beepcast.billing.engine.log;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.beepcast.database.DatabaseLibrary;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingLogDAO {

  static final DLogContext lctx = new SimpleContext( "BillingLogDAO" );

  private DatabaseLibrary dbLib;

  public BillingLogDAO() {
    dbLib = DatabaseLibrary.getInstance();
  }

  public int log( List listBillingLogBeans ) {
    int totalLog = 0;

    if ( listBillingLogBeans == null ) {
      return totalLog;
    }

    // compose sql
    String sqlInsert = "INSERT INTO client_trans_unit (account_id,account_prof";
    sqlInsert += ",client_id,payment_type,payment_level,event_id,unit,balance";
    sqlInsert += ",description,date_inserted) ";
    String sqlValues = "VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , NOW() ) ";
    String sql = sqlInsert + sqlValues;

    // compose param set
    int idxRec = 0 , maxRec = listBillingLogBeans.size();
    Object[][] params = new Object[maxRec][];
    BillingLogBean billingLogBean = null;
    Iterator iterBillingLogBeans = listBillingLogBeans.iterator();
    while ( iterBillingLogBeans.hasNext() ) {
      billingLogBean = (BillingLogBean) iterBillingLogBeans.next();
      billingLogBean = cleanBillingLogBean( billingLogBean );
      if ( billingLogBean == null ) {
        continue;
      }
      params[idxRec] = new Object[9];
      params[idxRec][0] = billingLogBean.getAccountId();
      params[idxRec][1] = billingLogBean.getAccountProfile();
      params[idxRec][2] = new Integer( billingLogBean.getClientId() );
      params[idxRec][3] = new Integer( billingLogBean.getPaymentType() );
      params[idxRec][4] = new Integer( billingLogBean.getPaymentLevel() );
      params[idxRec][5] = new Integer( billingLogBean.getEventId() );
      params[idxRec][6] = Double.toString( billingLogBean.getUnit() );
      params[idxRec][7] = Double.toString( billingLogBean.getBalance() );
      params[idxRec][8] = billingLogBean.getDescription();
      idxRec = idxRec + 1;
    }

    // execute sql
    int[] results = dbLib
        .executeBatchStatement( "transactiondb" , sql , params );
    if ( results == null ) {
      DLog.warning( lctx , "Failed to execute the batch insert into "
          + "gateway xipme table" );
      return totalLog;
    }

    // calculate total inserted
    int i , n = results.length;
    for ( i = 0 ; i < n ; i++ ) {
      totalLog = totalLog + results[i];
    }

    return totalLog;
  }

  public boolean log( BillingLogBean billingLogBean ) {
    boolean result = false;

    // read params

    String accountId = billingLogBean.getAccountId();
    String accountProfile = billingLogBean.getAccountProfile();
    int clientId = billingLogBean.getClientId();
    int paymentType = billingLogBean.getPaymentType();
    int paymentLevel = billingLogBean.getPaymentLevel();
    int eventId = billingLogBean.getEventId();
    double unit = billingLogBean.getUnit();
    double balance = billingLogBean.getBalance();
    String description = billingLogBean.getDescription();

    // compose sql

    String sqlInsert = "INSERT INTO client_trans_unit (account_id,account_prof"
        + ",client_id,payment_type,payment_level,event_id,unit,balance"
        + ",description,date_inserted) ";

    String sqlValues = "VALUES ('" + StringEscapeUtils.escapeSql( accountId )
        + "','" + StringEscapeUtils.escapeSql( accountProfile ) + "',"
        + clientId + "," + paymentType + "," + paymentLevel + "," + eventId
        + "," + unit + "," + balance + ",'"
        + StringEscapeUtils.escapeSql( description ) + "',NOW()) ";

    String sql = sqlInsert + sqlValues;

    // execute sql

    Integer irslt = dbLib.executeQuery( "transactiondb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    return result;
  }

  private BillingLogBean cleanBillingLogBean( BillingLogBean billingLogBean ) {

    String accountId = billingLogBean.getAccountId();
    String accountProfile = billingLogBean.getAccountProfile();
    String description = billingLogBean.getDescription();

    accountId = ( accountId == null ) ? "" : accountId.trim();
    accountProfile = ( accountProfile == null ) ? "" : accountProfile.trim();
    description = ( description == null ) ? "" : description.trim();

    billingLogBean.setAccountId( accountId );
    billingLogBean.setAccountProfile( accountProfile );
    billingLogBean.setDescription( description );

    return billingLogBean;
  }

}
