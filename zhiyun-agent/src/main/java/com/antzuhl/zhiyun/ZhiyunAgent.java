package com.antzuhl.zhiyun;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * agent init
 * @author AntzUhl
 * @Date 15:53
 */
public class ZhiyunAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("this is an perform monitor agent.");

        ClassFileTransformer transformer = new PerformMonitorTransformer();
        inst.addTransformer(transformer);

    }
}
