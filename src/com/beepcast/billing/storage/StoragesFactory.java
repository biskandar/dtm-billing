package com.beepcast.billing.storage;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.BillingConf;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class StoragesFactory {

  static final DLogContext lctx = new SimpleContext( "StoragesFactory" );

  public static Storages generateStorages( List ids , Map map ) {
    Storages storages = null;

    if ( ids == null ) {
      return storages;
    }

    if ( map == null ) {
      return storages;
    }

    // create storages with no thread safe because
    // there will be no changes list inside
    storages = new Storages( false );

    String id;
    Storage storage;

    String headerLog;
    Iterator iterIds = ids.iterator();
    while ( iterIds.hasNext() ) {
      id = (String) iterIds.next();
      // validate id
      if ( ( id == null ) || ( id.equals( "" ) ) ) {
        continue;
      }

      headerLog = "[Storage-" + id + "] ";

      // get media property
      String media = (String) map.get( id + BillingConf.FIELD_SEPARATOR
          + BillingConf.ACCOUNT_STORAGE_MEDIA );
      if ( media == null ) {
        DLog.warning( lctx , headerLog + "Bypass read , found empty media" );
        continue;
      }

      // generate threadsafe storage and put in the storages
      if ( media.equalsIgnoreCase( "memory" ) ) {
        storage = StorageFactory.generateStorageWithThreadSafeAccounts( id );
        if ( storages.addStorage( storage ) ) {
          DLog.debug( lctx , headerLog
              + "Successfully created a new threadsafe storage" );
        } else {
          DLog.warning( lctx , headerLog
              + "Failed to created a new threadsafe storage" );
        }
      }

    }

    return storages;
  }
}
