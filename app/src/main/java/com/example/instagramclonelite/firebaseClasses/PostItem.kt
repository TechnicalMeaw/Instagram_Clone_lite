package com.example.instagramclonelite.firebaseClasses

data class PostItem(val userId: String,
                    val userName: String,
                    var userProfileUrl: String,
                    val postId: String,
                    val postType: String,
                    var postMediaUrl: String,
                    val postCaption: String,
                    val likeCount: Long,
                    val dislikeCount: Long,
                    val postTimeInMillis: Long){
    constructor() : this("","","","","","","",0,0,-1)
}
