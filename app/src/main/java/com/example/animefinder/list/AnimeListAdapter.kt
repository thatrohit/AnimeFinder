package com.example.animefinder.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.example.animefinder.R
import com.example.animefinder.models.AnimeListModel
import kotlinx.android.synthetic.main.item_template_anime_list.view.*

class AnimeListAdapter(
    private val items: List<AnimeListModel>,
    private val callback: IAnimeListCallback
) : RecyclerView.Adapter<AnimeListAdapter.AnimeListViewHolder>() {

    class AnimeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeListViewHolder =
        AnimeListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_template_anime_list, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AnimeListViewHolder, position: Int) {
        val item = items[position]
        item.imageUrl?.let {
            Glide.with(holder.itemView.context).load(it).into(holder.itemView.ivCoverPhoto)
        }
        holder.itemView.tvItAnimeName.setText(item.name)
        holder.itemView.setOnClickListener { callback.onItemClick(item) }
    }
}

interface IAnimeListCallback {
    fun onItemClick(item: AnimeListModel)
}