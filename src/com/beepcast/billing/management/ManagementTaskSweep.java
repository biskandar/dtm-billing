package com.beepcast.billing.management;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.acc.Account;
import com.beepcast.billing.common.AccountUtils;
import com.beepcast.billing.common.MapUtils;
import com.beepcast.billing.engine.BillingEngine;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ManagementTaskSweep extends ManagementTask {

  static final DLogContext lctx = new SimpleContext( "ManagementTaskSweep" );

  private int expiry;

  public ManagementTaskSweep( String threadName , boolean debug ) {
    super( threadName , debug );
  }

  public int getExpiry() {
    return expiry;
  }

  public void setExpiry( int expiry ) {
    this.expiry = expiry;
  }

  public void task( BillingEngine billingEngine ) {
    Map map = billingEngine.listActiveAccounts();
    if ( ( map == null ) || ( map.size() < 1 ) ) {
      return;
    }
    if ( super.isDebug() ) {
      DLog.debug( lctx , "Found total " + map.size()
          + " active account(s) plan to be swept" );
    }
    String accountId , accountProfileId;
    Account account;
    List listIds = MapUtils.getListKeys( map );
    Iterator iterIds = listIds.iterator();
    while ( iterIds.hasNext() ) {
      accountId = (String) iterIds.next();
      if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
        continue;
      }
      accountProfileId = (String) map.get( accountId );
      if ( ( accountProfileId == null ) || ( accountProfileId.equals( "" ) ) ) {
        continue;
      }
      account = billingEngine.queryAccount( accountProfileId , accountId );
      if ( account == null ) {
        continue;
      }
      processSweepAccount( account , billingEngine );
    }
  }

  private void processSweepAccount( Account account ,
      BillingEngine billingEngine ) {
    String headerLog = "";
    try {
      account.getRwLock().writeLock().acquire();
      // get accountId
      String accountId = AccountUtils.getAccountId( account );
      // compose headerLog
      headerLog = headerLog() + "[Account-" + accountId + "] ";
      // get lastDate
      Date lastDate = account.getLastHit();
      // get lastMillis
      long lastMillis = lastDate.getTime();
      // get currentDate
      Date currentDate = new Date();
      // get currentMillis
      long currentMillis = currentDate.getTime();
      // calculate deltaMillis
      long deltaMillis = currentMillis - lastMillis;
      // validate delta millis if expired than sweep the account
      if ( deltaMillis >= expiry ) {
        DLog.debug( lctx , headerLog + "Found idle account ( " + deltaMillis
            + " ms ) , trying to sweep account" );
        if ( billingEngine.removeAccount( account ) ) {
          DLog.debug( lctx , headerLog + "Swept account , balance : "
              + account.getCurrentUnit() );
        } else {
          DLog.warning( lctx , headerLog + "Failed to peform sweep account" );
        }
      }
    } catch ( InterruptedException e ) {
      DLog.warning( lctx , headerLog + "Failed to process "
          + "sweep account , " + e );
    } finally {
      account.getRwLock().writeLock().release();
    }
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "ManagementTaskSweep ( " + super.toString() + TAB + "expiry = "
        + this.expiry + TAB + " )";
    return retValue;
  }

}
