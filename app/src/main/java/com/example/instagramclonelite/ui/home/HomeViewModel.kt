package com.example.instagramclonelite.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.example.instagramclonelite.repo.PostRepository

class HomeViewModel : ViewModel() {

    private val repository = PostRepository()

    fun getAllPosts() : LiveData<List<PostItem>> {
        repository.fetchPosts()
        return repository.allPosts
    }

    fun addLike(postId: String) {
        repository.addLike(postId)
    }

    fun removeLike(postId: String) {
        repository.removeLike(postId)
    }

    fun removeDislike(postId: String) {
        repository.removeDislike(postId)
    }

    fun addDislike(postId: String) {
        repository.addDislike(postId)
    }

}