package com.triptraxxapps.list.einkaufsliste;

import android.content.ClipData;
import android.content.Context;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
        View tmpView = convertView;
        if(tmpView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            tmpView = inflater.inflate(R.layout.rowlayout_list, parent, false);
        }
        final View rowView = tmpView;
        TextView tvListName = (TextView) rowView.findViewById(R.id.list_name_row);
        ShoppingList list = objects.get(position);
        int count = storage.getUncheckedListItems(list.id);
        TextView tvItemCount = (TextView) rowView.findViewById(R.id.item_count_row);
        tvItemCount.setText(count+"");
        tvListName.setText(list.name);

        ImageButton btn = rowView.findViewById(R.id.move_list);
        btn.setTag(list);
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ShoppingList list = (ShoppingList) v.getTag();
                Log.d(TAG, "Drag starten " + list.name);
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(rowView);
                v.startDrag(data, shadowBuilder, list, 0);


                return true;
            }
        });
        rowView.setOnDragListener(new ListOnDragListener(context, list));
        return rowView;
    }

    public List<ShoppingList> getList(){
        return objects;
    }

}
