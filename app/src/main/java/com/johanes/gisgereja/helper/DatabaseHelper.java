package com.johanes.gisgereja.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.johanes.gisgereja.utils.Gereja;
import com.johanes.gisgereja.utils.Users;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by j on 5/17/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dataGereja.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER = "user";
    private static final String TABLE_GEREJA = "gereja";

    /*
    * table user
    * */
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_LOGIN = "user_login_name";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    /*
    * table gereja
    * */
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_GEREJA_NAMA = "nama_gereja";
    private static final String COLUMN_GEREJA_ALAMAT = "alamat_gereja";
    private static final String COLUMN_GEREJA_DESKRIPSI = "deskripsi_gereja";
    private static final String COLUMN_GEREJA_IMAGE = "image";
    private static final String COLUMN_GEREJA_LONGITUD = "long";
    private static final String COLUMN_GEREJA_LATITUDE = "lati";


//    private final Context ctx;
    private static String DB_PATH="";
    private static final String TAG = "SQLITE";


//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        this.ctx = context;
//        DB_PATH = "/data/data/"
//                + context.getApplicationContext().getPackageName()
//                + "/databases/";
//        // TODO Auto-generated constructor stub
//    }

    /*
    * create table
    * */
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_LOGIN + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String CREATE_GEREJA_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GEREJA + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_GEREJA_NAMA + " TEXT,"
            + COLUMN_GEREJA_ALAMAT + " TEXT, " + COLUMN_GEREJA_DESKRIPSI + " TEXT,"
            + COLUMN_GEREJA_IMAGE + " BLOB, " + COLUMN_GEREJA_LONGITUD + " REAL,"
            + COLUMN_GEREJA_LATITUDE + " REAL" + " )";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_GEREJA_TABLE = "DROP TABLE IF EXISTS " + TABLE_GEREJA;

    public static DatabaseHelper openSharedDB(Context context){
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/com.johanes/");
        dir.mkdirs();
        return new DatabaseHelper(context, dir.getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_GEREJA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_GEREJA_TABLE);
        // Create tables again
        onCreate(db);

    }


    /**
     * This method is to create user record
     *
     * @param user
     */

    // ======================================  USER LOGIN =================================
    // add user
    public void addUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_LOGIN, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    public List<Users> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_LOGIN,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<Users> userList = new ArrayList<Users>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Users user = new Users();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_LOGIN)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    /**
     * This method to update user record
     *
     * @param user
     */
    public void updateUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_LOGIN, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    /**
     * This method to check user exist or not
     *
     * @param username
     * @return true/false
     */
    public boolean checkUser(String username) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_USER_LOGIN + " = ?";

        // selection argument
        String[] selectionArgs = {username};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'name@mail.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param username
     * @param password
     * @return true/false
     */
    public boolean checkUser(String username, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_LOGIN + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {username, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'name@mail.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    // =================================== EOF USER LOGIN =================================

    // ====================================== GEREJA DATA =================================

    public boolean checkGereja(String namaGereja)
    {
        // array of columns to fetch
        String[] columns = {
                COLUMN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_GEREJA_NAMA + " = ?";

        // selection argument
        String[] selectionArgs = {namaGereja};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'name@mail.com';
         */
        Cursor cursor = db.query(TABLE_GEREJA, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public void addGereja(Gereja gereja) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, gereja.getName());
        values.put(COLUMN_USER_LOGIN, gereja.getName());
        values.put(COLUMN_USER_PASSWORD, gereja.getAlamat());
        values.put(COLUMN_GEREJA_IMAGE, gereja.getImage());
        // Inserting Row
        db.insert(TABLE_GEREJA, null, values);
        db.close();
    }

    public void addGereja(String name, String alamat, String deskripsi,
                           double lon, double lat, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();
        //String sql = "INSERT INTO " + TABLE_GEREJA + " VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        ContentValues values = new ContentValues();
        values.put(COLUMN_GEREJA_NAMA, name);
        values.put(COLUMN_GEREJA_ALAMAT, alamat);
        values.put(COLUMN_GEREJA_DESKRIPSI, deskripsi);
        values.put(COLUMN_GEREJA_LONGITUD,lon);
        values.put(COLUMN_GEREJA_LATITUDE, lat);
        values.put(COLUMN_GEREJA_IMAGE, image);
        // Inserting Row
        database.insert(TABLE_GEREJA, null, values);
        database.close();
    }

    public void updateGereja(int id, String name, String alamat, String deskripsi,
                          double lon, double lat, byte[] image) {
        SQLiteDatabase database = getWritableDatabase();
        //String sql = "INSERT INTO " + TABLE_GEREJA + " VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        ContentValues values = new ContentValues();
        values.put(COLUMN_GEREJA_NAMA, name);
        values.put(COLUMN_GEREJA_ALAMAT, alamat);
        values.put(COLUMN_GEREJA_DESKRIPSI, deskripsi);
        values.put(COLUMN_GEREJA_LONGITUD,lon);
        values.put(COLUMN_GEREJA_LATITUDE, lat);
        values.put(COLUMN_GEREJA_IMAGE, image);
        // Inserting Row
        database.update(TABLE_GEREJA, values, COLUMN_ID + "=" + id, null);
        database.close();
    }

//    public Cursor getData(String sql){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.rawQuery(sql, null);
//    }
public List<Gereja> getAllGereja() {
    // array of columns to fetch
    String[] columns = {
            COLUMN_ID,
            COLUMN_GEREJA_NAMA,
            COLUMN_GEREJA_ALAMAT,
            COLUMN_GEREJA_DESKRIPSI,
            COLUMN_GEREJA_IMAGE,
            COLUMN_GEREJA_LONGITUD,
            COLUMN_GEREJA_LATITUDE
    };
    // sorting orders
    String sortOrder =
            COLUMN_GEREJA_NAMA + " ASC";
    List<Gereja> gerejaList = new ArrayList<Gereja>();

    SQLiteDatabase db = this.getReadableDatabase();

    // query the user table
    /**
     * Here query function is used to fetch records from user table this function works like we use sql query.
     * SQL query equivalent to this query function is
     * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
     */
    Cursor cursor = db.query(TABLE_GEREJA, //Table to query
            columns,    //columns to return
            null,        //columns for the WHERE clause
            null,        //The values for the WHERE clause
            null,       //group the rows
            null,       //filter by row groups
            sortOrder); //The sort order


    // Traversing through all rows and adding to list
    if (cursor.moveToFirst()) {
        do {
            Gereja gereja = new Gereja();
            gereja.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID))));
            gereja.setName(cursor.getString(cursor.getColumnIndex(COLUMN_GEREJA_NAMA)));
            gereja.setAlamat(cursor.getString(cursor.getColumnIndex(COLUMN_GEREJA_ALAMAT)));
            gereja.setDeskripsi(cursor.getString(cursor.getColumnIndex(COLUMN_GEREJA_DESKRIPSI)));
            gereja.setLongi(cursor.getString(cursor.getColumnIndex(COLUMN_GEREJA_LONGITUD)));
            gereja.setLati(cursor.getString(cursor.getColumnIndex(COLUMN_GEREJA_LATITUDE)));
            gereja.setImage(cursor.getBlob(cursor.getColumnIndex(COLUMN_GEREJA_IMAGE)));
            // Adding user record to list
            gerejaList.add(gereja);
        } while (cursor.moveToNext());
    }
    cursor.close();
    db.close();

    // return user list
    return gerejaList;
}

    public void deleteGereja(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_GEREJA, COLUMN_ID + " = ?",  new String[]{String.valueOf(id)});
        db.close();
    }


    // ================================== EOF GEREJA DATA =================================


//


}
