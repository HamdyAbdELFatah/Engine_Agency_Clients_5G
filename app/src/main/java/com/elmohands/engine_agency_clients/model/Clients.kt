package com.elmohands.engine_agency_clients.model
data class Clients(
    val id: Int,
    val idDocument:String,
    val name:String,
    val number:String,
    val price:String,
    val note:String,
    val date:String,
    val category:String){

    var checked=false

    constructor() : this(0,"","","","","","","")
}