package com.example.instagramclonelite.ui.addPost

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.example.instagramclonelite.repo.PostRepository

class CreatePostViewModel : ViewModel() {
    private val repository = PostRepository()

    fun addPost(post: PostItem, imageBitmap: Bitmap) {
        repository.addPost(post, imageBitmap)
    }

}