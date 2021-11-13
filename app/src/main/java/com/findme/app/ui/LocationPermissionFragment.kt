package com.findme.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.findme.app.MainActivity
import com.findme.app.MainViewModel
import com.findme.app.R
import com.findme.app.utils.toast
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationPermissionFragment : Fragment() {

    companion object {
        const val TAG = "LocationPermissionFragment"
        @JvmStatic
        fun newInstance() =
            LocationPermissionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locationBtn = view.findViewById<MaterialButton>(R.id.btn_location)

        locationBtn.setOnClickListener {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) || shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                mainViewModel.resultLauncher?.launch(locationPermissions)
            } else {
                toast("Show rationale")
                mainViewModel.resultLauncher?.launch(locationPermissions)
            }
        }

    }

}