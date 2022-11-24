package com.example.instagramclonelite.adapter

import android.animation.Animator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instagramclonelite.R
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.example.instagramclonelite.listeners.DoubleClickListener
import com.example.instagramclonelite.listeners.PostListeners
import com.example.instagramclonelite.utils.Constants.Companion.getPostDislikeReference
import com.example.instagramclonelite.utils.Constants.Companion.getPostLikeReference
import com.example.instagramclonelite.utils.DateTimeUtils.Companion.getDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class HomeFeedAdapter(private val context: Context, private val listener: PostListeners) :
    RecyclerView.Adapter<HomeFeedAdapter.FeedViewHolder>() {

    private val allPosts: ArrayList<PostItem> = ArrayList()

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userDp: CircleImageView = itemView.findViewById(R.id.postDpCircleImageView)
        val name: TextView = itemView.findViewById(R.id.postNameTextView)
        val timeStamp: TextView = itemView.findViewById(R.id.postTimeStamp)
        val postImage: ImageView = itemView.findViewById(R.id.postImageView)
        val captionTextView: TextView = itemView.findViewById(R.id.postCaptionTextView)
        val likeTextView: TextView = itemView.findViewById(R.id.postLikeTextView)
        val likeBtn: ImageButton = itemView.findViewById(R.id.postLikeBtn)
        val dislikeTextView: TextView = itemView.findViewById(R.id.postDislikeTextView)
        val dislikeBtn: ImageButton = itemView.findViewById(R.id.postDislikeBtn)
        val shareBtn: ImageButton = itemView.findViewById(R.id.postShareBtn)
        val loveAnimation: LottieAnimationView = itemView.findViewById(R.id.loveAnimation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val viewHolder = FeedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        )


        return viewHolder
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentPost = allPosts[position]

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)


        try {
            // Load Post Image
            Glide.with(context).load(currentPost.postMediaUrl).into(holder.postImage)


            /**
             * Fetch User DP
             * And Profile Name
             */

            Glide.with(context).load(currentPost.userProfileUrl).apply(options).into(holder.userDp)
            holder.name.text = currentPost.userName

            /*<=============>*/

            /**
             * Handle Caption
             */
            if (currentPost.postCaption != "") {
                holder.captionTextView.visibility = View.VISIBLE
                holder.captionTextView.text = currentPost.postCaption
            } else {
                holder.captionTextView.visibility = View.GONE
            }


            /**
             * Likes and Dislikes
             */
            holder.timeStamp.text = getDate(currentPost.postTimeInMillis)
            holder.likeTextView.text = currentPost.likeCount.toString() + " Likes"
            holder.dislikeTextView.text = currentPost.dislikeCount.toString() + " Dislikes"


            /**
             * Check Liked or Not
             */
            getPostLikeReference(currentPost.postId).child(FirebaseAuth.getInstance().uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            try {
                                holder.likeBtn.backgroundTintList =
                                    AppCompatResources.getColorStateList(
                                        context,
                                        R.color.purple_500
                                    )

                            } catch (e: Exception) {
                                e.stackTrace
                            }
                        } else {
                            holder.likeBtn.backgroundTintList =
                                AppCompatResources.getColorStateList(context, R.color.gray)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            /**
             * Check Disliked or Not
             */
            getPostDislikeReference(currentPost.postId).child(FirebaseAuth.getInstance().uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            try {
                                holder.dislikeBtn.backgroundTintList =
                                    AppCompatResources.getColorStateList(
                                        context,
                                        R.color.purple_500
                                    )

                            } catch (e: Exception) {
                                e.stackTrace
                            }
                        } else {
                            holder.dislikeBtn.backgroundTintList =
                                AppCompatResources.getColorStateList(context, R.color.gray)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            /*<============>*/
        } catch (e: Exception) {
            e.stackTrace
        }


        // On like
        holder.likeBtn.setOnClickListener { listener.onLikeButtonClicked(currentPost) }

        // On Dislike
        holder.dislikeBtn.setOnClickListener { listener.onDislikeButtonClicked(currentPost) }

        // On Share
        holder.shareBtn.setOnClickListener { listener.onShareButtonClicked(currentPost) }


        /**
         * On double clicked
         */
        holder.postImage.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick(v: View) {
                // Play animation
                holder.loveAnimation.playAnimation()
                listener.onPostDoubleClicked(currentPost)
            }
        })

        holder.loveAnimation.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
                holder.loveAnimation.visibility = View.VISIBLE
                holder.loveAnimation.elevation = 10F
            }

            override fun onAnimationEnd(animation: Animator) {
                holder.loveAnimation.elevation = -1F
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

        })
    }

    override fun getItemCount(): Int {
        return allPosts.size
    }

    fun updatePosts(list: List<PostItem>) {
        allPosts.clear()
        allPosts.addAll(list)
        notifyDataSetChanged()
    }

}