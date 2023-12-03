package com.mobdeve.s16.uy.kenneth.angpao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "Angpao.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_ANGPAO = "tblAngpao";
    public static final String COLUMN_ANGPAO_ID = "angpaoID";
    public static final String COLUMN_ANGPAO_NINONG_ID = "angpaoNinongID";
    public static final String COLUMN_ANGPAO_AMOUNT = "angpaoAmount";
    public static final String COLUMN_ANGPAO_DATE = "angpaoDate";

    public static final String TABLE_NINONG = "tblNinong";
    public static final String COLUMN_NINONG_ID = "ninongID";
    public static final String COLUMN_NINONG_PIC = "ninongPic";
    public static final String COLUMN_NINONG_TYPE = "ninongType";
    public static final String COLUMN_NINONG_NAME = "ninongName";
    public static final String COLUMN_NINONG_DESC = "ninongDesc";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAngpaoTable = "CREATE TABLE " + TABLE_ANGPAO + "(" +
                COLUMN_ANGPAO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ANGPAO_NINONG_ID + " INTEGER, " +
                COLUMN_ANGPAO_AMOUNT + " INTEGER, " +
                COLUMN_ANGPAO_DATE + " TEXT);";

        String createNinongTable = "CREATE TABLE " + TABLE_NINONG + "(" +
                COLUMN_NINONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NINONG_PIC + " TEXT, " +
                COLUMN_NINONG_TYPE + " TEXT, " +
                COLUMN_NINONG_NAME + " TEXT, " +
                COLUMN_NINONG_DESC + " TEXT);";

        // Execute the SQL statements
        sqLiteDatabase.execSQL(createAngpaoTable);
        sqLiteDatabase.execSQL(createNinongTable);
    }

    public NinongData getNinongById(int ninongId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_NINONG_ID,
                COLUMN_NINONG_PIC,
                COLUMN_NINONG_TYPE,
                COLUMN_NINONG_NAME,
                COLUMN_NINONG_DESC
        };

        String selection = COLUMN_NINONG_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ninongId)};

        Cursor cursor = db.query(TABLE_NINONG, columns, selection, selectionArgs, null, null, null);

        NinongData ninongData = null;

        if (cursor.moveToFirst()) {
            int picIndex = cursor.getColumnIndex(COLUMN_NINONG_PIC);
            int typeIndex = cursor.getColumnIndex(COLUMN_NINONG_TYPE);
            int nameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);
            int descIndex = cursor.getColumnIndex(COLUMN_NINONG_DESC);

            String pic = cursor.getString(picIndex);
            String type = cursor.getString(typeIndex);
            String name = cursor.getString(nameIndex);
            String desc = cursor.getString(descIndex);

            ninongData = new NinongData(ninongId, pic, type, name, desc);
        }

        cursor.close();
        db.close();

        return ninongData;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ANGPAO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NINONG);
        onCreate(sqLiteDatabase);
    }

    public long insertAngpao(int ninongId, int amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANGPAO_NINONG_ID, ninongId);
        values.put(COLUMN_ANGPAO_AMOUNT, amount);
        values.put(COLUMN_ANGPAO_DATE, date);
        long result = db.insert(TABLE_ANGPAO, null, values);
        if (result == -1) {
            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Angpao Added!", Toast.LENGTH_SHORT).show();
        }
        db.close();
        return result;
    }

    public int updateAngpao(int angpaoId, int newNinongId, int amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANGPAO_NINONG_ID, newNinongId);
        values.put(COLUMN_ANGPAO_AMOUNT, amount);
        values.put(COLUMN_ANGPAO_DATE, date);

        String whereClause = COLUMN_ANGPAO_ID + " = ?";
        String[] whereArgs = {String.valueOf(angpaoId)};
        Log.d("insert", angpaoId + " : " + newNinongId + " : "+ amount + " : " + date + " : ");

        int result = db.update(TABLE_ANGPAO, values, whereClause, whereArgs);

        if (result > 0) {
            Toast.makeText(context, "Angpao Updated!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to update Angpao", Toast.LENGTH_SHORT).show();
        }

        db.close();
        return result;
    }


    public int deleteAngpao(int angpaoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ANGPAO_ID + " = ?";
        String[] whereArgs = {String.valueOf(angpaoId)};
        int result = db.delete(TABLE_ANGPAO, whereClause, whereArgs);
        db.close();
        return result;
    }

    public long insertNinong(String prefix, String pic, String name, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NINONG_PIC, pic);
        values.put(COLUMN_NINONG_TYPE, prefix); // Assuming COLUMN_NINONG_TYPE is used for storing the prefix
        values.put(COLUMN_NINONG_NAME, name);
        values.put(COLUMN_NINONG_DESC, desc);
        long result = db.insert(TABLE_NINONG, null, values);
        db.close();
        return result;
    }

    public int deleteNinong(int ninongId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = -1;

        try {
            // Delete angpaos associated with the ninongId
            String angpaoWhereClause = COLUMN_ANGPAO_NINONG_ID + " = ?";
            String[] angpaoWhereArgs = {String.valueOf(ninongId)};
            db.delete(TABLE_ANGPAO, angpaoWhereClause, angpaoWhereArgs);

            // Delete the ninong
            String ninongWhereClause = COLUMN_NINONG_ID + " = ?";
            String[] ninongWhereArgs = {String.valueOf(ninongId)};
            result = db.delete(TABLE_NINONG, ninongWhereClause, ninongWhereArgs);

            Log.d("MyDatabaseHelper", "Deleted Ninong ID: " + ninongId + ", Result: " + result);
        } catch (SQLException e) {
            Log.e("MyDatabaseHelper", "Error deleting Ninong: " + e.getMessage());
        } finally {
            db.close();
        }
        return result;
    }


    public int updateNinong(int ninongId, String pic, String type, String name, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NINONG_PIC, pic);
        values.put(COLUMN_NINONG_TYPE, type);
        values.put(COLUMN_NINONG_NAME, name);
        values.put(COLUMN_NINONG_DESC, desc);

        String whereClause = COLUMN_NINONG_ID + " = ?";
        String[] whereArgs = {String.valueOf(ninongId)};

        int result = db.update(TABLE_NINONG, values, whereClause, whereArgs);
        db.close();
        return result;
    }
    public int getNinongIdByName(String ninongName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int ninongId = -1;

        // Define the columns you want to retrieve
        String[] projection = {COLUMN_NINONG_ID};

        // Specify the WHERE clause
        String selection = COLUMN_NINONG_NAME + " = ?";
        String[] selectionArgs = {ninongName};

        Cursor cursor = db.query(
                TABLE_NINONG,            // The table to query
                projection,               // The array of columns to return (pass null to get all)
                selection,                // The columns for the WHERE clause
                selectionArgs,            // The values for the WHERE clause
                null,                     // don't group the rows
                null,                     // don't filter by row groups
                null                      // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {
            ninongId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NINONG_ID));
            cursor.close();
        }

        return ninongId;
    }

    public int getAccumulatedAngpaoAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int accumulatedAmount = 0;

        // Query to sum up angpaoAmount from tblAngpao
        String query = "SELECT SUM(" + COLUMN_ANGPAO_AMOUNT + ") FROM " + TABLE_ANGPAO;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            accumulatedAmount = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return accumulatedAmount;
    }

    public List<NinongData> getAllNinongs() {
        List<NinongData> ninongsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Ensure that the columns exist in the cursor
        String[] columns = {COLUMN_NINONG_ID, COLUMN_NINONG_PIC, COLUMN_NINONG_TYPE, COLUMN_NINONG_NAME, COLUMN_NINONG_DESC};

        Cursor cursor = db.query(TABLE_NINONG, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int ninongIdIndex = cursor.getColumnIndex(COLUMN_NINONG_ID);
                int picIndex = cursor.getColumnIndex(COLUMN_NINONG_PIC);
                int typeIndex = cursor.getColumnIndex(COLUMN_NINONG_TYPE);
                int nameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);
                int descIndex = cursor.getColumnIndex(COLUMN_NINONG_DESC);

                int ninongId = cursor.getInt(ninongIdIndex);
                String pic = cursor.getString(picIndex);
                String type = cursor.getString(typeIndex);
                String name = cursor.getString(nameIndex);
                String desc = cursor.getString(descIndex);

                NinongData ninongData = new NinongData(ninongId, pic, type, name, desc);
                ninongsList.add(ninongData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ninongsList;
    }

    public List<HistoryData> getAllAngpaos() {
        List<HistoryData> angpaosList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Specify the columns you want to retrieve
        String[] columns = {COLUMN_ANGPAO_ID, COLUMN_ANGPAO_NINONG_ID, COLUMN_ANGPAO_AMOUNT, COLUMN_ANGPAO_DATE};

        // Perform the JOIN operation to get Ninong names
        String query = "SELECT a." + COLUMN_ANGPAO_ID + ", a." + COLUMN_ANGPAO_NINONG_ID +
                ", a." + COLUMN_ANGPAO_AMOUNT + ", a." + COLUMN_ANGPAO_DATE +
                ", n." + COLUMN_NINONG_TYPE + ", n." + COLUMN_NINONG_NAME +
                " FROM " + TABLE_ANGPAO + " a" +
                " INNER JOIN " + TABLE_NINONG + " n ON a." + COLUMN_ANGPAO_NINONG_ID + " = n." + COLUMN_NINONG_ID;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int angpaoIdIndex = cursor.getColumnIndex(COLUMN_ANGPAO_ID);
                int ninongIdIndex = cursor.getColumnIndex(COLUMN_ANGPAO_NINONG_ID);
                int amountIndex = cursor.getColumnIndex(COLUMN_ANGPAO_AMOUNT);
                int dateIndex = cursor.getColumnIndex(COLUMN_ANGPAO_DATE);
                int ninongPrefixIndex = cursor.getColumnIndex(COLUMN_NINONG_TYPE);
                int ninongNameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);

                int angpaoId = cursor.getInt(angpaoIdIndex);
                int ninongId = cursor.getInt(ninongIdIndex);
                String amount = cursor.getString(amountIndex);
                String date = cursor.getString(dateIndex);
                String ninongName = cursor.getString(ninongNameIndex);
                String ninongPrefix = cursor.getString(ninongPrefixIndex);

                HistoryData angpaoData = new HistoryData(angpaoId, ninongId, amount, date, ninongName, ninongPrefix);
                angpaosList.add(angpaoData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return angpaosList;
    }

    public List<HistoryData> getAngpaosByNinongId(int ninongId) {
        List<HistoryData> angpaosList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ANGPAO_ID, COLUMN_ANGPAO_NINONG_ID, COLUMN_ANGPAO_AMOUNT, COLUMN_ANGPAO_DATE};
        String selection = COLUMN_ANGPAO_NINONG_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ninongId)};

        Cursor cursor = db.query(TABLE_ANGPAO, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int angpaoIdIndex = cursor.getColumnIndex(COLUMN_ANGPAO_ID);
                int fetchedNinongIdIndex = cursor.getColumnIndex(COLUMN_ANGPAO_NINONG_ID);
                int amountIndex = cursor.getColumnIndex(COLUMN_ANGPAO_AMOUNT);
                int dateIndex = cursor.getColumnIndex(COLUMN_ANGPAO_DATE);

                int angpaoId = cursor.getInt(angpaoIdIndex);
                int fetchedNinongId = cursor.getInt(fetchedNinongIdIndex);
                String amount = cursor.getString(amountIndex);
                String date = cursor.getString(dateIndex);

                // Retrieve Ninong name from Ninong table using fetchedNinongId
                String ninongName = getNinongNameById(fetchedNinongId);
                String ninongPrefix = getNinongPrefixById(fetchedNinongId);

                HistoryData angpaoData = new HistoryData(angpaoId, fetchedNinongId, amount, date, ninongName, ninongPrefix);
                angpaosList.add(angpaoData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return angpaosList;
    }

    public HistoryData getAngpaoById(int angpaoId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_ANGPAO_ID,
                COLUMN_ANGPAO_NINONG_ID,
                COLUMN_ANGPAO_AMOUNT,
                COLUMN_ANGPAO_DATE
        };

        String selection = COLUMN_ANGPAO_ID + " = ?";
        String[] selectionArgs = {String.valueOf(angpaoId)};

        Cursor cursor = db.query(TABLE_ANGPAO, columns, selection, selectionArgs, null, null, null);

        HistoryData angpaoData = null;

        if (cursor.moveToFirst()) {
            int ninongIdIndex = cursor.getColumnIndex(COLUMN_ANGPAO_NINONG_ID);
            int amountIndex = cursor.getColumnIndex(COLUMN_ANGPAO_AMOUNT);
            int dateIndex = cursor.getColumnIndex(COLUMN_ANGPAO_DATE);

            int ninongId = cursor.getInt(ninongIdIndex);
            String amount = cursor.getString(amountIndex);
            String date = cursor.getString(dateIndex);

            // Retrieve Ninong name from Ninong table using ninongId
            String ninongName = getNinongNameById(ninongId);
            String ninongPrefix = getNinongPrefixById(ninongId);

            angpaoData = new HistoryData(angpaoId, ninongId, amount, date, ninongName, ninongPrefix);
        }

        cursor.close();
        db.close();

        return angpaoData;
    }


    public String getNinongPrefixById(int ninongId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NINONG_TYPE};
        String selection = COLUMN_NINONG_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ninongId)};

        Cursor cursor = db.query(TABLE_NINONG, columns, selection, selectionArgs, null, null, null);

        String ninongPrefix = "Default"; // Set a default value

        if (cursor.moveToFirst()) {
            int prefixIndex = cursor.getColumnIndex(COLUMN_NINONG_TYPE);
            ninongPrefix = cursor.getString(prefixIndex);
        }

        cursor.close();
        db.close();
        return ninongPrefix;
    }

    private String getNinongNameById(int ninongId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NINONG_NAME};
        String selection = COLUMN_NINONG_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ninongId)};

        Cursor cursor = db.query(TABLE_NINONG, columns, selection, selectionArgs, null, null, null);

        String ninongName = null;
        if (cursor.moveToFirst()) {
            int ninongNameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);
            ninongName = cursor.getString(ninongNameIndex);
        }

        cursor.close();
        db.close();

        return ninongName;
    }

    public List<String> getNinongNames() {
        List<String> ninongNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Ensure that the columns exist in the cursor
        String[] columns = {COLUMN_NINONG_NAME};

        Cursor cursor = db.query(TABLE_NINONG, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);
                String name = cursor.getString(nameIndex);
                ninongNames.add(name);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ninongNames;
    }

    public List<NinongAccumulatedAmount> getNinongAccumulatedAmounts() {
        List<NinongAccumulatedAmount> accumulatedAmountsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get the accumulated amount and prefix for each Ninong
        String query = "SELECT " +
                "N." + COLUMN_NINONG_ID + ", " +
                "N." + COLUMN_NINONG_NAME + ", " +
                "N." + COLUMN_NINONG_TYPE + ", " +
                "SUM(A." + COLUMN_ANGPAO_AMOUNT + ") AS AccumulatedAmount " +
                "FROM " + TABLE_NINONG + " N " +
                "LEFT JOIN " + TABLE_ANGPAO + " A " +
                "ON N." + COLUMN_NINONG_ID + " = A." + COLUMN_ANGPAO_NINONG_ID + " " +
                "GROUP BY N." + COLUMN_NINONG_ID + ", N." + COLUMN_NINONG_NAME + ", N." + COLUMN_NINONG_TYPE + " " +
                "ORDER BY AccumulatedAmount DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int ninongIdIndex = cursor.getColumnIndex(COLUMN_NINONG_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_NINONG_TYPE);
                int accumulatedAmountIndex = cursor.getColumnIndex("AccumulatedAmount");

                int ninongId = cursor.getInt(ninongIdIndex);
                String name = cursor.getString(nameIndex);
                String type = cursor.getString(typeIndex);
                int accumulatedAmount = cursor.getInt(accumulatedAmountIndex);

                NinongAccumulatedAmount ninongAccumulatedAmount = new NinongAccumulatedAmount(ninongId, type, name, accumulatedAmount);
                accumulatedAmountsList.add(ninongAccumulatedAmount);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return accumulatedAmountsList;
    }

    public List<NinongAccumulatedTimes> getNinongAccumulatedTimes() {
        List<NinongAccumulatedTimes> accumulatedTimesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get the accumulated times and prefix for each Ninong
        String query = "SELECT " +
                "N." + COLUMN_NINONG_ID + ", " +
                "N." + COLUMN_NINONG_NAME + ", " +
                "N." + COLUMN_NINONG_TYPE + ", " +
                "COUNT(A." + COLUMN_ANGPAO_NINONG_ID + ") AS AccumulatedTimes " +
                "FROM " + TABLE_NINONG + " N " +
                "LEFT JOIN " + TABLE_ANGPAO + " A " +
                "ON N." + COLUMN_NINONG_ID + " = A." + COLUMN_ANGPAO_NINONG_ID + " " +
                "GROUP BY N." + COLUMN_NINONG_ID + ", N." + COLUMN_NINONG_NAME + ", N." + COLUMN_NINONG_TYPE + " " +
                "ORDER BY AccumulatedTimes DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int ninongIdIndex = cursor.getColumnIndex(COLUMN_NINONG_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NINONG_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_NINONG_TYPE);
                int accumulatedTimesIndex = cursor.getColumnIndex("AccumulatedTimes");

                int ninongId = cursor.getInt(ninongIdIndex);
                String name = cursor.getString(nameIndex);
                String type = cursor.getString(typeIndex);
                int accumulatedTimes = cursor.getInt(accumulatedTimesIndex);

                NinongAccumulatedTimes ninongAccumulatedTimes = new NinongAccumulatedTimes(ninongId, type, name, accumulatedTimes);
                accumulatedTimesList.add(ninongAccumulatedTimes);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return accumulatedTimesList;
    }

}


