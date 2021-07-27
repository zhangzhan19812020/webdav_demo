package com.gupaoedu.webdav;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import com.gupaoedu.serializer.TimeStampFormatSerializer;
import com.gupaoedu.util.StringHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class WebdavUtil {
    @Value("${webdav.username}")
    private String username;
    @Value("${webdav.password}")
    private String password;
    @Value("${webdav.server}")
    private String server;

    @Autowired
    private Sardine sardine;

    @Bean
    public Sardine getSardine() {
        return SardineFactory.begin(username, password);
    }

    /**
     * 列举指定路径下的文件及文件夹
     * @param path
     * @return
     * @throws IOException
     */
    public List<DavResource> listPath(String path) throws IOException {
        path = StringHelper.isNullOrEmpty(path) ? "/" :  (path.startsWith("/") ? path : "/" + path);
        return sardine.list(server + path);
    }


    /**
     * 判断路径或文件是否存在
     * @param path
     * @return
     * @throws IOException
     */
    public boolean exists(String path) throws IOException {
        path = StringHelper.isNullOrEmpty(path) ? "/" :  (path.startsWith("/") ? path : "/" + path);
        return sardine.exists(server + path);
    }

    /**
     * 创建目录
     * @param path
     * @return
     * @throws IOException
     */
    public String createDirectory(String path) throws IOException {
        path = StringHelper.isNullOrEmpty(path) ? "/" :  (path.startsWith("/") ? path : "/" + path);
        String[] paths = path.split("/");
        String parent = "";
        for (String p:paths) {
            if (!StringHelper.isNullOrEmpty(p)) {
                parent += "/" + p;
                if (!exists(parent)) {
                    sardine.createDirectory(server + parent + "/");
                }
            }
        }
        return path;
    }

    /**
     * 删除文件
     * @param path
     * @return
     * @throws IOException
     */
    public String delete(String path) throws IOException {
        path = StringHelper.isNullOrEmpty(path) ? "/" :  (path.startsWith("/") ? path : "/" + path);
        sardine.delete(server + path);
        return path;
    }

    /**
     * 上传文件到指定目录，并且在该目录下创建年月日层级的文件夹
     * @param path
     * @param file
     * @return
     * @throws IOException
     */
    public WebDavNode upload(String path, MultipartFile file) throws IOException {

        path = StringHelper.isNullOrEmpty(path) ? "/" :  (path.startsWith("/") ? path : "/" + path);
        if(!path.endsWith("/")){
            path += "/";
        }

        Calendar calendar = Calendar.getInstance();
        path += calendar.get(Calendar.YEAR) + "/";
        path += (calendar.get(Calendar.MONTH) + 1) + "/";
        path += calendar.get(Calendar.DATE) + "/";

        if(!exists(path)) {
            createDirectory(path);
        }

        String fileName = StringHelper.getFileName(file.getOriginalFilename());
        String fileExtensionName = StringHelper.getFileExtensionName(fileName);

        String webDavFileName = calendar.get(Calendar.HOUR) + "_" + calendar.get(Calendar.MINUTE) + "_";
        webDavFileName += calendar.get(Calendar.SECOND) + "_" + calendar.get(Calendar.MILLISECOND);

        if(!StringHelper.isNullOrEmpty(fileExtensionName)) {
            webDavFileName += "." + fileExtensionName;
        }

        sardine.put(server + path + webDavFileName, file.getInputStream());

        return new WebDavNode(new Date(), new Date(), webDavFileName, path, fileName);

    }


    /**
     * 下载指定路径的文件
     * @param path
     * @return
     * @throws IOException
     */
    public ResponseEntity<byte[]> download(String path) throws IOException {
        path = StringHelper.isNullOrEmpty(path) ? "/" :  (path.startsWith("/") ? path : "/" + path);
        
        InputStream is = sardine.get(server + path);
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(fileName.getBytes("gbk"), "iso-8859-1") + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bytes = IOUtils.toByteArray(is);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }


    public static class WebDavNode {
        @JsonSerialize(using = TimeStampFormatSerializer.class)
        private Date createTime;
        @JsonSerialize(using = TimeStampFormatSerializer.class)
        private Date updateTime;
        private String webDavFileName;
        private String webDavPath;
        private String originalFilename;

        public WebDavNode() {
        }

        public WebDavNode(Date createTime, Date updateTime, String webDavFileName, String webDavPath, String originalFilename) {
            this.createTime = createTime;
            this.updateTime = updateTime;
            this.webDavFileName = webDavFileName;
            this.webDavPath = webDavPath;
            this.originalFilename = originalFilename;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public String getWebDavFileName() {
            return webDavFileName;
        }

        public void setWebDavFileName(String webDavFileName) {
            this.webDavFileName = webDavFileName;
        }

        public String getWebDavPath() {
            return webDavPath;
        }

        public void setWebDavPath(String webDavPath) {
            this.webDavPath = webDavPath;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }

        public void setOriginalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
        }
    }

    public static class WebDavPath {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class ResultData {
        public static final String successCode = "0000";
        public static final String failCode = "0001";

        private String resCode;
        private Object data;
        private String msg;

        public ResultData() {
        }

        public ResultData(String resCode, Object data, String msg) {
            this.resCode = resCode;
            this.data = data;
            this.msg = msg;
        }

        public String getResCode() {
            return resCode;
        }

        public void setResCode(String resCode) {
            this.resCode = resCode;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
