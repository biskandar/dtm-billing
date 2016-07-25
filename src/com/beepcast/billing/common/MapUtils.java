package com.beepcast.billing.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {

  public static List getListKeys( Map map ) {
    List list = null;
    if ( map != null ) {
      list = new ArrayList( map.keySet() );
    }
    return list;
  }

}
