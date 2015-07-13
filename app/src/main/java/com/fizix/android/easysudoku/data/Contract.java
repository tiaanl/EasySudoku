package com.fizix.android.easysudoku.data;


import android.provider.BaseColumns;

public class Contract {

    public static final class Boards implements BaseColumns {

        // The name of the table.
        public static final String TABLE_NAME = "boards";

        // Columns
        public static final String COL_DIFFICULTY = "difficulty";
        public static final String COL_SEL_BLOCK_X = "sel_block_x";
        public static final String COL_SEL_BLOCK_Y = "sel_block_y";
        public static final String COL_SEL_NUMBER = "sel_number";

    }

    public static final class Blocks implements BaseColumns {

        // The name of the table.
        public static final String TABLE_NAME = "blocks";

        // Columns
        public static final String COL_BOARD_ID = "board_id";
        public static final String COL_INDEX = "idx";
        public static final String COL_NUMBER = "number";

    }

}
