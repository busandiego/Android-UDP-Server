package com.tcpip.server

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress


class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"

    private var port = 8000
    private var socket: DatagramSocket? = null
    private var receivePacket: DatagramPacket? = null
    var udpPacket: DatagramPacket? = null
    var data = ByteArray(128)
    val SERVERIP = "192.168.58.112"
    var datagramSocketStream: DatagramSocket? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread {
            //    setUDP()
            openUdpPort()
        }.start()

        val server = Server()
        val thread = Thread(server)
      //  thread.start()
    }


    fun setUDP() {
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
    }


    private fun openUdpPort() {
            try {
                datagramSocketStream = DatagramSocket(null)
                datagramSocketStream?.reuseAddress = true
                datagramSocketStream?.broadcast = true
                datagramSocketStream?.bind(InetSocketAddress(50001))

                val bufferLen = 1500
                val bufferByteArray = ByteArray(bufferLen)
                val datagramPacket = DatagramPacket(bufferByteArray, bufferByteArray.size)

                datagramSocketStream?.receive(datagramPacket)

                val noBytesRead = datagramPacket.length
                Log.d(TAG, "openUdpPort: >>>>>>>>>>>>>>>> $noBytesRead")
              //  interpretUdpData(datagramPacket.data)

            } catch (e: Exception) {
                e.printStackTrace()

            }
        }

    override fun onDestroy() {
        super.onDestroy()
        socket?.close()
        datagramSocketStream?.close()
    }

}
