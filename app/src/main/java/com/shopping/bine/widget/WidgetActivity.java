package com.shopping.bine.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shopping.bine.database.Storage;
import com.shopping.bine.pojos.Item;

public class WidgetActivity extends AppCompatActivity {

    final static String TAG = WidgetActivity.class.getName();
    public final static String EXTRA_HS_LIST = "homescreen_list";
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.get(EXTRA_HS_LIST) != null){
                storage = new Storage(this);
                long id = (long) bundle.get(EXTRA_HS_LIST);
                Item i = storage.getItemById(id);
                if(i.isChecked){
                    i.isChecked = false;
                }else{
                    i.isChecked = true;
                }
                storage.updateItem(i);
            }
        }
        updateWidget();
        moveTaskToBack(true);
        finish();
    }

    private void updateWidget(){
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
