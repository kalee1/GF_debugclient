package com.company;

import java.io.IOException;
import java.net.*;

public class UdpServer implements Runnable{
    private final int clientPort;

    public UdpServer(int clientPort) {
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        //port doesn't really matter but can't be the same as any other program
        try(DatagramSocket serverSocket = new DatagramSocket(50000)){
            for(int i = 0; i < 300; i ++){
                String message = "Message number " + i;

                DatagramPacket datagramPacket = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        InetAddress.getByName("192.168.11.139"),
                        clientPort
                );
                serverSocket.send(datagramPacket);
            }
        }catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}