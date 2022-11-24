package com.example.instagramclonelite.listeners

import com.example.instagramclonelite.firebaseClasses.PostItem

interface PostListeners {
    fun onLikeButtonClicked(currentPost: PostItem)

    fun onDislikeButtonClicked(currentPost: PostItem)

    fun onShareButtonClicked(currentPost: PostItem)

    fun onPostDoubleClicked(currentPost: PostItem)
}