package com.tcpip.server

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import androidx.core.app.ActivityCompat.requestPermissions
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var ipText: TextView? = null
    private var portText: TextView? = null
    private var running = true
    private var socket: DatagramSocket ?= null
    private var clientAddr: InetAddress ?= null
    private var clientPort: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ipText = findViewById(R.id.ipText)
        portText = findViewById(R.id.portText)
            ///

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wInfo = wifiManager.connectionInfo
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val macAddress =  wInfo.macAddress
            Log.d(TAG, "macAddress: >>>>>>>> $macAddress")
        }

        val mac = getMacAddr()
        Log.d(TAG, "onCreate: >>>>>>>> $mac")

        Log.d(TAG, "wifiManager.dhcpInfo.ipAddress ${wifiManager.dhcpInfo.ipAddress}")
        Log.d(TAG, "wifiManager.dhcpInfo.serverAddress ${wifiManager.dhcpInfo.serverAddress}")
        Log.d(TAG, "wifiManager.dhcpInfo.dns1 ${wifiManager.dhcpInfo.dns1}")
        Log.d(TAG, "wifiManager.dhcpInfo.dns2 ${wifiManager.dhcpInfo.dns2}")




        /*  val permissionlistener: PermissionListener = object : PermissionListener {
              override fun onPermissionGranted() {
                  Toast.makeText(this@MainActivity, "권한 허가", Toast.LENGTH_SHORT).show()
              }

              override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
              }

              fun onPermissionDenied(deniedPermissions: ArrayList<String?>) {
                  Toast.makeText(this@MainActivity, "권한 거부\n$deniedPermissions", Toast.LENGTH_SHORT)
                      .show()
              }
          }

          TedPermission.with(this)
              .setPermissionListener(permissionlistener)
              .setDeniedMessage("접근 거부하셨습니다.\n[설정] - [권한]에서 권한을 허용해주세요.")
              .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_SETTINGS)
              .check();*/


      //  requestPermissions(this, String(Manifest.permission.WRITE_SETTINGS))
      //  requestPermissions(this, String(Manifest.permission.WRITE_SETTINGS))
        val server = ServerKotlin()
        val thread = Thread(server)
        thread.start()


    }

    internal inner class ServerKotlin : Runnable {
        override fun run() {
            // TODO Auto-generated method stub
            running = true;

            try {
                /* Retrieve the ServerName */
                 val serverAddr = InetAddress.getByName(SERVERIP) //
                Log.d("UDP", "S: Connecting...")

                /* Create new UDP-Socket */
                socket = DatagramSocket(SERVERPORT) //50001 port로 UDP 서버를 실행 // , serverAddr
                socket!!.broadcast = true

                /* By magic we know, how much data will be waiting for us */
                val buf = ByteArray(1500)
                while(running){ // client 요청을 기다려야해서 while문 사용
                    /* Prepare a UDP-Packet that can
                * contain the data we want to receive */
                    var packet = DatagramPacket(buf, buf.size)
                    Log.d("UDP", "S: Receiving...")

                    /* Receive the UDP-Packet */
                    socket!!.receive(packet)

                    clientAddr = packet.address
                    clientPort = packet.port


                    Log.d("UDP", "clientAddr: $clientAddr")
                    Log.d("UDP", "clientPort: $clientPort")

                    packet = DatagramPacket(buf, buf.size, clientAddr, clientPort);

                    val received = String(packet.data, 0, packet.length).trim { it <= ' ' }
                    Log.d("UDP", "S: Received: '" + received + "'")
                    Log.d("UDP", "S: Done.")

                    if(clientAddr!= null){ // ip를 받게 되면 running이 false 처리되서 while을 빠져나옴
                        running = false
                        continue // 필수로 써야됨 안쓰면 오류
                    }
                    // socket!!.send(packet);
                    // Log.d("UDP", "S: send Packet: '" + packet + "'")
                }

                socket!!.close()
                Log.d("UDP", "S: Socket Close.")
                Log.d("UDP", "clientAddr.toString() >>. ${clientAddr.toString()}")

                val uniqueID: String = UUID.randomUUID().toString()
                Log.d("UDP", "uniqueID >>. ${uniqueID}")
                //Change the text on the main activity view
                runOnUiThread {
                    ipText?.text = clientAddr.toString()
                    portText?.text = clientPort.toString()
                }

                 setCM(this@MainActivity)
             /*   val nsh = NetworkStatusHelper(this@MainActivity)
                nsh.onActive()*/
               // nsh.onInactive()

                Log.d("run", "clientAddr: >>>>>>>>>>>>> $clientAddr")

                // 오류나서 주석처리함
                // buf = s.getBytes();
                // packet = new DatagramPacket(buf, buf.length, clientAddr, clientPort);
                // Log.d("UDP", "S: Sending: '" + String(buf) + "'")
                // socket.send(packet);




            } catch (e: java.lang.Exception) {
                Log.e("UDP", "S: Error", e)
            }
        }
    }
    companion object {
        // public static final String SERVERIP = "192.168.58.112"; // 'Within' the emulator!
        // 192.168.2.2
     //   const val SERVERIP = "255.168.1.255" // 'Within' the emulator!
      //  const val SERVERIP = "192.168.1.255" // 'Within' the emulator!
       const val SERVERIP = "255.255.255.255" // 'Within' the emulator!
     //   const val SERVERIP = "192.168.25.255" // 'Within' the emulator!
     //   const val SERVERIP = "192.168.43.255" // 'Within' the emulator!
     //   const val SERVERIP = "192.168.43.1" // 'Within' the emulator!
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
                buf = s.toByteArray() //Byte단위Connecting...로 변환합니다.
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


    fun setCM(context: Context) {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            // .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        val connectivityManager = context.getSystemService(
            ConnectivityManager::class.java
        )

      //  connectivityManager.getLinkProperties(android.net.Network)

        val networkCallback: NetworkCallback = object : NetworkCallback() {
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                val dhcp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val dhcp = linkProperties.dhcpServerAddress
                    val dhcp2 = linkProperties.dhcpServerAddress?.hostAddress
                    Log.d(TAG, "dhcp: >>>>>>>>> $dhcp")
                    Log.d(TAG, "dhcp: >>>>>>>>> $dhcp2")
                } else {

                }

            }

            override fun onAvailable(network: Network) {
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {

                val wifiInfo = networkCapabilities.transportInfo as WifiInfo?
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.macAddress}")
         //       Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ networkCapabilities.capabilities.}")
                // Log.d(TAG, "networkCapabilities.ownerUid: >>>>>>>> ${networkCapabilities.ownerUid}")
                Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.ssid}")
                Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.bssid}")
                Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.bssid}")
                // Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.get}")
                        // wifiInfo.
            } // etc.
        }
        connectivityManager.requestNetwork(request, networkCallback) // For request
        connectivityManager.registerNetworkCallback(request, networkCallback) // For listen
    }

/*
    class NetworkStatusHelper(private val context: Context) {

        private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback
        var connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val wifiManager =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        private fun getConnectivityManagerCallback() =
            object : ConnectivityManager.NetworkCallback() {

                override fun onUnavailable() {
                    super.onUnavailable()
                    //  announceStatus(Pair(NetworkStatus.UNAVAILABLE, ""))
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        Log.d(
                            "TAG",
                            "onCapabilitiesChanged: transport info = ${
                                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.transportInfo
                            }"
                        )

                    }
                    try {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                            Log.d("TAG", "onCapabilitiesChanged: wifi SSID ${wifiInfo}")
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return
                            }
                            Log.d("TAG", "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo.macAddress}")
                            //       Log.d(TAG, "onCapabilitiesChanged: >>>>>>>> ${ networkCapabilities.capabilities.}")
                            // Log.d(TAG, "networkCapabilities.ownerUid: >>>>>>>> ${networkCapabilities.ownerUid}")
                            Log.d("TAG", "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.ssid}")
                            Log.d("TAG", "onCapabilitiesChanged: >>>>>>>> ${ wifiInfo?.bssid}")

                            if (wifiInfo.ssid.contains("XXXX-")) {
                                //  announceStatus(Pair(NetworkStatus.CONNECTED_TO_XXXX, wifiInfo.ssid))
                            } else {
                                //  announceStatus(Pair(NetworkStatus.CONNECTED_TO_OTHER, wifiInfo.ssid))
                            }

                        } else {

                        }

                    } catch (e: Exception) {
                        Log.d("TAG", "onCapabilitiesChanged: exception = $e")
                    }

                    try {
                        Log.d("TAG", "onCapabilitiesChanged: ${wifiManager.connectionInfo.ssid}")
                    } catch (e: Exception) {
                        Log.d("TAG", "onAvailable: exception = $e")
                    }

                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    //    announceStatus(Pair(NetworkStatus.CONNECTION_LOST, ""))
                }

            }

        */
/*fun announceStatus(networkResposePair: Pair<NetworkStatus, String>) {
          //  postValue(networkResposePair)
        }*//*


        fun onActive() {
            //  super.onActive()
            connectivityManagerCallback = getConnectivityManagerCallback()
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
        }

        fun onInactive() {
            // super.onInactive()
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        }
    }
*/

    fun getMacAddr(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.getName().equals("wlan0", ignoreCase=true)) continue

                val macBytes = nif.getHardwareAddress() ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b))
                }

                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {

        }

        return "02:00:00:00:00:00"
    }


}
