package com.softech.code.boombox.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import com.softech.code.boombox.R
import kotlinx.android.synthetic.main.song_item.view.*

class SwipeSongAdapter :BaseSongAdapter(R.layout.swipe_item){
    override val differ = AsyncListDiffer(this,diffCallBack)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song=songs[position]
        holder.itemView.apply {
            val text = "${song.title} - ${song.subtitle}"
            tvPrimary.text = text
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click->
                click(songs[position])
            }
        }
    }
}