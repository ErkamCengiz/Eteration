package com.example.eteration.Utility

import android.content.Context
import com.example.eteration.App.App

object DisplayUtils {

    // Ekranın yüksekliğini almak için statik bir fonksiyon
    fun getScreenHeight(): Int {
        val displayMetrics = App.getAppContext().resources.displayMetrics
        return displayMetrics.heightPixels
    }

    // Ekranın genişliğini almak için statik bir fonksiyon
    fun getScreenWidth(): Int {
        val displayMetrics = App.getAppContext().resources.displayMetrics
        return displayMetrics.widthPixels
    }
}