package com.antzuhl.zhiyun.config;

import com.antzuhl.zhiyun.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author AntzUhl
 * Blog    http://antzuhl.cn
 * Github  https://github.com/CasterWx
 * Date 2020/09/27 16:27
 */
@Slf4j
public class ZookeeperConnection {

    private static ZooKeeper zooKeeper;

    private ZookeeperConnection() {}

    public static ZooKeeper getInstance() {
        while (zooKeeper == null) {
            zkClient();
        }
        return zooKeeper;
    }

    public static void zkClient() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zooKeeper = new ZooKeeper(Constants.ZOOKEEPER_ADDRESS,
                    Constants.ZOOKEEPER_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            log.info("Init Zookeeper connect state: {}", zooKeeper.getState());
        } catch (IOException e) {
            log.info("Zookeeper Connection IOException : {}", e.getMessage());
        } catch (InterruptedException e) {
            log.info("Zookeeper Connection InterruptedException : {}", e.getMessage());
        }
    }
}
