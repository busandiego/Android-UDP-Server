package com.tcpip.server;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

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
            Log.d("run", "clientAddr: >>>>>>>>>>>>> " + clientAddr);
            String s = "Thanks";
           // buf = s.getBytes();
           // packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);

            Log.d("UDP", "S: Sending: '" + new String(buf) + "'");
            // socket.send(packet);




        } catch (Exception e) {
            Log.e("UDP", "S: Error", e);
        }
    }

    public void setC(Context context){
        final NetworkRequest request =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
        final ConnectivityManager connectivityManager =
                context.getSystemService(ConnectivityManager.class);
        final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {

            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                WifiInfo wifiInfo = (WifiInfo) networkCapabilities.getTransportInfo();
            }
            // etc.
        };
        connectivityManager.requestNetwork(request, networkCallback); // For request
        connectivityManager.registerNetworkCallback(request, networkCallback); // For listen
    }
}