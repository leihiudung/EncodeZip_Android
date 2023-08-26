package com.bvt.encodezip;

import android.os.Environment;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.LocalFileHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ZipInputStreamExample {

    public void extractWithZipInputStream(File zipFile, char[] password) throws IOException {
        String extSdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String logFolderPath = combinePath(extSdRootPath, "decode");

        LocalFileHeader localFileHeader;
        int readLen;
        byte[] readBuffer = new byte[4096];

        InputStream inputStream = new FileInputStream(zipFile);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, password)) {

//            creatFile()
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {

                File extractedFile = new File(localFileHeader.getFileName());
                try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
                    while ((readLen = zipInputStream.read(readBuffer)) != -1) {
                        outputStream.write(readBuffer, 0, readLen);
                    }
                }
            }
        }
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

    public static String combinePath(String path1, String path2)

    {

        File file1 = new File(path1);

        File file2 = new File(file1, path2);

        return file2.getPath();

    }

    /**
     * 根据所给密码解压zip压缩包到指定目录
     * <p>
     * 如果指定目录不存在,可以自动创建,不合法的路径将导致异常被抛出
     *
     * @param zipFile  zip压缩包绝对路径
     * @param dest     指定解压文件夹位置
     * @param password 密码(可为null)
     * @return 解压后的文件数组
     * @throws ZipException 异常
     */
    public static File[] deCompress(File zipFile, String dest, String password) throws ZipException {
        //1.判断指定目录是否存在
        File destDir = new File(dest);
        boolean jj = destDir.isDirectory();
        if (!destDir.exists()) {
            boolean mkdir = destDir.mkdir();
            System.out.println(mkdir);
        }

        // 先做后缀转换
        File currentSuffixFile = renameSuffix(zipFile);
        if (currentSuffixFile == null) {
            return null;
        }

        //2.初始化zip工具
        ZipFile zFile = new ZipFile(currentSuffixFile);
        zFile.setCharset(Charset.forName("UTF-8"));
        if (!zFile.isValidZipFile()) {
            throw new ZipException("压缩文件不合法,可能被损坏.");
        }
        //3.判断是否已加密
        if (zFile.isEncrypted()) {
            zFile.setPassword(password.toCharArray());
        }
        //4.解压所有文件
        zFile.extractAll(dest);
        List headerList = zFile.getFileHeaders();
        List<File> extractedFileList = new ArrayList<>();
        for (Object object : headerList) {
            FileHeader fileHeader = (FileHeader) object;
            if (!fileHeader.isDirectory()) {
                extractedFileList.add(new File(destDir, fileHeader.getFileName()));
            }
        }
        File[] extractedFiles = new File[extractedFileList.size()];
        extractedFileList.toArray(extractedFiles);
        return extractedFiles;
    }

    private static File renameSuffix(File mp4File) {
        String encodeFilePath = mp4File.getParentFile().getParentFile().getAbsolutePath();
        String decodeFilePath = combinePath(encodeFilePath, "decode");
        File zipSuffixFile = new File(combinePath(decodeFilePath, "decode.zip"));
        if (mp4File.renameTo(zipSuffixFile)) {
            return zipSuffixFile;
        }
        return null;
    }

}