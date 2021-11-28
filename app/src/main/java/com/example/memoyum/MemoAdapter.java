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

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> implements OnMemoClickListener{
    ArrayList<Memo> items = new ArrayList<Memo>();
    OnMemoClickListener listener;
    ////////////////////////////////////////
    //long click
    public interface OnItemLongClickEventListener {
        void onItemLongClick(MemoAdapter.ViewHolder holder, View view, int position);
    }
    OnItemLongClickEventListener mItemLongClickListener;
    //////////////////////////////////////////////

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card, parent, false);

        return new ViewHolder(itemView, listener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.ViewHolder holder, int position) {
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

    public void setOnItemClickListener(OnMemoClickListener listener) {
        this.listener = listener;
    }

    //long click
    public void setOnItemLongClickListener(OnItemLongClickEventListener a_listener) { this.mItemLongClickListener = a_listener; }


    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        Button place;
        TextView nm;
        Button visited;
        TextView tag;


        public ViewHolder(View v,
                          final  OnMemoClickListener listener,
                          final OnItemLongClickEventListener a_itemLongClickListener) {
            super(v);

            img = v.findViewById(R.id.cardImg);
            place = v.findViewById(R.id.cardPlace);
            nm = v.findViewById(R.id.cardNm);
            visited = v.findViewById(R.id.cardVisited);
            tag =v.findViewById(R.id.cardTag);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View a_view) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        a_itemLongClickListener.onItemLongClick(ViewHolder.this, a_view, position);
                    }

                    return true;
                }
            });

        }


        public void setItem(Memo memo) {
            img.setImageResource(R.drawable.rag3);
            String[] longPlace = memo.place.split(" ");
            String showPlace = longPlace[1];
            place.setText(showPlace);
            nm.setText(memo.name);
            if(!memo.visited){
                visited.setVisibility(View.GONE);
            }
            String tagList = memo.tagsToString();
            tag.setText(tagList);
        }

    }
}
