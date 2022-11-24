package com.example.instagramclonelite.ui.addPost

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.instagramclonelite.databinding.FragmentCreatePostBinding
import com.example.instagramclonelite.firebaseClasses.PostItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*


class CreatePostFragment : Fragment() {

    companion object {
        fun newInstance() = CreatePostFragment()
    }

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var viewModel: CreatePostViewModel
    private var imageBitmap : Bitmap? = null
    private lateinit var contentUri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[CreatePostViewModel::class.java]


        binding.chooseImageBtn.setOnClickListener { chooseImage(this.requireContext()) }

        binding.sendPostBtn.setOnClickListener {
            if (imageBitmap != null) {
                val post = PostItem(FirebaseAuth.getInstance().uid.toString(),
                    Firebase.auth.currentUser?.displayName!!, Firebase.auth.currentUser?.photoUrl.toString(), UUID.randomUUID().toString(),
                    "Image", "", binding.newPostCaptionEditText.text.toString(), 0, 0, System.currentTimeMillis())

                Thread{
                    viewModel.addPost(post, imageBitmap!!)
                }.start()
                Toast.makeText(this.requireContext(), "Updating post...", Toast.LENGTH_SHORT).show()
                this.requireActivity().onBackPressed()
            }else{
                Toast.makeText(this.requireContext(), "Please choose a image first", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }




    // function to let's the user to choose image from camera or gallery
    private fun chooseImage(context: Context) {
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Choose from drive",
            "Exit"
        ) // create a menuOption Array
        // create a dialog for showing the optionsMenu
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        // set the items in builder
        builder.setItems(optionsMenu,
            DialogInterface.OnClickListener { dialogInterface, i ->
                if (optionsMenu[i] == "Take Photo") {
                    // Open the camera and get the photo

                    val newFile = File(this.requireContext().applicationContext.filesDir, "captured_image.png")
                    contentUri =
                        FileProvider.getUriForFile(this.requireContext().applicationContext, "com.example.instagramclonelite.fileprovider", newFile)

//                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, contentUri);

//                    cameraResultLauncher.launch(takePicture)
                    imageCapture.launch(contentUri)

                } else if (optionsMenu[i] == "Choose from Gallery") {
                    // choose from  external storage
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryResultLauncher.launch(pickPhoto)
                } else if (optionsMenu[i] == "Choose from drive") {
                    // Choose from drive
                    val pickFromDrive = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryResultLauncher.launch(pickFromDrive)
                } else if (optionsMenu[i] == "Exit") {
                    dialogInterface.dismiss()
                }
            })
        builder.show()
    }


    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data

            if (data != null) {
                val imageUri = data.data
                binding.newPostImageView.setImageURI(imageUri)
                binding.newPostImageView.invalidate()
                val dr = binding.newPostImageView.drawable
                imageBitmap = dr.toBitmap()
                Glide.with(this).load(imageUri).into(binding.newPostImageView)
            }
        }
    }


    private val imageCapture = registerForActivityResult(ActivityResultContracts.TakePicture()){

        val stream = this.requireContext().contentResolver.openInputStream(contentUri)
        imageBitmap = BitmapFactory.decodeStream(stream)
        imageBitmap = rotateImage(imageBitmap!!, 90f)

        Glide.with(this).load(imageBitmap).into(binding.newPostImageView)
    }


    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

}