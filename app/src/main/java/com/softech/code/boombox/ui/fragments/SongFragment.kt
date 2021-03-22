package com.softech.code.boombox.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.softech.code.boombox.R
import com.softech.code.boombox.another.Status
import com.softech.code.boombox.data.entities.Song
import com.softech.code.boombox.exoplayer.isPlaying
import com.softech.code.boombox.exoplayer.toSong
import com.softech.code.boombox.ui.viewModels.MainViewModel
import com.softech.code.boombox.ui.viewModels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()
    private var currentlyPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekBar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()
        ivPlayPauseDetail.setOnClickListener {
            currentlyPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    setCurrentPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }
        })
        ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
        ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }
    }

    private fun updateTitleAndImage(song: Song) {
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { results ->
                when (results.status) {
                    Status.SUCCESS -> {
                        results.data?.let { songs ->
                            if (currentlyPlayingSong == null && songs.isNotEmpty()) {
                                currentlyPlayingSong = songs[0]
                                updateTitleAndImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
        mainViewModel.currentlyPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            currentlyPlayingSong = it.toSong()
            updateTitleAndImage(currentlyPlayingSong!!)
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            ivPlayPauseDetail.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.pause_circle_filled_24 else R.drawable.play_arrow_24
            )
            seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.currentPlayerPosition.observe(viewLifecycleOwner){
            if (shouldUpdateSeekBar){
                seekBar.progress = it.toInt()
                setCurrentPlayerTimeToTextView(it)
            }
        }
        songViewModel.currentSongDuration.observe(viewLifecycleOwner){
            seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            tvSongDuration.text = dateFormat.format(it)
        }
    }

    private fun setCurrentPlayerTimeToTextView(ms:Long){
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tvCurTime.text = dateFormat.format(ms)
    }

}