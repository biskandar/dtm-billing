package com.beepcast.billing.management;

import com.beepcast.billing.Module;
import com.beepcast.billing.engine.BillingEngine;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public abstract class ManagementTask extends Thread implements Module {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "ManagementTask" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean debug;
  private String id;
  private int interval;
  private BillingEngine billingEngine;
  protected boolean activeThread;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ManagementTask( String threadName , boolean debug ) {
    super( threadName );
    this.debug = debug;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Set / Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public boolean isDebug() {
    return debug;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public void setInterval( int interval ) {
    this.interval = interval;
  }

  public void setBillingEngine( BillingEngine billingEngine ) {
    this.billingEngine = billingEngine;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Base Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  protected String id() {
    return id;
  }

  public abstract void task( BillingEngine billingEngine );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void moduleStart() {
    activeThread = true;
    super.start();
  }

  public void moduleStop() {
    activeThread = false;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void run() {
    String headerLog = headerLog();

    if ( billingEngine == null ) {
      DLog.warning( lctx , headerLog + "Failed to run management task "
          + ", found null billing engine" );
    }

    DLog.debug( lctx , headerLog + "thread is running" );
    int counter = 0;
    while ( activeThread ) {
      delay1s();
      if ( counter >= interval ) {
        task( billingEngine );
        counter = 0;
      }
      counter = counter + 1000;
    }
    DLog.debug( lctx , headerLog + "thread is stopped" );

  }

  public String headerLog() {
    return "[ManagementTask-" + id + "] ";
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private void delay1s() {
    try {
      Thread.sleep( 1000 );
    } catch ( InterruptedException e ) {
      DLog.warning( lctx , "Failed to perform delay , " + e );
    }
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Helper
  //
  // ////////////////////////////////////////////////////////////////////////////

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "ManagementTask ( " + "id = " + this.id + TAB + "interval = "
        + this.interval + TAB + " )";
    return retValue;
  }

}
