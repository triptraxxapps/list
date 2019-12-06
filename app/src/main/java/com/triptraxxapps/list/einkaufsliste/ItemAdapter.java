package com.triptraxxapps.list.einkaufsliste;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.pojos.Item;
import com.triptraxxapps.list.pojos.ShoppingList;

import java.util.List;


public class ItemAdapter extends ArrayAdapter {
    private static final String TAG = ItemAdapter.class.getSimpleName();
    private final Context context;
    private final List<Item> objects;

    public ItemAdapter(Context context, int resource, int textViewResourceId, List<Item> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tmpView = convertView;
        if(tmpView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            tmpView = inflater.inflate(R.layout.rowlayout_item, parent, false);
        }
        final View rowView = tmpView;
        TextView tvName = (TextView) rowView.findViewById(R.id.itemname_row);
        TextView tvAmount = (TextView) rowView.findViewById(R.id.itemamount_row);
        TextView tvUnit = (TextView) rowView.findViewById(R.id.itemunit_row);
        ImageButton button = rowView.findViewById(R.id.delete_item);
        ImageButton moveBtn = rowView.findViewById(R.id.move_item);

        Item i = objects.get(position);
        button.setTag(i);
        moveBtn.setTag(i);
        if(i.isChecked){
            tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            tvName.setPaintFlags(0);
        }
        tvName.setText(i.name);
        if(i.color != 0)
            tvName.setTextColor(i.color);
        tvAmount.setText(getStringAmount(i.amount));
        tvUnit.setText(i.unit);
        moveBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Item it = (Item) v.getTag();
                Log.d(TAG, "Drag starten " + it.name);
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(rowView);
                v.startDrag(data, shadowBuilder, it, 0);

                return true;
            }
        });
        rowView.setOnDragListener(new ItemOnDragListener(context, i));

        return rowView;
    }

    public String getStringAmount(float f){
        Float tF = new Float(f);
        String str = tF.toString();
        int point = 0;
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == '.'){
                point = i;
                int ix = Integer.valueOf(str.substring(i+1,str.length()));
                if(ix > 0){
                    return str;
                }
            }
        }
        return str.substring(0, point);
    }

    public List<Item> getList(){
        return objects;
    }

}
