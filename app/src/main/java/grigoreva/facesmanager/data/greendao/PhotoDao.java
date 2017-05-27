package grigoreva.facesmanager.data.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import grigoreva.facesmanager.data.greendao.Photo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PHOTO".
*/
public class PhotoDao extends AbstractDao<Photo, Long> {

    public static final String TABLENAME = "PHOTO";

    /**
     * Properties of entity Photo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PhotoUrl = new Property(1, String.class, "photoUrl", false, "PHOTO_URL");
        public final static Property NoiseX = new Property(2, Float.class, "noiseX", false, "NOISE_X");
        public final static Property NoiseY = new Property(3, Float.class, "noiseY", false, "NOISE_Y");
        public final static Property LeftEyesX = new Property(4, Float.class, "leftEyesX", false, "LEFT_EYES_X");
        public final static Property LeftEyesY = new Property(5, Float.class, "leftEyesY", false, "LEFT_EYES_Y");
        public final static Property RightEyesX = new Property(6, Float.class, "rightEyesX", false, "RIGHT_EYES_X");
        public final static Property RightEyesY = new Property(7, Float.class, "rightEyesY", false, "RIGHT_EYES_Y");
    };


    public PhotoDao(DaoConfig config) {
        super(config);
    }
    
    public PhotoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PHOTO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PHOTO_URL\" TEXT," + // 1: photoUrl
                "\"NOISE_X\" REAL," + // 2: noiseX
                "\"NOISE_Y\" REAL," + // 3: noiseY
                "\"LEFT_EYES_X\" REAL," + // 4: leftEyesX
                "\"LEFT_EYES_Y\" REAL," + // 5: leftEyesY
                "\"RIGHT_EYES_X\" REAL," + // 6: rightEyesX
                "\"RIGHT_EYES_Y\" REAL);"); // 7: rightEyesY
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PHOTO\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Photo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String photoUrl = entity.getPhotoUrl();
        if (photoUrl != null) {
            stmt.bindString(2, photoUrl);
        }
 
        Float noiseX = entity.getNoiseX();
        if (noiseX != null) {
            stmt.bindDouble(3, noiseX);
        }
 
        Float noiseY = entity.getNoiseY();
        if (noiseY != null) {
            stmt.bindDouble(4, noiseY);
        }
 
        Float leftEyesX = entity.getLeftEyesX();
        if (leftEyesX != null) {
            stmt.bindDouble(5, leftEyesX);
        }
 
        Float leftEyesY = entity.getLeftEyesY();
        if (leftEyesY != null) {
            stmt.bindDouble(6, leftEyesY);
        }
 
        Float rightEyesX = entity.getRightEyesX();
        if (rightEyesX != null) {
            stmt.bindDouble(7, rightEyesX);
        }
 
        Float rightEyesY = entity.getRightEyesY();
        if (rightEyesY != null) {
            stmt.bindDouble(8, rightEyesY);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Photo readEntity(Cursor cursor, int offset) {
        Photo entity = new Photo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // photoUrl
            cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2), // noiseX
            cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3), // noiseY
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // leftEyesX
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // leftEyesY
            cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6), // rightEyesX
            cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7) // rightEyesY
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Photo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhotoUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNoiseX(cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2));
        entity.setNoiseY(cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3));
        entity.setLeftEyesX(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setLeftEyesY(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setRightEyesX(cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6));
        entity.setRightEyesY(cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Photo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Photo entity) {
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
