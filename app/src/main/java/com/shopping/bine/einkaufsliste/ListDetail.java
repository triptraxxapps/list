package com.shopping.bine.einkaufsliste;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shopping.bine.R;
import com.shopping.bine.database.Storage;
import com.shopping.bine.io.ExportImportList;
import com.shopping.bine.pojos.Item;
import com.shopping.bine.pojos.ShoppingList;
import com.shopping.bine.widget.WidgetProvider;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListDetail extends AppCompatActivity implements AdapterView.OnItemClickListener, TextWatcher {

    private static final String TAG = ListDetail.class.getSimpleName();
    public static final int COLOR_GRAY = -6710887;
    private Storage storage;
    private ShoppingList shoppingList;
    private ItemAdapter adapter;
    private List<Item> items;
    private ListView listview;
    private AutoCompleteTextView actv_itemname;
    private ExportImportList exportImportList;
    private Handler exportImportHandler;
    private Runnable exportImportRunnable;
    private ShareActionProvider mShareActionProvider;
    private TextView tvItemName;
    private TextView tvItemAmount;
    private int textColor;
    private Spinner unitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_detail);;
        storage = new Storage(this);

        List<String> itemNames = storage.getItemMeta();
        actv_itemname = (AutoCompleteTextView) findViewById(R.id.new_item_name);
        actv_itemname.addTextChangedListener(this);
        actv_itemname.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, itemNames));

        Long id = this.getIntent().getLongExtra("list_id", 0);
        shoppingList = storage.getShoppingListById(id);

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setTitle(shoppingList.name);

        unitSpinner = (Spinner)findViewById(R.id.unit_spinner);
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this, R.array.unit_array,
                        android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);
        listview = (ListView) findViewById(R.id.list_items);

        items =  storage.getItemsByList(shoppingList.id);
        sortList(items);

        adapter = new ItemAdapter(this,android.R.layout.simple_list_item_1, 0, items);
        adapter.setNotifyOnChange(true);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        exportImportHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == 0){
                    Toast.makeText(ListDetail.this, "success", Toast.LENGTH_LONG).show();
                }else if(msg.arg1 == -1){
                    Toast.makeText(ListDetail.this, "error", Toast.LENGTH_LONG).show();
                }
            }
        };
        exportImportList = new ExportImportList(this, exportImportHandler);
        exportImportRunnable = new Runnable() {
            @Override
            public void run() {
                exportImportList.createExportFile(shoppingList);
            }
        };

        tvItemName = (TextView)findViewById(R.id.new_item_name);
        tvItemAmount = (TextView)findViewById(R.id.new_item_amount);
        // Hide soft keybord on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void onClickSaveItem(View v){
        String itemName = tvItemName.getText().toString();
        String itemAmount = tvItemAmount.getText().toString();
        String unit = unitSpinner.getSelectedItem().toString();
        if(itemName.trim().equals("")){
            Toast.makeText(this, R.string.itemname_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        for(Item i : items){
            if(i.name.equals(itemName)){
                Toast.makeText(this, R.string.itemname_exists, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(textColor == 0)
            textColor = COLOR_GRAY;
        long id = storage.saveItem(itemName, Float.parseFloat(itemAmount), unit, textColor,false, shoppingList.id);
        sortList(storage.getItemsByList(shoppingList.id));

        adapter.notifyDataSetChanged();
        tvItemName.setText("");
        tvItemAmount.setText("1");
        updateWidgets();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item i = (Item)parent.getItemAtPosition(position);

        if(i.isChecked){
            i.isChecked = false;
        }else{
            i.isChecked = true;
        }
        storage.updateItem(i);
        sortList(items);
        adapter.notifyDataSetChanged();
        updateWidgets();
    }

    private void sortList(List<Item> sortItems){

        items = sortList(sortItems, items);
    }

    public static List<Item> sortList(List<Item> sortItems, List<Item> itemList){
        List<Item> checked = new ArrayList<Item>();
        List<Item> unchecked = new ArrayList<Item>();
        for(Item i : sortItems){
            if(i.isChecked)
                checked.add(i);
            else
                unchecked.add(i);
        }
        itemList.clear();
        for(Item i : unchecked){
            itemList.add(i);
        }
        for(Item i : checked){
            itemList.add(i);
        }
        return itemList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_detail_menu, menu);
        MenuItem item = menu.findItem(R.id.action_share_list);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String provider = getApplicationContext().getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(ListDetail.this, provider, new File(getFilesDir(), ExportImportList.EXPORTFILENAME));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("text/*");
        mShareActionProvider.setShareIntent(shareIntent);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_list:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListDetail.this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.delete_list));
                alertDialogBuilder
                        .setMessage(getResources().getString(R.string.dialog_delete_list))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                storage.deleteShoppingList(shoppingList.id);
                                updateWidgets();
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.action_clear_list:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ListDetail.this);
                dialogBuilder.setTitle(getResources().getString(R.string.clear_list));
                dialogBuilder
                        .setMessage(getResources().getString(R.string.dialog_clear_list))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                storage.deleteItemsByList(shoppingList.id);
                                items.clear();
                                adapter.notifyDataSetChanged();
                                updateWidgets();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                break;
            case R.id.action_clear_checked:
                for(Item i : items){
                    if(i.isChecked) {
                        storage.deleteItem(i.id);
                    }
                }
                items.clear();
                List<Item> tmpList = storage.getItemsByList(shoppingList.id);
                for(Item i : tmpList){
                    items.add(i);
                }
                adapter.notifyDataSetChanged();
                updateWidgets();
                break;
            case  R.id.action_check_all:
                for(Item i : items){
                    i.isChecked = true;
                    storage.updateItem(i);
                }
                adapter.notifyDataSetChanged();
                updateWidgets();
                break;
            case R.id.action_uncheck_all:
                for(Item i : items){
                    i.isChecked = false;
                    storage.updateItem(i);
                }
                adapter.notifyDataSetChanged();
                updateWidgets();
                break;
            case R.id.action_share_list:
                if(!ExportImportList.running) {
                    exportImportHandler.post(exportImportRunnable);
                }
                break;
            case R.id.action_sort_color:
                sortItemColor();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortItemColor() {
        Collections.sort(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exportImportHandler.removeCallbacks(exportImportRunnable);
    }

    @Override
    public void afterTextChanged(Editable arg0) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    public void onClickChooseColor(View v) {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.getColorPickerView().setPaletteDrawable(getResources().getDrawable(R.drawable.palettebar));
        builder.setTitle(R.string.item_color_dialog);
        builder.setPositiveButton(getString(R.string.ok), new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                tvItemName.setTextColor(colorEnvelope.getColor());
                textColor = colorEnvelope.getColor();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateWidgets(){
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
