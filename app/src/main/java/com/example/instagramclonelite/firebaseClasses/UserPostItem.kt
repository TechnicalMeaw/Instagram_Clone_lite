package com.example.instagramclonelite.firebaseClasses

data class UserPostItem(val userId: String, val postId: String){
    constructor() : this("", "")
}
