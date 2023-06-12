package com.app.playlistmaker

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.withContext
import java.util.Locale
import java.text.SimpleDateFormat

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val albumsImg: ImageView = itemView.findViewById(R.id.albums_img)
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val bandName: TextView = itemView.findViewById(R.id.band_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(item: Track) {
        val cornerRadius = itemView.resources.getDimensionPixelSize(R.dimen.radius2dp)
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .centerCrop()
            .placeholder(R.drawable.placeholderview)
            .transform(RoundedCorners(cornerRadius))
            .into(albumsImg)
        trackName.text = item.trackName
        bandName.text = item.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
    }
}