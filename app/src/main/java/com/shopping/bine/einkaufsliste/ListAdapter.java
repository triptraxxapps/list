package com.shopping.bine.einkaufsliste;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shopping.bine.R;
import com.shopping.bine.database.Storage;
import com.shopping.bine.pojos.Item;
import com.shopping.bine.pojos.ShoppingList;

import java.util.List;

/**
 * Created by bine on 13.12.18.
 */

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
