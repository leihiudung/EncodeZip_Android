package com.bvt.encodezip.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bvt.encodezip.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBrowseActivity extends AppCompatActivity {

    private String fileName;
    private String fileSuffix;
    public WebView mWebView;
    public ImageView imageView;

    public boolean isOnlineFile;

//    private String url ="https://lingsy.oss-cn-beijing.aliyuncs.com/%E6%B5%85%E8%B0%88%E3%80%8A%E8%AF%97%E7%BB%8F%E3%80%8B%E4%B8%AD%E7%94%9F%E5%91%BD%E6%84%8F%E8%B1%A1%E7%9A%84%E6%83%85%E6%84%9F%E5%86%85%E6%B6%B5%E4%B8%8E%E4%BB%B7%E5%80%BC_%E9%A1%BE%E6%99%B4%E5%B7%9D.pdf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browse);
        Intent tempIntent = this.getIntent();
        fileName = tempIntent.getStringExtra("filename");
        fileSuffix = tempIntent.getStringExtra("fileSuffix");
        isOnlineFile = tempIntent.getBooleanExtra("isOnlineFile", false);

        initView();
    }

    private void initView() {
        mWebView = findViewById(R.id.fl_downloaded_pdf);
        imageView = findViewById(R.id.fl_downloaded_image);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSavePassword(false);
        webSettings.setBuiltInZoomControls(true);

//        mWebView.setWebChromeClient(new WebChromeClient());

        if (fileSuffix.equalsIgnoreCase("jpg") || fileSuffix.equalsIgnoreCase("png") || fileSuffix.equalsIgnoreCase("img")) {
            mWebView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            setWebImage(fileName, fileSuffix);
        } else {
            setWebViewContent(fileName, fileSuffix);
        }

    }

    /**
     * 展示PDF
     * @param fileName  文件名
     * @param fileSuffix 文件后缀
     */
    private void setWebViewContent(String fileName, String fileSuffix) {

        File localFile = new File(getCacheDir().getPath(), isOnlineFile ? "temp" : "file");
        File pdfFile = null;
        try {
            String savePath = localFile.getAbsolutePath();
            File zipFile = new File(getCacheDir().getPath()+ File.separator + (isOnlineFile ? "temp" : "file") + File.separator + fileName + ".zip");
            Log.d("zipfile", zipFile.exists() + " " + zipFile.isDirectory());
            ZipFile zFile = new ZipFile(localFile.getPath() + File.separator + fileName + ".zip");
            zFile.setCharset(Charset.forName("UTF-8"));
            if (!zFile.isValidZipFile()) {
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            //3.判断是否已加密
            if (zFile.isEncrypted()) {
                zFile.setPassword("abcgo".toCharArray());
            }
            //4.解压所有文件
            zFile.extractAll(localFile.getPath());
            List headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<>();
            for (Object object : headerList) {
                FileHeader fileHeader = (FileHeader) object;
                if (!fileHeader.isDirectory()) {
                    extractedFileList.add(new File(localFile, fileHeader.getFileName()));
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            pdfFile = extractedFileList.get(0);
        } catch (Exception e) {

        }

        String savePath = localFile.getAbsolutePath();

        try {
//                    mWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + "file:///android_asset/agreement.pdf");

            mWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + pdfFile.getPath());

        }catch (RuntimeException e) {
            Log.e("loadUrl error", e.getMessage());
        }
    }

    /**
     * 展示图片
     * @param fileName
     * @param fileSuffix
     */
    private void setWebImage(String fileName, String fileSuffix) {
        File localFile = new File(getCacheDir().getAbsoluteFile(), isOnlineFile ? "temp" : "file");

//        File localFile = new File(getExternalFilesDir(null), "encode_dir");
//        Uri fileUri = FileProvider.getUriForFile(this, "com.bvt.encodezip", localFile);
        File imageFile = null;
        try {
            String savePath = localFile.getAbsolutePath();
            ZipFile zFile = new ZipFile(localFile.getPath() + File.separator + fileName + ".zip");
            zFile.setCharset(Charset.forName("UTF-8"));
            if (!zFile.isValidZipFile()) {
                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            //3.判断是否已加密
            if (zFile.isEncrypted()) {
                zFile.setPassword("abcgo".toCharArray());
            }
            //4.解压所有文件
            zFile.extractAll(localFile.getPath());
            List headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<>();
            for (Object object : headerList) {
                FileHeader fileHeader = (FileHeader) object;
                if (!fileHeader.isDirectory()) {
                    extractedFileList.add(new File(localFile, fileHeader.getFileName()));
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            imageFile = extractedFileList.get(0);
        } catch (Exception e) {

        }



        //这是里的mContext是我提前获取了android的context
//        File docFile = new File(savePath+File.separator + fileName + "." + fileSuffix);
//        Log.d("file_exist", docFile.isFile() ? "YES" : "NO");
        imageView.setImageURI(Uri.fromFile(imageFile));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }

    }
}
