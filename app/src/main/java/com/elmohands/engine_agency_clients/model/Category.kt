package com.elmohands.engine_agency_clients.model
data class Category(
val id: Int,
val idDocument:String,
val categoryName:String
){

    constructor() : this(0,"","")
}