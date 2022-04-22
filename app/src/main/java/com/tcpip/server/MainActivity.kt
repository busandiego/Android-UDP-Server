package com.tcpip.server

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    var datagramSocketStream: DatagramSocket? = null
    private var ipText: TextView? = null
    private var portText: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ipText = findViewById<TextView>(R.id.ipText)
        portText = findViewById<TextView>(R.id.portText)

        val server = ServerKotlin()
        val thread = Thread(server)
        thread.start()
    }

    internal inner class ServerKotlin : Runnable {
        override fun run() {
            // TODO Auto-generated method stub
            try {
                /* Retrieve the ServerName */
                val serverAddr = InetAddress.getByName(SERVERIP)
                Log.d("UDP", "S: Connecting...")

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

                //Change the text on the main activity view
                runOnUiThread {
                    ipText?.text = clientAddr.toString()
                    portText?.text = clientPort.toString()
                }

                Log.d("run", "clientAddr: >>>>>>>>>>>>> $clientAddr")
                val s = "Thanks"

                // 오류나서 주석처리함
                // buf = s.getBytes();
                // packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);

                Log.d("UDP", "S: Sending: '" + String(buf) + "'")
                // socket.send(packet);
            } catch (e: java.lang.Exception) {
                Log.e("UDP", "S: Error", e)
            }
        }
    }
    companion object {
        // public static final String SERVERIP = "192.168.58.112"; // 'Within' the emulator!
        const val SERVERIP = "255.255.255.255" // 'Within' the emulator!
        const val SERVERPORT = 50001
    }



    /*fun setUDP() {
         var port = 8000
         var socket: DatagramSocket? = null
        var receivePacket: DatagramPacket? = null

        try {
            val serverAddr = InetAddress.getByName(SERVERIP);
            socket = DatagramSocket(port, serverAddr) //8000 port로 UDP 서버를 실행합니다.
            Log.d("UDP", "S: Connecting...")
            var buf = ByteArray(1500) //byte를 선언 합니다.
            while (true) {  //Client에서 요청을 기다려야 하기 때문에 while문을 사용합니다.
                receivePacket = DatagramPacket(buf, buf.size)
                Log.d("UDP", "S: Receiving...")
                socket!!.receive(receivePacket)
                Log.d("UDP", "S: Received : " + String(receivePacket!!.getData()) + "")
                Log.d("UDP", "S: Done.")
                val clientAddr: InetAddress = receivePacket!!.getAddress()
                port = receivePacket!!.getPort()
                var s: String = String(receivePacket!!.getData()) //String을 복사합니다.
                Log.d("UDP", "Receive : $s")
                s = s.toUpperCase() //대문자로 변환합니다.
                buf = s.toByteArray() //Byte단위로 변환합니다.
                Log.d("UDP", "UpperCase : $s")

                //Client로 다시 전송합니다.
                receivePacket = DatagramPacket(buf, buf.size, clientAddr, port)
                Log.d("UDP", "Send " + String(buf))
                socket!!.send(receivePacket)
            }
        } catch (ex: Exception) {
            Log.e("UDP", "S: Error", ex)
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
    }

}
