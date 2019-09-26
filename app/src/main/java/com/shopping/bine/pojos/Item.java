package com.shopping.bine.pojos;

import android.support.annotation.NonNull;

/**
 * Created by bine on 06.12.18.
 */

public class Item implements Comparable<Item>{

    public long id;
    public String name;
    public float amount;
    public String unit;
    public boolean isChecked;
    public int color;
    public ShoppingList shoppingList;
    public static final String ITEM_TABLE = "item";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_ID = "id";
    public static final String ITEM_AMOUNT = "amount";
    public static final String ITEM_UNIT = "unit";
    public static final String ITEM_COLOR = "color";
    public static final String ITEM_CHECKED = "checked";
    public static final String ITEM_LIST = "list";
    public static final String ITEM_META_TABLE = "item_meta";
    public static final String ITEM_META_ID = "id";
    public static final String ITEM_META_NAME = "name";



    @Override
    public String toString() {
        return amount + " " + unit + " x " + name;
    }

    @Override
    public int compareTo(@NonNull Item compareItem) {
        return this.color - compareItem.color;
    }
}
