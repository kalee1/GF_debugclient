package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

//gottteee
public class UdpUnicastClient implements Runnable {
    private final int port;

    MessageProcessing myMessageProcessor;
    public UdpUnicastClient(int port) {
        this.port = port;
        myMessageProcessor = new MessageProcessing();
    }

    @Override
    public void run() {
        System.out.println("running");
        try(DatagramSocket clientSocket = new DatagramSocket(port)){
            byte[] buffer = new byte[65507];
            clientSocket.setSoTimeout(6000);//if don't receive anything from server for 3000 millis, it will give exception
            while (true){
                DatagramPacket datagramPacket = new DatagramPacket(buffer,0,buffer.length);
                clientSocket.receive(datagramPacket);

                String receivedMessage = new String(datagramPacket.getData());
                myMessageProcessor.processMessage(receivedMessage);
                System.out.println(receivedMessage);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Timeout. Client is closing.");
        }
    }
}
