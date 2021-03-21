package com.softech.code.boombox.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.softech.code.boombox.another.Constants.NETWORK_ERROR
import com.softech.code.boombox.another.Event
import com.softech.code.boombox.another.Resource

class MusicServiceConnection(
    context: Context
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playBackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _currentlyPlayingSong = MutableLiveData<MediaMetadataCompat>()
    val currentlyPlayingSong: LiveData<MediaMetadataCompat> = _currentlyPlayingSong


    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallBack = MediaBrowserConnectionCallBack(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallBack,
        null
    )

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId:String, callBack:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentId,callBack)
    }

    fun unsubscribe(parentId:String, callBack:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.unsubscribe(parentId,callBack)
    }

    private inner class MediaBrowserConnectionCallBack(private val context: Context):MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            mediaController = MediaControllerCompat(context,mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallbacks())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.error(
                "Connection was suspended",false
            )))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(Event(Resource.error(
                "Couldn't connect to media browser",false
            )))
        }
    }

    private inner class MediaControllerCallbacks:MediaControllerCompat.Callback(){
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentlyPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event){
                NETWORK_ERROR ->_networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server check internet connection",
                            null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallBack.onConnectionSuspended()
        }
    }

}