package com.beepcast.billing.profile;

public class Level {

  public static final int UNKNOWN = -1;
  public static final int CLIENT = 0;
  public static final int EVENT = 1;

  public static String levelToString( int level ) {
    String strLevel = "";
    switch ( level ) {
    case UNKNOWN :
      strLevel = "UNKNOWN";
      break;
    case CLIENT :
      strLevel = "CLIENT";
      break;
    case EVENT :
      strLevel = "EVENT";
      break;
    }
    return strLevel;
  }

}
