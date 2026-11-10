package com.example.hotspotlimiter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class DeviceAdapter(
    private val devices: List<Device>,
    private val onToggle: (Device, Boolean) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtIp: TextView = view.findViewById(R.id.txtIp)
        val txtMac: TextView = view.findViewById(R.id.txtMac)
        val switchWhitelist: SwitchMaterial = view.findViewById(R.id.switchWhitelist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = devices[position]
        holder.txtIp.text = item.ip
        holder.txtMac.text = item.mac
        holder.switchWhitelist.isChecked = item.isWhitelisted

        holder.switchWhitelist.setOnCheckedChangeListener { _, isChecked ->
            onToggle(item, isChecked)
        }
    }

    override fun getItemCount() = devices.size
}
