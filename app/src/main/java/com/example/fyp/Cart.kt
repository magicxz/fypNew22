package com.example.fyp

class Cart (val cartId: String,val userId: String,val cartQuantity: Int,val name: String,val price: Double, val image: String){
    constructor():this("","",0,"",0.0,""){

    }
}