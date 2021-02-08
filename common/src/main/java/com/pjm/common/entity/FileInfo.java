package com.pjm.common.entity;

import lombok.Data;

@Data
public class FileInfo {
    private String fileName;
    private String filePath;
    private long fileSize;
    private String fileType;
    private long uploadData;
}
