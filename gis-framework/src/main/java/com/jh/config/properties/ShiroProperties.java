//package com.jh.config.properties;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//@ConfigurationProperties(prefix = "shiro")
//@Component
//@Configuration
//public class ShiroProperties {
//    @Value("shiro.cookie.cookieName")
//    private String cookieName;
//
//    @Value("shiro.session.timeout")
//    private int timeout;
//
//    public String getCookieName() {
//        return cookieName;
//    }
//
//    public void setCookieName(String cookieName) {
//        this.cookieName = cookieName;
//    }
//
//    public int getTimeout() {
//        return timeout;
//    }
//
//    public void setTimeout(int timeout) {
//        this.timeout = timeout;
//    }
//}
