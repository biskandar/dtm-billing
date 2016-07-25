package com.beepcast.billing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.beepcast.billing.BillingConf.BillingLogWorkerConf;
import com.beepcast.util.properties.GlobalEnvironment;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;
import com.firsthop.common.util.xml.TreeUtil;

public class BillingConfFactory {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "BillingConf" );

  static final GlobalEnvironment globalEnv = GlobalEnvironment.getInstance();

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public static BillingConf generateBillingConf( String propertyFileBilling ) {
    BillingConf billingConf = new BillingConf();

    if ( ( propertyFileBilling == null ) || ( propertyFileBilling.equals( "" ) ) ) {
      return billingConf;
    }

    DLog.debug( lctx , "Loading from property = " + propertyFileBilling );

    Element element = globalEnv.getElement( BillingConf.class.getName() ,
        propertyFileBilling );
    if ( element != null ) {
      boolean result = validateTag( element );
      if ( result ) {
        extractElement( element , billingConf );
      }
    }

    return billingConf;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private static boolean validateTag( Element element ) {
    boolean result = false;

    if ( element == null ) {
      DLog.warning( lctx , "Found empty in element xml" );
      return result;
    }

    Node node = TreeUtil.first( element , "billing" );
    if ( node == null ) {
      DLog.warning( lctx , "Can not find root tag <billing>" );
      return result;
    }

    result = true;
    return result;
  }

  private static boolean extractElement( Element element ,
      BillingConf billingConf ) {
    boolean result = false;

    Node nodeBilling = TreeUtil.first( element , "billing" );
    if ( nodeBilling == null ) {
      return result;
    }

    String stemp;

    stemp = TreeUtil.getAttribute( nodeBilling , "debug" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      billingConf.setDebug( stemp.equals( "true" ) );
    }

    Node nodeAccountStorages = TreeUtil.first( nodeBilling , "accountStorages" );
    if ( nodeAccountStorages != null ) {
      extractNodeAccountStorages( nodeAccountStorages , billingConf );
    }

    Node nodeAccountProfiles = TreeUtil.first( nodeBilling , "accountProfiles" );
    if ( nodeAccountProfiles != null ) {
      extractNodeAccountProfiles( nodeAccountProfiles , billingConf );
    }

    Node nodeManagementTasks = TreeUtil.first( nodeBilling , "managementTasks" );
    if ( nodeManagementTasks != null ) {
      extractNodeManagementTasks( nodeManagementTasks , billingConf );
    }

    Node nodeLogWorker = TreeUtil.first( nodeBilling , "logWorker" );
    if ( nodeLogWorker != null ) {
      extractNodeLogWorker( nodeLogWorker , billingConf
          .getBillingLogWorkerConf() );
    }

    result = true;
    return result;
  }

  private static void extractNodeAccountStorages( Node nodeAccountStorages ,
      BillingConf billingConf ) {
    Node nodeAccountStorage = TreeUtil.first( nodeAccountStorages ,
        "accountStorage" );
    while ( nodeAccountStorage != null ) {
      String id = TreeUtil.getAttribute( nodeAccountStorage ,
          BillingConf.ACCOUNT_STORAGE_ID );
      if ( ( id != null ) && ( !id.equals( "" ) ) ) {

        billingConf.getAccountStorageIds().add( id );

        String media = TreeUtil.getAttribute( nodeAccountStorage ,
            BillingConf.ACCOUNT_STORAGE_MEDIA );
        if ( ( media != null ) && ( !media.equals( "" ) ) ) {
          billingConf.getAccountStorages().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_STORAGE_MEDIA , media );
        }

      }
      nodeAccountStorage = TreeUtil
          .next( nodeAccountStorage , "accountStorage" );
    }
  }

  private static void extractNodeAccountProfiles( Node nodeAccountProfiles ,
      BillingConf billingConf ) {
    Node nodeAccountProfile = TreeUtil.first( nodeAccountProfiles ,
        "accountProfile" );
    while ( nodeAccountProfile != null ) {
      String id = TreeUtil.getAttribute( nodeAccountProfile ,
          BillingConf.ACCOUNT_PROFILE_ID );
      if ( ( id != null ) && ( !id.equals( "" ) ) ) {

        billingConf.getAccountProfileIds().add( id );

        String level = TreeUtil.getAttribute( nodeAccountProfile ,
            BillingConf.ACCOUNT_PROFILE_LEVEL );
        if ( ( level != null ) && ( !level.equals( "" ) ) ) {
          billingConf.getAccountProfiles().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_PROFILE_LEVEL , level );
        }

        String paymentType = TreeUtil.getAttribute( nodeAccountProfile ,
            BillingConf.ACCOUNT_PROFILE_PAYMENTTYPE );
        if ( ( paymentType != null ) && ( !paymentType.equals( "" ) ) ) {
          billingConf.getAccountProfiles().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_PROFILE_PAYMENTTYPE , paymentType );
        }

        String mapKeyPrefix = TreeUtil.getAttribute( nodeAccountProfile ,
            BillingConf.ACCOUNT_PROFILE_MAPKEYPREFIX );
        if ( ( mapKeyPrefix != null ) && ( !mapKeyPrefix.equals( "" ) ) ) {
          billingConf.getAccountProfiles().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_PROFILE_MAPKEYPREFIX , mapKeyPrefix );
        }

        String storageId = TreeUtil.getAttribute( nodeAccountProfile ,
            BillingConf.ACCOUNT_PROFILE_STORAGEID );
        if ( ( storageId != null ) && ( !storageId.equals( "" ) ) ) {
          billingConf.getAccountProfiles().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_PROFILE_STORAGEID , storageId );
        }

        String lowestUnit = TreeUtil.getAttribute( nodeAccountProfile ,
            BillingConf.ACCOUNT_PROFILE_LOWESTUNIT );
        if ( ( lowestUnit != null ) && ( !lowestUnit.equals( "" ) ) ) {
          billingConf.getAccountProfiles().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_PROFILE_LOWESTUNIT , lowestUnit );
        }

        String roundDecimalPlaces = TreeUtil.getAttribute( nodeAccountProfile ,
            BillingConf.ACCOUNT_PROFILE_ROUNDDECIMALPLACES );
        if ( ( roundDecimalPlaces != null )
            && ( !roundDecimalPlaces.equals( "" ) ) ) {
          billingConf.getAccountProfiles().put(
              id + BillingConf.FIELD_SEPARATOR
                  + BillingConf.ACCOUNT_PROFILE_ROUNDDECIMALPLACES ,
              roundDecimalPlaces );
        }

      } // if ( ( id != null ) && ( !id.equals( "" ) ) )
      nodeAccountProfile = TreeUtil
          .next( nodeAccountProfile , "accountProfile" );
    }
  }

  private static void extractNodeManagementTasks( Node nodeManagementTasks ,
      BillingConf billingConf ) {
    Node nodeManagementTask = TreeUtil.first( nodeManagementTasks ,
        "managementTask" );
    while ( nodeManagementTask != null ) {
      String id = TreeUtil.getAttribute( nodeManagementTask ,
          BillingConf.MANAGEMENT_TASK_ID );
      if ( ( id != null ) && ( !id.equals( "" ) ) ) {

        billingConf.getManagementTaskIds().add( id );

        Node nodeProperty = TreeUtil.first( nodeManagementTask , "property" );
        while ( nodeProperty != null ) {
          String name = TreeUtil.getAttribute( nodeProperty , "name" );
          String value = TreeUtil.getAttribute( nodeProperty , "value" );

          billingConf.getManagementTasks().put(
              id + BillingConf.FIELD_SEPARATOR + name , value );

          nodeProperty = TreeUtil.next( nodeProperty , "property" );
        }

      }
      nodeManagementTask = TreeUtil
          .next( nodeManagementTask , "managementTask" );
    }
  }

  private static void extractNodeLogWorker( Node nodeLogWorker ,
      BillingLogWorkerConf billingLogWorkerConf ) {
    if ( nodeLogWorker == null ) {
      return;
    }
    if ( billingLogWorkerConf == null ) {
      return;
    }

    String stemp = null;

    stemp = TreeUtil.getAttribute( nodeLogWorker , "workerSize" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        billingLogWorkerConf.setWorkerSize( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = TreeUtil.getAttribute( nodeLogWorker , "queueSize" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        billingLogWorkerConf.setQueueSize( Integer.parseInt( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = TreeUtil.getAttribute( nodeLogWorker , "queueTimeout" );
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        billingLogWorkerConf.setQueueTimeout( Long.parseLong( stemp ) );
      } catch ( NumberFormatException e ) {
      }
    }

  }

}
