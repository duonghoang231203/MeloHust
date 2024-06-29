package com.example.melohust

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.melohust.adapter.SongsListAdapter
import com.example.melohust.databinding.ActivitySongsListBinding
import com.example.melohust.models.CategoryModel

class SongsListActivity : AppCompatActivity() {

    companion object{
        lateinit var category : CategoryModel
        //lateinit var artist :ArtistModel
    }

    lateinit var  binding: ActivitySongsListBinding
    lateinit var songsListAdapter: SongsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameTextView.text = category.name
        //binding.nameTextView.text = artist.name
        Glide.with(binding.coverImageView).load(category.coverUrl)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(binding.coverImageView)

//        Glide.with(binding.coverImageView).load(artist.coverUrl)
//            .apply(
//                RequestOptions().transform(RoundedCorners(32))
//            )
//            .into(binding.coverImageView)


        setupSongsListRecyclerView()
    }

    private fun setupSongsListRecyclerView(){
        songsListAdapter = SongsListAdapter(category.songs)
//        songsListAdapter = SongsListAdapter(artist.songs)
        binding.songsListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.songsListRecyclerView.adapter = songsListAdapter
    }

}