package com.letstellastory.android.letstellastory;

import android.provider.BaseColumns;

/**
 * Created by dozie on 2017-07-01.
 */

public class DBStory {
        public static final String DB_NAME = "Story DB";
        public static final int DB_VERSION = 1;

public class DBMain implements BaseColumns {
    public static final String TABLE = "stories";
    public static final String COL_TITLE = "title";
    public static final String COL_GENRE = "genre";
    public static final String COL_STORY = "story";


}

}

