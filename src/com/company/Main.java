package com.company;


public class Main {
    public static void main(String[] args){
        UdpUnicastClient udpUnicastClient = new UdpUnicastClient(11115);
        Thread runner = new Thread(udpUnicastClient);
        runner.start();
    }
}