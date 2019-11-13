package com.triptraxxapps.list.einkaufsliste;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.ShoppingList;
import com.triptraxxapps.list.widget.WidgetProvider;

import java.util.List;



public class Lists extends AppCompatActivity {

    private static final String TAG = Lists.class.getSimpleName();
    private Storage storage;
    private ListAdapter adapter;
    private List<ShoppingList> list;
    private ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists);

        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.lists_title);
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent, null)));

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
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ShoppingList item = (ShoppingList) parent.getItemAtPosition(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Lists.this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.delete_list));
                alertDialogBuilder
                        .setMessage(getResources().getString(R.string.dialog_delete_list))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                storage.deleteShoppingList(item.id);
                                adapter.remove(item);
                                adapter.notifyDataSetChanged();
                                updateWidgets();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    private void updateWidgets(){
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
