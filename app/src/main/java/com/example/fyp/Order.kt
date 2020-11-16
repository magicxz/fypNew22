package com.example.fyp

class Order (val OrderId: String,val OrderDateTime: String,val orderName: String,val Quantity: Int,val status: String,val totalAmount: Double){
    constructor():this("","","",0,"",0.0){

    }
}