package com.triptraxxapps.list.einkaufsliste;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;

public class CreateList extends AppCompatActivity {
    private static final String TAG = CreateList.class.getSimpleName();
    private Storage storage;
    private EditText newListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_list);
        storage = new Storage(this);

        newListName = (EditText) findViewById(R.id.new_list_name);
        newListName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    saveList();
                    return true;
                }
                return false;
            }
        });
    }

    private void saveList(){
        String listName = newListName.getText().toString();
        storage.saveList(listName);
        onBackPressed();
    }

    public void onClickSaveList(View view){
        saveList();
    }
}
