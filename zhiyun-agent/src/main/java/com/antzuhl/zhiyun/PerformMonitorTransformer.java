package com.antzuhl.zhiyun;

import com.antzuhl.zhiyun.common.Constants;
import com.antzuhl.zhiyun.config.ZookeeperConnection;
import com.antzuhl.zhiyun.service.ZookeeperService;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
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

    private static final String PACKAGE_PREFIX = "com.antzuhl.zhiyun    ";

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
            ZookeeperService.createNode(Constants.ZOOKEEPER_BASE_PATH, "");

            for (CtBehavior method : methods) {
                // registry method
                ZookeeperService.createNode(Constants.ZOOKEEPER_BASE_PATH
                        + "/" + method.getName(), "");
                System.out.println("method trans:"+method.getName() + ", class:"+className);
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            log.error("PerformMonitorTransformer transform error: {}", e.getMessage());
        }
        return null;
    }
}
