package com.bvt.encodezip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bvt.encodezip.R;
import com.bvt.encodezip.vo.FileVO;

import java.util.List;

public class FileDownloadedListViewAdapter extends BaseAdapter {

    private List<String> fileList;
    private Context context;

    public FileDownloadedListViewAdapter(Context context, List<String> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int i) {
        if (fileList.isEmpty()) {
            return null;
        }
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FileDownloadedListViewAdapter.ViewHolder vh = new FileDownloadedListViewAdapter.ViewHolder();
        if (view ==null) {
            view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.file_list_cell, viewGroup, false);
            vh.tv = view.findViewById(R.id.file_list_name);
            vh.iv = view.findViewById(R.id.file_list_type_img);
            view.setTag(vh);
        } else {
            vh = (FileDownloadedListViewAdapter.ViewHolder)view.getTag();
        }
        vh.tv.setText(fileList.get(i));
        return view;
    }

    public class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}
