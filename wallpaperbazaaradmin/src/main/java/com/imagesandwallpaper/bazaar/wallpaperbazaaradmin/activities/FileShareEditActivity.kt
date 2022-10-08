package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.news_and_review.*
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.NewsCardLayoutBinding
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.news_and_reviews.DetailsModel
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods
import kotlinx.android.synthetic.main.activity_file_share_edit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class FileShareEditActivity : AppCompatActivity(),
    NewsDetailsClickInterface, ReviewsDetailsClickInterface {
    lateinit var newsDetailsAdapter: NewsDetailsAdapter
    lateinit var reviewsDetailsAdapter: ReviewsDetailsAdapter
    lateinit var newsDetailsViewModel: NewsDetailsViewModel
    lateinit var reviewsDetailsViewModel: ReviewsDetailsViewModel
    var newsDetailsModels = ArrayList<DetailsModel>()
    var reviewsDetailsModels = ArrayList<DetailsModel>()
    var apiInterface = ApiWebServices.getApiInterface()
    var map = mutableMapOf<String, String>()
    var hashMap = HashMap<String, String>()
    lateinit var encodedImage: String
    lateinit var fileShareDetailsDialog: Dialog
    lateinit var cardLayoutBinding: NewsCardLayoutBinding
    lateinit var loadingBinding: Dialog
    lateinit var launcher: ActivityResultLauncher<String>
    lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_share_edit)
        loadingBinding = CommonMethods.loadingDialog(this@FileShareEditActivity)

        val key = intent.getStringExtra("key")

        when (key) {
            "News" -> {
                newsDetailsAdapter = NewsDetailsAdapter(this, this)
                newsDetailsViewModel = ViewModelProvider(this)[NewsDetailsViewModel::class.java]
                details_edit_RV.adapter = newsDetailsAdapter
                fetchNewsDetails()
            }
            "Reviews" -> {
                reviewsDetailsAdapter = ReviewsDetailsAdapter(this, this)
                reviewsDetailsViewModel =
                    ViewModelProvider(this)[ReviewsDetailsViewModel::class.java]
                details_edit_RV.adapter = reviewsDetailsAdapter
                fetchReviewsDetails()
            }
        }


        launcher = registerForActivityResult(GetContent()) { result: Uri? ->
            if (result != null) {
                Glide.with(this).load(result).into(cardLayoutBinding.selectImage)
                try {
                    val inputStream = this.contentResolver.openInputStream(result)
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    encodedImage = imageStore(bitmap, 80)
                    Log.d("checkEncoded", encodedImage)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun imageStore(bitmap: Bitmap, imageQuality: Int): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, stream)
        val imageBytes = stream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun fetchNewsDetails() {
        newsDetailsViewModel.newsDetails.observe(this) { newsDetailsModelList ->
            if (newsDetailsModelList != null) {
                newsDetailsModels.clear()
                newsDetailsModels.addAll(newsDetailsModelList.data)
                newsDetailsAdapter.updateList(newsDetailsModels)
            }
            // loadingDialog.dismiss()
        }
    }

    private fun fetchReviewsDetails() {
        reviewsDetailsViewModel.reviewsDetails.observe(this) { reviewsDetailsModelList ->
            if (reviewsDetailsModelList != null) {
                reviewsDetailsModels.clear()
                reviewsDetailsModels.addAll(reviewsDetailsModelList.data)
                reviewsDetailsAdapter.updateList(reviewsDetailsModels)
            }
            // loadingDialog.dismiss()
        }
    }

    override fun OnNewsClicked(detailsModel: DetailsModel?) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Edit or Delete")
            setPositiveButton("Delete") { dialog, which ->
                loadingBinding.show()
                if (detailsModel != null) {
                    map["id"] = detailsModel.id
                    map["title"] = "newsDetails"
                    map["path"] = "fs_news_details_img/" + detailsModel.newsImg
                }

                deleteDetails(map, "News")
            }
            setNegativeButton("Edit"
            ) { dialog, which ->
                if (detailsModel != null) {
                    showDetailsDialog(detailsModel, "News")
                }
            }
            show()
        }
    }

    override fun OnReviewsClicked(detailsModel: DetailsModel?) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Edit or Delete")
            setPositiveButton("Delete") { dialog, which ->
                loadingBinding.show()
                if (detailsModel != null) {
                    map["id"] = detailsModel.id
                    map["title"] = "reviewsDetails"
                    map["path"] = "fs_review_details_img/" + detailsModel.newsImg
                }

                deleteDetails(map, "Reviews")

            }
            setNegativeButton("Edit"
            ) { dialog, which ->
                if (detailsModel != null) {
                    showDetailsDialog(detailsModel, "Reviews")
                }
            }
            show()
        }
    }

    private fun deleteDetails(map: Map<String, String>, key: String) {
        val call: Call<MessageModel> = apiInterface.deleteCategory(map)
        call.enqueue(object : Callback<MessageModel> {
            override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FileShareEditActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT)
                        .show()
                    when (key) {
                        "News" -> {
                            fetchNewsDetails()
                            loadingBinding.dismiss()
                        }
                        "Reviews" -> {
                            fetchReviewsDetails()
                            loadingBinding.dismiss()
                        }
                    }
                }

            }

            override fun onFailure(call: Call<MessageModel>, t: Throwable) {

            }
        })
    }

    private fun showDetailsDialog(detailsModel: DetailsModel, key: String) {
        fileShareDetailsDialog = Dialog(this)
        cardLayoutBinding = NewsCardLayoutBinding.inflate(layoutInflater)
        fileShareDetailsDialog.setContentView(cardLayoutBinding.getRoot())
        fileShareDetailsDialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        fileShareDetailsDialog.window?.setBackgroundDrawable(ContextCompat
            .getDrawable(this@FileShareEditActivity, R.drawable.item_bg))
        fileShareDetailsDialog.setCancelable(false)
        fileShareDetailsDialog.show()
        encodedImage = detailsModel.newsImg

        if (key == "News") {
            Glide.with(this@FileShareEditActivity)
                .load("https://gedgetsworld.in/Wallpaper_Bazaar/fs_news_details_img/" + detailsModel.newsImg)
                .into(cardLayoutBinding.selectImage)
        } else {
            Glide.with(this@FileShareEditActivity)
                .load("https://gedgetsworld.in/Wallpaper_Bazaar/fs_review_details_img/" + detailsModel.newsImg)
                .into(cardLayoutBinding.selectImage)
        }

        cardLayoutBinding.titleEdt.setText(detailsModel.newsTitle)
        cardLayoutBinding.urlEdt.setText(detailsModel.url)
        cardLayoutBinding.descEdt.setText(detailsModel.newsDesc)




        cardLayoutBinding.selectImage.setOnClickListener { v ->
            launcher.launch("image/*")
        }
        cardLayoutBinding.backBtn.setOnClickListener { view -> fileShareDetailsDialog.cancel() }
        cardLayoutBinding.title.setText(key)
        cardLayoutBinding.okBtn.setOnClickListener { view ->

            val fsTitletxt = cardLayoutBinding.titleEdt.getText().toString()
            val fsURLTxt = cardLayoutBinding.urlEdt.getText().toString()
            val fsDescTxt = cardLayoutBinding.descEdt.getText().toString()

            if (TextUtils.isEmpty(fsTitletxt)) {
                cardLayoutBinding.titleEdt.setError("Title required!")
            } else if (TextUtils.isEmpty(fsURLTxt)) {
                cardLayoutBinding.urlEdt.setError("Title required!")
            } else if (TextUtils.isEmpty(fsDescTxt)) {
                cardLayoutBinding.descEdt.setError("Field required!")
            } else {
                loadingBinding.show()
                map["id"] = detailsModel.id.toString()
                map["img"] = encodedImage
                map["title"] = fsTitletxt
                map["url"] = fsURLTxt
                map["newsDesc"] = fsDescTxt

                if (key == "News") {
                    map["deleteImg"] = "fs_news_details_img/" + detailsModel.newsImg.toString()
                    map["tableName"] = "News_Details"
                    map["folderName"] = "fs_news_details_img/"
                } else {
                    map["deleteImg"] = "fs_review_details_img/" + detailsModel.newsImg.toString()
                    map["tableName"] = "Review_Details"
                    map["folderName"] = "fs_review_details_img/"
                }
                if (encodedImage.length < 100) {
                    map["imgKey"] = "0"
                } else {
                    map["imgKey"] = "1"
                }
                Log.d("allDetails",
                    detailsModel.id + " " + encodedImage.length.toString() + "" + detailsModel.newsImg +
                            "" + fsTitletxt + "" + fsURLTxt + "" + fsDescTxt)
//                hashMap.put("id", detailsModel.id.toString())
//                hashMap.put("img", encodedImage)
//                hashMap.put("deleteImg", detailsModel.newsImg.toString())
//                hashMap.put("title", fsTitletxt)
//                hashMap.put("url", fsURLTxt)
//                hashMap.put("newsDesc", fsDescTxt)
//
//                if (key=="News") {
//                    hashMap.put("tableName", "News_Details")
//                    hashMap.put("folderName", "fs_news_details_img/")
//                }else{
//                    hashMap.put("tableName", "Review_Details")
//                    hashMap.put("folderName", "fs_review_details_img/")
//                }
//                if (encodedImage.length < 100) {
//                    hashMap.put("imgKey", "0")
//                }else{
//                    hashMap.put("imgKey", "1")
//                }
                updateDetails(map, key)

            }
        }
    }

    private fun updateDetails(map: Map<String, String>, key: String) {
        val call: Call<MessageModel> = apiInterface.updateAllDetails(map)
        call.enqueue(object : Callback<MessageModel> {
            override fun onResponse(call: Call<MessageModel>, response: Response<MessageModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FileShareEditActivity,
                        response.body()!!.message,
                        Toast.LENGTH_SHORT)
                        .show()
                    when (key) {
                        "News" -> {
                            fetchNewsDetails()
                            loadingBinding.dismiss()
                            fileShareDetailsDialog.dismiss()
                        }
                        "Reviews" -> {
                            fetchReviewsDetails()
                            loadingBinding.dismiss()
                            fileShareDetailsDialog.dismiss()
                        }
                    }
                }

            }

            override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                Log.d("checking", t.message.toString())
                Toast.makeText(this@FileShareEditActivity, t.message, Toast.LENGTH_SHORT).show()
                loadingBinding.dismiss()
            }
        })
    }
}