package com.techcorp.teluguvideostatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {

    //define static variable
    public static int dbversion =1;
    public static String dbname = "VideoStatus_DB";
    public static String dbTable = "VideoStatusFav";
    public static String dbTabledownload = "VideoStatusDownload";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context,dbname,null, dbversion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement,item_id, item_name, video_url, image_url, total_views, is_type, date, cat_id, cat_name)");
            db.execSQL("CREATE TABLE "+dbTabledownload+" (_id INTEGER PRIMARY KEY autoincrement,item_id, item_name, video_url, image_url, total_views, is_type, date, cat_id, cat_name)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+dbTable);
            db.execSQL("DROP TABLE IF EXISTS "+dbTabledownload);
            onCreate(db);
        }
    }

    private final Context c;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlDb;

    public DbAdapter(Context context) {
        this.c = context;
    }
    public DbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
        return this;
    }

    //insert data
    public void insert(String item_id,String item_name,String video_url,String image_url,String total_views,String is_type,String date,String cat_id,String cat_name) {
        if(!isExist(item_id)) {
            sqlDb.execSQL("INSERT INTO "+dbTable+" (item_id,item_name,video_url,image_url,total_views,is_type,date,cat_id,cat_name) VALUES('"+item_id+"','"+item_name+"','"+video_url+"','"+image_url+"','"+total_views+"','"+is_type+"','"+date+"','"+cat_id+"','"+cat_name+"')");
        }
    }

    public void insertdownload(String item_id,String item_name,String video_url,String image_url,String total_views,String is_type,String date,String cat_id,String cat_name) {
        if(!isExistdownload(item_id)) {
            sqlDb.execSQL("INSERT INTO "+dbTabledownload+" (item_id,item_name,video_url,image_url,total_views,is_type,date,cat_id,cat_name) VALUES('"+item_id+"','"+item_name+"','"+video_url+"','"+image_url+"','"+total_views+"','"+is_type+"','"+date+"','"+cat_id+"','"+cat_name+"')");
        }else{
            ContentValues cv = new ContentValues();
            cv.put("item_name",item_name);
            cv.put("video_url",video_url);
            cv.put("image_url",image_url);
            cv.put("total_views",total_views);
            sqlDb.update(dbTabledownload, cv, "item_id="+item_id, null);
        }
    }

    //check entry already in database or not
    public boolean isExist(String num){
        String query = "SELECT item_id FROM "+dbTable+" WHERE item_id='"+num+"' LIMIT 1";
        Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }


    public boolean isExistdownload(String num){
        String query = "SELECT item_id FROM "+dbTabledownload+" WHERE item_id='"+num+"' LIMIT 1";
        Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }

    //delete data
    public void delete(String item_id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE item_id='"+item_id+"'");
    }


    public void deletedownload(String item_id) {
        if(!isExistdownload(item_id)) {
            sqlDb.execSQL("DELETE FROM " + dbTabledownload + " WHERE item_id='" + item_id + "'");
        }
    }

    //fetch data
    public Cursor fetchAllData() {
        String query = "SELECT * FROM "+dbTable;
        Cursor row = sqlDb.rawQuery(query, null);
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    public Cursor fetchAllDownload() {
        String query = "SELECT * FROM "+dbTabledownload;
        Cursor row = sqlDb.rawQuery(query, null);
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    public Cursor fetchsingledata(String item_id) throws SQLException {
        Cursor row = null;
        String query = "SELECT * FROM "+dbTabledownload+" WHERE item_id='" + item_id + "'";
        row = sqlDb.rawQuery(query, null);

        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }

    public Cursor fetchdatabyfilter(String inputText,String filtercolumn) throws SQLException {
        Cursor row = null;
        String query = "SELECT * FROM "+dbTable;
        if (inputText == null  ||  inputText.length () == 0)  {
            row = sqlDb.rawQuery(query, null);
        }else {
            query = "SELECT * FROM "+dbTable+" WHERE "+filtercolumn+" like '%"+inputText+"%'";
            row = sqlDb.rawQuery(query, null);
        }
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }
}
