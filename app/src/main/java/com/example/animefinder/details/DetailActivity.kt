package com.example.animefinder.details

import AnimeDetailsQuery
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.rx2.rxQuery
import com.bumptech.glide.Glide
import com.example.animefinder.R
import com.example.animefinder.apollo.ApolloController.apolloClient
import com.example.animefinder.list.ListActivity
import com.example.animefinder.models.AnimeListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

class DetailActivity : AppCompatActivity() {

    var model: AnimeListModel? = null
    val vm: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        getDataFromExtra()
        setListeners()
        vm.getAnimeDetails(model)
    }

    private fun setListeners() {
        vm.detailsSuccessLiveData.observe(this, Observer {
            handleSuccess(it)
        })
        vm.detailsExceptionLiveData.observe(this, Observer {
            handleFailure(it)
        })
    }

    private fun handleFailure(it: Throwable?) {
        Toast.makeText(
            this@DetailActivity,
            "Something went wrong: ${it?.message}",
            Toast.LENGTH_LONG
        ).show()
        Log.e("ERROR", it?.message ?: "Exception")
        finish()
    }

    private fun handleSuccess(response: Response<AnimeDetailsQuery.Data>?) {
        showAnimeDetails(response?.data())
        vfDetailPage.displayedChild = 1
    }

    private fun getDataFromExtra() {
        model = intent?.extras?.getSerializable(ListActivity.KEY_ANIME) as? AnimeListModel
    }

    private fun showAnimeDetails(data: AnimeDetailsQuery.Data?) {
        val frag = data?.media?.fragments?.mediaFragment
        Glide.with(this).load(frag?.coverImage?.large).into(ivAnimePhoto)
        Glide.with(this).load(frag?.bannerImage).into(ivAnimeCover)
        tvAnimeTitle.setText(frag?.title?.romaji)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvAnimeDescription.text = Html.fromHtml(frag?.description, 0)
        }
        val reviews = StringBuilder()
        data?.page?.reviews?.forEach {
            reviews.append("✔️ ${it?.summary}").append("\n\n")
        }
        val layoutInflater = LayoutInflater.from(this@DetailActivity)
        frag?.genres?.forEach {
            val tag = layoutInflater.inflate(R.layout.item_template_tags, null, false) as? TextView
            val layoutManager = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutManager.setMargins(0, 20, 20, 0)
            tag?.setText("#${it?.lowercase(Locale.getDefault())}")
            tag?.layoutParams = layoutManager
            fbTags.addView(tag)
        }
        tvAnimeReviews.setText(if (reviews.isEmpty()) "No reviews yet 😞" else reviews.toString())
        tvAnimeTrendingScore.setText("Trending Score: ${data?.mediaTrend?.trending ?: 0}")
    }

}