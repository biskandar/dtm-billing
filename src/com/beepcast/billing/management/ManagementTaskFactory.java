package com.beepcast.billing.management;

import com.beepcast.billing.engine.BillingEngine;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ManagementTaskFactory {

  static final DLogContext lctx = new SimpleContext( "ManagementTaskFactory" );

  public static ManagementTask generateManagementTaskSynch( String id ,
      boolean debug , int interval , BillingEngine billingEngine ) {
    ManagementTaskSynch managementTask = null;

    if ( ( id == null ) || ( id.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to generate management task sync "
          + ", found null id" );
      return managementTask;
    }

    if ( interval < 1 ) {
      DLog.warning( lctx , "Failed to generate management task sync "
          + ", found zero interval" );
      return managementTask;
    }

    managementTask = new ManagementTaskSynch( id , debug );
    managementTask.setId( id );
    managementTask.setInterval( interval );
    managementTask.setBillingEngine( billingEngine );
    return managementTask;
  }

  public static ManagementTask generateManagementTaskSweep( String id ,
      boolean debug , int interval , BillingEngine billingEngine , int expiry ) {
    ManagementTaskSweep managementTask = null;

    if ( ( id == null ) || ( id.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to generate management task sync "
          + ", found null id" );
      return managementTask;
    }

    if ( interval < 1 ) {
      DLog.warning( lctx , "Failed to generate management task sync "
          + ", found zero interval" );
      return managementTask;
    }

    if ( expiry < 1 ) {
      DLog.warning( lctx , "Failed to generate management task sync "
          + ", found zero expiry" );
      return managementTask;
    }

    managementTask = new ManagementTaskSweep( id , debug );
    managementTask.setId( id );
    managementTask.setInterval( interval );
    managementTask.setBillingEngine( billingEngine );
    managementTask.setExpiry( expiry );
    return managementTask;
  }
}
