package com.shopping.bine.einkaufsliste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shopping.bine.R;
import com.shopping.bine.database.Storage;
import com.shopping.bine.pojos.ShoppingList;

import java.util.List;



public class Lists extends Activity {

    private static final String TAG = Lists.class.getSimpleName();
    private Storage storage;
    private ListAdapter adapter;
    private List<ShoppingList> list;
    private ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createList = new Intent(Lists.this, CreateList.class);
                startActivity(createList);
            }
        });
        storage = new Storage(this);

        listview = (ListView) findViewById(R.id.shopplists);
        list =  storage.getAllShoppingLists();
        adapter = new ListAdapter(this,android.R.layout.simple_list_item_1, 0, list);
        adapter.setNotifyOnChange(true);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final ShoppingList item = (ShoppingList) parent.getItemAtPosition(position);
                Intent detailActivity = new Intent (Lists.this, ListDetail.class);
                detailActivity.putExtra("list_id", item.id);
                startActivity(detailActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        List<ShoppingList> tmpList = storage.getAllShoppingLists();
        for(ShoppingList sl : tmpList){
            list.add(sl);
        }
        adapter.notifyDataSetChanged();
     }
}
