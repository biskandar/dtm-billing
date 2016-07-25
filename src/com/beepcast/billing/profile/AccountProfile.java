package com.beepcast.billing.profile;

import com.beepcast.billing.storage.Storage;

public class AccountProfile {

  private String id;
  private int level;
  private int paymentType;
  private String mapKeyPrefix;
  private Storage storage;
  private double lowestUnit;
  private int roundDecimalPlaces;

  public AccountProfile() {
    lowestUnit = 0;
    roundDecimalPlaces = 5;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel( int level ) {
    this.level = level;
  }

  public int getPaymentType() {
    return paymentType;
  }

  public void setPaymentType( int paymentType ) {
    this.paymentType = paymentType;
  }

  public String getMapKeyPrefix() {
    return mapKeyPrefix;
  }

  public void setMapKeyPrefix( String mapKeyPrefix ) {
    this.mapKeyPrefix = mapKeyPrefix;
  }

  public Storage getStorage() {
    return storage;
  }

  public void setStorage( Storage storage ) {
    this.storage = storage;
  }

  public double getLowestUnit() {
    return lowestUnit;
  }

  public void setLowestUnit( double lowestUnit ) {
    this.lowestUnit = lowestUnit;
  }

  public int getRoundDecimalPlaces() {
    return roundDecimalPlaces;
  }

  public void setRoundDecimalPlaces( int roundDecimalPlaces ) {
    this.roundDecimalPlaces = roundDecimalPlaces;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "AccountProfile ( " + "id = " + this.id + TAB + "level = "
        + Level.levelToString( this.level ) + TAB + "paymentType = "
        + PaymentType.paymentTypeToString( this.paymentType ) + TAB
        + "mapKeyPrefix = " + this.mapKeyPrefix + TAB + "storageId = "
        + this.storage.getStorageId() + TAB + " )";
    return retValue;
  }

}
