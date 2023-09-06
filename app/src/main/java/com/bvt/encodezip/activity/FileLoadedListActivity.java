package com.bvt.encodezip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bvt.encodezip.R;
import com.bvt.encodezip.adapter.FileDownloadedListViewAdapter;
import com.bvt.encodezip.utils.FileUtils;
import com.bvt.encodezip.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.collections.builders.MapBuilder;

public class FileLoadedListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private FileDownloadedListViewAdapter adapter;
    public ListView fileListView;

    private List<String> fileNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_loaded_list);
        initView();

        initData();
    }

    private void initData() {
        String filesName = PreferenceUtil.getPreFileDownloaded(this, FileUtils.DWONLOADED_FILE_LIST);
        String[] fileNameArr = filesName.split(",");


        fileNameList = new ArrayList<String>(fileNameArr.length);

        Collections.addAll(fileNameList, fileNameArr);
        adapter = new FileDownloadedListViewAdapter(this, fileNameList);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemLongClickListener(this);
    }

    private void initView() {
       fileListView = findViewById(R.id.file_downloaded_list);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        String fileSuffix = PreferenceUtil.getString(this, fileNameList.get(i) + "fileSuffix");

        String fileName = fileNameList.get(i);
        Intent intent = new Intent(this, FileBrowseActivity.class);
        intent.putExtra("filename", fileName);
        intent.putExtra("fileSuffix", fileSuffix);
        startActivity(intent);
        return true;
    }
}