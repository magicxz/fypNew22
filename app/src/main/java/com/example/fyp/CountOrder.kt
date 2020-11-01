package com.example.fyp

class CountOrder{

    companion object{
        var number:Int = 0
        var total:Int = 0
        var commentCount : Int = 0

        var getUser : Users = Users()
        var getPost : Post = Post()

        fun get():Int{
            return CountOrder.number
        }

        fun get1():Int{
            return CountOrder.total
        }

        fun get2():Int{
            return CountOrder.commentCount
        }
    }
}