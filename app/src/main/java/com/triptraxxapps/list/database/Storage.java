package com.triptraxxapps.list.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.triptraxxapps.list.pojos.*;

import java.util.ArrayList;
import java.util.List;

public class Storage extends SQLiteOpenHelper {
    private static final String TAG = Storage.class.getSimpleName();
    private static final String DATENBANK_NAME =  "einkaufsliste.db";
    private static final int DATENBANK_VERSION = 4;
    Context context;

    public static final String[] ALL_LIST_COLUMNS = new String[] {
            ShoppingList.LIST_ID,
            ShoppingList.LIST_NAME,
            ShoppingList.LIST_POSITION,
    };

    public static final String[] ALL_ITEM_META_COLUMNS = new String[] {
            Item.ITEM_META_ID,
            Item.ITEM_META_NAME,
    };

    public static final String[] ALL_ITEM_COLUMNS = new String[] {
            Item.ITEM_ID,
            Item.ITEM_NAME,
            Item.ITEM_AMOUNT,
            Item.ITEM_COLOR,
            Item.ITEM_CHECKED,
            Item.ITEM_UNIT,
            Item.ITEM_LIST,
            Item.ITEM_POSITION
    };

    public Storage(Context context) {
        super(context, DATENBANK_NAME, null, DATENBANK_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE '" + ShoppingList.LIST_TABLE + "' ('"
                + ShoppingList.LIST_ID + "' integer primary key autoincrement,'"
                + ShoppingList.LIST_NAME + "' text NOT NULL,'"
                + ShoppingList.LIST_POSITION + "' integer);");

        db.execSQL("CREATE TABLE '" + Item.ITEM_TABLE + "' ('"
                + Item.ITEM_ID + "' integer primary key autoincrement,'"
                + Item.ITEM_NAME + "' text NOT NULL,'"
                + Item.ITEM_AMOUNT + "' real,'"
                + Item.ITEM_COLOR + "' integer,'"
                + Item.ITEM_CHECKED + "' bool NOT NULL,'"
                + Item.ITEM_UNIT  + "' text,'"
                + Item.ITEM_LIST + "' integer NOT NULL REFERENCES '" + ShoppingList.LIST_TABLE + "' ('" + ShoppingList.LIST_ID + "'),'"
                + Item.ITEM_POSITION + "' integer);");

        db.execSQL("CREATE TABLE '" + Item.ITEM_META_TABLE + "' ('"
                + Item.ITEM_META_ID + "' integer primary key autoincrement,'"
                + Item.ITEM_META_NAME + "' text NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 2 && newVersion == 3) {
            db.execSQL("ALTER  TABLE '" + Item.ITEM_TABLE + "' ADD COLUMN '"
                    + Item.ITEM_UNIT + "' text;");
        }
        if(oldVersion == 3 && newVersion == 4) {
            db.execSQL("ALTER  TABLE '" + Item.ITEM_TABLE + "' ADD COLUMN '"
                    + Item.ITEM_POSITION + "' integer;");
            db.execSQL("ALTER  TABLE '" + ShoppingList.LIST_TABLE + "' ADD COLUMN '"
                    + ShoppingList.LIST_POSITION + "' integer;");
        }
    }

    public long saveList(String name){
        final ContentValues daten = new ContentValues();
        daten.put(ShoppingList.LIST_NAME, name);
        final SQLiteDatabase dbCon = getWritableDatabase();
        final long id = dbCon.insertOrThrow(ShoppingList.LIST_TABLE, null, daten);
        dbCon.close();
        return id;
    }

    public void saveListOrder(List<ShoppingList>lists){
        final SQLiteDatabase dbCon = getWritableDatabase();
        final ContentValues daten = new ContentValues();

        for(ShoppingList sl : lists){
            daten.put(ShoppingList.LIST_POSITION, lists.indexOf(sl));
            dbCon.update(ShoppingList.LIST_TABLE, daten, ShoppingList.LIST_ID + "=?", new String[]{Long.toString(sl.id)});
            daten.clear();
        }
        dbCon.close();
    }

    public List<ShoppingList> getAllShoppingLists(){
        List<ShoppingList> slists = new ArrayList<ShoppingList>();

        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.query(ShoppingList.LIST_TABLE, ALL_LIST_COLUMNS, null, null, null,null,ShoppingList.LIST_POSITION+" ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ShoppingList slist = cursorToShoppingList(cursor);
            slists.add(slist);
            cursor.moveToNext();
        }
        cursor.close();
        dbCon.close();
        return slists;
    }

    public ShoppingList getShoppingListById(long id){
        ShoppingList l = null;
        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.query(ShoppingList.LIST_TABLE, ALL_LIST_COLUMNS,
                ShoppingList.LIST_ID + " = ?", new String[]{Long.toString(id)}, null, null, null);
        if(cursor.moveToFirst()) {
            l = cursorToShoppingList(cursor);
        }
        cursor.close();
        dbCon.close();
        return l;
    }

    private ShoppingList cursorToShoppingList(Cursor cursor) {
        ShoppingList slist = new ShoppingList();
        slist.id = cursor.getLong(0);
        slist.name = cursor.getString(1);
        slist.position = cursor.getInt(2);
        return slist;
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.id = cursor.getLong(0);
        item.name = cursor.getString(1);
        item.amount = cursor.getFloat(2);
        item.color = cursor.getInt(3);
        if(cursor.getInt(4) == 1)
            item.isChecked = true;
        else
            item.isChecked = false;
        item.unit = cursor.getString(5);
        item.position = cursor.getInt(7);
        return item;
    }

    public boolean checkItemMeta(String name){
        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.query(Item.ITEM_META_TABLE, ALL_ITEM_META_COLUMNS,
                Item.ITEM_META_NAME + " = ? COLLATE NOCASE", new String[]{name}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public long saveItem(String name, float amount, String unit, int color, boolean isChecked, long shoppingListId){
        final SQLiteDatabase dbCon = getWritableDatabase();
        if(!checkItemMeta(name)){
            final ContentValues metaDaten = new ContentValues();
            metaDaten.put(Item.ITEM_META_NAME, name);
            dbCon.insertOrThrow(Item.ITEM_META_TABLE, null, metaDaten);
        }
        final ContentValues daten = new ContentValues();
        daten.put(Item.ITEM_NAME, name);
        daten.put(Item.ITEM_AMOUNT, amount);
        daten.put(Item.ITEM_UNIT, unit);
        daten.put(Item.ITEM_COLOR, color);
        daten.put(Item.ITEM_CHECKED, isChecked);
        daten.put(Item.ITEM_LIST, shoppingListId);
        daten.put(Item.ITEM_LIST, shoppingListId);
        daten.put(Item.ITEM_POSITION, "0");
        final long id = dbCon.insertOrThrow(Item.ITEM_TABLE, null, daten);
        dbCon.close();
        return id;
    }

    public void updateItem(Item item){
        ContentValues daten = new ContentValues();
        daten.put(Item.ITEM_NAME, item.name);
        daten.put(Item.ITEM_AMOUNT, item.amount);
        daten.put(Item.ITEM_UNIT, item.unit);
        daten.put(Item.ITEM_COLOR, item.color);
        daten.put(Item.ITEM_CHECKED, item.isChecked);

        SQLiteDatabase dbCon = getWritableDatabase();
        dbCon.update(Item.ITEM_TABLE, daten, Item.ITEM_ID + "=?", new String[]{Long.toString(item.id)});
        dbCon.close();
    }

    public List<Item> getItemsByList(long listId){
        List<Item> items = new ArrayList<Item>();

        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.query(Item.ITEM_TABLE, ALL_ITEM_COLUMNS,
                Item.ITEM_LIST + " = ?", new String[]{Long.toString(listId)}, null, null, Item.ITEM_POSITION+" ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        dbCon.close();
        return items;
    }

    public Item getItemById(long id){
        Item i = null;
        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.query(Item.ITEM_TABLE, ALL_ITEM_COLUMNS,
                Item.ITEM_ID + " = ?", new String[]{Long.toString(id)}, null, null, null);
        if(cursor.moveToFirst()) {
            i = cursorToItem(cursor);
        }
        cursor.close();
        dbCon.close();
        return i;
    }

    public void deleteShoppingList(long listId){
        final SQLiteDatabase dbCon = getWritableDatabase();
        dbCon.delete(Item.ITEM_TABLE, Item.ITEM_LIST + " = ?", new String[]{Long.toString(listId)});
        dbCon.delete(ShoppingList.LIST_TABLE, ShoppingList.LIST_ID + " = ?", new String[]{Long.toString(listId)});
        dbCon.close();
    }

    public void deleteItemsByList(long listId){
        final SQLiteDatabase dbCon = getWritableDatabase();
        dbCon.delete(Item.ITEM_TABLE, Item.ITEM_LIST + " = ?", new String[]{Long.toString(listId)});
        dbCon.close();
    }

    public void deleteItem(long itemId){
        final SQLiteDatabase dbCon = getWritableDatabase();
        dbCon.delete(Item.ITEM_TABLE, Item.ITEM_ID + " = ?", new String[]{Long.toString(itemId)});
        dbCon.close();
    }

    public List<String> getItemMeta(){
        List<String> itemNames = new ArrayList<String>();
        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.query(Item.ITEM_META_TABLE, ALL_ITEM_META_COLUMNS, null, null, null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            itemNames.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        dbCon.close();
        return itemNames;
    }

    public int getUncheckedListItems(long listId){
        List<Item> items = new ArrayList<Item>();

        SQLiteDatabase dbCon = getReadableDatabase();
        Cursor cursor = dbCon.rawQuery(Item.UNCHECKED_ITEM_QUERY, new String[]{String.valueOf(listId)});
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        dbCon.close();
        return count;
    }

    public void saveItemOrder(List<Item> items) {
        final SQLiteDatabase dbCon = getWritableDatabase();
        final ContentValues daten = new ContentValues();

        for(Item i : items){
            daten.put(Item.ITEM_POSITION, items.indexOf(i));
            dbCon.update(Item.ITEM_TABLE, daten, Item.ITEM_ID + "=?", new String[]{Long.toString(i.id)});
            daten.clear();
        }
        dbCon.close();
    }

    public void updateList(ShoppingList shoppingList) {
        final ContentValues daten = new ContentValues();
        daten.put(ShoppingList.LIST_NAME, shoppingList.name);
        final SQLiteDatabase dbCon = getWritableDatabase();
        dbCon.update(ShoppingList.LIST_TABLE, daten, ShoppingList.LIST_ID + "=?", new String[]{Long.toString(shoppingList.id)});
        dbCon.close();
    }
}
