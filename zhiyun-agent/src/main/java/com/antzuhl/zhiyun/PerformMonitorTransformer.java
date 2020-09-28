package com.antzuhl.zhiyun;

import com.antzuhl.zhiyun.common.Constants;
import com.antzuhl.zhiyun.service.ZookeeperService;
import javassist.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Calendar;

/**
 * registry class info
 * @author AntzUhl
 * @Date 2020/09/27/16:38
 * */
@Slf4j
public class PerformMonitorTransformer implements ClassFileTransformer {

    private static final java.lang.String PACKAGE_PREFIX = "com.antzuhl.zhiyun";

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        try {
            log.info("=================================transform start=======================================");
            log.info("className: {}", className);
            // TODO 需要判断是初次加载还是从zk中重载的class
            /**
             * HOW TO CHECK
             * 在加载时判断被修改路径是否存在该class，如果存在就去加载该class，如果不存在就正常执行
             * */
            if (className == null) {
                log.info("PerformMonitorTransformer transform className is null.");
                log.info("className is null, other info: {}", classBeingRedefined);
                return null;
            }
            String currentClassName = className.replaceAll("/", ".");

            if (!currentClassName.startsWith(PACKAGE_PREFIX)) {
                return null;
            }


            CtClass ctClass = ClassPool.getDefault().get(currentClassName);
            CtBehavior[] methods = ctClass.getDeclaredBehaviors();
            // 存在change, data为修改后的class路径
            if (ZookeeperService.exists(Constants.ZOOKEEPER_CACHE_PATH + currentClassName, false) != null) {
                // i need transform use zk data
                log.info("getDeclaredMethod");
                CtMethod cm = ClassPool.getDefault().get(currentClassName).getDeclaredMethod("print");
                // 修改方法代码体
                cm.setBody("{ System.out.println(\"nohelo---\"); }");
                // delete zk info
                ZookeeperService.deleteNode(Constants.ZOOKEEPER_CACHE_PATH + currentClassName);
                return ctClass.toBytecode();
            }
            // init base path
            if (ZookeeperService.exists(Constants.ZOOKEEPER_BASE_PATH, false) == null) {
                log.info("Zookeeper base path is null. now create {}", Constants.ZOOKEEPER_BASE_PATH);
                ZookeeperService.createNode(Constants.ZOOKEEPER_BASE_PATH, "", false);
            }
            byte []classCode = ctClass.toBytecode();

            log.info("ctClass: {}", ctClass);
            for (CtBehavior method : methods) {
                ZookeeperService.createNode(Constants.ZOOKEEPER_BASE_PATH
                        + "/" + currentClassName + "?version=" + System.getProperty("zhiyun.version")
                        + "&method=" + method.getName()
                        + "&timestampFormat=" + Calendar.getInstance().getTime().getTime(),
                        new String(classCode), false);
//                  System.out.println("method trans:" + method.getName() + ", class:"+className);
            }
            File file = new File(System.getProperty("user.home") + "\\.zhiyun\\");
            if (!file.exists()) {
                file.mkdir();
            }

            FileOutputStream output = new FileOutputStream(System.getProperty("user.home") + "\\.zhiyun\\" + currentClassName + ".class");
            output.write(classCode);
            output.close();
            System.out.println("文件" + currentClassName + "写入成功!!!");
            return ctClass.toBytecode();
        } catch (Exception e) {
            log.error("PerformMonitorTransformer transform error: {}", e.getMessage());
        }
        return null;
    }
}
