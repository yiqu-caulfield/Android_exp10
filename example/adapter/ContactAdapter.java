package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.entityclass.Contact;
import com.example.message_application.R;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private final int resourceId;

    public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //获取当前项的contact实例
        Contact contact = getItem(position);
        @SuppressLint("ViewHolder")
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView name = (TextView) view.findViewById(R.id.contact_name);
        TextView number = (TextView) view.findViewById(R.id.contact_number);
        name.setText(contact.getName());
        number.setText(contact.getNumber());
        return view;
    }
}
