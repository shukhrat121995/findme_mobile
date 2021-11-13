package com.findme.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.findme.app.model.Sensor
import com.findme.app.ui.LocationPermissionFragment
import com.findme.app.ui.LocationsFragment
import com.findme.app.utils.replaceFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)

        showFragmentAccordingPermissionResult()

        viewModel.resultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                showFragmentAccordingPermissionResult()
            }

        fetchFID()

    }

    private fun showFragmentAccordingPermissionResult() {
        if (shouldRequestLocationPermission()) {
            replaceFragment(
                LocationPermissionFragment.newInstance(),
                LocationPermissionFragment.TAG
            )
        } else {
            replaceFragment(LocationsFragment.newInstance(), LocationsFragment.TAG)
        }
    }

    private fun shouldRequestLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
    }

    private fun fetchFID() {
        lifecycleScope.launch(Dispatchers.IO) {
            val fid = FirebaseAnalytics.getInstance(applicationContext).firebaseInstanceId
            viewModel.fid = fid
        }
    }


}