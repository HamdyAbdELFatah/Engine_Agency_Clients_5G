package com.elmohands.engine_agency_clients.my_interface

import com.elmohands.engine_agency_clients.model.Clients


interface CallBack {
    fun onClick(check:Boolean,position:Clients)
    fun onLongClick(check:Boolean,position:Clients)
}