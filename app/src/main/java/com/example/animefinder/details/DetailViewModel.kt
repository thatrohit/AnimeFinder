package com.example.animefinder.details

import AnimeDetailsQuery
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.rx2.rxQuery
import com.example.animefinder.apollo.ApolloController
import com.example.animefinder.models.AnimeListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*

class DetailViewModel: ViewModel() {
    private val disposables = CompositeDisposable()

    val detailsSuccessLiveData = MutableLiveData<Response<AnimeDetailsQuery.Data>>()
    val detailsExceptionLiveData = MutableLiveData<Throwable>()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun getAnimeDetails(model: AnimeListModel?) {
        val apolloQuery = AnimeDetailsQuery(
            id = model?.id ?: 1,
            page = 1.toInput(),
            perPage = 5.toInput(),
            isFromSearch = false
        )
        disposables.add(
            ApolloController.apolloClient.rxQuery(apolloQuery).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    detailsSuccessLiveData.value = it

                }, {
                    detailsExceptionLiveData.value = it
                })
        )
    }

}