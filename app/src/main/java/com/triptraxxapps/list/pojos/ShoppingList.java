package com.triptraxxapps.list.pojos;

import java.util.List;

public class ShoppingList {

    public long id;
    public String name;
    public List<Item> items;
    public int position;
    public static final String LIST_TABLE = "list";
    public static final String LIST_NAME = "name";
    public static final String LIST_ID = "id";
    public static final String LIST_POSITION = "position";

    @Override
    public String toString() {
        return name;
    }
}
