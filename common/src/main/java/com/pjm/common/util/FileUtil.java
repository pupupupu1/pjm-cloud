package com.pjm.common.util;

import com.pjm.common.entity.FileInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

@Component
public class FileUtil {
    public String saveFile(MultipartFile file, String uploadPath, String requestPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //是否是图片
        String fileTypePath = "";
        if (checkIsImage(fileType)) {
            fileTypePath = "img/";
        } else {
            fileTypePath = "file/";
        }
        Calendar calendar = Calendar.getInstance();
        String realPath = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
        File dir = new File(uploadPath + fileTypePath + realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileType;
        String filePath = uploadPath + fileTypePath + realPath + fileName;
        file.transferTo(new File(filePath));
        return requestPath + fileTypePath + realPath + fileName;
    }

    public String saveFile(MultipartFile file, String uploadPath, String requestPath, String type) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //是否是图片
        String fileTypePath = type + "/";
        Calendar calendar = Calendar.getInstance();
        String realPath = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
        File dir = new File(uploadPath + fileTypePath + realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileType;
        String filePath = uploadPath + fileTypePath + realPath + fileName;
        file.transferTo(new File(filePath));
        return requestPath + fileTypePath + realPath + fileName;
    }

    public FileInfo saveFile4FileInfo(MultipartFile file, String uploadPath, String requestPath, String userAccount) throws IOException {
        FileInfo fileInfo = new FileInfo();
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        long fileSize = file.getSize();
        //是否是图片
        String fileTypePath = userAccount + "/";
        Calendar calendar = Calendar.getInstance();
        String realPath = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
        File dir = new File(uploadPath + fileTypePath + realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileType;
        String filePath = uploadPath + fileTypePath + realPath + fileName;
        file.transferTo(new File(filePath));
        fileInfo.setFileName(originalFilename);
        fileInfo.setFileSize(fileSize);
        fileInfo.setFileType(fileType);
        fileInfo.setFilePath(requestPath + fileTypePath + realPath + fileName);
        fileInfo.setUploadData(System.currentTimeMillis());
        return fileInfo;
    }

    public boolean checkIsImage(String fileType) {
        String imgType = "BMP,JPG,JPEG,PNG,PCD,PSD,DXF,TIFF,PCX";
        if (imgType.indexOf(fileType.toUpperCase()) > -1) {
            return true;
        }
        return false;
    }


    public boolean delete(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.isFile()) {
                if (!f.delete()) {
                    System.out.println(f.getAbsolutePath() + " delete error!");
                    return false;
                }
            } else {
                if (!this.delete(f.getAbsolutePath())) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public int[] getWightAndHeight(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(in);
        return new int[]{image.getWidth(), image.getHeight()};
    }
}
