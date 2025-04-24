package com.example.skillswap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_PERSONAL_DETAILS = "personal_details";
    public static final String COLUMN_USER_EMAIL = "user_email"; // Foreign Key
    public static final String COLUMN_OCCUPATION = "occupation";
    public static final String COLUMN_SKILL = "skill";
    public static final String COLUMN_EXPERIENCE = "experience";
    public static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_WORKLINK = "worklink";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_ACHIEVEMENTS = "achievements";
    private static final String COLUMN_IMAGE = "image_column";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHelper", "onCreate called. Creating tables.");

        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIRST_NAME + " TEXT, "
                + COLUMN_LAST_NAME + " TEXT, "
                + COLUMN_MOBILE + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create personal details table
        final String CREATE_TABLE_PERSONAL_DETAILS =
                "CREATE TABLE personal_details (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_email TEXT NOT NULL, " +  // Foreign key reference
                        "occupation TEXT, " +
                        "skill TEXT, " +
                        "experience INTEGER, " +
                        "location TEXT, " +
                        "worklink TEXT, " +
                        "description TEXT, " +
                        "achievements TEXT, " +
                        "FOREIGN KEY(user_email) REFERENCES users(email) ON DELETE CASCADE);";

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DBHelper", "onUpgrade called. Upgrading database from " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            String ALTER_TABLE = "ALTER TABLE " + TABLE_PERSONAL_DETAILS + " ADD COLUMN " + COLUMN_IMAGE + " BLOB";
            db.execSQL(ALTER_TABLE);
        }
    }

    public boolean addUser(String firstName, String lastName, String mobile, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1;
    }

    public boolean validateUser(String emailOrPhone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE (" +
                COLUMN_EMAIL + "=? OR " + COLUMN_MOBILE + "=?) AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{emailOrPhone, emailOrPhone, password});
        boolean isValid = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return isValid;
    }

    public boolean addUserDetails(String email, String occupation, String skill, int experience,
                                  String location, String workLink, String description,
                                  String achievements, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add new data to be updated
        values.put(COLUMN_OCCUPATION, occupation);
        values.put(COLUMN_SKILL, skill);
        values.put(COLUMN_EXPERIENCE, experience);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_WORKLINK, workLink);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_ACHIEVEMENTS, achievements);

        // Update image column only if a new image is provided
        if (image != null) {
            values.put(COLUMN_IMAGE, image);
        }

        // Perform the update
        int rowsUpdated = db.update(TABLE_PERSONAL_DETAILS, values, COLUMN_USER_EMAIL + "=?", new String[]{email});

        db.close();
        return rowsUpdated > 0;
    }



    public Cursor getUserDetailsWithImage(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PERSONAL_DETAILS + " WHERE " + COLUMN_USER_EMAIL + "=?";
        return db.rawQuery(query, new String[]{email});
    }

    public int getRowCount(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        String query = "SELECT COUNT(*) FROM " + TABLE_PERSONAL_DETAILS + " WHERE " + COLUMN_USER_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0); // Get the count value
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    public Cursor getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PERSONAL_DETAILS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
    }

    public Cursor searchItems(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_SKILL+ " FROM " + TABLE_PERSONAL_DETAILS +
                " WHERE " + COLUMN_SKILL + " LIKE ?";
        return db.rawQuery(sql, new String[]{"%" + query + "%"});
    }


}
