package com.antzuhl.zhiyun;

import com.antzuhl.zhiyun.common.Constants;
import com.antzuhl.zhiyun.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.KeeperException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * agent init
 * @author AntzUhl
 * @Date 15:53
 */
@Slf4j
public class ZhiyunAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("this is an perform monitor agent.");
        System.setProperty("zhiyun.version", "1.0");
        ClassFileTransformer transformer = new PerformMonitorTransformer();
        inst.addTransformer(transformer, true);
        log.info("Allow redefineClasses: {}", inst.isRedefineClassesSupported());
        log.info("Allow retransformClasses: {}", inst.isRetransformClassesSupported());

        // 延迟读取类字节码
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ZookeeperService.exists(Constants.ZOOKEEPER_CACHE_PATH_EXIST, false) == null) {
                        ZookeeperService.createNode(Constants.ZOOKEEPER_CACHE_PATH_EXIST, "");
                        log.info("创建change根路径");
                    }

                    List<String> retransformClass = ZookeeperService.getChildren(Constants.ZOOKEEPER_CACHE_PATH_EXIST);
                    log.info("CollectionUtils retransformClass: {}", retransformClass);
                    if (!CollectionUtils.isEmpty(retransformClass)) {
                        retransformClass.stream().forEach(item -> {
                            try {
                                // 需要重新加载的class
                                log.info("retransformClasses: {}", item);
                                Class clazz = Class.forName(item);
                                inst.retransformClasses(clazz);
                                // 执行完成后必须删除对应节点
                            } catch (ClassNotFoundException | UnmodifiableClassException e) {
                                log.error("Agent retransformClasses className: {}, errorMsg: {}", item, e.getMessage());
                            }
                        });
                    }
                } catch (InterruptedException | KeeperException e) {
                    log.error("Anent scheduleAtFixedRate running error : {}", e.getMessage());

                }
            }
        }, 10, 20, TimeUnit.SECONDS);
    }
}
