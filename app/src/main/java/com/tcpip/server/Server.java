package com.tcpip.server;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server implements Runnable {

    // public static final String SERVERIP = "192.168.58.112"; // 'Within' the emulator!
    public static final String SERVERIP = "255.255.255.255"; // 'Within' the emulator!
    public static final int SERVERPORT = 50001;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            /* Retrieve the ServerName */
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.d("UDP", "S: Connecting...");

          /*  DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] buf = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);*/

            /* Create new UDP-Socket */
            // SERVERPORT, serverAddr
            DatagramSocket socket = new DatagramSocket(SERVERPORT, serverAddr); //serverAddr
            socket.setBroadcast(true);
            /* By magic we know, how much data will be waiting for us */
            byte[] buf = new byte[1500];
            /* Prepare a UDP-Packet that can
             * contain the data we want to receive */
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            Log.d("UDP", "S: Receiving...");

            /* Receive the UDP-Packet */
            socket.receive(packet);

            Log.d("UDP", "S: Received: '" + new String(packet.getData()) + "'");
            Log.d("UDP", "S: Done.");

            InetAddress clientAddr = packet.getAddress();
            int clientPort = packet.getPort();
            String s = "Thanks";
            buf = s.getBytes();
            packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);

            Log.d("UDP", "S: Sending: '" + new String(buf) + "'");
            socket.send(packet);

        } catch (Exception e) {
            Log.e("UDP", "S: Error", e);
        }
    }
}