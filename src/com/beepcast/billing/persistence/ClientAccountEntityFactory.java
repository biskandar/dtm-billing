package com.beepcast.billing.persistence;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientAccountEntityFactory {

  static final DLogContext lctx = new SimpleContext(
      "ClientAccountEntityFactory" );

  public static ClientAccountEntity generateClientAccountEntity(
      String accountId , String accountProf , double balance ) {
    return generateClientAccountEntity( 0 , accountId , accountProf , balance );
  }

  public static ClientAccountEntity generateClientAccountEntity( int id ,
      String accountId , String accountProf , double balance ) {
    ClientAccountEntity entity = null;

    if ( accountId == null ) {
      DLog.warning( lctx , "Failed to generate client account "
          + ", found null accountId" );
      return entity;
    }
    if ( accountProf == null ) {
      DLog.warning( lctx , "Failed to generate client account "
          + ", found null accountProf" );
      return entity;
    }

    entity = new ClientAccountEntity();
    entity.setId( id );
    entity.setAccountId( accountId );
    entity.setAccountProf( accountProf );
    entity.setBalance( balance );
    return entity;
  }

}
