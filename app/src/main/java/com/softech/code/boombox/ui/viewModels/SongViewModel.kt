package com.softech.code.boombox.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softech.code.boombox.another.Constants.UPDATE_PLAYER_POSITION_INTERVAL
import com.softech.code.boombox.exoplayer.MusicService
import com.softech.code.boombox.exoplayer.MusicServiceConnection
import com.softech.code.boombox.exoplayer.currentPlayBackPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongViewModel @ViewModelInject constructor(
    musicServiceConnection: MusicServiceConnection
):ViewModel(){
    private val playbackState = musicServiceConnection.playBackState

    private val _currentSongDuration = MutableLiveData<Long>()
    val currentSongDuration :LiveData<Long> = _currentSongDuration

    private val _currentPlayerPosition = MutableLiveData<Long>()
    val currentPlayerPosition :LiveData<Long> = _currentPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch {
            while (true){
                val position = playbackState.value?.currentPlayBackPosition
                if (currentPlayerPosition.value!=position){
                    _currentPlayerPosition.postValue(position)
                    _currentSongDuration.postValue(MusicService.currentSongDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }

}