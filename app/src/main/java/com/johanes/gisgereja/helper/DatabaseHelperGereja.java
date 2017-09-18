package com.johanes.gisgereja.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by j on 6/16/17.
 */

public class DatabaseHelperGereja extends SQLiteOpenHelper {

    public DatabaseHelperGereja(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

//    public DatabaseHelperGereja() {
//        super();
//    }


    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

//    public void insertData(String name, String alamat, String deskripsi, byte[] image) {
        public void insertData(String name, String alamat, String deskripsi,
                               double lon, double lat, byte[] image) {
        SQLiteDatabase database = super.getWritableDatabase();
        /*String sql = "INSERT INTO GerejaBaru VALUES (NULL, ?, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, alamat);
        statement.bindString(3, deskripsi);
        statement.bindDouble(4, lon);
        statement.bindDouble(5, lat);
        statement.bindBlob(6, image);

        statement.executeInsert();*/
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("alamat", alamat);
            values.put("deskripsi", deskripsi);
            values.put("longi", lon);
            values.put("lati", lat);
            values.put("image", image);
            // Inserting Row
            database.insert("GerejaBaru", null, values);
            database.close();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = super.getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}