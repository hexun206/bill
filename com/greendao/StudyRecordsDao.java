package com.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.greendao.StudyRecords;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table STUDY_RECORDS.
*/
public class StudyRecordsDao extends AbstractDao<StudyRecords, Long> {

    public static final String TABLENAME = "STUDY_RECORDS";

    /**
     * Properties of entity StudyRecords.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Cid = new Property(1, String.class, "cid", false, "CID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Eids = new Property(3, String.class, "eids", false, "EIDS");
        public final static Property Date = new Property(4, java.util.Date.class, "date", false, "DATE");
        public final static Property Currentprogress = new Property(5, String.class, "currentprogress", false, "CURRENTPROGRESS");
        public final static Property Completed = new Property(6, String.class, "completed", false, "COMPLETED");
        public final static Property Choicesforuser = new Property(7, String.class, "choicesforuser", false, "CHOICESFORUSER");
        public final static Property Userid = new Property(8, String.class, "userid", false, "USERID");
        public final static Property Type = new Property(9, String.class, "type", false, "TYPE");
        public final static Property Today = new Property(10, String.class, "today", false, "TODAY");
        public final static Property Exercisenum = new Property(11, Integer.class, "exercisenum", false, "EXERCISENUM");
        public final static Property Isdelete = new Property(12, String.class, "isdelete", false, "ISDELETE");
        public final static Property Usedtime = new Property(13, Integer.class, "usedtime", false, "USEDTIME");
        public final static Property Rightnum = new Property(14, Integer.class, "rightnum", false, "RIGHTNUM");
        public final static Property Lastprogress = new Property(15, Integer.class, "lastprogress", false, "LASTPROGRESS");
        public final static Property Ptimelimit = new Property(16, String.class, "ptimelimit", false, "PTIMELIMIT");
        public final static Property Choosecategory = new Property(17, String.class, "choosecategory", false, "CHOOSECATEGORY");
    };


    public StudyRecordsDao(DaoConfig config) {
        super(config);
    }
    
    public StudyRecordsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'STUDY_RECORDS' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'CID' TEXT," + // 1: cid
                "'NAME' TEXT," + // 2: name
                "'EIDS' TEXT," + // 3: eids
                "'DATE' INTEGER," + // 4: date
                "'CURRENTPROGRESS' TEXT," + // 5: currentprogress
                "'COMPLETED' TEXT," + // 6: completed
                "'CHOICESFORUSER' TEXT," + // 7: choicesforuser
                "'USERID' TEXT," + // 8: userid
                "'TYPE' TEXT NOT NULL ," + // 9: type
                "'TODAY' TEXT," + // 10: today
                "'EXERCISENUM' INTEGER," + // 11: exercisenum
                "'ISDELETE' TEXT," + // 12: isdelete
                "'USEDTIME' INTEGER," + // 13: usedtime
                "'RIGHTNUM' INTEGER," + // 14: rightnum
                "'LASTPROGRESS' INTEGER," + // 15: lastprogress
                "'PTIMELIMIT' TEXT," + // 16: ptimelimit
                "'CHOOSECATEGORY' TEXT);"); // 17: choosecategory
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_STUDY_RECORDS_CHOOSECATEGORY ON STUDY_RECORDS" +
                " (CHOOSECATEGORY);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'STUDY_RECORDS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, StudyRecords entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cid = entity.getCid();
        if (cid != null) {
            stmt.bindString(2, cid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String eids = entity.getEids();
        if (eids != null) {
            stmt.bindString(4, eids);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(5, date.getTime());
        }
 
        String currentprogress = entity.getCurrentprogress();
        if (currentprogress != null) {
            stmt.bindString(6, currentprogress);
        }
 
        String completed = entity.getCompleted();
        if (completed != null) {
            stmt.bindString(7, completed);
        }
 
        String choicesforuser = entity.getChoicesforuser();
        if (choicesforuser != null) {
            stmt.bindString(8, choicesforuser);
        }
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(9, userid);
        }
        stmt.bindString(10, entity.getType());
 
        String today = entity.getToday();
        if (today != null) {
            stmt.bindString(11, today);
        }
 
        Integer exercisenum = entity.getExercisenum();
        if (exercisenum != null) {
            stmt.bindLong(12, exercisenum);
        }
 
        String isdelete = entity.getIsdelete();
        if (isdelete != null) {
            stmt.bindString(13, isdelete);
        }
 
        Integer usedtime = entity.getUsedtime();
        if (usedtime != null) {
            stmt.bindLong(14, usedtime);
        }
 
        Integer rightnum = entity.getRightnum();
        if (rightnum != null) {
            stmt.bindLong(15, rightnum);
        }
 
        Integer lastprogress = entity.getLastprogress();
        if (lastprogress != null) {
            stmt.bindLong(16, lastprogress);
        }
 
        String ptimelimit = entity.getPtimelimit();
        if (ptimelimit != null) {
            stmt.bindString(17, ptimelimit);
        }
 
        String choosecategory = entity.getChoosecategory();
        if (choosecategory != null) {
            stmt.bindString(18, choosecategory);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public StudyRecords readEntity(Cursor cursor, int offset) {
        StudyRecords entity = new StudyRecords( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // eids
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // date
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // currentprogress
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // completed
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // choicesforuser
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // userid
            cursor.getString(offset + 9), // type
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // today
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // exercisenum
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // isdelete
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // usedtime
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // rightnum
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // lastprogress
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // ptimelimit
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17) // choosecategory
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, StudyRecords entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setEids(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setCurrentprogress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCompleted(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setChoicesforuser(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUserid(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setType(cursor.getString(offset + 9));
        entity.setToday(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setExercisenum(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setIsdelete(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUsedtime(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setRightnum(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setLastprogress(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setPtimelimit(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setChoosecategory(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(StudyRecords entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(StudyRecords entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}