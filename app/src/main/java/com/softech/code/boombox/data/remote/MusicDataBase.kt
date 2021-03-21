package com.softech.code.boombox.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.softech.code.boombox.another.Constants.SONGS_COLLECTION
import com.softech.code.boombox.data.entities.Song
import kotlinx.coroutines.tasks.await

class MusicDataBase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONGS_COLLECTION)

    suspend fun getAllSongs():List<Song>{
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }catch (e:Exception){
            emptyList()
        }
    }

}