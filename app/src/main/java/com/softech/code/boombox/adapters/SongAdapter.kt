package com.softech.code.boombox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.softech.code.boombox.R
import com.softech.code.boombox.data.entities.Song
import kotlinx.android.synthetic.main.song_item.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(private val glide: RequestManager) :BaseSongAdapter(R.layout.song_item){
    override val differ = AsyncListDiffer(this,diffCallBack)

    override fun onBindViewHolder(holder: BaseSongAdapter.SongViewHolder, position: Int) {
        val song=songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click->
                click(songs[position])
            }
        }
    }
}