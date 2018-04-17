package com.example.leebet_pc.saggip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelperPastCrimeReports extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "reports.db";
    public static final String REPORTS_TABLE_NAME = "reports";
    public static final String REPORTS_COLUMN_ID = "id";
    public static final String REPORTS_COLUMN_DATE = "date";
    public static final String REPORTS_COLUMN_LOCATION = "location";
    public static final String REPORTS_COLUMN_DETAILS = "details";

    private HashMap hp;

    public DBHelperPastCrimeReports(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table reports " +
                        "(id integer primary key, date text, location text, details text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS reports");
        onCreate(db);
    }

    public boolean insertReport (String date, String location, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("location", location);
        contentValues.put("details", details);
        db.insert("reports", null, contentValues);
        return true;
    }

    /*
    public Cursor getData(String date,String location, String details) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from reports where date="+date+" and location="+location+"" and details="details", null );
        return res;
    }
    */

    /*
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, REPORTS_TABLE_NAME);
        return numRows;
    }
    */

    /*
    public boolean updateReport (Integer id, String date, String location, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("location", location);
        contentValues.put("details", details);
        db.update("reports", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    */

    /*
    public Integer deleteReport (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("reports",
                "id = ? ",
                new String[] { id });
    }
    */

    public Integer deleteDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("reports",
                null,
                null );
    }

    public ArrayList<String> getAllReports() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from reports", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(REPORTS_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }
    public ArrayList<CrimeReportModel> getAllReportsList() {
        ArrayList<CrimeReportModel> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from reports", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String date = res.getString(res.getColumnIndex(REPORTS_COLUMN_DATE));
            String location = res.getString(res.getColumnIndex(REPORTS_COLUMN_LOCATION));
            String details = res.getString(res.getColumnIndex(REPORTS_COLUMN_DETAILS));


            array_list.add(new CrimeReportModel(date,location,details));
            res.moveToNext();
        }
        return array_list;
    }
}
