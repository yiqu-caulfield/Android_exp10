package com.example.message_application;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.adapter.ContactAdapter;
import com.example.adapter.MessageAdapter;
import com.example.entityclass.Contact;
import com.example.entityclass.Message;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends BaseActivity{
    //log t + tab
    private static final String TAG = "MessageActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    TextView textView1;
    private final List<Message> messageList = new ArrayList<>();

    public static void actionStart(Context context,String name,String number){
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("number",number);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //载入自定义布局
        setContentView(R.layout.activity_message);
        //隐藏系统自带标题栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        Intent intent = getIntent();
        TextView textView = findViewById(R.id.text_title);
        String name = intent.getStringExtra("name");
        String number = intent.getStringExtra("number");
        textView.setText(name);

        EditText msg = findViewById(R.id.edittext_msg);
        ImageButton titleBack = findViewById(R.id.button_back);
        ImageButton titleEdit = findViewById(R.id.button_edit);
        ImageButton titlePhone = findViewById(R.id.button_phone);
        ImageButton addFile = findViewById(R.id.button_add);
        ImageButton emoji = findViewById(R.id.button_emoji);
        ImageButton sendMsg = findViewById(R.id.button_send);

        //title逻辑部分
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titlePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+number));
                startActivity(intent);
            }
        });
        titleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MessageActivity.this,
                        "You clicked Edit button",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //短信对话部分

        //初始化数据
        initMessage(number);
        //指定适配器（上下文，子项布局id，载入的数据）
        MessageAdapter adapter = new MessageAdapter(MessageActivity.this,
                R.layout.message_item,messageList);
        ListView messageView = (ListView) findViewById(R.id.list_message);
        messageView.setAdapter(adapter);

        //edittext逻辑部分
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送调用相机请求
                Toast.makeText(MessageActivity.this,
                        "Mission Successful",
                        Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            }
        });
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开表情包
                //打开系统相册
            }
        });
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //动态申请发送短信权限
                if (ActivityCompat.checkSelfPermission(MessageActivity.this,
                        Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessageActivity.this,
                            new String[]{Manifest.permission.SEND_SMS}, 103);
                    return;
                }
                //发送信息
                SmsManager smsManager = SmsManager.getDefault();
                //sendTextMessage(String destinationAddress, String scAddress,
                // String text, PendingIntent sentIntent, PendingIntent deliveryIntent)
                smsManager.sendTextMessage(number, null,
                        msg.getText().toString(), null, null);
                Toast.makeText(MessageActivity.this,
                        "Mission Successful",
                        Toast.LENGTH_SHORT).show();
                //刷新界面
                MessageActivity.actionStart(MessageActivity.this,name,number);
                finish();

            }
        });
    }

    private void initMessage(String number) {
        queryMessageLog(number);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //创建保存图像的文件
    private File createImageFile() throws IOException {
        // Create an image file name
        // 定义一个时间戳，以时间戳为名避免重复
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        String imageFileName = "camera_" + timeStamp + "_";
        File path = getExternalFilesDir(DIRECTORY_PICTURES);//应用私有存储
        //Path = /storage/emulated/0/Android/data/com.example.message_application/files/Pictures
        File storageDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);//公共存储
        ///storageDir = storage/emulated/0/Pictures
        Log.e(TAG, "save "+imageFileName+" in "+storageDir);
        //注意:用户卸载该应用后，在 getExternalFilesDir() 或 getFilesDir() 提供的目录中保存的文件会被删除。
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.e(TAG, "currentPhotoPath "+currentPhotoPath);
        return image;
    }

    //请求调用系统摄像机
    /*
     *  getUriForFile(Context, String, File)  返回 content:// URI
     * 对于以 Android 7.0（API 级别 24）及更高版本为目标平台的最新应用，
     * 跨越软件包边界传递 file:// URI 会导致出现 FileUriExposedException。
     * 因此，更推荐更通用的方法FileProvider，使用 FileProvider 存储图片。
     * */
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Log.e(TAG, "enter dispatchTakePictureIntent()");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // 确保有可用的camera接受调用，避免不可用的startActivityForResult导致程序崩溃
        // 官网的if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        // 在android11以上不起作用，应使用
        // if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.message_application.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //查询历史短信
    private void queryMessageLog(String number) {
        //动态申请读取联系人权限
        if (ActivityCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MessageActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 101);
            return;
        }
        //动态申请读取短信权限
        if (ActivityCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MessageActivity.this,
                    new String[]{Manifest.permission.READ_SMS}, 102);
            return;
        }
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.CONTENT_URI, new String[]{
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE,
        }, Telephony.Sms.ADDRESS + "= ?", new String[]{number},
                "date ASC");
        //select where ADDRESS = number，DESC按时间倒序排列，限制显示前五条短信,ASC升序排序
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SMSMessage message = new SMSMessage();
                message.address = cursor.getString(0);
                message.body = cursor.getString(1);
                message.date = cursor.getLong(2);
                message.read = getMessageRead(cursor.getInt(3));
                message.status = getMessageStatus(cursor.getInt(4));
                message.type = getMessageType(cursor.getInt(5));
                message.person = getPerson(message.address);
                Log.e(TAG, "queryMessageLog: "+message.toString());
                //将读取到的信息封装进messageList
                Message msg = new Message(message.address,message.body,message.type);
                messageList.add(msg);
            }
            cursor.close();
        }
    }

    //短信发件人
    private String getPerson(String address) {
        try {
            ContentResolver resolver = getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
            Cursor cursor;
            cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        return cursor.getString(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //短信接受状态
    private String getMessageStatus(int anInt) {
        switch (anInt) {
            case -1:
                return "接收";
            case 0:
                return "complete";
            case 64:
                return "pending";
            case 128:
                return "failed";
            default:
                break;
        }
        return null;
    }

    //短信类型（收到/发出）
    private String getMessageType(int anInt) {
        if (1 == anInt) {
            return "收到的";
        }
        if (2 == anInt) {
            return "已发出";
        }
        return null;
    }

    //短信阅读状态
    private String getMessageRead(int anInt) {
        if (1 == anInt) {
            return "已读";
        }
        if (0 == anInt) {
            return "未读";
        }
        return null;
    }
}
class SMSMessage {
    long date;
    String address;
    String body;
    String person;
    String read;
    String status;
    String type;

    @NonNull
    @Override
    public String toString() {
        //定义转化为字符串的日期格式
        // 将时间转化为类似 2020-02-13 16:01:30 格式的字符串
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "SMSMessage{" +
                "date=" + sdf.format(date) +
                ", address=" + address +
                ", body=" + body +
                ", person=" + person +
                ", read=" + read +
                ", status=" + status +
                ", type=" + type +
                "}";
    }
}