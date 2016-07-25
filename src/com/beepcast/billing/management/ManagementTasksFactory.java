package com.beepcast.billing.management;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.BillingConf;
import com.beepcast.billing.engine.BillingEngine;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ManagementTasksFactory {

  static final DLogContext lctx = new SimpleContext( "ManagementTasksFactory" );

  static final int DEFAULT_INTERVAL = 60000; // one minutes
  static final int DEFAULT_EXPIRY = 300000; // five minutes

  public static ManagementTasks generateManagementTasks( boolean debug ,
      List ids , Map map , BillingEngine billingEngine ) {
    ManagementTasks managementTasks = null;

    if ( ids == null ) {
      DLog.warning( lctx , "Failed to generate management tasks "
          + ", found null ids" );
      return managementTasks;
    }

    if ( map == null ) {
      DLog.warning( lctx , "Failed to generate management tasks "
          + ", found null map" );
      return managementTasks;
    }

    if ( billingEngine == null ) {
      DLog.warning( lctx , "Failed to generate management tasks "
          + ", found null billing engine" );
      return managementTasks;
    }

    managementTasks = new ManagementTasks();

    String id;
    ManagementTask managementTask;

    String stemp;

    String headerLog;
    Iterator iterIds = ids.iterator();
    while ( iterIds.hasNext() ) {
      id = (String) iterIds.next();

      // validate id
      if ( ( id == null ) || ( id.equals( "" ) ) ) {
        continue;
      }

      headerLog = "[ManagementTask-" + id + "] ";

      managementTask = null;

      // when synch task
      if ( id.equals( BillingConf.MANAGEMENT_TASK_SYNCH ) ) {

        // get interval property
        int interval = 0;
        stemp = (String) map.get( id + BillingConf.FIELD_SEPARATOR
            + BillingConf.MANAGEMENT_TASK_SYNCH_INTERVAL );
        try {
          interval = Integer.parseInt( stemp );
        } catch ( NumberFormatException e ) {
        }
        if ( interval < 1 ) {
          DLog.warning( lctx , headerLog + "Failed to retrieve interval "
              + ", using default" );
          interval = DEFAULT_INTERVAL;
        }

        managementTask = ManagementTaskFactory.generateManagementTaskSynch( id ,
            debug , interval , billingEngine );
      }

      // when sweep task
      if ( id.equals( BillingConf.MANAGEMENT_TASK_SWEEP ) ) {

        // get interval property
        int interval = 0;
        stemp = (String) map.get( id + BillingConf.FIELD_SEPARATOR
            + BillingConf.MANAGEMENT_TASK_SWEEP_INTERVAL );
        try {
          interval = Integer.parseInt( stemp );
        } catch ( NumberFormatException e ) {
        }
        if ( interval < 1 ) {
          DLog.warning( lctx , headerLog + "Failed to retrieve interval "
              + ", using default" );
          interval = DEFAULT_INTERVAL;
        }

        // get expiry property
        int expiry = 0;
        stemp = (String) map.get( id + BillingConf.FIELD_SEPARATOR
            + BillingConf.MANAGEMENT_TASK_SWEEP_EXPIRY );
        try {
          expiry = Integer.parseInt( stemp );
        } catch ( NumberFormatException e ) {
        }
        if ( expiry < 1 ) {
          DLog.warning( lctx , headerLog + "Failed to retrieve expiry "
              + ", using default" );
          expiry = DEFAULT_EXPIRY;
        }

        managementTask = ManagementTaskFactory.generateManagementTaskSweep( id ,
            debug , interval , billingEngine , expiry );
      }

      // validate , store , and log it
      if ( managementTask != null ) {
        if ( managementTasks.addManagementTask( managementTask ) ) {
          DLog.debug( lctx , headerLog + "Successfully generated "
              + managementTask );
        }
      }

    } // iterate all managementTask

    return managementTasks;
  }

}
