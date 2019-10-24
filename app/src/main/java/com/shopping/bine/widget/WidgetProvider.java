package com.shopping.bine.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.shopping.bine.R;
import com.shopping.bine.database.Storage;
import com.shopping.bine.einkaufsliste.Lists;

public class WidgetProvider extends AppWidgetProvider {

    final static String TAG = WidgetProvider.class.getName();
    final static String EXTRA_APPWIDGET_LIST = "appwidgetList";


    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        Log.d(TAG, "onRestored");
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.d(TAG, "onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Storage storage = new Storage(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        String color = WidgetConfigure.loadColorPref(context, appWidgetId);
        long listId = WidgetConfigure.loadListPref(context, appWidgetId);
        if(storage.getShoppingListById(listId) == null || storage.getItemsByList(listId).size() == 0){
            views.setInt(R.id.widget_msg, "setVisibility", View.VISIBLE);
        }else {
            views.setInt(R.id.widget_msg, "setVisibility", View.GONE);
            Intent listIntent = new Intent(context, ListViewWidgetService.class);
            listIntent.putExtra(EXTRA_APPWIDGET_LIST, listId);
            listIntent.putExtra("color", color);
            listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.hs_list, listIntent);
        }
        if(context.getResources().getString(R.string.widget_background_light).equals(color)) {
            views.setInt(R.id.widget_root, "setBackgroundResource", R.color.widgetLight);
//            views.setInt(R.id.to_list, "setBackgroundResource", R.color.widgetLight);
        }else if(context.getResources().getString(R.string.widget_background_dark).equals(color)) {
            views.setInt(R.id.widget_root, "setBackgroundResource", R.color.widgetDark);
//            views.setInt(R.id.to_list, "setBackgroundResource", R.color.widgetDark);
        }

        //go to app when button is pressed
        Intent intent = new Intent(context, Lists.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);
        //strike item when item is klicked
        Intent serviceIntent = new Intent(context, WidgetService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.hs_list, pi);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive ");
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") || intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS")) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.hs_list);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            for(int i = 0; i < appWidgetIds.length; i++){
                updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
            }
//            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }

}
