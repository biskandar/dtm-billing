package com.beepcast.billing.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Storages {

  // Synchronization needs to be done only when there is a chance of changing
  // the data from different threads simultaneously. In your case, it is simply
  // going to be a read, the synchronization is not required. If you need to
  // remove or modify the values in the hashmap, then you [may] need to
  // synchronize that.

  private Map map;

  public Storages( boolean threadsafe ) {
    map = new HashMap();
    if ( threadsafe ) {
      map = Collections.synchronizedMap( map );
    }
  }

  public int size() {
    return map.size();
  }

  public Storage getStorage( String storageId ) {
    Storage storage = null;
    if ( ( storageId != null ) && ( !storageId.equals( "" ) ) ) {
      storage = (Storage) map.get( storageId );
    }
    return storage;
  }

  public boolean addStorage( Storage s ) {
    boolean result = false;
    if ( s != null ) {
      Storage st = getStorage( s.getStorageId() );
      if ( st == null ) {
        map.put( s.getStorageId() , s );
        result = true;
      }
    }
    return result;
  }

}
