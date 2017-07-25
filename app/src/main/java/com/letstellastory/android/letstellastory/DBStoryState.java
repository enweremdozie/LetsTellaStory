package com.letstellastory.android.letstellastory;

import android.provider.BaseColumns;

/**
 * Created by dozie on 2017-07-01.
 */

public class DBStoryState {
    public static final String DB_NAME = "Story DB";
    public static final int DB_VERSION = 1;

    public class DBMain implements BaseColumns {
        public static final String TABLE = "state";
        public static final String COL_POS = "position";


    }

}