package com.triptraxxapps.list.einkaufsliste;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.ShoppingList;

import java.util.List;

public class ListOnDragListener implements View.OnDragListener {

    private static final String TAG = ListOnDragListener.class.getSimpleName();
    ShoppingList list;
    Context context;
    Storage storage;

    ListOnDragListener(Context context, ShoppingList list){
        this.list = list;
        this.context = context;
        storage = new Storage(context);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundResource(R.color.background_dragdrop);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundResource(R.color.background);
                break;
            case DragEvent.ACTION_DROP:
                ShoppingList sl = (ShoppingList)event.getLocalState();

                ListView newParent = (ListView)v.getParent();
                ListAdapter destAdapter = (ListAdapter)(newParent.getAdapter());
                List<ShoppingList> destList = destAdapter.getList();

                int removeLocation = destList.indexOf(sl);
                int insertLocation = destList.indexOf(list);

                if(removeLocation != insertLocation){
                    if(destList.remove(sl)){
                        destList.add(insertLocation, sl);
                    }
                    destAdapter.notifyDataSetChanged();
                    storage.saveListOrder(destList);
                }
                v.setBackgroundResource(R.color.background);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundResource(R.color.background);
            default:
                break;
        }
        return true;
    }
}
