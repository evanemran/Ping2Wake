package com.evanemran.wolclient.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.evanemran.wolclient.model.Device;

import java.util.List;

@Dao
public interface MainDAO {
    @Insert(onConflict = REPLACE)
    void insert(Device device);

    @Delete
    void delete(Device device);

    @Delete
    void reset(List<Device> deviceList);

    @Query("UPDATE device SET deviceName = :title, deviceIp = :ip WHERE deviceId = :id")
    void update(int id, String title, String ip);

    @Query("SELECT * FROM device ORDER BY deviceId DESC")
    List<Device> getAll();

}
