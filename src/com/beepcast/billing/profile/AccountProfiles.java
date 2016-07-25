package com.beepcast.billing.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.common.MapUtils;

public class AccountProfiles {

  private Map map;

  public AccountProfiles( boolean threadsafe ) {
    map = new HashMap();
    if ( threadsafe ) {
      map = Collections.synchronizedMap( map );
    }
  }

  public int size() {
    return map.size();
  }

  public List getAccountProfilesIds() {
    return MapUtils.getListKeys( map );
  }

  public AccountProfile getAccountProfile( String profileId ) {
    AccountProfile profile = null;
    if ( ( profileId != null ) && ( !profileId.equals( "" ) ) ) {
      profile = (AccountProfile) map.get( profileId );
    }
    return profile;
  }

  public boolean addAccountProfile( AccountProfile profile ) {
    boolean result = false;
    if ( profile != null ) {
      AccountProfile st = getAccountProfile( profile.getId() );
      if ( st == null ) {
        map.put( profile.getId() , profile );
        result = true;
      }
    }
    return result;
  }

}
