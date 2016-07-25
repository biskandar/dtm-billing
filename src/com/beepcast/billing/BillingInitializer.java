package com.beepcast.billing;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.beepcast.util.properties.GlobalEnvironment;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingInitializer implements ServletContextListener {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static final String PROPERTY_FILE_BILLING = "billing.config.file";

  static final DLogContext lctx = new SimpleContext( "BillingInitializer" );

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void contextInitialized( ServletContextEvent sce ) {

    ServletContext context = sce.getServletContext();
    String logStr = "";

    GlobalEnvironment globalEnv = GlobalEnvironment.getInstance();

    BillingConf billingConf = BillingConfFactory
        .generateBillingConf( PROPERTY_FILE_BILLING );
    logStr = this.getClass() + " : initialized " + billingConf;
    context.log( logStr );
    System.out.println( logStr );
    DLog.debug( lctx , logStr );

    BillingApp billingApp = BillingApp.getInstance();
    billingApp.init( billingConf );
    billingApp.moduleStart();
    logStr = this.getClass() + " : initialized " + billingApp;
    context.log( logStr );
    System.out.println( logStr );
    DLog.debug( lctx , logStr );

  }

  public void contextDestroyed( ServletContextEvent sce ) {

    ServletContext context = sce.getServletContext();
    String logStr = "";

    GlobalEnvironment globalEnv = GlobalEnvironment.getInstance();

    BillingApp billingApp = BillingApp.getInstance();
    billingApp.moduleStop();
    logStr = this.getClass() + " : destroyed ";
    context.log( logStr );
    System.out.println( logStr );
    DLog.debug( lctx , logStr );

  }

}
