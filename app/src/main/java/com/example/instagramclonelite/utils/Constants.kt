package com.example.instagramclonelite.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Constants {
    companion object {
        const val ALL_POSTS = "allPosts"
        private const val LIKES = "likes"
        private const val DISLIKES = "dislikes"
        private const val userPostLocation = "/userPost"


        fun getAllPostReference(): DatabaseReference {
            return FirebaseDatabase.getInstance(" https://instagram-clone-lite-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("$ALL_POSTS/")
        }

        fun getUserPostReference(userId: String): DatabaseReference{
            return FirebaseDatabase.getInstance(" https://instagram-clone-lite-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("$userPostLocation/$userId/")
        }

        fun getSpecificPostReference(postId: String): DatabaseReference {
            return FirebaseDatabase.getInstance(" https://instagram-clone-lite-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("$ALL_POSTS/$postId/")
        }

        fun getPostLikeReference(postId: String): DatabaseReference{
            return FirebaseDatabase.getInstance(" https://instagram-clone-lite-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("$LIKES/$postId/")
        }

        fun getPostDislikeReference(postId: String): DatabaseReference{
            return FirebaseDatabase.getInstance(" https://instagram-clone-lite-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("$DISLIKES/$postId/")
        }




        private const val postLocation = "/Posts"


        fun getPostStorageReference(): StorageReference {
            return FirebaseStorage.getInstance().getReference(postLocation)
        }

        fun getStorageReferenceByUrl(url: String): StorageReference {
            return FirebaseStorage.getInstance().getReferenceFromUrl(url)
        }
    }
}