package com.shopping.bine.einkaufsliste;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shopping.bine.R;
import com.shopping.bine.pojos.Item;

import java.util.List;

/**
 * Created by bine on 13.12.18.
 */

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
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout_item, parent, false);
        TextView tvName = (TextView) rowView.findViewById(R.id.itemname_row);
        TextView tvAmount = (TextView) rowView.findViewById(R.id.itemamount_row);
        TextView tvUnit = (TextView) rowView.findViewById(R.id.itemunit_row);

        Item i = objects.get(position);
        if(i.isChecked){
            tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        tvName.setText(i.name);
        if(i.color != 0)
            tvName.setTextColor(i.color);
        tvAmount.setText(getStringAmount(i.amount));
        tvUnit.setText(i.unit);
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

}
