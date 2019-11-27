package com.triptraxxapps.list.einkaufsliste;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.widget.ListView;

import com.triptraxxapps.list.R;
import com.triptraxxapps.list.database.Storage;
import com.triptraxxapps.list.pojos.Item;
import com.triptraxxapps.list.pojos.ShoppingList;

import java.util.List;

public class ItemOnDragListener implements View.OnDragListener {

    private static final String TAG = ItemOnDragListener.class.getSimpleName();
    Item item;
    Context context;
    Storage storage;

    ItemOnDragListener(Context context, Item item){
        this.item = item;
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
                Item it = (Item)event.getLocalState();

                ListView newParent = (ListView)v.getParent();
                ItemAdapter destAdapter = (ItemAdapter)(newParent.getAdapter());
                List<Item> destList = destAdapter.getList();

                int removeLocation = destList.indexOf(it);
                int insertLocation = destList.indexOf(item);

                if(removeLocation != insertLocation){
                    if(destList.remove(it)){
                        destList.add(insertLocation, it);
                    }
                    destAdapter.notifyDataSetChanged();
                    storage.saveItemOrder(destList);
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
