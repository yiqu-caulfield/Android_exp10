package com.example.message_application;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.ContactAdapter;
import com.example.entityclass.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends BaseActivity {
    private static final String TAG = "ContactActivity";

    public static void actionStart(Context context){
        Intent intent = new Intent(context,ContactActivity.class);
        context.startActivity(intent);
    }

    private final List<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //初始化数据
        initContact();
        //指定适配器（上下文，子项布局id，载入的数据）
        ContactAdapter adapter = new ContactAdapter(ContactActivity.this,
                R.layout.contact_item,contactList);
        ListView contactView = (ListView) findViewById(R.id.list_contact);
        contactView.setAdapter(adapter);

        //点击事件
        contactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = contactList.get(i);
                Toast.makeText(ContactActivity.this,
                        contact.getName()+":"+contact.getNumber(),
                        Toast.LENGTH_SHORT).show();

                MessageActivity.actionStart(ContactActivity.this,contact.getName(),contact.getNumber());
            }
        });
    }

    private void initContact() {
        ifPermission();
    }
    //获取读取联系人权限
    private void ifPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }
        else{
            readContacts();
        }
    }

    //从系统读取联系人数据
    private void readContacts(){
        try (Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null)) {
            //查询联系人数据
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //获取联系人姓名
                    @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //获取联系人手机号
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contact contact = new Contact(displayName,number);
                    contactList.add(contact);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回权限获取结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            } else {
                Toast.makeText(this, "the permission request was denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}