package com.antzuhl.zhiyun.common;

/**
 * @author AntzUhl
 * Date 2020/09/27 16:17
 */
public class Constants {

    /** zookeeper address */
    public static final String ZOOKEEPER_ADDRESS = "localhost:2181";

    /** zookeeper timeout */
    public static final int ZOOKEEPER_TIMEOUT = 3000;

    /** zookeeper base path */
    public static final String ZOOKEEPER_BASE_PATH = "/zhiyun";

    /** zookeeper cache path */
    public static final String ZOOKEEPER_CACHE_PATH = "/zhiyun/change/";
    public static final String ZOOKEEPER_CACHE_PATH_EXIST = "/zhiyun/change";
}
