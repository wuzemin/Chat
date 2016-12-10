package com.min.smalltalk.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.min.smalltalk.bean.FriendInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FRIEND_INFO".
*/
public class FriendInfoDao extends AbstractDao<FriendInfo, Void> {

    public static final String TABLENAME = "FRIEND_INFO";

    /**
     * Properties of entity FriendInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UserId = new Property(0, String.class, "userId", false, "USER_ID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property PortraitUri = new Property(2, String.class, "portraitUri", false, "PORTRAIT_URI");
        public final static Property DisplayName = new Property(3, String.class, "displayName", false, "DISPLAY_NAME");
        public final static Property Status = new Property(4, String.class, "status", false, "STATUS");
        public final static Property Timestamp = new Property(5, Long.class, "timestamp", false, "TIMESTAMP");
        public final static Property Letters = new Property(6, String.class, "letters", false, "LETTERS");
        public final static Property Phone = new Property(7, String.class, "phone", false, "PHONE");
        public final static Property Email = new Property(8, String.class, "email", false, "EMAIL");
    }


    public FriendInfoDao(DaoConfig config) {
        super(config);
    }
    
    public FriendInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FRIEND_INFO\" (" + //
                "\"USER_ID\" TEXT," + // 0: userId
                "\"NAME\" TEXT," + // 1: name
                "\"PORTRAIT_URI\" TEXT," + // 2: portraitUri
                "\"DISPLAY_NAME\" TEXT," + // 3: displayName
                "\"STATUS\" TEXT," + // 4: status
                "\"TIMESTAMP\" INTEGER," + // 5: timestamp
                "\"LETTERS\" TEXT," + // 6: letters
                "\"PHONE\" TEXT," + // 7: phone
                "\"EMAIL\" TEXT);"); // 8: email
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FRIEND_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FriendInfo entity) {
        stmt.clearBindings();
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(1, userId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String portraitUri = entity.getPortraitUri();
        if (portraitUri != null) {
            stmt.bindString(3, portraitUri);
        }
 
        String displayName = entity.getDisplayName();
        if (displayName != null) {
            stmt.bindString(4, displayName);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
 
        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(6, timestamp);
        }
 
        String letters = entity.getLetters();
        if (letters != null) {
            stmt.bindString(7, letters);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(8, phone);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(9, email);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FriendInfo entity) {
        stmt.clearBindings();
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(1, userId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String portraitUri = entity.getPortraitUri();
        if (portraitUri != null) {
            stmt.bindString(3, portraitUri);
        }
 
        String displayName = entity.getDisplayName();
        if (displayName != null) {
            stmt.bindString(4, displayName);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
 
        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(6, timestamp);
        }
 
        String letters = entity.getLetters();
        if (letters != null) {
            stmt.bindString(7, letters);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(8, phone);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(9, email);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public FriendInfo readEntity(Cursor cursor, int offset) {
        FriendInfo entity = new FriendInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // userId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // portraitUri
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // displayName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // status
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // timestamp
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // letters
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // phone
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // email
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FriendInfo entity, int offset) {
        entity.setUserId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPortraitUri(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDisplayName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTimestamp(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setLetters(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPhone(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setEmail(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(FriendInfo entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(FriendInfo entity) {
        return null;
    }

    @Override
    public boolean hasKey(FriendInfo entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
