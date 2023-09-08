package com.bvt.encodezip.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {



    /**
     * 上传文件
     */
    public static void postFile(Context context, Uri fileUri) throws IOException {

        Log.d("uri路径", Environment.getDataDirectory().getPath());

        String filePath = UriUtils.getFileAbsolutePath(context, fileUri);
        File file = new File(filePath);
//        Cursor cursor = context.getContentResolver().query(fileUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
//        while (cursor.moveToNext()) {
//            //图片名称
//            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
//            Log.d("路径", name);
//            // 图片绝对路径
//            int data_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            String path = cursor.getString(data_index);
//            file = new File(path);
//            Log.d("TAG", "name= "+name+"  path= "+ path);
//
//        }

        /**
         * 写数据到文件中,只是模拟下，
         */
//        File file = new File(fileUri.toString());
//        FileOutputStream fos = openFileOutput(file.getName(), MODE_PRIVATE);
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
//        writer.write("写入文件！！！");
//        writer.close();
//        fos.close();
        /**
         * 上传文件到服务器中
         */
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient client = new OkHttpClient();//获取OkHttpClient实例

        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);//文件和json参数共同上传
        if (file != null) {//添加文件到form-data
            RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("files", file.getName(), body);
        }
        //添加taskid 字段到form-data
//        requestBody.addFormDataPart("taskid","7e44afffa884a7476acf77aa6bc083bb1b2aeaff");

        String token = PreferenceUtil.getString(context, PreferenceUtil.getTokenPreference());

        String uri = FileUtils.SERVER_ADDR + "/employee/upload";
        final Request request = new Request.Builder().url(uri).addHeader("Authorization", token).post(requestBody.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Network", "文件上传onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("Network", "文件上传onResponse: " + response.body().string());
                notifyUploadState(context);
            }
        });

    }

    public static void notifyUploadState(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);

        Intent uploadIntent = new Intent("com.bvt.encodezip.upload");
        uploadIntent.putExtra("msg", "success");
        localBroadcastManager.sendBroadcast(uploadIntent);
    }
}
