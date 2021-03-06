package grigoreva.facesmanager.data.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import grigoreva.facesmanager.data.greendao.customtype.LandmarkType;
import grigoreva.facesmanager.data.greendao.customtype.LandmarkTypeConverter;

import grigoreva.facesmanager.data.greendao.PhotoLandmark;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PHOTO_LANDMARK".
*/
public class PhotoLandmarkDao extends AbstractDao<PhotoLandmark, Long> {

    public static final String TABLENAME = "PHOTO_LANDMARK";

    /**
     * Properties of entity PhotoLandmark.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property LandmarkType = new Property(1, Integer.class, "landmarkType", false, "LANDMARK_TYPE");
        public final static Property PhotoId = new Property(2, Long.class, "photoId", false, "PHOTO_ID");
        public final static Property PointX = new Property(3, Float.class, "pointX", false, "POINT_X");
        public final static Property PointY = new Property(4, Float.class, "pointY", false, "POINT_Y");
        public final static Property NormPointX = new Property(5, Float.class, "normPointX", false, "NORM_POINT_X");
        public final static Property NormPointY = new Property(6, Float.class, "normPointY", false, "NORM_POINT_Y");
    };

    private final LandmarkTypeConverter landmarkTypeConverter = new LandmarkTypeConverter();

    public PhotoLandmarkDao(DaoConfig config) {
        super(config);
    }
    
    public PhotoLandmarkDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PHOTO_LANDMARK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"LANDMARK_TYPE\" INTEGER," + // 1: landmarkType
                "\"PHOTO_ID\" INTEGER," + // 2: photoId
                "\"POINT_X\" REAL," + // 3: pointX
                "\"POINT_Y\" REAL," + // 4: pointY
                "\"NORM_POINT_X\" REAL," + // 5: normPointX
                "\"NORM_POINT_Y\" REAL);"); // 6: normPointY
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PHOTO_LANDMARK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PhotoLandmark entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        LandmarkType landmarkType = entity.getLandmarkType();
        if (landmarkType != null) {
            stmt.bindLong(2, landmarkTypeConverter.convertToDatabaseValue(landmarkType));
        }
 
        Long photoId = entity.getPhotoId();
        if (photoId != null) {
            stmt.bindLong(3, photoId);
        }
 
        Float pointX = entity.getPointX();
        if (pointX != null) {
            stmt.bindDouble(4, pointX);
        }
 
        Float pointY = entity.getPointY();
        if (pointY != null) {
            stmt.bindDouble(5, pointY);
        }
 
        Float normPointX = entity.getNormPointX();
        if (normPointX != null) {
            stmt.bindDouble(6, normPointX);
        }
 
        Float normPointY = entity.getNormPointY();
        if (normPointY != null) {
            stmt.bindDouble(7, normPointY);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PhotoLandmark readEntity(Cursor cursor, int offset) {
        PhotoLandmark entity = new PhotoLandmark( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : landmarkTypeConverter.convertToEntityProperty(cursor.getInt(offset + 1)), // landmarkType
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // photoId
            cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3), // pointX
            cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4), // pointY
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // normPointX
            cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6) // normPointY
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PhotoLandmark entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLandmarkType(cursor.isNull(offset + 1) ? null : landmarkTypeConverter.convertToEntityProperty(cursor.getInt(offset + 1)));
        entity.setPhotoId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setPointX(cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3));
        entity.setPointY(cursor.isNull(offset + 4) ? null : cursor.getFloat(offset + 4));
        entity.setNormPointX(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setNormPointY(cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PhotoLandmark entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PhotoLandmark entity) {
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
