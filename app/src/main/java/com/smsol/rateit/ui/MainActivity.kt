package com.smsol.rateit.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import com.smsol.mp.BaseActivity
import com.smsol.mp.utils.NavigationUtils
import com.smsol.rateit.R
import com.smsol.rateit.databinding.ActivityMainBinding
import com.smsol.rateit.utils.CurrentLocationListener
import android.view.WindowManager
import android.os.Build



class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var TAG = "MainActivity"

    private var mainFragment = MainFragment()
    override fun getLayoutId(): Int {
        return com.smsol.rateit.R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(com.smsol.rateit.R.color.colorPrimaryDark)
        }
        NavigationUtils.ReplaceFragment(
            mainFragment,
            supportFragmentManager,
            com.smsol.rateit.R.id.maincontainer
        )


    }

    override fun onDestroy() {
        super.onDestroy()
        CurrentLocationListener.getInstance(applicationContext).onDestroy()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        var currentfragment = supportFragmentManager.findFragmentById(com.smsol.rateit.R.id.maincontainer)
        if (currentfragment is MainFragment) {
            mainFragment.Refresh()
        }
    }

    fun showDialog(msg: String, goback: Boolean) {
        var dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(com.smsol.rateit.R.layout.custom_dialo)
        var tvmsg = dialog.findViewById<TextView>(com.smsol.rateit.R.id.tvmsg)
        var btnok = dialog.findViewById<TextView>(com.smsol.rateit.R.id.btnok)
        tvmsg.text = msg
        btnok.setOnClickListener {
            dialog.dismiss()
            if (goback) {
                onBackPressed()
            }

        }
        dialog.show()


    }
}
