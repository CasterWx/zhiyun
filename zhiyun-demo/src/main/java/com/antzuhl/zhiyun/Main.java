package com.antzuhl.zhiyun;

/**
 * @author AntzUhl
 * @Date 16:49
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("ins main run...");
        Print print = new Print();
        while(true) {
            print.print();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
