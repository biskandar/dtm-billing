package com.beepcast.billing.acc;

import java.util.Date;
import java.util.List;

import com.beepcast.billing.common.AccountIdUtils;
import com.beepcast.billing.common.AccountUtils;
import com.beepcast.billing.profile.AccountProfile;
import com.beepcast.billing.storage.MapAccounts;
import com.beepcast.billing.storage.Storage;
import com.beepcast.common.util.concurrent.ReadWriteLock;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class AccountEngineImpl implements AccountEngine {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "AccountEngineImpl" );

  static final int UPDATE_CMD_STORE_ACC = 1;
  static final int UPDATE_CMD_CLEAN_ACC = 2;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private ReadWriteLock rwLock;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public AccountEngineImpl() {
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Set / Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public ReadWriteLock getRwLock() {
    return rwLock;
  }

  public void setRwLock( ReadWriteLock rwLock ) {
    this.rwLock = rwLock;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inherited Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public List listActiveAccountIds( AccountProfile accountProfile ) {
    List list = null;
    if ( !validateInit() ) {
      return list;
    }

    // validate account profile param
    if ( accountProfile == null ) {
      DLog.warning( lctx , "Failed to list active account id(s) "
          + ", found null accountProfile" );
      return list;
    }

    // trying to list account id(s) with rwLock read
    try {
      rwLock.readLock().acquire();

      Storage storage = accountProfile.getStorage();
      if ( storage != null ) {
        MapAccounts mapAccounts = storage.getMapAccounts();
        if ( mapAccounts != null ) {
          list = mapAccounts.getIds();
        }
      }

    } catch ( InterruptedException e ) {
      DLog.warning( lctx , "Failed to list active account id(s) , " + e );
    } finally {
      rwLock.readLock().release();
    }

    return list;
  }

  public Account queryAccount( AccountProfile accountProfile , Integer accId ,
      boolean silent ) {
    Account account = null;
    if ( !validateInit() ) {
      return account;
    }

    // validate accId
    if ( accId == null ) {
      DLog.warning( lctx , "Failed to query account , found null accId" );
      return account;
    }

    // generate and validate accountId
    String accountId = AccountIdFactory.generateAccountId( accountProfile ,
        accId );
    if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to query account "
          + ", found null accountId" );
      return account;
    }

    return queryAccount( accountProfile , accountId , silent );
  }

  public Account queryAccount( AccountProfile accountProfile ,
      String accountId , boolean silent ) {
    Account account = null;
    if ( !validateInit() ) {
      return account;
    }

    if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to query account "
          + ", found null accountId" );
      return account;
    }

    String headerLog = generateHeaderLog( accountId );

    // validate account profile param
    if ( accountProfile == null ) {
      DLog.warning( lctx , "Failed to query account "
          + ", found null accountProfile" );
      return account;
    }

    // trying to query account with rwLock read
    try {
      rwLock.readLock().acquire();

      Storage storage = accountProfile.getStorage();
      if ( storage != null ) {
        MapAccounts mapAccounts = storage.getMapAccounts();
        if ( mapAccounts != null ) {
          account = mapAccounts.getAccount( accountId );
          if ( !silent ) {
            if ( account != null ) {
              DLog.debug( lctx , headerLog
                  + "Successfully query account from storage "
                  + storage.getStorageId() );
            } else {
              DLog.warning( lctx , headerLog
                  + "Failed to query account from storage "
                  + storage.getStorageId() );
            }
          }
        } else {
          DLog.warning( lctx , headerLog + "Failed to query account "
              + ", found empty map accounts" );
        }
      } else {
        DLog.warning( lctx , headerLog + "Failed to query account "
            + ", found empty storage" );
      }

    } catch ( InterruptedException e ) {
      DLog.warning( lctx , headerLog + "Failed to query account , " + e );
    } finally {
      rwLock.readLock().release();
    }

    return account;
  }

  public boolean storeAccount( Account account , boolean silent ) {
    return updateAccount( UPDATE_CMD_STORE_ACC , account , silent );
  }

  public boolean cleanAccount( Account account , boolean silent ) {
    return updateAccount( UPDATE_CMD_CLEAN_ACC , account , silent );
  }

  public Double readBalance( Account account ) {
    Double balance = null;

    if ( account == null ) {
      return balance;
    }

    // generate headerLog
    String headerLog = generateHeaderLog( account.getAccountId() );

    // get account rwLock
    ReadWriteLock accRwLock = account.getRwLock();
    if ( accRwLock == null ) {
      DLog.warning( lctx , headerLog + "Failed to read balance "
          + ", found null account rwLock" );
      return balance;
    }

    // trying to read balance with account rwLock
    try {
      accRwLock.readLock().acquire();
      Double currentUnit = account.getCurrentUnit();
      if ( currentUnit != null ) {
        balance = new Double( currentUnit.doubleValue() );
      }
    } catch ( InterruptedException e ) {
      DLog.warning( lctx , headerLog + "Failed to read balance , " + e );
    } finally {
      accRwLock.readLock().release();
    }

    return balance;
  }

  public boolean writeBalance( Account account , Double balance ) {
    boolean result = false;

    if ( account == null ) {
      return result;
    }

    // generate headerLog
    String headerLog = generateHeaderLog( account.getAccountId() );

    if ( balance == null ) {
      DLog.warning( lctx , headerLog + "Failed to write balance "
          + ", found null balance" );
      return result;
    }

    // get account rwLock
    ReadWriteLock accRwLock = account.getRwLock();
    if ( accRwLock == null ) {
      DLog.warning( lctx , headerLog + "Failed to write balance "
          + ", found null account rwLock" );
      return result;
    }

    // trying to write balance with account rwLock
    try {
      accRwLock.writeLock().acquire();
      Double currentUnit = new Double( balance.doubleValue() );
      account.setCurrentUnit( currentUnit );
      account.setLastHit( new Date() );
      result = true;
    } catch ( InterruptedException e ) {
      DLog.warning( lctx , headerLog + "Failed to write balance , " + e );
    } finally {
      accRwLock.writeLock().release();
    }

    return result;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Core Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean updateAccount( int updateCommand , Account account ,
      boolean silent ) {
    boolean result = false;

    if ( !validateInit() ) {
      return result;
    }

    // compose errorLog
    String errorLog = "Failed to update account ";
    switch ( updateCommand ) {
    case UPDATE_CMD_STORE_ACC :
      errorLog = "Failed to store account ";
      break;
    case UPDATE_CMD_CLEAN_ACC :
      errorLog = "Failed to clean account ";
      break;
    }

    // validate account param
    if ( account == null ) {
      DLog.warning( lctx , errorLog + ", found null account" );
      return result;
    }

    // query , validate , and compose headerLog from accountId
    String accountId = AccountUtils.getAccountId( account );
    if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
      DLog.warning( lctx , errorLog + ", found null accountId" );
      return result;
    }
    String headerLog = generateHeaderLog( accountId );

    // get account profile
    AccountProfile accountProfile = account.getAccountProfile();
    if ( accountProfile == null ) {
      DLog.warning( lctx , headerLog + errorLog
          + ", found null account profile" );
      return result;
    }

    // get accId param
    Integer accId = AccountIdUtils.getAccId( accountId );
    if ( accId == null ) {
      DLog.warning( lctx , headerLog + errorLog + ", found null accId" );
      return result;
    }

    // validate is prev account already exist / empty ?
    Account a = queryAccount( accountProfile , accId , true );
    if ( ( updateCommand == UPDATE_CMD_STORE_ACC ) && ( a != null ) ) {
      DLog.warning( lctx , headerLog + errorLog + ", is already exist" );
      return result;
    }
    if ( ( updateCommand == UPDATE_CMD_CLEAN_ACC ) && ( a == null ) ) {
      DLog.warning( lctx , headerLog + errorLog + ", is already empty" );
      return result;
    }

    // trying to store account with rwLock write
    try {
      rwLock.writeLock().acquire();

      Storage storage = accountProfile.getStorage();
      if ( storage != null ) {
        MapAccounts mapAccounts = storage.getMapAccounts();
        if ( mapAccounts != null ) {
          switch ( updateCommand ) {
          case UPDATE_CMD_STORE_ACC :
            if ( mapAccounts.addAccount( account ) ) {
              result = true;
              if ( !silent ) {
                DLog.debug( lctx , headerLog
                    + "Successfully store account into storage "
                    + storage.getStorageId() );
              }
            } else {
              DLog.warning( lctx , headerLog + errorLog + " into storage "
                  + storage.getStorageId() );
            }
            break;
          case UPDATE_CMD_CLEAN_ACC :
            if ( mapAccounts.delAccount( account ) ) {
              result = true;
              if ( !silent ) {
                DLog.debug( lctx , headerLog
                    + "Successfully clean account from storage "
                    + storage.getStorageId() );
              }
            } else {
              DLog.warning( lctx , headerLog + errorLog + " from storage "
                  + storage.getStorageId() );
            }
            break;
          }
        } else {
          DLog.warning( lctx , headerLog + errorLog
              + ", found empty map accounts" );
        }
      } else {
        DLog.warning( lctx , headerLog + errorLog + ", found empty storage" );
      }

    } catch ( InterruptedException e ) {
      DLog.warning( lctx , headerLog + errorLog + ", " + e );
    } finally {
      rwLock.writeLock().release();
    }

    return result;
  }

  private boolean validateInit() {
    if ( rwLock == null ) {
      return false;
    }
    return true;
  }

  private String generateHeaderLog( String accountId ) {
    return "[Account-" + accountId + "] ";
  }

}
