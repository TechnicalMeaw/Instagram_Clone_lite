package com.example.instagramclonelite.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.instagramclonelite.firebaseClasses.UserPostItem
import com.example.instagramclonelite.repo.ProfileRepo

class ProfileViewModel : ViewModel() {
    private val repository : ProfileRepo = ProfileRepo()

    fun getUserActivityPosts(userId : String) : LiveData<List<UserPostItem>> {
        repository.getALlUserActivityPosts(userId)
        return repository.userActivityPosts
    }
}