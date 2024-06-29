package com.example.melohust.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.melohust.models.SongModel
import com.example.melohust.R
import com.example.melohust.SearchSongActivity

class SearchSongRecyclerAdapter(private val songList: List<SongModel>) :
    RecyclerView.Adapter<SearchSongRecyclerAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.search_songname_input)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_search_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.titleText.text = song.title

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SearchSongActivity::class.java)
            intent.putExtra("songTitle", song.title)
            holder.itemView.context.startActivity(intent)
        }
    }
}
