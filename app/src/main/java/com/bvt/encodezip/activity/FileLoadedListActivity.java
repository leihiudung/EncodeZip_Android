package com.bvt.encodezip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.bvt.encodezip.R;
import com.bvt.encodezip.adapter.FileDownloadedListViewAdapter;
import com.bvt.encodezip.adapter.FileListAdapter;
import com.bvt.encodezip.utils.FileUtils;
import com.bvt.encodezip.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileLoadedListActivity extends AppCompatActivity {

    private FileDownloadedListViewAdapter adapter;
    public ListView fileList;

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
        fileList.setAdapter(adapter);
    }

    private void initView() {
       fileList = findViewById(R.id.file_downloaded_list);

    }
}