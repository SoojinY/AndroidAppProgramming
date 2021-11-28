package com.example.memoyum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoViewHolder>{
    ArrayList<Memo> items = new ArrayList<Memo>();

    ////////////////////////////////////////
    public interface OnItemClickEventListener {
        void onItemClick(View a_view, int a_position);
    }

    public interface OnItemLongClickEventListener {
        void onItemLongClick(View view, int position);
    }

    private OnItemClickEventListener mItemClickListener;
    private OnItemLongClickEventListener mItemLongClickListener;
    //////////////////////////////////////////////


    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card, parent, false);

        return new MemoViewHolder(itemView, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(Memo item) {
        items.add(item);
    }

    public void setItems(ArrayList<Memo> items) {
        this.items = items;
    }

    public Memo getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Memo item) {
        items.set(position, item);
    }

    public void setOnItemClickListener(OnItemClickEventListener listener) {
        this.mItemClickListener = listener;
    }

    //long click
    public void setOnItemLongClickListener(OnItemLongClickEventListener listener) {
        this.mItemLongClickListener = listener;
    }


}
