package com.example.newstoryapp.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newstoryapp.R
import com.example.newstoryapp.Adapter.LoadingAdapter
import com.example.newstoryapp.databinding.ActivityMainBinding
import com.example.newstoryapp.View.ViewModelFactory
import com.example.newstoryapp.View.AddStoryActivity
import com.example.newstoryapp.View.DetailStoryActivity
import com.example.newstoryapp.View.MapsActivity
import com.example.newstoryapp.View.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: MainStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoginStatus()
        setupRecyclerView()
        binding.progressbar.visibility = View.GONE
        setupLogoutAction()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
        setupStorySelection()
    }

    private fun setupStorySelection() {
        storyAdapter.setOnItemClickCallback { item ->
            val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.DETAIL_STORY, item)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvStory
        recyclerView.layoutManager = LinearLayoutManager(this)
        storyAdapter = MainStoryAdapter()

        recyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingAdapter { storyAdapter.retry() }
        )

        viewModel.stories.observe(this, {pagingData ->
            Log.d("MainActivity", "Paging data received: ${pagingData}")
            storyAdapter.submitData(lifecycle, pagingData)
        })

        lifecycleScope.launch {
            storyAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressbar.isVisible = loadStates.refresh is LoadState.Loading
                val errorState = loadStates.refresh as? LoadState.Error
                errorState?.let {
                    Toast.makeText(this@MainActivity, "Error: ${it.error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupLogoutAction() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logoutButton -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Log Out")
                        setMessage("Apakah anda yakin ingin log out?")
                        setPositiveButton("Log Out") { _, _ ->
                            viewModel.logout()
                            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("Batal") { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                    true
                }

                R.id.mapsButton -> {
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun checkLoginStatus() {
        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "User session: $user")
            if (!user.isLogin || user.token.isEmpty()) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                viewModel.setToken(user.token)
            }
        }
    }
}