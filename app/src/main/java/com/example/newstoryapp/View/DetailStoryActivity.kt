package com.example.newstoryapp.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newstoryapp.Response.ListStoryItem
import com.example.newstoryapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val detail = intent.getParcelableExtra<ListStoryItem>(DetailStoryActivity.DETAIL_STORY) as ListStoryItem
        setupAction(detail)

        supportActionBar?.show()
        supportActionBar?.title = "Detail Story"
    }

    private fun setupAction(detail: ListStoryItem){
        Glide.with(this)
            .load(detail.photoUrl)
            .into(binding.ivDetailImage)
        binding.tvDetailName.text = detail.name
        binding.tvDetailDescription.text = detail.description
    }
    companion object {
        const val DETAIL_STORY = "detail_story"
    }
}