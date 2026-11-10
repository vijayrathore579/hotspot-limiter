package com.example.hotspotlimiter

import com.topjohnwu.superuser.Shell

object TrafficManager {

    fun getHotspotInterface(): String {
        val result = Shell.cmd("ip route | grep -E '192.168\\.' | awk '{print $3}' | head -n 1").exec()
        val iface = result.out.firstOrNull()?.trim()
        return if (!iface.isNullOrEmpty()) iface else "wlan1"
    }

    fun initTrafficControl(iface: String) {
        Shell.cmd(
            "tc qdisc del dev $iface root 2>/dev/null",
            "tc qdisc add dev $iface root handle 1: htb default 20",
            "tc class add dev $iface parent 1: classid 1:10 htb rate 50mbit ceil 50mbit",
            "tc class add dev $iface parent 1: classid 1:20 htb rate 200kbit ceil 200kbit"
        ).exec()
    }

    fun setHighSpeed(iface: String, ip: String) {
        Shell.cmd(
            "tc filter add dev $iface protocol ip parent 1: prio 1 u32 match ip dst $ip/32 flowid 1:10"
        ).exec()
    }

    fun resetAll(iface: String) {
        Shell.cmd("tc qdisc del dev $iface root 2>/dev/null").exec()
    }

    fun getConnectedDevices(): List<Device> {
        val list = mutableListOf<Device>()
        val result = Shell.cmd("ip neigh show").exec()
        for (line in result.out) {
            if (line.contains("lladdr") && !line.contains("FAILED")) {
                val parts = line.split("\\s+".toRegex())
                if (parts.size >= 5) {
                    val ip = parts[0]
                    val mac = parts[4]
                    list.add(Device(ip, mac, isWhitelisted = false))
                }
            }
        }
        return list
    }
}

data class Device(val ip: String, val mac: String, var isWhitelisted: Boolean)
