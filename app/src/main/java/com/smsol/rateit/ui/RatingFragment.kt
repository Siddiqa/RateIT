package com.smsol.rateit.ui

import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.smsol.mp.BaseFragment
import com.smsol.rateit.R
import com.smsol.rateit.databinding.RatingFragmentBinding
import com.smsol.rateit.model.Rating
import com.smsol.rateit.model.Result
import com.smsol.rateit.utils.LocalPrefrence
import android.view.Gravity
import android.widget.TextView




class RatingFragment : BaseFragment<RatingFragmentBinding>() {

    lateinit var restdata: Result
    lateinit var ratingFragmentBinding: RatingFragmentBinding

    override fun getLayoutId(): Int {
        return com.smsol.rateit.R.layout.rating_fragment
    }

    companion object {
        fun newinstance(data: Result, rate: Int): RatingFragment {
            var ratingFragment = RatingFragment()
            ratingFragment.arguments = Bundle().apply {
                putSerializable("Rest", data)
                putInt("rating", rate)
            }

            return ratingFragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ratingFragmentBinding = getViewBinding()
        restdata = arguments?.getSerializable("Rest") as Result
        var prevrate = arguments?.getInt("rating")
        ratingFragmentBinding.raingbar.rating = prevrate!!.toFloat()

        ratingFragmentBinding.tvname.text = restdata.name
        ratingFragmentBinding.tvaddress.text = restdata.vicinity
       var  android_id = Settings.Secure.getString(context!!.getContentResolver(),
            Settings.Secure.ANDROID_ID)

        ratingFragmentBinding.btnsubmit.setOnClickListener {
            var pref = LocalPrefrence(context!!)
            var rating = Rating(
                restdata.place_id,
                restdata.name,
                restdata.vicinity,
                ratingFragmentBinding.raingbar.rating.toInt(),
                android_id
            )
            pref.addRatings(rating)
            (activity as MainActivity).showDialog("Rating Done!!",true)
           /* val builder = AlertDialog.Builder(context!!)
            val titleView = builder.create().findViewById<TextView>(android.R.id.title)
            titleView?.gravity = Gravity.CENTER
            builder.setTitle(getString(com.smsol.rateit.R.string.app_name))
            builder.setMessage("Rating Done!!!")
            builder.setCancelable(false)
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                activity!!.onBackPressed()
            }
            builder.show()
*/

        }


    }
}