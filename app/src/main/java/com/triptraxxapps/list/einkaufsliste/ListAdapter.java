package com.triptraxxapps.list.einkaufsliste;

import android.content.Context;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.ShoppingList;

import java.util.List;

public class ListAdapter extends ArrayAdapter {
    private static final String TAG = ListAdapter.class.getSimpleName();
    private final Context context;
    private final List<ShoppingList> objects;
    private Storage storage;

    public ListAdapter(Context context, int resource, int textViewResourceId, List<ShoppingList> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        storage = new Storage(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout_list, parent, false);
        TextView tvListName = (TextView) rowView.findViewById(R.id.list_name_row);
        ShoppingList list = objects.get(position);
        int count = storage.getUncheckedListItems(list.id);
        TextView tvItemCount = (TextView) rowView.findViewById(R.id.item_count_row);
        tvItemCount.setText(count+"");
        tvListName.setText(list.name);
        return rowView;
    }

}
