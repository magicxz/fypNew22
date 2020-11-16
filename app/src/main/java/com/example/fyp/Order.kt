package com.example.fyp

class Order (val OrderId: String,val OrderDateTime: String,val status: String,val subtotal : Double, val deliveryfee : Double,val totalAmount: Double, val paymentMethod : String, val userId : String){
    constructor():this("","","",0.0,0.0,0.0,"",""){

    }
}