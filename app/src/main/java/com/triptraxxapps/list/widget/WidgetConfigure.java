package com.triptraxxapps.list.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class WidgetConfigure extends Activity implements AdapterView.OnItemClickListener {
    private Spinner colorSpinner;
    private ListView widgetList;
    private Storage storage;
    private static final String PREFS_NAME = "com.triptraxxapps.list.widget.WidgetProvider";
    private static final String PREF_PREFIX_KEY_COLOR = "prefix_color_";
    private static final String PREF_PREFIX_KEY_LIST = "prefix_list_";
    private static final String TAG = WidgetConfigure.class.getSimpleName();
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        List<String> colList = new ArrayList<String>();
        colList.add(getResources().getString(R.string.widget_background_transparent));
        colList.add(getResources().getString(R.string.widget_background_light));
        colList.add(getResources().getString(R.string.widget_background_dark));
        colorSpinner = (Spinner) findViewById(R.id.spn_widget_color);
        ArrayAdapter<String> colAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, 0, colList);
        colorSpinner.setAdapter(colAdapter);
        String col = loadColorPref(this, mAppWidgetId);
        if(col.equals(getResources().getString(R.string.widget_background_transparent)))
            colorSpinner.setSelection(0);
        else if(col.equals(getResources().getString(R.string.widget_background_light)))
            colorSpinner.setSelection(1);
        else
            colorSpinner.setSelection(2);

        storage = new Storage(this);
        List<ShoppingList> lists = storage.getAllShoppingLists();
        widgetList = (ListView) findViewById(R.id.widget_list);
        ArrayAdapter<ShoppingList> adapter = new ArrayAdapter<ShoppingList>(this, android.R.layout.simple_list_item_activated_1, 0, lists);
        widgetList.setAdapter(adapter);
        widgetList.setOnItemClickListener(this);
        long listId = loadListPref(this, mAppWidgetId);
        if(listId != -1) {
            int pos = 0;
            for(ShoppingList l : lists){
                if(l.id == listId)
                    break;
                pos++;
            }
            widgetList.setItemChecked(pos, true);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    static void savePref(Context context, int appWidgetId, String color, long listId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY_COLOR + appWidgetId, color);
        prefs.putLong(PREF_PREFIX_KEY_LIST + appWidgetId, listId);
        prefs.commit();
    }

    static String loadColorPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY_COLOR + appWidgetId, context.getResources().getString(R.string.widget_background_transparent));
    }

    static long loadListPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_KEY_LIST + appWidgetId, -1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Context context = WidgetConfigure.this;
        final ShoppingList list = (ShoppingList) parent.getItemAtPosition(position);
        String color = colorSpinner.getSelectedItem().toString();
        savePref(context, mAppWidgetId, color, list.id);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        WidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finishAndRemoveTask();
    }
}
