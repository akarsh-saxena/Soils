package com.application.soil.soils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akarsh on 05-01-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "data.db";
    public String TABLE_NAME = "city";
    public String COLUMN_SOIL = "soil";
    public static final String COLUMN_POTASH = "potash";
    public static final String COLUMN_PHOSPHORIC= "phosphoric";
    public static final String COLUMN_ALKALI = "alkali";
    public static final String COLUMN_NITROGEN = "nitrogen";
    public static final String COLUMN_IRON = "iron";
    public static final String COLUMN_LIME = "lime";
    Context context;
    //private DatabaseHelper databaseHelper;

    public DatabaseHelper(Context context, String cityName) {
        super(context, DB_NAME, null, 1);
        cityName = cityName.trim();
        TABLE_NAME = cityName;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+COLUMN_SOIL+" TEXT, "+COLUMN_POTASH+" TEXT, "+COLUMN_PHOSPHORIC+" TEXT, "+COLUMN_ALKALI+" TEXT, "+COLUMN_NITROGEN+" TEXT, "+COLUMN_IRON+" TEXT,"+COLUMN_LIME+" TEXT);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void createTable(String tableName) {
        tableName = tableName.trim();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "CREATE TABLE IF NOT EXISTS "+tableName+" ("+COLUMN_SOIL+" TEXT, "+COLUMN_POTASH+" TEXT, "+COLUMN_PHOSPHORIC+" TEXT, "+COLUMN_ALKALI+" TEXT, "+COLUMN_NITROGEN+" TEXT, "+COLUMN_IRON+" TEXT,"+COLUMN_LIME+" TEXT);";
        sqLiteDatabase.execSQL(query);
    }

    public boolean insertData(String soil, String potash, String phos, String alkali, String nitro, String iron, String lime) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_SOIL, soil);
        contentValues.put(COLUMN_POTASH, potash);
        contentValues.put(COLUMN_PHOSPHORIC, phos);
        contentValues.put(COLUMN_ALKALI, alkali);
        contentValues.put(COLUMN_NITROGEN, nitro);
        contentValues.put(COLUMN_IRON, iron);
        contentValues.put(COLUMN_LIME, lime);

        TABLE_NAME = TABLE_NAME.trim();

        String queryDelete = "DELETE FROM "+TABLE_NAME+" WHERE "+COLUMN_SOIL+"='"+soil+"';";
        String query = "INSERT INTO "+TABLE_NAME+" VALUES ('"+soil+"', '"+potash+"', '"+phos+"', '"+alkali+"', '"+nitro+"', '"+iron+"', '"+lime+"');";
        sqLiteDatabase.execSQL(queryDelete);
        sqLiteDatabase.execSQL(query);

        //long l1 = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return true;
/*

        if(l1 > 0)
            return true;
        else
            return false;
*/
    }

    public List<SoilModel> getSoilInformation(String tableName) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<SoilModel> soilModels = new ArrayList<>();
        String query = "SELECT * FROM "+tableName+";";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        do {
            String soil = cursor.getString(cursor.getColumnIndex(COLUMN_SOIL));
            String potash = cursor.getString(cursor.getColumnIndex(COLUMN_POTASH));
            String phos = cursor.getString(cursor.getColumnIndex(COLUMN_PHOSPHORIC));
            String alkali = cursor.getString(cursor.getColumnIndex(COLUMN_ALKALI));
            String nitro = cursor.getString(cursor.getColumnIndex(COLUMN_NITROGEN));
            String iron = cursor.getString(cursor.getColumnIndex(COLUMN_IRON));
            String lime = cursor.getString(cursor.getColumnIndex(COLUMN_LIME));
            SoilModel soilModel = new SoilModel();
            soilModel.setName(soil);
            soilModel.setPotash(potash);
            soilModel.setPhos(phos);
            soilModel.setAlkali(alkali);
            soilModel.setNitro(nitro);
            soilModel.setIron(iron);
            soilModel.setLime(lime);
            soilModels.add(soilModel);
        } while(cursor.moveToNext());
        cursor.close();
        Log.d("muy", soilModels.toString());
        return soilModels;
    }

    public boolean isTableExist(String tableName) {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        //String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'";
        try {
            String query = "SELECT * FROM " + tableName + ";";
            /*Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    Toast.makeText(context, "Returning true", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(context, "Returning false from else", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            Toast.makeText(context, "Returning false", Toast.LENGTH_SHORT).show();
            return false;*/
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if(cursor.getCount()==0) {
                return false;
            }
            else {
                return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}