package com.example.animefinder.search

import AdvancedSearchQuery
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.cache.normalized.ApolloStoreOperation
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.rx2.rxPrefetch
import com.apollographql.apollo.rx2.rxQuery
import com.apollographql.apollo.rx2.rxSubscribe
import com.example.animefinder.R
import com.example.animefinder.apollo.ApolloController
import com.example.animefinder.apollo.ApolloController.apolloClient
import com.example.animefinder.list.ListActivity
import com.example.animefinder.models.SearchModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import type.MediaStatus

class SearchActivity : AppCompatActivity() {
    companion object {
        const val KEY_SEARCH = "Search"
    }
    val statusMap = hashMapOf(
        "Finished" to MediaStatus.FINISHED,
        "Currently Running" to MediaStatus.RELEASING,
        "Coming Soon" to MediaStatus.NOT_YET_RELEASED,
        "Cancelled" to MediaStatus.CANCELLED
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initViews()
        setListeners()
    }

    private fun initViews() {
        spinnerGenre.adapter = getGenreAdapter()
        spinnerMonth.adapter = getMonthAdapter()
        spinnerMediaStatus.adapter = getMediaStatusAdapter()
    }

    private fun getGenreAdapter(): ArrayAdapter<String> = ArrayAdapter<String>(
        this,
        R.layout.item_template_simple_pinner_item,
        listOf(
            "Select Genre", "Action", "Adventure", "Comedy", "Sports", "Romance", "Drama"
        )
    )

    private fun getMonthAdapter(): ArrayAdapter<String> = ArrayAdapter<String>(
        this,
        R.layout.item_template_simple_pinner_item,
        listOf(
            "Select Month",
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
    )

    private fun getMediaStatusAdapter(): ArrayAdapter<String> = ArrayAdapter<String>(
        this,
        R.layout.item_template_simple_pinner_item,
        listOf(
            "Select Status", "Finished", "Currently Running", "Coming Soon", "Cancelled"
        )
    )

    private fun setListeners() {
        btnSearch.setOnClickListener {
            val year = if(etYear.text.isNullOrEmpty()) null else etYear.text.toString()
            val month = if (year.isNullOrEmpty()) "00" else String.format("%02d", spinnerMonth.selectedItemPosition)
            val status = statusMap.getOrDefault(spinnerMediaStatus.selectedItem as? String, null)
            val searchModel = SearchModel(
                page = 1,
                perPage = 20,
                search = if(etSearch.text.toString().isNullOrBlank()) null else etSearch.text.toString(),
                genre = if(spinnerGenre.selectedItemId == 0L) null else spinnerGenre.selectedItem as String,
                isFromSearch = true,
                minimumEpisodes = etMinimuEpisodes.text.toString().toIntOrNull(),
                startDate = if(year == null) null else "${year}${month}00",
                status = status
            )
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra(KEY_SEARCH, searchModel)
            startActivity(intent)
        }
    }
}
