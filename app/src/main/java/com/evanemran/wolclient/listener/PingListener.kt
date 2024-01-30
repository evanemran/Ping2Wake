package com.evanemran.wolclient.listener

interface PingListener {
    fun onSuccess(message: String)
    fun onFailure(errorMessage: String)
}