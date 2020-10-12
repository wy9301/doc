package com.flyf.mapper;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public interface QiniuMapper {
    String uploadFile(File file) throws QiniuException;
    String uploadFile(MultipartFile file) throws QiniuException;

    String uploadFile(InputStream inputStream) throws QiniuException;

    Response delete(String key) throws QiniuException;
    //Response deletelist(String key[]) throws QiniuException;

}
