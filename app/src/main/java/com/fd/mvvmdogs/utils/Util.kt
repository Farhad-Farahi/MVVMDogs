package com.fd.mvvmdogs.utils

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fd.mvvmdogs.R

/**
 *   Glide will handle the thread that we load image and will cache it after first time we downloaded from api
 */

//function that will give us a little spinner that we can display on the image while we are waiting for it to be downloaded
fun getProgressDrawable(context: Context) : CircularProgressDrawable{
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}
fun ImageView.loadImage(uri : String , progressDrawable: CircularProgressDrawable){
    val option = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_dog_icon)

    Glide.with(context)
        .setDefaultRequestOptions(option)
        .load(uri)
        .into(this)

}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView , url:String?){
    url?.let {
        view.loadImage(it, getProgressDrawable(view.context))
    }
}