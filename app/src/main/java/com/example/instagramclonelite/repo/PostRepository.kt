package com.example.instagramclonelite.repo

import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.instagramclonelite.firebaseClasses.LikeUnlikeItem
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.example.instagramclonelite.firebaseClasses.UserPostItem
import com.example.instagramclonelite.utils.CommonUtils.Companion.bitmapToByteArray
import com.example.instagramclonelite.utils.Constants
import com.example.instagramclonelite.utils.Constants.Companion.getAllPostReference
import com.example.instagramclonelite.utils.Constants.Companion.getPostDislikeReference
import com.example.instagramclonelite.utils.Constants.Companion.getPostLikeReference
import com.example.instagramclonelite.utils.Constants.Companion.getPostStorageReference
import com.example.instagramclonelite.utils.Constants.Companion.getSpecificPostReference
import com.example.instagramclonelite.utils.Constants.Companion.getUserPostReference
import com.example.instagramclonelite.utils.ImageResizer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class PostRepository {

    private val TAG: String = "PostRepository"

    private val _allPosts: MutableLiveData<List<PostItem>> = MutableLiveData()
    val allPosts: LiveData<List<PostItem>> = _allPosts

    fun addPost(post: PostItem, bitmap: Bitmap) {
        uploadPostImage(post, bitmap)
    }

    private fun uploadPostImage(postItem: PostItem, bitmap: Bitmap) {
        val filename = UUID.randomUUID().toString()
        val ref = getPostStorageReference().child(filename)

        ref.putBytes(bitmapToByteArray(bitmap, 1000000)).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                postItem.postMediaUrl = it.toString()
                updatePostToDatabase(postItem)
            }
        }
    }

    private fun updatePostToDatabase(
        post: PostItem
    ) {
        getAllPostReference().child(post.postId).setValue(post).addOnSuccessListener {
            updatePostToUserProfile(post.postId, post)
        }
    }

    private fun updatePostToUserProfile(postId: String, postItem: PostItem) {
        getUserPostReference(FirebaseAuth.getInstance().uid.toString()).child(postId)
            .setValue(UserPostItem(FirebaseAuth.getInstance().uid.toString(), postId))
    }


    private val postMap: HashMap<String, PostItem> = HashMap()

    fun fetchPosts() {
        getAllPostReference().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val post = snapshot.getValue(PostItem::class.java)
                    if (post != null) {

                        postMap[snapshot.key.toString()] = post
                        _allPosts.postValue(postMap.values.toList())

                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val post = snapshot.getValue(PostItem::class.java)
                    if (post != null) {
                        if (postMap.containsKey(snapshot.key.toString())) {
                            postMap[snapshot.key.toString()] = post
                            _allPosts.postValue(postMap.values.toList())
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (postMap.containsKey(snapshot.key.toString())) {
                    postMap.remove(snapshot.key.toString())
                    _allPosts.postValue(_allPosts.value?.toList() ?: listOf())
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


    fun addLike(postId: String) {
        getSpecificPostReference(postId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val like = snapshot.child("likeCount").value as Long
                    snapshot.child("likeCount").ref.setValue(like + 1)
                    checkIfDisliked(postId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkIfDisliked(postId: String) {
        val ref = getPostDislikeReference(postId).child(FirebaseAuth.getInstance().uid.toString())

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.ref.removeValue()
                    removeDislike(postId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun removeLike(postId: String) {
        getSpecificPostReference(postId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val like = snapshot.child("likeCount").value as Long
                    snapshot.child("likeCount").ref.setValue(like - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun addDislike(postId: String) {
        getSpecificPostReference(postId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val like = snapshot.child("dislikeCount").value as Long
                    snapshot.child("dislikeCount").ref.setValue(like + 1)
                    checkIfLiked(postId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkIfLiked(postId: String) {
        val ref = getPostLikeReference(postId).child(FirebaseAuth.getInstance().uid.toString())

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.ref.removeValue()
                    removeLike(postId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun removeDislike(postId: String) {
        getSpecificPostReference(postId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val like = snapshot.child("dislikeCount").value as Long
                    snapshot.child("dislikeCount").ref.setValue(like - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}