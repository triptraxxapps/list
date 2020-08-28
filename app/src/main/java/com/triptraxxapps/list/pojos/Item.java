package com.triptraxxapps.list.pojos;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class Item implements Comparable<Item>{

    public long id;
    public String name;
    public float amount;
    public String unit;
    public boolean isChecked;
    public int color;
    public int position;
    public ShoppingList shoppingList;
    public static final String ITEM_TABLE = "item";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_ID = "id";
    public static final String ITEM_AMOUNT = "amount";
    public static final String ITEM_UNIT = "unit";
    public static final String ITEM_COLOR = "color";
    public static final String ITEM_POSITION = "position";
    public static final String ITEM_CHECKED = "checked";
    public static final String ITEM_LIST = "list";
    public static final String ITEM_META_TABLE = "item_meta";
    public static final String ITEM_META_ID = "id";
    public static final String ITEM_META_NAME = "name";
    public static final String UNCHECKED_ITEM_QUERY = "select count(*) from " + ITEM_TABLE + " where " + ITEM_LIST + " = ? and checked = 0;";


    @Override
    public String toString() {
        return amount + " " + unit + " x " + name;
    }

    public static Comparator<Item> NameComparator = new Comparator<Item>() {
        @Override
        public int compare(Item i1, Item i2) {
            return i1.name.toLowerCase().compareTo(i2.name.toLowerCase());
        }
    };

    @Override
    public int compareTo(@NonNull Item compareItem) {
        return this.color - compareItem.color;
    }
}
