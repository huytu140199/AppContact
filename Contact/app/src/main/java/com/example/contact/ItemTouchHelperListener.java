package com.example.contact;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder);
}
