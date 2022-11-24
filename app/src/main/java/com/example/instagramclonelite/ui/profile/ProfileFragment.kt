package com.example.instagramclonelite.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.instagramclonelite.R
import com.example.instagramclonelite.adapter.ProfileActivityAdapter
import com.example.instagramclonelite.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var adapter : ProfileActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        //Setup the recyclerView
        adapter = ProfileActivityAdapter(this.requireContext())
        binding.activityRecyclerView.layoutManager = GridLayoutManager(this.requireContext(), 3)
        binding.activityRecyclerView.adapter = this.adapter

        // Set current user data
        val user = Firebase.auth.currentUser
        if (user != null){
            binding.userProfileName.text = user.displayName
            Glide.with(this.requireContext()).load(user.photoUrl).into(binding.userDpCircleImageView)
        }

        // Observe user activities
        viewModel.getUserActivityPosts(FirebaseAuth.getInstance().uid.toString()).observe(viewLifecycleOwner,
            Observer {
                adapter.updateUserActivities(it)
            })

        return binding.root
    }

}