package com.antzuhl.zhiyun;

import com.antzuhl.zhiyun.common.Constants;
import com.antzuhl.zhiyun.service.ZookeeperService;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * registry class info
 * @author AntzUhl
 * @Date 2020/09/27/16:38
 * */
@Slf4j
public class PerformMonitorTransformer implements ClassFileTransformer {

    private static final String PACKAGE_PREFIX = "com.antzuhl.zhiyun";

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        try {
            if (className == null) {
                log.info("PerformMonitorTransformer transform className is null.");
                return null;
            }
            String currentClassName = className.replaceAll("/", ".");

            if (!currentClassName.startsWith(PACKAGE_PREFIX)) {
                return null;
            }

            CtClass ctClass = ClassPool.getDefault().get(currentClassName);
            CtBehavior[] methods = ctClass.getDeclaredBehaviors();
            // init base path
            if (ZookeeperService.exists(Constants.ZOOKEEPER_BASE_PATH, false) == null) {
                log.info("Zookeeper base path is null. now create {}", Constants.ZOOKEEPER_BASE_PATH);
                ZookeeperService.createNode(Constants.ZOOKEEPER_BASE_PATH, "");
            }

            for (CtBehavior method : methods) {
                /**
                 * registry method info
                 * template:
                 *      dubbo://10.155.10.154:20063
                 *             /com.kooup.k12.message.service.IMessagePoolService
                 *                  ?anyhost=true
                 *                  &application=kooup-dubbo-project-biz
                 *                  &methods=queryNeedSendMessage,updateStatusByKey,insert,updateStatusByIds
                 *                  &side=provider&threads=1200
                 *                  &timestamp=1601192935507
                 *                  &timestampFormat=2020-09-27_15-48-55.507
                 * */
                System.out.println("source file:" + ctClass.getClassFile().getSourceFile());
                ZookeeperService.createNode(Constants.ZOOKEEPER_BASE_PATH
                        + "/" + currentClassName + "&" + method.getName(), ctClass.getClassFile().getSourceFile());
//                System.out.println("method trans:" + method.getName() + ", class:"+className);
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            log.error("PerformMonitorTransformer transform error: {}", e.getMessage());
        }
        return null;
    }
}
