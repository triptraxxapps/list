package com.shopping.bine.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.shopping.bine.R;
import com.shopping.bine.database.Storage;
import com.shopping.bine.einkaufsliste.ListDetail;
import com.shopping.bine.pojos.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    static final String TAG = ListViewRemoteViewsFactory.class.getName();
    private Context mContext;
    private List<Item> records;
    private Storage storage;
    private long listId;
    private static final int COLOR_BLUE = -13551186;
    private static final int COLOR_BRIGHT_BLUE = -16720385;

    private int appWidgetId;
    private String color;

    public ListViewRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        storage = new Storage(mContext);
        listId = intent.getLongExtra(WidgetProvider.EXTRA_APPWIDGET_LIST, -1);
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        color = intent.getStringExtra("color");
    }
    public void onCreate() {
        fetchData();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.rowlayout_widget);
        Item i = records.get(position);

        if(i.isChecked)
            rv.setInt(R.id.widget_item, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        else
            rv.setInt(R.id.widget_item, "setPaintFlags", 0);
        if(i.color != 0)
            if(i.color == COLOR_BLUE && color.equals(mContext.getResources().getString(R.string.widget_background_dark)))
                rv.setInt(R.id.widget_item, "setTextColor", COLOR_BRIGHT_BLUE);
            else
                rv.setInt(R.id.widget_item, "setTextColor", i.color);
        else
            rv.setInt(R.id.widget_item, "setTextColor", ListDetail.COLOR_GRAY);

        rv.setTextViewText(R.id.widget_item, i.name);
        Bundle extras = new Bundle();
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(WidgetService.EXTRA_HS_LIST, i.id);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
        return rv;
    }

    public int getCount() {
        return records.size();
    }

    private void fetchData(){
        List<Item> items = storage.getItemsByList(listId);
        Collections.sort(items);
        records = ListDetail.sortList(items, new ArrayList<Item>());
    }

    public void onDataSetChanged() {
        fetchData();
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public void onDestroy() {
        records.clear();
    }

    public boolean hasStableIds() {
        return true;
    }

    public RemoteViews getLoadingView() {
        return null;
    }
}
