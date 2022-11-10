package com.evanemran.wolclient.listener

import com.evanemran.wolclient.model.Device

interface AddListener {
    fun onAddClicked(device: Device)
}