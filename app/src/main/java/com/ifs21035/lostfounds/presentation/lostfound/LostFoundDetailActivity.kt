package com.ifs21035.lostfounds.presentation.lostfound

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ifs21035.lostfounds.R
import com.ifs21035.lostfounds.data.local.entity.LostFoundEntity
import com.ifs21035.lostfounds.data.model.LostFound
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.LostFoundDetail
import com.ifs21035.lostfounds.databinding.ActivityLostFoundDetailBinding
import com.ifs21035.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21035.lostfounds.presentation.ViewModelFactory

class LostFoundDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLostFoundDetailBinding
    private val viewModel by viewModels<LostFoundViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isFavorite: Boolean = false
    private var lostfound: LostFoundEntity? = null

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundManageActivity.RESULT_CODE) {
            recreate()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostFoundDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }
    private fun setupAction() {
        val lostFoundId = intent.getIntExtra(KEY_LOSTFOUND_ID, 0)
        if (lostFoundId == 0) {
            finish()
            return
        }
        observeGetLostFound(lostFoundId)
        binding.appbarLostFoundDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, true)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun observeGetLostFound(lostFoundId: Int) {
        viewModel.getLostFound(lostFoundId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    loadLostFound(result.data.data.lostFound)
                }
                is MyResult.Error -> {
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun loadLostFound(lostFound: LostFoundDetail) {
        showComponent(true)
        binding.apply {
            tvLostFoundDetailTitle.text = lostFound.title
            tvLostFoundDetailDate.text = "Created at: ${lostFound.createdAt}"
            tvLostFoundDetailDesc.text = lostFound.description
            tvLostFoundDetailStatus.text = lostFound.status

            if(lostFound.cover != null){
                ivLostFoundDetailCover.visibility = View.VISIBLE
                Glide.with(this@LostFoundDetailActivity)
                    .load(lostFound.cover)
                    .placeholder(R.drawable.ic_image_24)
                    .into(ivLostFoundDetailCover)
            }else{
                ivLostFoundDetailCover.visibility = View.GONE
            }

            viewModel.getLocalLostFound(lostFound.id).observeOnce {
                if(it != null){
                    lostfound = it
                    setFavorite(true)
                }else{
                    setFavorite(false)
                }
            }

            cbLostFoundDetailIsCompleted.isChecked = lostFound.isCompleted == 1
            cbLostFoundDetailIsCompleted.setOnCheckedChangeListener { _, isChecked ->
                viewModel.putLostFound(
                    lostFound.id,
                    lostFound.title,
                    lostFound.description,
                    lostFound.status,
                    isChecked
                ).observeOnce {
                    when (it) {
                        is MyResult.Error -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Fail completing: " + lostFound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Fail not completing: " + lostFound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is MyResult.Success -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Success completing: " + lostFound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@LostFoundDetailActivity,
                                    "Success not completing: " + lostFound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else -> {}
                    }
                }
            }
            ivLostFoundDetailActionFavorite.setOnClickListener {
                if(isFavorite){
                    setFavorite(false)
                    if(lostfound != null){
                        viewModel.deleteLocalTodo(lostfound!!)
                    }
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Lost found has been deleted from favorite list.",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    lostfound = LostFoundEntity(
                        id = lostFound.id,
                        title = lostFound.title,
                        description = lostFound.description,
                        isCompleted = lostFound.isCompleted,
                        cover = lostFound.cover,
                        createdAt = lostFound.createdAt,
                        updatedAt = lostFound.updatedAt,
                        author = lostFound.author,
                        status = lostFound.status,
                        userId = lostFound.userId
                    )
                    setFavorite(true)
                    viewModel.insertLocalTodo(lostfound!!)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Success adding lost found to favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            ivLostFoundDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@LostFoundDetailActivity)
                builder.setTitle("Delete Lost Found")
                    .setMessage("Are you sure want to delete this?")
                builder.setPositiveButton("Yes") { _, _ ->
                    observeDeleteTodo(lostFound.id)
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss() // Menutup dialog
                }
                val dialog = builder.create()
                dialog.show()
            }
            ivLostFoundDetailActionEdit.setOnClickListener {
                val lostfound = LostFound(
                    lostFound.id,
                    lostFound.title,
                    lostFound.description,
                    lostFound.status,
                    lostFound.isCompleted == 1,
                    lostFound.cover
                )
                val intent = Intent(
                    this@LostFoundDetailActivity,
                    LostFoundManageActivity::class.java
                )
                intent.putExtra(LostFoundManageActivity.KEY_IS_ADD, false)
                intent.putExtra(LostFoundManageActivity.KEY_LOSTFOUND, lostfound)
                launcher.launch(intent)
            }
        }
    }
    private fun setFavorite(status: Boolean){
        isFavorite = status
        if(status){
            binding.ivLostFoundDetailActionFavorite
                .setImageResource(R.drawable.ic_favorite_24)
        }else{
            binding.ivLostFoundDetailActionFavorite
                .setImageResource(R.drawable.ic_favorite_border_24)
        }
    }

    private fun observeDeleteTodo(todoId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deleteLostFound(todoId).observeOnce {
            when (it) {
                is MyResult.Error -> {
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Failed delete lost found: ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MyResult.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@LostFoundDetailActivity,
                        "Success delete lost found",
                        Toast.LENGTH_SHORT
                    ).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_IS_CHANGED, true)
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                else -> {}
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbLostFoundDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showComponent(status: Boolean) {
        binding.llLostFoundDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    companion object {
        const val KEY_LOSTFOUND_ID = "lostFound_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
        const val KEY_IMAGE_URI = "image_uri"
    }
}
