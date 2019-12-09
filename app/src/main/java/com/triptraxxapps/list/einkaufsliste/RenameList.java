package com.triptraxxapps.list.einkaufsliste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.ShoppingList;

public class RenameList extends AppCompatActivity {
    private static final String TAG = RenameList.class.getSimpleName();
    private Storage storage;
    private EditText listName;
    private ShoppingList shoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rename_list);

        storage = new Storage(this);
        Long id = this.getIntent().getLongExtra("list_id", 0);
        shoppingList = storage.getShoppingListById(id);
        listName = findViewById(R.id.rename_list_name);
        listName.setText(shoppingList.name);
        listName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    renameList();
                    return true;
                }
                return false;
            }
        });
    }

    private void renameList(){
        shoppingList.name = listName.getText().toString();
        storage.updateList(shoppingList);
        Intent intent=new Intent();
        setResult(ListDetail.RENAME_LIST_RQ, intent);
        finish();
    }

    public void onClickRenameList(View view) {
        renameList();
    }
}
