package com.mservice.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * @author uyen.tran
 */
public class LogUtils {
    static Logger logger = LoggerFactory.getLogger(LogUtils.class);

    public static void init(){
        if (logger == null) {
            logger = LoggerFactory.getLogger(LogUtils.class);
        }
    }

    public static void info(String serviceCode, Object object){
        if (logger != null) {
            logger.info("[{}]: {}", serviceCode, object != null ? object.toString() : "null");
        }
    }
    
    public static void info(Object object){
        if (logger != null) {
            logger.info(object != null ? object.toString() : "null");
        }
    }

    public static void debug(Object object){
        if (logger != null) {
            logger.debug(object != null ? object.toString() : "null");
        }
    }

    public static void error(Object object){
        if (logger != null) {
            logger.error(object != null ? object.toString() : "null");
        }
    }

    public static void warn(Object object){
        if (logger != null) {
            logger.warn(object != null ? object.toString() : "null");
        }
    }
}
