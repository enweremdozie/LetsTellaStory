package com.letstellastory.android.letstellastory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dozie on 2017-06-19.
 */

public class Horror extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new Horror_Fragment())
                .commit();
    }
}
