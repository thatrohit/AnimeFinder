package com.example.animefinder.list

import AdvancedSearchQuery
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.rx2.rxQuery
import com.example.animefinder.R
import com.example.animefinder.apollo.ApolloController.apolloClient
import com.example.animefinder.details.DetailActivity
import com.example.animefinder.models.AnimeListModel
import com.example.animefinder.models.SearchModel
import com.example.animefinder.search.SearchActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import type.MediaStatus

class ListActivity : AppCompatActivity(), IAnimeListCallback {
    companion object {
        const val KEY_ANIME = "anime"
    }

    private lateinit var adapter: AnimeListAdapter
    private var model: SearchModel? = null
    private val animes = ArrayList<AnimeListModel>()

    private val vm: ListViewModel by viewModels()

    private var page = 1
    private var isLastPage = false
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        getDataFromExtra()
        initRecyclerView()
        vm.getSearchResult(model)
        setListeners()
    }

    private fun setListeners() {
        vm.searchResultExceptionLiveData.observe(this, Observer {
            isLoading = false
            pbAnimeListLoadMore.visibility = View.GONE
            handleError(it)
        })

        vm.searchResultSuccessLiveData.observe(this, Observer {
            isLoading = false
            pbAnimeListLoadMore.visibility = View.GONE
            handleSuccess(it)
        })
    }

    private fun handleSuccess(it: Response<AdvancedSearchQuery.Data>) {
        if (page == 1) vfListPage.displayedChild = 1
        Log.d("SUCCESS", it.data().toString())
        if (it.data()?.page?.pageInfo?.hasNextPage == true) page++ else isLastPage = true
        it.data()?.page?.media?.forEach {
            with(it?.fragments?.mediaFragment) {
                animes.add(
                    AnimeListModel(
                        id = this?.id,
                        imageUrl = this?.coverImage?.large,
                        name = this?.title?.romaji
                    )
                )
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun handleError(it: Throwable) {
        Toast.makeText(
            this@ListActivity,
            "Something went wrong: ${it.message}",
            Toast.LENGTH_LONG
        ).show()
        Log.e("EX", it.message.toString())
        finish()
    }

    private fun initRecyclerView() {
        adapter = AnimeListAdapter(animes, this)
        rvAnimeList.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        rvAnimeList.layoutManager = layoutManager
        rvAnimeList.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = isLoading

            override fun loadMoreItems() {
                isLoading = true
                pbAnimeListLoadMore.visibility = View.VISIBLE
                vm.performSearch(page)
            }
        })
    }

    private fun getDataFromExtra() {
        model = intent?.extras?.getSerializable(SearchActivity.KEY_SEARCH) as? SearchModel
    }


    override fun onItemClick(item: AnimeListModel) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(KEY_ANIME, item)
        })
    }
}