package com.smsol.mp

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<V:ViewDataBinding> :AppCompatActivity() {
    lateinit var viewDataBinding: ViewDataBinding
    abstract fun getLayoutId():Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding=DataBindingUtil.setContentView(this,getLayoutId())

    }

    fun getViewBinding():V{
        return viewDataBinding as V
    }
}