package com.example.memoyum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmViewHolder>{
    ArrayList<Alarm> items = new ArrayList<Alarm>();

    public interface OnDeleteClickEventListener{
        void onDeleteClick(View v,int position);
    }

    private OnDeleteClickEventListener mDeleteClickListener;


    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.alarm, parent, false);

        return new AlarmViewHolder(itemView, mDeleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Alarm item){items.add(item);}
    public void setItems(ArrayList<Alarm> items) {
        this.items = items;
    }
    public Alarm getItem(int position) {
        return items.get(position);
    }
    public void setItem(int position, Alarm item) {
        items.set(position, item);
    }


    public void setOnDeleteClickListener(OnDeleteClickEventListener listener){
        this.mDeleteClickListener = listener;
    }

}
