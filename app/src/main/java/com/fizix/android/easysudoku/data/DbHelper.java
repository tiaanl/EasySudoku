package com.fizix.android.easysudoku.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fizix.android.easysudoku.data.Contract.Boards;
import com.fizix.android.easysudoku.data.Contract.Blocks;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EasySudoku.db";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table holding the boards.
        final String SQL_CREATE_BOARDS_TABLE = "CREATE TABLE " + Boards.TABLE_NAME + " (" +
                Boards._ID + " INTEGER PRIMARY KEY, " +
                Boards.COL_DIFFICULTY + " INTEGER NOT NULL UNIQUE, " +
                Boards.COL_SEL_BLOCK_X + " INTEGER NOT NULL, " +
                Boards.COL_SEL_BLOCK_Y + " INTEGER NOT NULL, " +
                Boards.COL_SEL_NUMBER + " INTEGER NOT NULL, " +
                "UNIQUE(" + Boards.COL_DIFFICULTY + ") ON CONFLICT REPLACE" +
                ")";

        // Create the table holding the blocks.
        final String SQL_CREATE_BLOCKS_TABLE = "CREATE TABLE " + Blocks.TABLE_NAME + " (" +
                Blocks._ID + " INTEGER PRIMARY KEY, " +
                Blocks.COL_BOARD_ID + " INTEGER NOT NULL, " +
                Blocks.COL_INDEX + " INTEGER NOT NULL, " +
                Blocks.COL_NUMBER + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + Blocks.COL_BOARD_ID + ") REFERENCES " + Boards.TABLE_NAME + " (" + Boards._ID + "), " +
                "UNIQUE(" + Blocks.COL_BOARD_ID + ", " + Blocks.COL_INDEX + ") ON CONFLICT REPLACE" +
                ")";

        db.execSQL(SQL_CREATE_BOARDS_TABLE);
        db.execSQL(SQL_CREATE_BLOCKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP_TABLE_IF EXISTS " + Blocks.TABLE_NAME);
        onCreate(db);
    }

}
