package com.antzuhl.zhiyun.service;

import com.antzuhl.zhiyun.config.ZookeeperConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author AntzUhl
 * Blog    http://antzuhl.cn
 * Github  https://github.com/CasterWx
 * Date 2020/09/27 11:24
 */
@Slf4j
public class ZookeeperService {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperService.class);

    /**
     * 判断指定节点是否存在
     *
     * @param path
     * @param needWatch 指定是否复用zookeeper中默认的Watcher
     * @return
     */
    public static Stat exists(String path, boolean needWatch) {
        try {
            return ZookeeperConnection.getInstance().exists(path, needWatch);
        } catch (Exception e) {
            logger.error("【断指定节点是否存在异常】{},{}", path, e);
            return null;
        }
    }

    /**
     * 检测结点是否存在 并设置监听事件
     * 三种监听类型： 创建，删除，更新
     *
     * @param path
     * @param watcher 传入指定的监听类
     * @return
     */
    public static Stat exists(String path, Watcher watcher) {
        try {
            return ZookeeperConnection.getInstance().exists(path, watcher);
        } catch (Exception e) {
            logger.error("【断指定节点是否存在异常】{},{}", path, e);
            return null;
        }
    }

    /**
     * 创建持久化节点
     * @param path
     * @param data
     */
    public static boolean createNode(String path, String data) {
        try {
            ZookeeperConnection.getInstance().create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (Exception e) {
            logger.error("【创建持久化节点异常】{},{},{}", path, data, e);
            return false;
        }
    }

    /**
     * 创建临时节点
     * @param path
     * @param data
     */
    public static boolean createNode(String path, String data, boolean mode) {
        try {
            ZookeeperConnection.getInstance().create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
            logger.error("【创建临时节点异常】{},{},{}", path, data, e);
            return false;
        }
    }

    /**
     * 修改持久化节点
     *
     * @param path
     * @param data
     */
    public static boolean updateNode(String path, String data) {
        try {
            //zk的数据版本是从0开始计数的。如果客户端传入的是-1，则表示zk服务器需要基于最新的数据进行更新。如果对zk的数据节点的更新操作没有原子性要求则可以使用-1.
            //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            ZookeeperConnection.getInstance().setData(path, data.getBytes(), -1);
            return true;
        } catch (Exception e) {
            logger.error("【修改持久化节点异常】{},{},{}", path, data, e);
            return false;
        }
    }

    /**
     * 删除持久化节点
     *
     * @param path
     */
    public static boolean deleteNode(String path) {
        try {
            //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            ZookeeperConnection.getInstance().delete(path, -1);
            return true;
        } catch (Exception e) {
            logger.error("【删除持久化节点异常】{},{}", path, e);
            return false;
        }
    }

    /**
     * 获取当前节点的子节点(不包含孙子节点)
     *
     * @param path 父节点path
     */
    public static List<String> getChildren(String path) throws KeeperException, InterruptedException {
        return ZookeeperConnection.getInstance().getChildren(path, false);
    }

    /**
     * 获取指定节点的值
     *
     * @param path
     * @return
     */
    public static String getData(String path, Watcher watcher) {
        try {
            Stat stat = new Stat();
            byte[] bytes = ZookeeperConnection.getInstance().getData(path, watcher, stat);
            if (bytes == null) {
                return "";
            }
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
