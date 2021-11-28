package com.example.memoyum;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MemoViewHolder extends RecyclerView.ViewHolder {
    ImageView img;
    Button place;
    TextView nm;
    Button visited;
    TextView tag;


    public MemoViewHolder(View v,
                      final MemoAdapter.OnItemClickEventListener mItemClickListener,
                      final MemoAdapter.OnItemLongClickEventListener a_itemLongClickListener) {
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

                if (mItemClickListener != null){
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View a_view) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    a_itemLongClickListener.onItemLongClick(a_view, position);
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
