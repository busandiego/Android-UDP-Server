package com.tcpip.server

import android.util.Log
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ServerKotlin : Runnable {
    override fun run() {
        // TODO Auto-generated method stub
        try {
            /* Retrieve the ServerName */
            val serverAddr = InetAddress.getByName(SERVERIP)
            Log.d("UDP", "S: Connecting...")

            /*  DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] buf = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);*/

            /* Create new UDP-Socket */
            val socket = DatagramSocket(SERVERPORT, serverAddr) //serverAddr
            socket.broadcast = true
            /* By magic we know, how much data will be waiting for us */
            val buf = ByteArray(1500)
            /* Prepare a UDP-Packet that can
             * contain the data we want to receive */
            val packet = DatagramPacket(buf, buf.size)
            Log.d("UDP", "S: Receiving...")

            /* Receive the UDP-Packet */socket.receive(packet)
            Log.d("UDP", "S: Received: '" + String(packet.data) + "'")
            Log.d("UDP", "S: Done.")
            val clientAddr = packet.address
            val clientPort = packet.port
            Log.d("run", "clientAddr: >>>>>>>>>>>>> $clientAddr")
            val s = "Thanks"

            // 오류나서 주석처리함
            // buf = s.getBytes();
            // packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);

            Log.d("UDP", "S: Sending: '" + String(buf) + "'")
            // socket.send(packet);
        } catch (e: Exception) {
            Log.e("UDP", "S: Error", e)
        }
    }

    companion object {
        // public static final String SERVERIP = "192.168.58.112"; // 'Within' the emulator!
        const val SERVERIP = "255.255.255.255" // 'Within' the emulator!
        const val SERVERPORT = 50001
    }
}