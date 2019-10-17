package com.shopping.bine.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.shopping.bine.R;
import com.shopping.bine.einkaufsliste.Lists;

public class WidgetProvider extends AppWidgetProvider {

    final static String TAG = WidgetProvider.class.getName();
    final static String EXTRA_APPWIDGET_LIST = "appwidgetList";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        String color = WidgetConfigure.loadColorPref(context, appWidgetId);
        long listId = WidgetConfigure.loadListPref(context, appWidgetId);

        Intent listIntent = new Intent(context, ListViewWidgetService.class);
        listIntent.putExtra(EXTRA_APPWIDGET_LIST, listId);
        listIntent.putExtra("color", color);
        listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));

        if(context.getResources().getString(R.string.widget_background_light).equals(color)) {
            views.setInt(R.id.widget_root, "setBackgroundResource", R.color.widgetLight);
            views.setInt(R.id.to_list, "setBackgroundResource", R.color.widgetLight);
        }else if(context.getResources().getString(R.string.widget_background_dark).equals(color)) {
            views.setInt(R.id.widget_root, "setBackgroundResource", R.color.widgetDark);
            views.setInt(R.id.to_list, "setBackgroundResource", R.color.widgetDark);
        }
        views.setRemoteAdapter(R.id.hs_list, listIntent);

        //go to app when button is pressed
        Intent intent = new Intent(context, Lists.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.to_list, pendingIntent);
        //strike item when item is klicked
        Intent startActivityIntent = new Intent(context,WidgetActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.hs_list, startActivityPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.hs_list);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }
}
