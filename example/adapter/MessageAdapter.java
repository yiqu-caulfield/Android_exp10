package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entityclass.Message;
import com.example.message_application.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private final int resourceId;

    public MessageAdapter(Context context, int textViewResourceId, List<Message> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @SuppressLint("RtlHardcoded")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //获取当前项的contact实例
        Message message = getItem(position);
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView msg = (TextView) view.findViewById(R.id.text_item_msg);
        msg.setText(message.getMsg());
        if (message.getType().equals("已发出")){
            msg.setGravity(Gravity.END);
        }
        else {
            msg.setGravity(Gravity.START);
        }
        return view;
    }
}
