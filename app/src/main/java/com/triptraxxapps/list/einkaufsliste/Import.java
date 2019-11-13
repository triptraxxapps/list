package com.triptraxxapps.list.einkaufsliste;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.triptraxxapps.list.io.ExportImportList;

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
        Intent launchActivity = new Intent (this, Lists.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(launchActivity);
    }



}
