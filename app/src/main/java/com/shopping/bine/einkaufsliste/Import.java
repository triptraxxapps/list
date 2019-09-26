package com.shopping.bine.einkaufsliste;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.shopping.bine.io.ExportImportList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Import extends AppCompatActivity {

    private static final String TAG = Import.class.getSimpleName();
    private ExportImportList exportImportList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = this.getIntent();

        Uri data = getIntent().getData();
        if(data!=null) {
            getIntent().setData(null);
            exportImportList = new ExportImportList(this, null);
            exportImportList.importData(data);
        }
        Log.d(TAG,"Main Activity starten");
        Intent launchActivity = new Intent (this, Lists.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }



}
