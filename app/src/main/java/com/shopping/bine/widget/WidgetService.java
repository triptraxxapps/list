package com.shopping.bine.widget;

import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.shopping.bine.database.Storage;
import com.shopping.bine.pojos.Item;

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
            int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
            String channelId = WidgetService.class.getName();
            startForeground(NOTIFICATION_ID, new Notification.Builder(this, channelId).build());
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
    }

    private void updateWidget(){
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
