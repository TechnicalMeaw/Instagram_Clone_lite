package com.example.instagramclonelite.firebaseClasses

data class UserPostItem(val userId: String, val postId: String, val timeStamp : Long = System.currentTimeMillis()){
    constructor() : this("", "")
}
