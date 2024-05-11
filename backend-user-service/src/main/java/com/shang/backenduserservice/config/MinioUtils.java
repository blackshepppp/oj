package com.shang.backenduserservice.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import io.minio.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class MinioUtils {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioInfo minioInfo;



    /**
     * 上传文件
     *
     * @param file
     * @param bucketName
     * @return
     * @throws Exception
     */
    public String uploadFile(MultipartFile file, String bucketName) {
        if (null == file || 0 == file.getSize()) {
            log.error("msg{}", "上传文件不能为空");
            return null;
        }
        try {
            boolean res = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!res) {
                // 创建bucket
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            // 原文件名
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Date date=new Date();
            String s = simpleDateFormat.format(date);
            String[] split = s.split("-");
            String m="";
            for (int i = 0; i < split.length; i++) {
                if (i!=0){
                    m=m+"/"+split[i];}else {
                    m=m+split[i];
                }
            }
            //更改文件名
            String originalFilename = file.getOriginalFilename();//去掉了文件前缀
            log.info("文件名为{}",originalFilename);
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 桶名称
                            .object(originalFilename) // 文件存储名称
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            inputStream.close();
            return minioInfo.getEndpoint() + "/" + bucketName + "/" + originalFilename;
        } catch (Exception e) {
            log.error("上传失败：{}", e.getMessage());
        }
        log.error("msg", "上传失败");
        return null;
    }


    /**
     * 通过字节流上传
     *
     * @param imageFullPath
     * @param bucketName
     * @param imageData
     * @return
     */
    public String uploadImage(String imageFullPath,
                              String bucketName,
                              byte[] imageData) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageData);
        try {
            // 判断是否存在

            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(imageFullPath)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(".jpg")
                    .build());
            return minioInfo.getEndpoint() + "/" + bucketName + "/" + imageFullPath;
        } catch (Exception e) {
            log.error("上传失败：{}", e.getMessage());
        }
        log.error("msg", "上传失败");
        return null;
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public int removeFile(String bucketName, String fileName) {
        try {
            // 判断桶是否存在
            boolean res = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (res) {
                // 删除文件
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName)
                        .object(fileName).build());
            }
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
            return 1;
        }
        System.out.println("删除文件成功");
        return 0;
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param bucketName
     * @param response
     */
    public void fileDownload(String fileName,
                             String bucketName,
                             HttpServletResponse response) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (StringUtils.isBlank(fileName)) {
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                String data = "文件下载失败";
                OutputStream ps = response.getOutputStream();
                ps.write(data.getBytes("UTF-8"));
                return;
            }
            outputStream = response.getOutputStream();
            // 获取文件对象
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            byte buf[] = new byte[1024];
            int length = 0;
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), "UTF-8"));
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            // 输出文件
            while ((length = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
            System.out.println("下载成功");
            inputStream.close();
        } catch (Throwable ex) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            String data = "文件下载失败";
            try {
                OutputStream ps = response.getOutputStream();
                ps.write(data.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                outputStream.close();
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 通过字节流下载
     * @param fileName 文件名称
     * @param bucketName 桶名称
     * @return
     */
    public byte[] byteDownload(String fileName,
                               String bucketName) {
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            byte[] buffer = StreamUtils.copyToByteArray(inputStream);
            inputStream.read(buffer);
            inputStream.close();
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
