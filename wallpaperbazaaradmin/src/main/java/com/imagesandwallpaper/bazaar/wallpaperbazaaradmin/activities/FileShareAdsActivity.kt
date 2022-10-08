package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.FileShareAdsAdapter

class FileShareAdsActivity : AppCompatActivity() {
    lateinit var adsAdapter : FileShareAdsAdapter
    var fileShareAdsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_share_ads)

        val fileShareRV = findViewById<RecyclerView>(R.id.file_share_recyclerView)
        fileShareRV.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        fileShareAdsList.apply {
            add("Turbo Share")
            add("Turbo Share2")
            add("Turbo Share3")
            add("Turbo Share4")
            add("Turbo Share5")
            add("Turbo Share7")
            add("Turbo Share8")
            add("Turbo Share9")
            add("Turbo Share10")
            add("Turbo Share11")
            add("Turbo Share12")
            add("Turbo Share13")
            add("Turbo Share14")
            add("Turbo Share15")
            add("Turbo Share16")
            add("Turbo Share17")
            add("Turbo Share18")
            add("Turbo Share19")
            add("Turbo Share20")
            add("Turbo Share21")
            add("Turbo Share22")
        }
        adsAdapter = FileShareAdsAdapter(fileShareAdsList,this)
        fileShareRV.adapter = adsAdapter

    }
}