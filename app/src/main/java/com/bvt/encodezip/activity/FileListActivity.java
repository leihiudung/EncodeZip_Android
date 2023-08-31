package com.bvt.encodezip.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bvt.encodezip.R;
import com.bvt.encodezip.adapter.FileListAdapter;
import com.bvt.encodezip.utils.PreferenceUtil;
import com.bvt.encodezip.vo.FileVO;
import com.google.android.material.badge.ExperimentalBadgeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileListActivity extends Activity {

    private FileListAdapter adapter;
    private List<FileVO> fileVO;

    private ListView fileListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist_layout);
        fileVO = new ArrayList<FileVO>();
        adapter = new FileListAdapter(this, fileVO);
        fileListView = findViewById(R.id.file_listview);
        fileListView.setAdapter(adapter);
        initData();
    }

    public void initData() {
        String token = PreferenceUtil.getString(this, PreferenceUtil.getTokenPreference());
        Request request = new Request.Builder().url("http://192.168.2.216:8081/employee/all_file").header("Authorization", token).build();

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

                        JSONArray dataJsonArr = jsonobj.getJSONArray("data");

                        for(int i = 0; i < dataJsonArr.length(); i++){
                            JSONObject jsonObject = (JSONObject) dataJsonArr.get(i);
                            FileVO fileVo = new FileVO();
                            fileVo.setId(jsonObject.getInt("id"));
                            fileVo.setFileName(jsonObject.getString("fileName"));
                            fileVo.setTeleporter(jsonObject.getString("teleporter"));

                            fileVO.add(fileVo);
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
    protected void onResume() {
        super.onResume();

    }
}
