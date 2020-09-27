package com.antzuhl.zhiyun;

/**
 * @author AntzUhl
 * @Date 16:49
 */
public class Main {

    public static void hello() {
        System.out.println("hello");
    }

    public static void main(String[] args) {
        System.out.println("ins main run...");
        hello();
        hello();
        hello();
        hello();
        hello();
        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hello();
        }
    }
}
