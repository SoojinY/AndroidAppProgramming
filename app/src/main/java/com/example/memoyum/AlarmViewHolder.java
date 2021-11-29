package com.example.memoyum;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class AlarmViewHolder extends RecyclerView.ViewHolder{
    EditText alarmNm;
    TextView alarmDt;
    ImageView alarmDelete;

    public AlarmViewHolder(View v,
                           final AlarmAdapter.OnDeleteClickEventListener mDeleteClickListener){
        super(v);

        alarmNm = v.findViewById(R.id.alarmNm);
        alarmDt = v.findViewById(R.id.alarmDt);
        alarmDelete = v.findViewById(R.id.alarmDelete);

        alarmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mDeleteClickListener.onDeleteClick(v, position);
                }
            }
        });

    }

    public void setItem(Alarm alarm){
        alarmNm.setText(alarm.name);
        alarmDt.setText(alarm.date);
    }
}
