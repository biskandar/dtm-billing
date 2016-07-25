package com.beepcast.billing.common;

import java.util.Date;

import com.beepcast.billing.acc.Account;
import com.beepcast.billing.profile.AccountProfile;
import com.beepcast.billing.storage.Storage;

public class AccountUtils {

  public static String getAccountId( Account account ) {
    String accountId = null;
    if ( account != null ) {
      accountId = account.getAccountId();
    }
    return accountId;
  }

  public static String getProfileId( Account account ) {
    String name = null;
    if ( account != null ) {
      AccountProfile accountProfile = account.getAccountProfile();
      if ( accountProfile != null ) {
        name = accountProfile.getId();
      }
    }
    return name;
  }

  public static int getLevel( Account account ) {
    int level = -1;
    if ( account != null ) {
      AccountProfile accountProfile = account.getAccountProfile();
      if ( accountProfile != null ) {
        level = accountProfile.getLevel();
      }
    }
    return level;
  }

  public static int getPaymentType( Account account ) {
    int paymentType = -1;
    if ( account != null ) {
      AccountProfile accountProfile = account.getAccountProfile();
      if ( accountProfile != null ) {
        paymentType = accountProfile.getPaymentType();
      }
    }
    return paymentType;
  }

  public static String getStorageId( Account account ) {
    String storageId = null;
    if ( account != null ) {
      AccountProfile accountProfile = account.getAccountProfile();
      if ( accountProfile != null ) {
        Storage storage = accountProfile.getStorage();
        if ( storage != null ) {
          storageId = storage.getStorageId();
        }
      }
    }
    return storageId;
  }

  public static boolean hitToAccount( Account account ) {
    boolean hit = false;
    if ( account != null ) {
      Date now = new Date();
      account.setLastHit( now );
      hit = true;
    }
    return hit;
  }

}
