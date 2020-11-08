package com.example.fyp

class getKey{

    companion object{
        var key:String = "abc"
        var key1:String = "abc"
        var stop:Boolean = false
        var size:Int = 0
        var first:Boolean = false

        fun get():String{
            return getKey.key
        }
        fun get1():String{
            return getKey.key1
        }

    }
}