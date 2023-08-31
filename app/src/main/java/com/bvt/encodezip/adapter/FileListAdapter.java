package com.bvt.encodezip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bvt.encodezip.R;
import com.bvt.encodezip.vo.FileVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListAdapter extends BaseAdapter {

    private List<FileVO> fileVOList;
    private Context context;

    public FileListAdapter(Context context) {
        this.context = context;
        this.fileVOList = new ArrayList<>();
    }

    public FileListAdapter(Context context, List<FileVO> fileVOList) {
        this.context = context;
        this.fileVOList = fileVOList;
    }

    @Override
    public int getCount() {
        return fileVOList.size();
    }

    @Override
    public Object getItem(int i) {
        if (fileVOList.isEmpty()) {
            return null;
        }
        return fileVOList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = new ViewHolder();
        if (view ==null) {
            view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.file_list_cell, viewGroup, false);
            vh.tv = view.findViewById(R.id.file_list_name);
            vh.iv = view.findViewById(R.id.file_list_type_img);
            view.setTag(vh);
        } else {
            vh = (ViewHolder)view.getTag();
        }
        vh.tv.setText(fileVOList.get(i).getFileName());
        return view;
    }

    public class ViewHolder {
        ImageView iv;
        TextView tv;
    }
}
