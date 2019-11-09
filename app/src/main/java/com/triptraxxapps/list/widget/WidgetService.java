package com.triptraxxapps.list.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.Item;

public class WidgetService extends Service {

    private static final String TAG = WidgetService.class.getSimpleName();
    public final static String EXTRA_HS_LIST = "homescreen_list";
    private Storage storage;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = WidgetService.class.getName();
            String channelName = "List Widget Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleEvent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleEvent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if(bundle.get(EXTRA_HS_LIST) != null){
                long id = (long) bundle.get(EXTRA_HS_LIST);
                storage = new Storage(this);
                Item i = storage.getItemById(id);
                if(i != null) {
                    if (i.isChecked) {
                        i.isChecked = false;
                    } else {
                        i.isChecked = true;
                    }
                    storage.updateItem(i);
                }
            }
        }
        updateWidget();
        stopSelf();
    }

    private void updateWidget(){
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

}
