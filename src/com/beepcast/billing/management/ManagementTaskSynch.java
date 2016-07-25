package com.beepcast.billing.management;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.acc.Account;
import com.beepcast.billing.common.AccountUtils;
import com.beepcast.billing.common.MapUtils;
import com.beepcast.billing.engine.BillingEngine;
import com.beepcast.billing.persistence.ClientAccountEntity;
import com.beepcast.billing.persistence.ClientAccountEntityFactory;
import com.beepcast.billing.persistence.ClientAccountService;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ManagementTaskSynch extends ManagementTask {

  static final DLogContext lctx = new SimpleContext( "ManagementTaskSynch" );

  private ClientAccountService caService;

  public ManagementTaskSynch( String threadName , boolean debug ) {
    super( threadName , debug );
    caService = new ClientAccountService();
  }

  public void task( BillingEngine billingEngine ) {
    Map map = billingEngine.listActiveAccounts();
    if ( ( map == null ) || ( map.size() < 1 ) ) {
      return;
    }
    if ( super.isDebug() ) {
      DLog.debug( lctx , "Found total " + map.size()
          + " active account(s) plan to be synced" );
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
      processSynchAccount( account );
    }
  }

  private void processSynchAccount( Account account ) {
    String headerLog = "";
    try {
      account.getRwLock().readLock().acquire();
      // get accountId
      String accountId = AccountUtils.getAccountId( account );
      // compose headerLog
      headerLog = headerLog() + "[Account-" + accountId + "] ";
      // get accountProfileId
      String accountProfileId = AccountUtils.getProfileId( account );
      // get balance
      Double balance = account.getCurrentUnit();
      // validate is account exist in table
      ClientAccountEntity entity = caService.queryEntity( accountId );
      if ( entity == null ) {
        // do the synch entity
        DLog.debug( lctx , headerLog + "Found empty entity "
            + ", trying to synch account" );
        entity = ClientAccountEntityFactory.generateClientAccountEntity(
            accountId , accountProfileId , balance.doubleValue() );
        if ( entity != null ) {
          if ( caService.insertNewEntity( entity ) ) {
            DLog.debug( lctx , headerLog
                + "Successfully perform synch account , balance : "
                + entity.getBalance() );
          } else {
            DLog.warning( lctx , headerLog
                + "Failed to insert entity into table "
                + ", synch account failed" );
          }
        } else {
          DLog.warning( lctx , headerLog + "Failed to create entity "
              + ", synch account failed" );
        }
      } else {
        if ( balance.doubleValue() != entity.getBalance() ) {
          // do the synch entity balance
          if ( caService.updateEntityBalance( entity.getId() , balance
              .doubleValue() ) ) {
            DLog.debug( lctx , headerLog + "Synch account balance : "
                + entity.getBalance() + " -> " + balance.doubleValue() );
          } else {
            DLog.warning( lctx , headerLog
                + "Failed to update new account balance : "
                + balance.doubleValue() );
          }
        }
      }
    } catch ( InterruptedException e ) {
      DLog.warning( lctx , headerLog + "Failed to process "
          + "synch account , " + e );
    } finally {
      account.getRwLock().readLock().release();
    }
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "ManagementTaskSynch ( " + super.toString() + TAB + " )";
    return retValue;
  }

}
