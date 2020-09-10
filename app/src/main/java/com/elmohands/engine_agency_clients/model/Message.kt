package com.elmohands.engine_agency_clients.model
data class Message(
val id: Int,
val idDocument:String,
val image:String,
val seen:String,
val messageText:String
){

    constructor() : this(0,"","","","")
}