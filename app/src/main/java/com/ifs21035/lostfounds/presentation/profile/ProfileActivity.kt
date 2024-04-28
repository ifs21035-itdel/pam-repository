package com.ifs21035.lostfounds.presentation.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifs21035.lostfounds.R
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.DataUserResponse
import com.ifs21035.lostfounds.databinding.ActivityProfileBinding
import com.ifs21035.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21035.lostfounds.helper.getImageUri
import com.ifs21035.lostfounds.helper.reduceFileImage
import com.ifs21035.lostfounds.helper.uriToFile
import com.ifs21035.lostfounds.presentation.ViewModelFactory
import com.ifs21035.lostfounds.presentation.login.LoginActivity
import com.ifs21035.lostfounds.presentation.lostfound.LostFoundManageActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView(){
        showLoading(true)
        observeGetMe()
    }
    private fun setupAction(){
        binding.apply {
            ivProfileBack.setOnClickListener {
                finish()
            }
            btnProfileCamera.setOnClickListener {
                startCamera()
            }
            btnProfileGallery.setOnClickListener{
                startGallery()
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.pbProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.llProfile.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
    private fun observeGetMe(){
        viewModel.getMe().observe(this){ result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }
                    is MyResult.Success -> {
                        showLoading(false)
                        loadProfileData(result.data)
                    }
                    is MyResult.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            applicationContext, result.error, Toast.LENGTH_LONG
                        ).show()
                        viewModel.logout()
                        openLoginActivity()
                    }
                }
            }
        }
    }

    private fun observeAddPhotoProfile() {
        val imageFile =
            uriToFile(selectedImageUri!!, this).reduceFileImage()
        val requestImageFile =
            imageFile.asRequestBody("image/jpeg".toMediaType())
        val reqPhoto =
            MultipartBody.Part.createFormData(
                "cover",
                imageFile.name,
                requestImageFile
            )
        viewModel.addPhotoProfile(
            reqPhoto
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(LostFoundManageActivity.RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    showLoading(false)
                    AlertDialog.Builder(this@ProfileActivity).apply {
                        setTitle("Oh No!")
                        setMessage("There is an error!")
                        setPositiveButton("Ok") { _, _ ->
                            val resultIntent = Intent()
                            setResult(LostFoundManageActivity.RESULT_CODE, resultIntent)
                            finishAfterTransition()
                        }
                        setCancelable(false)
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            showImage()
        } else {
            Toast.makeText(
                applicationContext,
                "No media had been chosen!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun showImage() {
        selectedImageUri?.let {
            binding.ivProfile.setImageURI(it)
        }
    }
    private fun startCamera() {
        selectedImageUri = getImageUri(this)
        launcherIntentCamera.launch(selectedImageUri)
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun loadProfileData(profile: DataUserResponse){
        binding.apply {
            if(profile.user.photo != null){
                val urlImg = "https://public-api.delcom.org/${profile.user.photo}"
                Glide.with(this@ProfileActivity)
                    .load(urlImg)
                    .placeholder(R.drawable.ic_person)
                    .into(ivProfile)
            }
            tvProfileName.text = profile.user.name
            tvProfileEmail.text = profile.user.email
        }
    }
    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
