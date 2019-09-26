package com.shopping.bine.io;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.shopping.bine.database.Storage;
import com.shopping.bine.pojos.Item;
import com.shopping.bine.pojos.ShoppingList;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ExportImportList {
    public static final String TAG = ExportImportList.class.getSimpleName();
    private Storage storage;
    private static Context context;
    private Gson gson;
    public static String EXPORTFILENAME = "listenexport.json";
    private Handler handler;
    public static boolean running = false;

    public ExportImportList(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
        storage = new Storage(context);
        gson = new Gson();
    }

    public boolean createExportFile(ShoppingList sl){
        running = true;
        sl.items = storage.getItemsByList(sl.id);
        String result = gson.toJson(sl);
        File exportFile;
        OutputStream out;
        try {
            exportFile = new File(context.getFilesDir(), EXPORTFILENAME);
            if(exportFile.exists()){
                exportFile.delete();
            }
            exportFile.createNewFile();
            out = context.openFileOutput(EXPORTFILENAME, Context.MODE_PRIVATE);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
        return true;
    }

    public void importData(Uri data) {
        final String scheme = data.getScheme();

        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                ContentResolver cr = context.getContentResolver();
                InputStream is = cr.openInputStream(data);
                if (is == null) {
                    Log.d(TAG, "InputStream is null");
                    return;
                }

                StringBuffer buf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str;
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    }
                }
                is.close();
                ShoppingList sl = gson.fromJson(buf.toString(), ShoppingList.class);
                long id = storage.saveList(sl.name);
                for(Item i : sl.items){
                    storage.saveItem(i.name, i.amount, i.unit, i.color, i.isChecked, id);
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }


}
