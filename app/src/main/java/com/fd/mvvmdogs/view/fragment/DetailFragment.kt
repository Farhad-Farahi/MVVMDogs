package com.fd.mvvmdogs.view.fragment

import android.R.attr.data
import android.R.attr.start
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.fd.mvvmdogs.R
import com.fd.mvvmdogs.databinding.FragmentDetailBinding
import com.fd.mvvmdogs.databinding.SendSmsDialogBinding
import com.fd.mvvmdogs.model.DogBreedModel
import com.fd.mvvmdogs.model.DogPalette
import com.fd.mvvmdogs.model.SmsInfoModel
import com.fd.mvvmdogs.view.MainActivity
import com.fd.mvvmdogs.viewmodel.DetailsViewModel


class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var databinding: FragmentDetailBinding

    private lateinit var viewModel: DetailsViewModel

    private var sendSmsStarted = false

    private var currentDog: DogBreedModel? = null

    private var dogUuid = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return databinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if the arguments is not null
        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid

        }
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        viewModel.fetch(dogUuid)


        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(
            viewLifecycleOwner,
            Observer { dog ->            //it : DogBreedModel
                currentDog = dog
                dog?.let {
                    databinding.dogDetailFragment = dog

                    it.imageUrl?.let {
                        setUpBackgroundColor(it)
                    }
                }

            })
    }

    private fun setUpBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                        val myPalette = DogPalette(intColor)
                        databinding.palette = myPalette
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_send_sms -> {
                sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT , "Check out this dog breed")
                intent.putExtra(Intent.EXTRA_TEXT , "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM , currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent,"Share with"))
            }
        }


        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {

                val smsInfo = SmsInfoModel(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                    currentDog?.imageUrl

                )

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS"){ dialog, which->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty() ){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel"){ dialog, which ->
//                        dialog.dismiss()
                    }.show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }
    private fun sendSms(smsInfo: SmsInfoModel){
        val intent = Intent(context,MainActivity::class.java)
        val pi = PendingIntent.getActivity(context,0,intent,0)
        val sms = SmsManager.getDefault()

        sms.sendTextMessage(smsInfo.to , null,smsInfo.text,pi,null)
    }
}