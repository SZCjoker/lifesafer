package com.lifesafer.network

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object UdpBroadcastHelper {
    private const val BROADCAST_PORT = 50000
    private const val BROADCAST_IP = "255.255.255.255"
    private var listening = false

    fun sendBroadcast(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = DatagramSocket()
                socket.broadcast = true
                val data = message.toByteArray()
                val packet = DatagramPacket(data, data.size, InetAddress.getByName(BROADCAST_IP), BROADCAST_PORT)
                socket.send(packet)
                socket.close()
            } catch (e: Exception) {
                Log.e("UdpBroadcastHelper", "Broadcast error: ${e.message}")
            }
        }
    }

    fun startListening(onMessageReceived: (String, InetAddress) -> Unit) {
        if (listening) return
        listening = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = DatagramSocket(BROADCAST_PORT)
                socket.broadcast = true
                val buffer = ByteArray(1024)
                while (listening) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    onMessageReceived(message, packet.address)
                }
                socket.close()
            } catch (e: Exception) {
                Log.e("UdpBroadcastHelper", "Listen error: ${e.message}")
            }
        }
    }

    fun stopListening() {
        listening = false
    }
}
