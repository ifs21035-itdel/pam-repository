package com.ifs21035.lostfounds.presentation.lostfound

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21035.lostfounds.data.model.LostFound
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.databinding.ActivityLostFoundManageBinding
import com.ifs21035.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21035.lostfounds.presentation.ViewModelFactory

class LostFoundManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostFoundManageBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostFoundManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showLoading(false)
    }
    private fun setupAction() {
        val isAddLostFound = intent.getBooleanExtra(KEY_IS_ADD, true)
        if (isAddLostFound) {
            manageAddLostFound()
        } else {
            val lostFound = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(KEY_LOSTFOUND, LostFound::class.java)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<LostFound>(KEY_LOSTFOUND)
                }
            }
            if (lostFound == null) {
                finishAfterTransition()
                return
            }
            manageEditLostFound(lostFound)
        }
        binding.appbarLostFoundManage.setNavigationOnClickListener {
            finishAfterTransition()
        }
        binding.btnAddImage.setOnClickListener {
            selectImageFromGallery()
        }
    }
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivLostFoundManageImage.setImageURI(uri)
            }
        } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
        }
    }
    private fun manageAddLostFound() {
        binding.apply {
            appbarLostFoundManage.title = "Add Lost Found"
            btnLostFoundManageSave.setOnClickListener {
                val title = etLostFoundManageTitle.text.toString()
                val description = etLostFoundManageDesc.text.toString()
                val status = etLostFoundManageStatus.text.toString()
                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("The field must not be empty!")
                        setPositiveButton("Ok") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePostLostFound(title, description, status)
            }
        }
    }
    private fun observePostLostFound(title: String, description: String, status: String) {
        viewModel.postLostFound(title, description, status).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent().apply {
                        putExtra(KEY_STATUS, status)
                        putExtra(LostFoundDetailActivity.KEY_IMAGE_URI, selectedImageUri?.toString()) // Pass the image URI here
                    }
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Ok") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }

    private fun manageEditLostFound(lostFound: LostFound) {
        binding.apply {
            appbarLostFoundManage.title = "Change Lost Found"
            etLostFoundManageTitle.setText(lostFound.title)
            etLostFoundManageDesc.setText(lostFound.description)
            etLostFoundManageStatus.setText(lostFound.status)
            btnLostFoundManageSave.setOnClickListener {
                val title = etLostFoundManageTitle.text.toString()
                val description = etLostFoundManageDesc.text.toString()
                val status = etLostFoundManageStatus.text.toString()
                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("The field must not be empty!")
                        setPositiveButton("Ok") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePutLostFound(lostFound.id, title, description, status, lostFound.isCompleted)
            }
        }
    }
    private fun observePutLostFound(
        todoId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) {
        viewModel.putLostFound(
            todoId,
            title,
            description,
            status,
            isCompleted
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent().apply{
                        putExtra(KEY_STATUS, status) // Pass the status here
                        putExtra(LostFoundDetailActivity.KEY_IMAGE_URI, selectedImageUri?.toString())
                    }
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@LostFoundManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbLostFoundManage.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnLostFoundManageSave.isActivated = !isLoading

        binding.btnLostFoundManageSave.text =
            if (isLoading) "" else "Save"
    }
    companion object {
        const val KEY_IS_ADD = "is_add"
        const val KEY_LOSTFOUND = "lostfound"
        const val RESULT_CODE = 1002
        const val KEY_STATUS = "status"
    }
}
