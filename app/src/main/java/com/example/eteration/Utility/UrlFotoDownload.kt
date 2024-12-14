package com.example.eteration.Utility

import android.annotation.SuppressLint
import android.widget.ImageView
import com.example.eteration.R
import com.squareup.picasso.Picasso

object UrlFotoDownload {

    @SuppressLint("PrivateResource")
    fun urlFotoSetImage(imageView: ImageView, url: String) {
        Picasso.get()
            .load(url) // Resmin URL'si
            .placeholder(R.drawable.dowloading_picture) // Yüklenirken gösterilecek görsel
            .error(R.drawable.error_picture_24) // Hata durumunda gösterilecek görsel
            .into(imageView);
    }
}