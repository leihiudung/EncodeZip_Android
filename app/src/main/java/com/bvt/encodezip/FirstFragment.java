package com.bvt.encodezip;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.bvt.encodezip.databinding.FragmentFirstBinding;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.buttonDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = new String("abcgo");
                String extSdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String logFolderPath = combinePath(extSdRootPath, "encode");
                String decodeFolderPath = combinePath(extSdRootPath, "decode");
                String zipFilePath = combinePath(logFolderPath, "encode.mp4");
//                ZipInputStreamExample inputStream = new ZipInputStreamExample();

//                String encodeOtherSuf = combinePath(logFolderPath, "encode.mp4");

//                File newFile = new File(combinePath(decodeFolderPath, "encode.zip"));
//                File encodeOtherSufFile = new File(encodeOtherSuf);
//                encodeOtherSufFile.renameTo(newFile);
//                File correctSuffix = new File(encodeOtherSuf).renameTo(newFile) == true ? newFile : null;

//                if (newFile == null) {
//                    return;
//                }
                try {
                    ZipInputStreamExample.deCompress(new File(zipFilePath), decodeFolderPath, "abcgo");
//                    ZipInputStreamExample.deCompress(newFile, decodeFolderPath, "abcgo");

                } catch (ZipException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        binding.buttonEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<File> needZipFile = new ArrayList<>();

                String extSdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String logFolderPath = combinePath(extSdRootPath, "encode");
                String zipFilePath = combinePath(logFolderPath, "encode.zip");

                File zipFile = new File(zipFilePath);


                File file = new File(logFolderPath);

                File[] tempList = file.listFiles();
                for (int i = 0; i < tempList.length; i++) {
                    if (tempList[i].isFile()) {
//              System.out.println("文     件：" + tempList[i]);
                        needZipFile.add(tempList[i]);
                    }
                    if (tempList[i].isDirectory()) {
//                      System.out.println("文件夹：" + tempList[i]);
                    }
                }

                String password = new String("abcgo");


                ZipOutputStreamExample outputStream = new ZipOutputStreamExample();
                try {
                    outputStream.zipOutputStreamExample(zipFile, needZipFile, password.toCharArray(), CompressionMethod.DEFLATE, true, EncryptionMethod.ZIP_STANDARD, AesKeyStrength.KEY_STRENGTH_256);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (zipFile.exists()) {
                    File newFile = new File(combinePath(logFolderPath, "encode.mp4"));
                    zipFile.renameTo(newFile);
                }
            }
        });
    }

    public static String combinePath(String path1, String path2)

    {

        File file1 = new File(path1);

        File file2 = new File(file1, path2);

        return file2.getPath();

    }

    /**
     * 创建文件夹
     * @param path  路径
     * @param fileName  文件加名
     */
    public void creatFile(String path, String fileName) {
        File file = new File(path, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}