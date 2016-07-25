package com.beepcast.billing.storage;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class StorageFactory {

  static final DLogContext lctx = new SimpleContext( "StorageFactory" );

  public static Storage generateStorageWithThreadSafeAccounts( String storageId ) {

    MapAccounts ma = MapAccountsFactory.generateMapAccounts( true );
    DLog.debug( lctx , "Generated thread safe map accounts" );

    return generateStorage( storageId , ma );
  }

  public static Storage generateStorage( String storageId ,
      MapAccounts mapAccounts ) {
    Storage storage = null;

    if ( ( storageId == null ) || storageId.equals( "" ) ) {
      return storage;
    }

    String headerLog = "[Storage-" + storageId + "] ";

    if ( mapAccounts == null ) {
      DLog.warning( lctx , headerLog + "Failed to generate storage "
          + ", found empty map accounts object" );
      return storage;
    }

    storage = new Storage();
    storage.setStorageId( storageId );
    storage.setMapAccounts( mapAccounts );

    return storage;
  }

}
