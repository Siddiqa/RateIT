package com.smsol.rateit.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.app.forecast.utils.GlobalRetrofit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.smsol.mp.BaseFragment
import com.smsol.rateit.R
import com.smsol.rateit.adapter.FavouriteAdapter
import com.smsol.rateit.adapter.NearbyAdapter
import com.smsol.rateit.databinding.MainfragmentBinding
import com.smsol.rateit.model.Rating
import com.smsol.rateit.model.Result
import com.smsol.rateit.utils.CurrentLocationListener
import com.smsol.rateit.utils.LocalPrefrence
import com.smsol.rateit.viewmodel.NearByModel


class MainFragment : BaseFragment<MainfragmentBinding>(), OnMapReadyCallback, View.OnClickListener {


    var TAG = "MainFragment"
    lateinit var gmap: GoogleMap


    private lateinit var mainfragmentBinding: MainfragmentBinding
    lateinit var mapview: SupportMapFragment
    var isinitialized = false
    var listdata = ArrayList<Result>()
    lateinit var nearByModel: NearByModel
    lateinit var nextpagetoken: String
    var nearbyAdapter: NearbyAdapter? = null
    var favadapter: FavouriteAdapter? = null
    private lateinit var currentLocationListener: CurrentLocationListener
    var isfirst = true
    var savedlist = ArrayList<Rating>()
    var dialogshow = false
    override fun getLayoutId(): Int {
        return com.smsol.rateit.R.layout.mainfragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainfragmentBinding = getViewBinding()
        nearByModel = ViewModelProviders.of(this).get(NearByModel::class.java)

        mapview = (childFragmentManager
            .findFragmentById(com.smsol.rateit.R.id.mapView) as SupportMapFragment?)!!
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                currentLocationListener = CurrentLocationListener.getInstance(context)
                initializemap()

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 200
                )
            } else {
                // We've never asked. Just do it.
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 200
                )
            }
        } else {
            currentLocationListener = CurrentLocationListener.getInstance(context)
        }

        mainfragmentBinding.btnfav.setOnClickListener(this)
        mainfragmentBinding.btnrest.setOnClickListener(this)


    }

    fun initializemap() {
        if (!isinitialized) {
            mapview.getMapAsync(this)
        }

    }

    override fun onMapReady(map: GoogleMap?) {

        gmap = map!!
        gmap.setMinZoomPreference(12f)
        gmap.setMyLocationEnabled(true)
        gmap.getUiSettings().setMyLocationButtonEnabled(false)
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL)
        // gmap.setMinZoomPreference(1.0f)
        gmap.setMaxZoomPreference(50.0f)
        gmap.setTrafficEnabled(false)
        gmap.setIndoorEnabled(false)
        gmap.setBuildingsEnabled(true)
        gmap.getUiSettings().setZoomControlsEnabled(false)
        gmap.getUiSettings().setZoomGesturesEnabled(true)
        gmap.getUiSettings().setCompassEnabled(true)
        gmap.getUiSettings().setRotateGesturesEnabled(true)
        var localPrefrence = LocalPrefrence(context!!)
        if (localPrefrence.getRatings() != null) {
            savedlist = localPrefrence.getRatings()!!
            savedlist.sortWith(compareByDescending { it.rating })

        }
        if (favadapter == null) {
            favadapter = FavouriteAdapter(savedlist)
            mainfragmentBinding.favlist.adapter = favadapter

        } else {
            favadapter?.notifyDataSetChanged()
        }

        CurrentLocationListener.getInstance(context).observe(this, Observer { t ->
            if (!isinitialized) {
                isinitialized = true
                val ny = LatLng(t.latitude, t.longitude)
                gmap.moveCamera(CameraUpdateFactory.newLatLng(ny))
                val cameraPosition = CameraPosition.Builder().target(ny).zoom(20.0f).build()
                gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
            if (GlobalRetrofit.verifyAvailableNetwork(activity as MainActivity)) {
                if (isfirst) {
                    getData(t.latitude.toString() + "," + t.longitude.toString(), false)
                }
            } else {
                if (!dialogshow) {
                    dialogshow = true
                    (activity as MainActivity).showDialog(getString(R.string.nointernet), false)
                }

            }


        })

    }

    fun getData(latlng: String, loadmore: Boolean) {
        var map = HashMap<String, String>()
        map["location"] = latlng
        map["rankby"] = "distance"
        map["type"] = "restaurant"
        map["key"] = getString(com.smsol.rateit.R.string.mapkey)
        if (loadmore) {
            map["next_page_token"] = nextpagetoken
        }
        nearByModel.getList(map).observe(this, Observer { t1 ->
            if (t1.status.equals("OK", true)) {
                isfirst = false
                nextpagetoken = t1.next_page_token
                listdata.addAll(t1.results)
                checkratings(listdata)
                addMarkers(listdata)


            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            initializemap()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: $requestCode : $resultCode : ")
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_CANCELED) {
                CurrentLocationListener.getInstance(context).onDestroy()
            } else if (resultCode == Activity.RESULT_OK) {
                initializemap()
            }
        }

    }

    fun checkratings(listdata: ArrayList<Result>) {
        if (savedlist.size > 0) {
            for (i in 0 until listdata.size) {
                for (j in 0 until savedlist.size) {
                    if (savedlist.get(j).placeid == listdata.get(i).place_id) {
                        listdata.get(i).userrating = savedlist.get(j).rating
                    }
                }
            }
            if (favadapter == null) {
                favadapter = FavouriteAdapter(savedlist)
                mainfragmentBinding.favlist.adapter = favadapter

            } else {
                favadapter?.notifyDataSetChanged()
            }
        }

        if (nearbyAdapter == null) {
            nearbyAdapter = NearbyAdapter(activity!!, listdata)
            mainfragmentBinding.nearbylist.adapter = nearbyAdapter

        } else {
            nearbyAdapter?.notifyDataSetChanged()
        }


    }

    fun addMarkers(listdata: ArrayList<Result>) {
        var builder = LatLngBounds.Builder();
        val biticon = getBitmapFromVectorDrawable(context!!, com.smsol.rateit.R.drawable.ic_marker1)
        for (i in 0 until listdata.size) {
            val latLng =
                LatLng(listdata.get(i).geometry.location.lat, listdata.get(i).geometry.location.lng)
            val options = MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(biticon))
                .title(listdata[i].name)
            gmap.addMarker(options)
            builder.include(latLng)


        }
        var bounds = builder.build()
        gmap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20))

    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)

        return bitmap
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnfav -> {
                Refresh()
                mainfragmentBinding.nearbylist.visibility = View.GONE
                mainfragmentBinding.favcontainer.visibility = View.VISIBLE
                if (savedlist.size > 0) {
                    mainfragmentBinding.favlist.visibility = View.VISIBLE
                    mainfragmentBinding.favnodata.visibility = View.GONE
                } else {
                    mainfragmentBinding.favlist.visibility = View.GONE
                    mainfragmentBinding.favnodata.visibility = View.VISIBLE
                }


            }
            R.id.btnrest -> {
                Refresh()
                mainfragmentBinding.nearbylist.visibility = View.VISIBLE
                mainfragmentBinding.favcontainer.visibility = View.GONE

            }

        }

    }

    fun Refresh() {
        var localPrefrence = LocalPrefrence(context!!)
        if (localPrefrence.getRatings() != null) {
            savedlist.clear()
            savedlist.addAll(localPrefrence.getRatings()!!)
            //savedlist.sortWith(compareBy { it.rating })
            savedlist.sortWith(compareByDescending { it.rating })

        }
        checkratings(listdata)
    }
}