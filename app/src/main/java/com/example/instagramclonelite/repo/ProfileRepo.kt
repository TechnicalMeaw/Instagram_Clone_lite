package com.example.instagramclonelite.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.instagramclonelite.firebaseClasses.UserPostItem
import com.example.instagramclonelite.utils.Constants
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ProfileRepo {

    private val _userActivityPosts : MutableLiveData<List<UserPostItem>> = MutableLiveData()
    val userActivityPosts : LiveData<List<UserPostItem>> = _userActivityPosts


    private val userPostMap : HashMap<String, UserPostItem> = HashMap()

    fun getALlUserActivityPosts(userId: String){
        Constants.getUserPostReference(userId).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val post = snapshot.getValue(UserPostItem::class.java)
                    if (post != null){
                        userPostMap[post.postId] = post
                        _userActivityPosts.postValue(userPostMap.values.toList())
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()){
                    val post = snapshot.getValue(UserPostItem::class.java)
                    if (post != null){
                        userPostMap[post.postId] = post
                        _userActivityPosts.postValue(userPostMap.values.toList())
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (userPostMap.containsKey(snapshot.key)){
                    userPostMap.remove(snapshot.key)
                    _userActivityPosts.postValue(userPostMap.values.toList())
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}