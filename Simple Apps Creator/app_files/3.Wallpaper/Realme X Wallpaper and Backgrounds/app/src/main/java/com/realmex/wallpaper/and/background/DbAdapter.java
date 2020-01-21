package com.realmex.wallpaper.and.background;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {

    //define static variable
    public static int dbversion =1;
    public static String dbname = "RealMeXWallpaper_DB";
    public static String dbTable = "RealMeXWallpaperFav";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context,dbname,null, dbversion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement,item_id, name, image, image_thumb, tview, tdownload, tags, date)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+dbTable);
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
    public void insert(String item_id,String name,String image,String image_thumb,String tview,String tdownload,String tags,String date) {
        if(!isExist(item_id)) {
            sqlDb.execSQL("INSERT INTO "+dbTable+" (item_id,name,image,image_thumb,tview,tdownload,tags,date) VALUES('"+item_id+"','"+name+"','"+image+"','"+image_thumb+"','"+tview+"','"+tdownload+"','"+tags+"','"+date+"')");
        }
    }

    //check entry already in database or not
    public boolean isExist(String num){
        String query = "SELECT item_id FROM "+dbTable+" WHERE item_id='"+num+"' LIMIT 1";
        Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }

    //delete data
    public void delete(String item_id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE item_id='"+item_id+"'");
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
