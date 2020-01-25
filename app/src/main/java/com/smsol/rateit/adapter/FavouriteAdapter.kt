package com.smsol.rateit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.smsol.rateit.R
import com.smsol.rateit.databinding.RowFavLayoutBinding
import com.smsol.rateit.model.Rating

class FavouriteAdapter(var listrate: ArrayList<Rating>) :
    RecyclerView.Adapter<FavouriteAdapter.FavHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavHolder {
        var rowNearbyLayoutBinding = DataBindingUtil.inflate<RowFavLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_fav_layout, parent, false
        )
        return FavHolder(rowFavLayoutBinding = rowNearbyLayoutBinding)

    }

    override fun getItemCount(): Int {
        if (listrate.size > 10) {
            return 10
        } else {
            return listrate.size
        }

    }

    override fun onBindViewHolder(holder: FavHolder, position: Int) {
        var idx = position
        holder.rowFavLayoutBinding.rating = listrate[position]
        holder.rowFavLayoutBinding.tvno.text = (idx + 1).toString()

    }

    class FavHolder(var rowFavLayoutBinding: RowFavLayoutBinding) :
        RecyclerView.ViewHolder(rowFavLayoutBinding.root)
}