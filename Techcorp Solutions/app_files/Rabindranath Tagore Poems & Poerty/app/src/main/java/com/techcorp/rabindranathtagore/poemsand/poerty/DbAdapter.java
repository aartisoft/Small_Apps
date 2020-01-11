package com.techcorp.rabindranathtagore.poemsand.poerty;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

    //define static variable
    public static int dbversion =1;
    public static String dbname = "KarelDB";
    public static String dbTable = "SPQFav";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context,dbname,null, dbversion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement,item_id, item_name, item_details, image, image_thumb, total_views, date, cat_id, cat_name,cat_img)");
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
    public void insert(String item_id,String item_name,String item_details,String image,String image_thumb,String total_views,String date,String cat_id,String cat_name,String cat_img) {
        if(!isExist(item_id)) {
            sqlDb.execSQL("INSERT INTO SPQFav (item_id,item_name,item_details,image,image_thumb,total_views,date,cat_id,cat_name,cat_img) VALUES('"+item_id+"','"+item_name+"','"+item_details+"','"+image+"','"+image_thumb+"','"+total_views+"','"+date+"','"+cat_id+"','"+cat_name+"','"+cat_img+"')");
        }
    }

    //check entry already in database or not
    public boolean isExist(String num){
        String query = "SELECT item_id FROM SPQFav WHERE item_id='"+num+"' LIMIT 1";
        Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }

    //edit data
    public void update(int id, String text2, String text3, String text4, String text5) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET item_id='"+text2+"', item_name='"+text3+"', item_details='"+text4+"', WHERE _id=" + id);
    }

    //delete data
    public void delete(String item_id) {
        Log.e("deleterecord",":::  "+item_id);
        sqlDb.execSQL("DELETE FROM SPQFav WHERE item_id='"+item_id+"'");
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

    //fetch data by filter
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
