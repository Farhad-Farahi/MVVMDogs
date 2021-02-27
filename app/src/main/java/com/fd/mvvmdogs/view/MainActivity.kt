package com.fd.mvvmdogs.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.fd.mvvmdogs.R
import com.fd.mvvmdogs.databinding.ActivityMainBinding
import com.fd.mvvmdogs.utils.Constants.PERMISSION_SEND_SMS
import com.fd.mvvmdogs.view.fragment.DetailFragment
import com.fd.mvvmdogs.view.fragment.ListFragment


/**
 * kotlin allow us to add parameters or methods to classes that we dont own with ktx wow
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUpBackArrowButton()


    }

    private fun setUpBackArrowButton() {
        navController = Navigation.findNavController(this, R.id.fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }


    fun checkSmsPermission(){
        //check we have send sms Permission or not
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            //if we need to explain for User
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.SEND_SMS
                )){


                AlertDialog.Builder(this)
                    .setTitle("Send SMS permission")
                    .setMessage("this app requires access to send an sms")
                    .setPositiveButton("Ask me"){ dialog, which ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No"){ dialog, which ->
                        notifyDetailFragment(false)
                    }.show()

            }else{
                requestSmsPermission()
            }
        }else{
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyDetailFragment(true)
                } else {
                    notifyDetailFragment(false)
                }
            }
        }


    }

    private fun notifyDetailFragment(permissionGranted: Boolean){
        //get current fragment
        val activeFragment  = this.supportFragmentManager.findFragmentById(R.id.fragment)?.childFragmentManager?.primaryNavigationFragment

        if (activeFragment is DetailFragment){
            (activeFragment as DetailFragment).onPermissionResult(permissionGranted)
        }

    }
}