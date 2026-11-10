package com.example.hotspotlimiter

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topjohnwu.superuser.Shell

class MainActivity : AppCompatActivity() {

    private lateinit var iface: String
    private lateinit var adapter: DeviceAdapter
    private val deviceList = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Shell.getShell()

        iface = TrafficManager.getHotspotInterface()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDevices)
        val btnActivate = findViewById<Button>(R.id.btnActivate)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)

        adapter = DeviceAdapter(deviceList) { device, isChecked ->
            device.isWhitelisted = isChecked
            if (isChecked) {
                TrafficManager.setHighSpeed(iface, device.ip)
                Toast.makeText(this, "${device.ip} Whitelisted (High Speed)", Toast.LENGTH_SHORT).show()
            } else {
                applyRules()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnActivate.setOnClickListener {
            applyRules()
            Toast.makeText(this, "Default 200 Kbps Limit Activated!", Toast.LENGTH_SHORT).show()
        }

        btnReset.setOnClickListener {
            TrafficManager.resetAll(iface)
            Toast.makeText(this, "All limits reset to full speed", Toast.LENGTH_SHORT).show()
        }

        btnRefresh.setOnClickListener {
            refreshDevices()
        }

        refreshDevices()
    }

    private fun refreshDevices() {
        deviceList.clear()
        deviceList.addAll(TrafficManager.getConnectedDevices())
        adapter.notifyDataSetChanged()
    }

    private fun applyRules() {
        TrafficManager.initTrafficControl(iface)
        deviceList.filter { it.isWhitelisted }.forEach {
            TrafficManager.setHighSpeed(iface, it.ip)
        }
    }
}
