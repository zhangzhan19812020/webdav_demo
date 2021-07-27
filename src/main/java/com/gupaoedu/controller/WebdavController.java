package com.gupaoedu.controller;

import com.github.sardine.DavResource;
import com.gupaoedu.webdav.WebdavUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/webdav")
public class WebdavController {
    @Autowired
    private WebdavUtil util;

    private Logger logger = LoggerFactory.getLogger(WebdavController .class);

    @GetMapping("/list")
    public WebdavUtil.ResultData listPath(String path) {
        try {

            List<DavResource> davResources = util.listPath(path);
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.successCode, davResources, "");
        } catch (IOException e) {

            logger.info(e.getMessage());
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.failCode, new ArrayList<DavResource>(), e.getMessage());
        }
    }

    @GetMapping("/exists")
    public WebdavUtil.ResultData exists(String path) {
        try {
            Boolean flag = util.exists(path);
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.successCode, flag, "");
        } catch (IOException e) {
            logger.info(e.getMessage());
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.failCode, false, e.getMessage());
        }
    }

    @PostMapping("/mkdir")
    public WebdavUtil.ResultData createDirectory(@RequestBody WebdavUtil.WebDavPath webDavPath) {
        try {
            String path = util.createDirectory(webDavPath.getPath());
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.successCode, path, "");
        } catch (IOException e) {
            logger.info(e.getMessage());
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.failCode, null, e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public WebdavUtil.ResultData delete(String path) {
        try {
            String removePath = util.delete(path);
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.successCode, removePath, "");
        } catch (IOException e) {
            logger.info(e.getMessage());
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.failCode, null, e.getMessage());
        }
    }

    @PostMapping("/upload")
    public WebdavUtil.ResultData upload(String path, MultipartFile file) {

        try {
            WebdavUtil.WebDavNode webDavNode = util.upload(path, file);
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.successCode, webDavNode, "");
        } catch (IOException e) {
            logger.info(e.getMessage());
            return new WebdavUtil.ResultData(WebdavUtil.ResultData.failCode, null, e.getMessage());
        }



//        try {
//            String removePath = util.delete(path);
//            return new WebdavUtil.ResultData(WebdavUtil.ResultData.successCode, removePath, "");
//        } catch (IOException e) {
//            logger.info(e.getMessage());
//            return new WebdavUtil.ResultData(WebdavUtil.ResultData.failCode, null, e.getMessage());
//        }
    }

}
