package com.beepcast.billing.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class MapAccountsFactory {

  static final DLogContext lctx = new SimpleContext( "MapAccountsFactory" );

  public static MapAccounts generateMapAccounts( boolean threadsafe ) {
    MapAccounts mapAccounts = new MapAccounts();

    DLog.debug( lctx , "Creating map accounts , thread safe = " + threadsafe );

    // compose the map of account(s)
    Map map = new HashMap();
    if ( threadsafe ) {
      map = Collections.synchronizedMap( map );
    }
    mapAccounts.setMap( map );

    return mapAccounts;
  }

}
