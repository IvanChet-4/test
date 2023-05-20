package com.app.playlistmaker

    import android.view.View
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

        private val albumsImg: ImageView = itemView.findViewById(R.id.albums_img)
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val bandName: TextView = itemView.findViewById(R.id.band_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)

        fun bind(item: Track) {
            val adaptedTrackName = item.trackName
            val adaptedBandName = item.bandName
            val adaptedTrackTime = item.trackTime


            Glide.with(itemView)
                .load(item.artworkUrl100)
                .placeholder(R.drawable.placeholderview)
                .into(albumsImg)

            trackName.text = adaptedTrackName
            bandName.text = adaptedBandName
            trackTime.text = adaptedTrackTime

        }
    }