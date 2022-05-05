package com.example.message_application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MessageReceiver extends BroadcastReceiver {
    private Message msg;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取广播中的bundle
        Bundle bundle = intent.getExtras();
        // 提取短信消息
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        // 获取发送方号码
        String address = messages[0].getOriginatingAddress();
        StringBuilder fullMessage = new StringBuilder();
        for (SmsMessage message : messages) {
            // 获取短信内容
            fullMessage.append(message.getMessageBody());
        }
        msg.getMsg(address, fullMessage.toString());
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //定义一个message接口，用于向activity传值
    interface Message {
        void getMsg(String address, String fullMessage);
    }
}