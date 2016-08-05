package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION			= 1;

	// Database Name
	private static final String DATABASE_NAME			= "vbeeping_database";

	// table names
	private static final String TABLE_SHAPES			= "map_shapes";

	// table fields
	private static final String DEVICE_ID				= "device_id";
	private static final String SHAPE_TYPE				= "shape_type";
	private static final String SHAPE_POINTS			= "shape_points";


	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_MEDIAS_TABLE = "CREATE TABLE " + TABLE_SHAPES + "("
				+ DEVICE_ID 		+ " TEXT PRIMARY KEY, "
				+ SHAPE_TYPE 	+ " TEXT, "
				+ SHAPE_POINTS 	+ " TEXT)";
		Log.v("vbbping:", CREATE_MEDIAS_TABLE);
		db.execSQL(CREATE_MEDIAS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHAPES);
		// Create tables again
		onCreate(db);
	}

	/*------------------------------------------------------Manage the SettingTable---------------------------------------------*/
	// Adding new Setting
	public boolean addShape(String deviceID, String shapeType, String shapePoints) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(DEVICE_ID, deviceID);
			values.put(SHAPE_TYPE, shapeType);
			values.put(SHAPE_POINTS, shapePoints);

			// Inserting Row
			db.insert(TABLE_SHAPES, null, values);

			db.close(); // Closing database connection
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			db.close();
			return false;
		}
	}

	// Getting Media
	public SafeJSONObject getShape(String deviceId) {
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_SHAPES,
					null, DEVICE_ID + "=?",
					new String[] { deviceId }, null, null, null, null);
			if (cursor != null)
				cursor.moveToFirst();
			
			SafeJSONObject shape = new SafeJSONObject();
			shape.putString("device_id", cursor.getString(0));
			shape.putString("shape_type", cursor.getString(1));
			shape.putString("shape_points", cursor.getString(2));
        	
			cursor.close();
			db.close();
			return shape;

		} catch (Exception e) {
			// TODO: handle exception
			db.close();
			return null;
		}
	}

	// Deleting Setting
	public void deleteShape(String deviceID) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SHAPES, DEVICE_ID + " = ?",
				new String[] { deviceID });
		db.close();
	}

	// Updating Setting
	public void updateSetting(String deviceId, String shapeType, String shapePoints) {
		if (getShape(deviceId) == null)
		{
			addShape(deviceId, shapeType, shapePoints);
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DEVICE_ID, deviceId);
		values.put(SHAPE_TYPE, shapeType);
		values.put(SHAPE_POINTS, shapePoints);
		// updating row
		db.update(TABLE_SHAPES, values, DEVICE_ID + " = ?",
				new String[] { deviceId });
		db.close();
	}

	public void deleteAllShapes() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SHAPES, null, null);
		db.close();
	}
}
