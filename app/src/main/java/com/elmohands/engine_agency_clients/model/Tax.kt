package com.elmohands.engine_agency_clients.model
data class Tax(
val id: Int,
val idDocument:String,
val price:String,
val note:String,
val date:String
){

    constructor() : this(0,"","","","")
}