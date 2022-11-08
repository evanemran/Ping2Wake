package com.evanemran.wolclient

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device")
class Device {
    @PrimaryKey(autoGenerate = true)
    var deviceId: Int = 0
    @ColumnInfo(name = "deviceName")
    var deviceName: String = ""
    @ColumnInfo(name = "deviceIp")
    var deviceIp: String = ""
    @ColumnInfo(name = "deviceMac")
    var deviceMac = ""
}