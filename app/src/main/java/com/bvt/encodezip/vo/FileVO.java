package com.bvt.encodezip.vo;

public class FileVO {

    private Integer id;
    private String fileName;

    private String fileAliasName;
    private String teleporter;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileAliasName() {
        return fileAliasName;
    }

    public void setFileAliasName(String fileAliasName) {
        this.fileAliasName = fileAliasName;
    }

    public String getTeleporter() {
        return teleporter;
    }

    public void setTeleporter(String teleporter) {
        this.teleporter = teleporter;
    }
}
