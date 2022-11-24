package com.example.instagramclonelite.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.instagramclonelite.R
import com.example.instagramclonelite.adapter.HomeFeedAdapter
import com.example.instagramclonelite.databinding.FragmentHomeBinding
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.example.instagramclonelite.listeners.PostListeners
import com.example.instagramclonelite.utils.Constants.Companion.getPostDislikeReference
import com.example.instagramclonelite.utils.Constants.Companion.getPostLikeReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment(), PostListeners {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Initialize recyclerView
        adapter = HomeFeedAdapter(this.requireContext(), this)
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.feedRecyclerView.adapter = this.adapter

        viewModel.getAllPosts().observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                binding.feedProgressBar.visibility = View.GONE
            }
            adapter.updatePosts(it)
        })

        return binding.root
    }






    override fun onLikeButtonClicked(currentPost: PostItem) {
        Log.d("HomeFragment", "Clicked on Like")
        val ref = getPostLikeReference(currentPost.postId)
        Thread{
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild(FirebaseAuth.getInstance().uid.toString())){
                            snapshot.child(FirebaseAuth.getInstance().uid.toString()).ref.removeValue()
                            viewModel.removeLike(currentPost.postId)
                        }else{
                            ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(System.currentTimeMillis())
                            viewModel.addLike(currentPost.postId)
                        }
                    }else{
                        ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(System.currentTimeMillis())
                        viewModel.addLike(currentPost.postId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }.start()
    }

    override fun onDislikeButtonClicked(currentPost: PostItem) {
        Log.d("HomeFragment", "Clicked on Like")
        val ref = getPostDislikeReference(currentPost.postId)
        Thread{
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        if (snapshot.hasChild(FirebaseAuth.getInstance().uid.toString())){
                            snapshot.child(FirebaseAuth.getInstance().uid.toString()).ref.removeValue()
                            viewModel.removeDislike(currentPost.postId)
                        }else{
                            ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(System.currentTimeMillis())
                            viewModel.addDislike(currentPost.postId)
                        }
                    }else{
                        ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(System.currentTimeMillis())
                        viewModel.addDislike(currentPost.postId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }.start()
    }

    override fun onShareButtonClicked(currentPost: PostItem) {
        Glide.with(this)
            .asBitmap()
            .load(currentPost.postMediaUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shareImage(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
    }

    override fun onPostDoubleClicked(currentPost: PostItem) {
        Log.d("HomeFragment", "Double Clicked on Post")
        val ref = getPostLikeReference(currentPost.postId)
        Thread{
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        if (!snapshot.hasChild(FirebaseAuth.getInstance().uid.toString())){
                            ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(System.currentTimeMillis())
                            viewModel.addLike(currentPost.postId)
                        }
                    }else{
                        ref.child(FirebaseAuth.getInstance().uid.toString()).setValue(System.currentTimeMillis())
                        viewModel.addLike(currentPost.postId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }.start()
    }


    fun shareImage(bitmap: Bitmap){
        // save bitmap to cache directory
        try {
            val cachePath: File = File(this.requireContext().applicationContext.filesDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val imagePath: File = File(this.requireContext().applicationContext.filesDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri: Uri =
            FileProvider.getUriForFile(this.requireContext(), "com.example.instagramclonelite.fileprovider", newFile)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, requireContext().contentResolver.getType(contentUri))
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out this awesome image I found on Instagram clone app")

        startActivity(Intent.createChooser(shareIntent, "Share image via..."))
    }

}