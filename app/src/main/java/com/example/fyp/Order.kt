package com.example.fyp

class Order (val OrderId: String,val OrderDateTime: String,val orderFoodName: String,val Quantity: Int,val status: String,val totalAmount: Double, val paymentMethod : String, val userId : String){
    constructor():this("","","",0,"",0.0,"",""){

    }
}