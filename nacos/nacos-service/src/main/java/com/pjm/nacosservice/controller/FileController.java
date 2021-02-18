package com.pjm.nacosservice.controller;

import com.pjm.common.entity.FileInfo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.FileUtil;
import com.pjm.common.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @author pjm
 * @since 2020-05-14
 */
@Api(tags = {"文件上传"})
@RestController
@CrossOrigin
@RequestMapping("file")
public class FileController {
    @Value("${com.pjm.file.path.upload}")
    public String fileUploadPath;
    @Value("${com.pjm.file.path.request}")
    public String fileRequestPath;
    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;

    @ApiOperation("单文件上传")
    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException {
//        FileUtil fileUtil = new FileUtil();
        String path = fileUtil.saveFile(file, fileUploadPath, fileRequestPath);
        return ResponseEntity.success(path);
    }

    @ApiOperation("单文件上传")
    @PostMapping("/uploadFile4FileInfo")
    public ResponseEntity<FileInfo> uploadFile4FileInfo(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String account = userUtil.getAccount(request);
        FileInfo fileInfo = fileUtil.saveFile4FileInfo(file, fileUploadPath, fileRequestPath, account);
        return ResponseEntity.success(fileInfo);
    }

    @ApiOperation("单文件上传(带额外参数)")
    @PostMapping("/uploadFileWithParam")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file, @RequestParam("fileType") String fileType) throws IOException {
        String path = fileUtil.saveFile(file, fileUploadPath, fileRequestPath, fileType);
        return ResponseEntity.success(path);
    }

    @ApiOperation("多文件上传")
    @PostMapping("/uploadFiles")
    public ResponseEntity<String> upload(@RequestParam(value = "files") MultipartFile[] files) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
//        FileUtil fileUtil = new FileUtil();
        for (MultipartFile file : files) {
            String path = fileUtil.saveFile(file, fileUploadPath, fileRequestPath);
            stringBuilder.append(stringBuilder.length() > 0 ? ";" + path : path);
        }
        System.out.println(stringBuilder.toString());
        return ResponseEntity.success(stringBuilder.toString());

    }
}
