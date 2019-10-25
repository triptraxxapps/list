package com.shopping.bine.einkaufsliste;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shopping.bine.R;
import com.shopping.bine.database.Storage;

public class CreateList extends AppCompatActivity {
    private static final String TAG = CreateList.class.getSimpleName();
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_list);
        storage = new Storage(this);
    }

    public void onClickSaveList(View view){
        String listName = ((TextView)findViewById(R.id.new_list_name)).getText().toString();
        Log.i(TAG, "save list "+listName);
        storage.saveList(listName);
        onBackPressed();
    }

}
