package com.ifs21035.lostfounds.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifs21035.lostfounds.R
import com.ifs21035.lostfounds.adapter.LostFoundsAdapter
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.LostFoundsResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundsItemResponse
import com.ifs21035.lostfounds.databinding.ActivityMainBinding
import com.ifs21035.lostfounds.helper.Utils.Companion.observeOnce
import com.ifs21035.lostfounds.presentation.ViewModelFactory
import com.ifs21035.lostfounds.presentation.login.LoginActivity
import com.ifs21035.lostfounds.presentation.profile.ProfileActivity
import com.ifs21035.lostfounds.presentation.lostfound.LostFoundDetailActivity
import com.ifs21035.lostfounds.presentation.lostfound.LostFoundFavoriteActivity
import com.ifs21035.lostfounds.presentation.lostfound.LostFoundManageActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LostFoundManageActivity.RESULT_CODE) {
            recreate()
        }
        if (result.resultCode == LostFoundDetailActivity.RESULT_CODE) {
            result.data?.let {
                val isChanged = it.getBooleanExtra(
                    LostFoundDetailActivity.KEY_IS_CHANGED,
                    false
                )
                if (isChanged) {
                    recreate()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponentNotEmpty(false)
        showEmptyError(false)
        showLoading(true)
        binding.appbarMain.overflowIcon =
            ContextCompat
                .getDrawable(this, R.drawable.ic_more_vert_24)
        observeGetLostFounds()
    }
    private fun setupAction() {
        binding.appbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainMenuProfile -> {
                    openProfileActivity()
                    true
                }
                R.id.mainMenuFavoriteLostFounds -> {
                    openFavoriteLostFoundActivity()
                    true
                }
                R.id.mainMenuLogout -> {
                    viewModel.logout()
                    openLoginActivity()
                    true
                }
                else -> false
            }
        }
        binding.fabMainAddLostFound.setOnClickListener {
            openAddLostFoundActivity()
        }
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                openLoginActivity()
            } else {
                observeGetLostFounds()
            }
        }
    }
    private fun observeGetLostFounds() {
        viewModel.getLostFounds().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is MyResult.Loading -> {
                        showLoading(true)
                    }
                    is MyResult.Success -> {
                        showLoading(false)
                        loadlostFoundsToLayout(result.data)
                    }
                    is MyResult.Error -> {
                        showLoading(false)
                        showEmptyError(true)
                    }
                }
            }
        }
    }
    private fun loadlostFoundsToLayout(response: LostFoundsResponse) {
        val lostFounds = response.data.lostFounds
        val layoutManager = LinearLayoutManager(this)
        binding.rvMainLostFounds.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(
            this,
            layoutManager.orientation
        )
        binding.rvMainLostFounds.addItemDecoration(itemDecoration)

        if (lostFounds.isEmpty()) {
            showEmptyError(true)
            binding.rvMainLostFounds.adapter = null
        } else {
            showComponentNotEmpty(true)
            showEmptyError(false)

            val adapter = LostFoundsAdapter()
            adapter.submitOriginalList(lostFounds)
            binding.rvMainLostFounds.adapter = adapter
            adapter.setOnItemClickCallback(object : LostFoundsAdapter.OnItemClickCallback {
                override fun onCheckedChangeListener(
                    lostFound: LostFoundsItemResponse,
                    isChecked: Boolean
                ) {
                    adapter.filter(binding.svMain.query.toString())
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
                                        this@MainActivity,
                                        "Fail completing: " + lostFound.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Fail not completing: " + lostFound.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is MyResult.Success -> {
                                if (isChecked) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Success completing: " + lostFound.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Success not completing: " + lostFound.title,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            else -> {}
                        }
                    }
                }
                override fun onClickDetailListener(lostFoundId: Int) {
                    val intent = Intent(
                        this@MainActivity,
                        LostFoundDetailActivity::class.java
                    )
                    intent.putExtra(LostFoundDetailActivity.KEY_LOSTFOUND_ID, lostFoundId)
                    launcher.launch(intent)
                }
            })
            binding.svMain.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter.filter(newText)
                        binding.rvMainLostFounds.layoutManager?.scrollToPosition(0)
                        return true
                    }
                })
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun openProfileActivity() {
        val intent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(intent)
    }
    private fun showComponentNotEmpty(status: Boolean) {
        binding.svMain.visibility =
            if (status) View.VISIBLE else View.GONE
        binding.rvMainLostFounds.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    private fun showEmptyError(isError: Boolean) {
        binding.tvMainEmptyError.visibility =
            if (isError) View.VISIBLE else View.GONE
    }
    private fun openLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    private fun openAddLostFoundActivity() {
        val intent = Intent(
            this@MainActivity,
            LostFoundManageActivity::class.java
        )
        intent.putExtra(LostFoundManageActivity.KEY_IS_ADD, true)
        launcher.launch(intent)
    }
    private fun openFavoriteLostFoundActivity() {
        val intent = Intent(
            this@MainActivity,
            LostFoundFavoriteActivity::class.java
        )
        launcher.launch(intent)
    }

}