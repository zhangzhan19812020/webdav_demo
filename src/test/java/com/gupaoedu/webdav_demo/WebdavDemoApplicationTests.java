package com.gupaoedu.webdav_demo;


import com.gupaoedu.webdav.WebdavUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebdavDemoApplicationTests {

    @Autowired
    private WebdavUtil util;




    @Test
    void contextLoads() {
        //util.show();
        //System.out.println(sardine.list(util.s));

        try {
           // util.upload();
//            System.out.println(util.exists("ddd"));
//            System.out.println(util.exists("ddd2"));
//            System.out.println(util.exists("ddd3"));
//            //util.createDirectory("/fuck");
            util.createDirectory("ddd/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
