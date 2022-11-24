package com.example.instagramclonelite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclonelite.R
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.example.instagramclonelite.firebaseClasses.UserPostItem
import com.example.instagramclonelite.utils.Constants.Companion.getSpecificPostReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProfileActivityAdapter(private val context: Context) : RecyclerView.Adapter<ProfileActivityAdapter.UserActivityViewHolder>() {

    private val allUserActivity : ArrayList<UserPostItem> = ArrayList()

    inner class UserActivityViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.userActivityImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserActivityViewHolder {
        return UserActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_post_item, parent, false))
    }

    override fun onBindViewHolder(holder: UserActivityViewHolder, position: Int) {
        val currentUserPost = allUserActivity[position]

        // Get post from postId
        getSpecificPostReference(currentUserPost.postId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val post = snapshot.getValue(PostItem::class.java)
                    if (post != null){
                        Glide.with(context).load(post.postMediaUrl).into(holder.image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getItemCount(): Int {
        return allUserActivity.size
    }

    fun updateUserActivities(list : List<UserPostItem>){
        allUserActivity.clear()
        allUserActivity.addAll(list)
        this.notifyDataSetChanged()
    }
}