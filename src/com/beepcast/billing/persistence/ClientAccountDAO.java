package com.beepcast.billing.persistence;

import java.util.Iterator;

import org.apache.commons.lang.StringEscapeUtils;

import com.beepcast.database.DatabaseLibrary;
import com.beepcast.database.DatabaseLibrary.QueryItem;
import com.beepcast.database.DatabaseLibrary.QueryResult;
import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientAccountDAO {

  static final DLogContext lctx = new SimpleContext( "ClientAccountDAO" );

  private DatabaseLibrary dbLib;

  public ClientAccountDAO() {
    dbLib = DatabaseLibrary.getInstance();
  }

  public ClientAccountEntity queryEntity( String accountId ) {
    ClientAccountEntity entity = null;

    String sqlSelect = "SELECT id , account_id , account_prof , balance ";
    String sqlFrom = "FROM client_account ";
    String sqlWhere = "WHERE ( account_id = '"
        + StringEscapeUtils.escapeSql( accountId ) + "' ) ";
    String sqlOrder = "ORDER BY id DESC ";
    String sqlLimit = "LIMIT 1 ";

    String sql = sqlSelect + sqlFrom + sqlWhere + sqlOrder + sqlLimit;

    QueryResult qr = dbLib.simpleQuery( "transactiondb" , sql );
    if ( ( qr != null ) && ( qr.size() > 0 ) ) {
      entity = populateRecord( qr );
    }

    /*
     * if ( entity == null ) { DLog.warning( lctx , "Failed to query entity ,
     * with sql " + sql ); }
     */

    return entity;
  }

  public boolean insertNewEntity( ClientAccountEntity entity ) {
    boolean result = false;

    String sqlInsert = "INSERT INTO client_account ";
    sqlInsert += "(account_id,account_prof,balance,date_inserted,date_updated) ";
    String sqlValues = "VALUES ";
    sqlValues += "('" + StringEscapeUtils.escapeSql( entity.getAccountId() )
        + "','" + StringEscapeUtils.escapeSql( entity.getAccountProf() ) + "',"
        + entity.getBalance() + ",NOW(),NOW()) ";

    String sql = sqlInsert + sqlValues;

    Integer irslt = dbLib.executeQuery( "transactiondb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    if ( !result ) {
      DLog.warning( lctx , "Failed to insert new entity , with sql " + sql );
    }

    return result;
  }

  public boolean updateEntityBalance( int clientAccountId , double balance ) {
    boolean result = false;

    String sqlUpdate = "UPDATE client_account ";
    String sqlSet = "SET balance = " + balance + " , date_updated = NOW() ";
    String sqlWhere = "WHERE ( id = " + clientAccountId + " ) ";

    String sql = sqlUpdate + sqlSet + sqlWhere;

    Integer irslt = dbLib.executeQuery( "transactiondb" , sql );
    if ( ( irslt != null ) && ( irslt.intValue() > 0 ) ) {
      result = true;
    }

    if ( !result ) {
      DLog.warning( lctx , "Failed to update entity balance , with sql " + sql );
    }

    return result;
  }

  private ClientAccountEntity populateRecord( QueryResult qr ) {
    ClientAccountEntity entity = null;

    QueryItem qi = null;
    Iterator iter = qr.iterator();
    if ( iter.hasNext() ) {
      qi = (QueryItem) iter.next();
    }
    if ( qi == null ) {
      return entity;
    }

    int id = 0;
    String accountId = null;
    String accountProf = null;
    double balance = 0;

    String stemp;

    stemp = (String) qi.get( 0 ); // id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        id = Integer.parseInt( stemp );
      } catch ( NumberFormatException e ) {
      }
    }

    stemp = (String) qi.get( 1 ); // account_id
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      accountId = stemp;
    }

    stemp = (String) qi.get( 2 ); // account_prof
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      accountProf = stemp;
    }

    stemp = (String) qi.get( 3 ); // balance
    if ( ( stemp != null ) && ( !stemp.equals( "" ) ) ) {
      try {
        balance = Double.parseDouble( stemp );
      } catch ( NumberFormatException e ) {
      }
    }

    entity = ClientAccountEntityFactory.generateClientAccountEntity( id ,
        accountId , accountProf , balance );

    return entity;
  }
}
