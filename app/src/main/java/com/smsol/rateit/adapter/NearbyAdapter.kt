package com.smsol.rateit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.smsol.mp.utils.NavigationUtils
import com.smsol.rateit.R
import com.smsol.rateit.databinding.RowNearbyLayoutBinding
import com.smsol.rateit.model.Result
import com.smsol.rateit.ui.MainActivity
import com.smsol.rateit.ui.RatingFragment

class NearbyAdapter(var context: Context, var data: ArrayList<Result>) :
    RecyclerView.Adapter<NearbyAdapter.NearbyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyHolder {
        var rowNearbyLayoutBinding = DataBindingUtil.inflate<RowNearbyLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_nearby_layout, parent, false
        )
        return NearbyHolder(rowNearbyLayoutBinding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: NearbyHolder, position: Int) {
        holder.rowNearbyLayoutBinding.result=data[position]
        holder.itemView.setOnClickListener {
            NavigationUtils.AddFragment(RatingFragment.newinstance(data[position],data[position].userrating),(context as MainActivity).supportFragmentManager,R.id.maincontainer)
        }

    }

    class NearbyHolder(var rowNearbyLayoutBinding: RowNearbyLayoutBinding) :
        RecyclerView.ViewHolder(rowNearbyLayoutBinding.root)
}