package com.beepcast.billing.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beepcast.billing.common.MapUtils;

public class ManagementTasks {

  private Map map;

  public ManagementTasks() {
    map = new HashMap();
  }

  public int size() {
    return map.size();
  }

  public List getManagementTaskIds() {
    return MapUtils.getListKeys( map );
  }

  public ManagementTask getManagementTask( String managementTaskId ) {
    ManagementTask managementTask = null;
    if ( ( managementTaskId != null ) && ( !managementTaskId.equals( "" ) ) ) {
      managementTask = (ManagementTask) map.get( managementTaskId );
    }
    return managementTask;
  }

  public boolean addManagementTask( ManagementTask managementTask ) {
    boolean result = false;
    if ( managementTask != null ) {
      ManagementTask mt = getManagementTask( managementTask.id() );
      if ( mt == null ) {
        map.put( managementTask.id() , managementTask );
        result = true;
      }
    }
    return result;
  }

}
