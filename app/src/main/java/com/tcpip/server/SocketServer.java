package com.tcpip.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable {


    public void startServer() {
        ServerSocket sockserv = null;
        Socket sockcli = null;

        try {
            sockserv = new ServerSocket(9629);
            while(true) {
                System.out.println("Server is waiting for request.");

                sockcli = sockserv.accept();
                System.out.println("Server Socket is Accepted!");

                InputStream stream = sockcli.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                String data = null;
                StringBuilder receiveData = new StringBuilder();
                while((data = in.readLine()) != null) {
                    receiveData.append(data);
                }
                System.out.println("Receive Data :"+receiveData);
                in.close();
                stream.close();
                sockcli.close();
                if(receiveData.toString().equals("End of TEST")) {
                    System.out.println("Stop Socket Server!");
                    break;
                }
                System.out.println("------");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(sockserv != null) {
                try {
                    sockserv.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        startServer();
    }
}

