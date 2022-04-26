package com.ikhokha.techcheck.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.databinding.ActivitySplashBinding
import com.ikhokha.techcheck.utilits.CheckPermissions
import com.ikhokha.techcheck.utilits.Constants
import com.ikhokha.techcheck.utilits.EFirebaseLoadingStatus
import com.ikhokha.techcheck.viewModel.SplashActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : AppCompatActivity(){

    private val viewModel: SplashActivityViewModel by viewModels()
    private lateinit var permissionsClass: CheckPermissions
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash)

        supportActionBar!!.hide()

        permissionsClass = CheckPermissions(this)
    }

    override fun onResume() {
        super.onResume()
        checkInternetAndPermissions()
    }

    private fun setupData(){
        viewModel.setProducts()
        viewModel.getLoadingStatus().observe(this) {
            when (it) {
                EFirebaseLoadingStatus.SUCCESS -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()

                }
                EFirebaseLoadingStatus.FAILURE -> {

                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_message),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(getString(R.string.try_again)) {
                            setupData()
                        }.show()
                }

                EFirebaseLoadingStatus.LOADING -> {

                }
            }

        }
    }

    private fun checkInternetAndPermissions(){
        if(permissionsClass.hasPermission()){
            if(isOnline(this)){
                setupData()
            }else{
                requestInternetAccess()
            }
        }else{
            permissionsClass.requestPermissions()
        }
    }

    private fun requestInternetAccess(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val view: View = LayoutInflater.from(this)
            .inflate(R.layout.dialog, findViewById(R.id.no_internet_layout))
        view.findViewById<View>(R.id.try_again).setOnClickListener {
            if (!isOnline(this)) {
                checkInternetAndPermissions()
            } else {
                startActivity(Intent(applicationContext, SplashActivity::class.java))
                finish()
            }
        }

        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.REQUEST_PERMISSION_CODE ){
            if (grantResults.isNotEmpty()) {
                checkInternetAndPermissions()
            }else{
                Snackbar.make(binding.root,getString(R.string.permissions_error_message),Snackbar.LENGTH_INDEFINITE).show()
            }
        }else{
            permissionsClass.requestPermissions()
        }
    }

}