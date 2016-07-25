package com.beepcast.billing.acc;

import java.util.Date;

import com.beepcast.billing.profile.AccountProfile;
import com.beepcast.common.util.concurrent.ReadWriteLock;

public class Account {

  private String accountId;
  private AccountProfile accountProfile;
  private Double currentUnit;
  private Date lastHit;
  private ReadWriteLock rwLock;

  public Account() {
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId( String accountId ) {
    this.accountId = accountId;
  }

  public AccountProfile getAccountProfile() {
    return accountProfile;
  }

  public void setAccountProfile( AccountProfile accountProfile ) {
    this.accountProfile = accountProfile;
  }

  public Double getCurrentUnit() {
    return currentUnit;
  }

  public void setCurrentUnit( Double currentUnit ) {
    this.currentUnit = currentUnit;
  }

  public Date getLastHit() {
    return lastHit;
  }

  public void setLastHit( Date lastHit ) {
    this.lastHit = lastHit;
  }

  public ReadWriteLock getRwLock() {
    return rwLock;
  }

  public void setRwLock( ReadWriteLock rwLock ) {
    this.rwLock = rwLock;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "AccountHolder ( " + "accountId = " + this.accountId + TAB
        + "accountProfile = " + this.accountProfile + TAB + " )";
    return retValue;
  }

}
