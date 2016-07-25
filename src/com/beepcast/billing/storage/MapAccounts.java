package com.beepcast.billing.storage;

import java.util.List;
import java.util.Map;

import com.beepcast.billing.acc.Account;
import com.beepcast.billing.common.MapUtils;

public class MapAccounts {

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ///////////////////////////////////////////////////////////////////////////

  private Map map;

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ///////////////////////////////////////////////////////////////////////////

  public MapAccounts() {
    // nothing to do
  }

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Set Function
  //
  // ///////////////////////////////////////////////////////////////////////////

  public void setMap( Map map ) {
    this.map = map;
  }

  // ///////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ///////////////////////////////////////////////////////////////////////////

  public void reset() {
    // reset to the map
    map.clear();
  }

  public List getIds() {
    List list = null;
    if ( map != null ) {
      // get all accountId from the map
      list = MapUtils.getListKeys( map );
    }
    return list;
  }

  public Account getAccount( String accountId ) {
    Account account = null;
    if ( accountId != null ) {
      // get account object from the map
      account = (Account) map.get( accountId );
    }
    return account;
  }

  public boolean addAccount( Account account ) {
    boolean added = false;
    if ( account != null ) {
      // find if the object exist already ?
      Account a = getAccount( account.getAccountId() );
      if ( a == null ) {
        // put account object in to the map
        map.put( account.getAccountId() , account );
        added = true;
      }
    }
    return added;
  }

  public boolean delAccount( Account account ) {
    boolean deleted = false;
    if ( account != null ) {
      // find if the object exist already ?
      Account a = getAccount( account.getAccountId() );
      if ( a != null ) {
        // delete account object from the map
        map.remove( account.getAccountId() );
        deleted = true;
      }
    }
    return deleted;
  }

}
