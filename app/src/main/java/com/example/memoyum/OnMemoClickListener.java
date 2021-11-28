package com.example.memoyum;

import android.view.View;

public interface OnMemoClickListener {
    void onItemClick(MemoAdapter.ViewHolder holder, View view, int position);
}
