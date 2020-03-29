package com.example.animefinder.list

import AdvancedSearchQuery
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.rx2.rxQuery
import com.example.animefinder.apollo.ApolloController
import com.example.animefinder.models.SearchModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import type.MediaStatus

class ListViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    private lateinit var searchQuery: Input<String>
    private lateinit var genre: Input<String>
    private lateinit var startDate: Input<Any>
    private lateinit var status: Input<MediaStatus>
    private lateinit var minEpisodes: Input<Int>

    val searchResultSuccessLiveData = MutableLiveData<Response<AdvancedSearchQuery.Data>>()
    val searchResultExceptionLiveData = MutableLiveData<Throwable>()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun getSearchResult(model: SearchModel?) {
        searchQuery = if (model?.search.isNullOrBlank()) Input.absent() else model?.search.toInput()
        genre = if (model?.genre == null) Input.absent() else model.genre.toInput()
        startDate = if (model?.startDate == null) Input.absent() else model.startDate.toInput()
        status = if (model?.status == null) Input.absent() else model.status.toInput()
        minEpisodes =
            if (model?.minimumEpisodes == null) Input.absent() else model.minimumEpisodes.toInput()
        Log.d("VALUES", "$searchQuery $genre $startDate $status $minEpisodes")

        performSearch(1)
    }

    fun performSearch(page: Int) {
        val apolloQuery = AdvancedSearchQuery(
            page = page,
            perPage = 20,
            search = searchQuery,
            genre = genre,
            isFromSearch = true,
            minimumEpisodes = minEpisodes,
            startDate = startDate,
            status = status
        )
        disposables.add(
            ApolloController.apolloClient.rxQuery(apolloQuery).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {
                        searchResultSuccessLiveData.value = it
                    },
                    {
                        searchResultExceptionLiveData.value = it
                    }
                )
        )
    }

}