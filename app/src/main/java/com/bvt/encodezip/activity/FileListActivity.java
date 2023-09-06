package com.bvt.encodezip.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bvt.encodezip.R;
import com.bvt.encodezip.adapter.FileListAdapter;
import com.bvt.encodezip.databinding.ActivityMainBinding;
import com.bvt.encodezip.utils.FileUtils;
import com.bvt.encodezip.utils.NetworkUtil;
import com.bvt.encodezip.utils.PreferenceUtil;
import com.bvt.encodezip.utils.UriUtils;
import com.bvt.encodezip.vo.FileVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class FileListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private static Integer FILE_SELECTOR_CODE = 9000;

    private FileListAdapter adapter;
    private List<FileVO> fileVOList;

    private ListView fileListView;

    private Toolbar toolbar;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filelist_layout);

        InnerFileReceiver fileReceiver = new InnerFileReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.bvt.encodezip.upload");
//        intentFilter.addCategory();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(fileReceiver, intentFilter);

        initView();
        fileVOList = new ArrayList<FileVO>();
        adapter = new FileListAdapter(this, fileVOList);
        fileListView = findViewById(R.id.file_listview);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemLongClickListener(this);
        initData();
    }

    private void initView() {
        toolbar = findViewById(R.id.file_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.file_list_title);

    }

    public void initData() {
        String token = PreferenceUtil.getString(this, PreferenceUtil.getTokenPreference());
        Request request = new Request.Builder().url(FileUtils.SERVER_ADDR + "/employee/all_file").header("Authorization", token).build();

        OkHttpClient okHttpClient = new OkHttpClient();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("Error");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonobj = new JSONObject(response.body().string());
                    int rescode = jsonobj.getInt("code");
                    String resmsg = jsonobj.getString("msg");
                    if(rescode==200){
                        fileVOList.clear();

                        JSONArray dataJsonArr = jsonobj.getJSONArray("data");

                        for(int i = 0; i < dataJsonArr.length(); i++){
                            JSONObject jsonObject = (JSONObject) dataJsonArr.get(i);
                            FileVO fileVo = new FileVO();
                            fileVo.setId(jsonObject.getInt("id"));
                            fileVo.setFileName(jsonObject.getString("fileName"));
                            fileVo.setTeleporter(jsonObject.getString("teleporter"));

                            fileVOList.add(fileVo);
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                reloadView();
                            }
                        });

                    }else{
                        runOnUiThread(new Runnable() {
                            public void run() {
                                reloadView();
                                Toast.makeText(getApplicationContext(), "用户名/密码不对", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private void reloadView() {
        fileListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.file_list_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.fl_action_select) {
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//            intent.setType("application/msword|application/vnd.openxmlformats-officedocument.wordprocessingml.document" + "|application/vnd.ms-powerpoint|application/vnd.openxmlformats-officedocument.presentationml.presentation|application/pdf");

            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, FILE_SELECTOR_CODE);
        } else if (item.getItemId() == R.id.fl_action_downloaded_list) {
            Intent intent = new Intent(this, FileLoadedListActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == FILE_SELECTOR_CODE) {

            Uri uri = data.getData();
            try {
//                UriUtils.getFileAbsolutePath(this, uri);
                NetworkUtil.postFile(this, uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                StringBuilder result = new StringBuilder();

                //获取输入流
                InputStream is = this.getContentResolver().openInputStream(uri);
                //创建用于字符输入流中读取文本的bufferReader对象
                BufferedReader br = new BufferedReader(new InputStreamReader(is));



                String line;
                while ((line = br.readLine()) != null) {
                    //将读取到的内容放入结果字符串
                    result.append(line);
                }
                //文件中的内容
                String content = result.toString();
            } catch (Exception e) {

            }
             //保存读取到的内容




        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        FileVO selectedFile = fileVOList.get(i);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.file_alert_dialog_long_selected).setPositiveButton(R.string.file_alert_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadFile(selectedFile);
            }
        }).setNegativeButton(R.string.file_alert_dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
        return true;
    }

    private void downloadFile(FileVO fileVO) {
        //下载路径，如果路径无效了，可换成你的下载路径
        final String url = FileUtils.SERVER_ADDR + "/employee/" + fileVO.getFileName();
        Request request = new Request.Builder().header("Authorization", PreferenceUtil.getString(this, PreferenceUtil.getTokenPreference())).url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                String filename = url.substring(url.lastIndexOf("/") + 1);

                File localFile = new File(getCacheDir().getAbsoluteFile(), "file");
//                File localFile = new File(getExternalFilesDir(null), "encode_dir");
                if (!localFile.mkdirs()) {
                    localFile.createNewFile();
                }

                Headers responseHeaders = response.headers();
                int responseHeadersLength = responseHeaders.size();
                for (int i = 0; i < responseHeadersLength; i++){
                    String headerName = responseHeaders.name(i);
                    String headerValue = responseHeaders.get(headerName);
                    System.out.print("TAG----------->Name:"+headerName+"------------>Value:"+headerValue+"\n");
                }
                String fileSuffix = response.header("filetype");


                String savePath = localFile.getAbsolutePath();
                //这是里的mContext是我提前获取了android的context
                File docFile = new File(savePath+File.separator+filename);
                try {
                    sink = Okio.sink(docFile);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
//                        String fileSuffix = response.header("filetype");
//                        showFinishDialog(R.string.file_alert_dialog_download_finish);
                        PreferenceUtil.putPreFileDownloaded(getApplicationContext(), filename, fileSuffix, savePath);
                    }

                }
            }
        });

    }

    private void showFinishDialog(int resId) {
        Toast.makeText(this, R.string.file_alert_dialog_long_selected, Toast.LENGTH_LONG).show();
    }

    class InnerFileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("com.bvt.encodezip.upload")) {
                Toast.makeText(context, "上传成功", Toast.LENGTH_LONG).show();
                intent.getStringExtra("msg");
                initData();

            }

        }
    }
}
