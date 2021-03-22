package com.softech.code.boombox.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.softech.code.boombox.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId?:"",
            it.title.toString(),
            it.subtitle.toString(),
            it.mediaUri.toString(),
            it.iconUri.toString()
        )
    }
}