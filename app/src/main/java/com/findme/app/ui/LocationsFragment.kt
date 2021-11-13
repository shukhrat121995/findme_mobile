package com.findme.app.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.findme.app.MainViewModel
import com.findme.app.R
import com.findme.app.databinding.FragmentLocationsBinding
import com.findme.app.model.Location
import com.findme.app.utils.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationsFragment : Fragment() {

    companion object {
        const val TAG = "LocationsFragment";

        @JvmStatic
        fun newInstance() =
            LocationsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private var locationList: List<Location> = emptyList()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationToken = CancellationTokenSource()

    private val timer: CountDownTimer = object : CountDownTimer(60 * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tvTimer.text = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
            )
        }

        override fun onFinish() {
            getLocation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timer.start()

        mainViewModel.getLocations()

        mainViewModel.data.observe(viewLifecycleOwner, {
            locationList = it.locations ?: emptyList()
        })

        binding.btnUpdateLocation.setOnClickListener {
            getLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        getLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        view?.findViewById<MaterialButton>(R.id.btn_update_location)?.isEnabled = false
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener {
            if (it != null)
                lifecycleScope.launch(Dispatchers.Main) {
                    toast(it.provider + " ${it.latitude} ${it.longitude}")
                    binding.tvLat.text = getString(R.string.lat_s, it.latitude.toString())
                    binding.tvLng.text = getString(R.string.lng_s, it.longitude.toString())
                    mainViewModel.updateLocation(it.latitude, it.longitude, locationList)
                }
            else {
                lifecycleScope.launch(Dispatchers.Main) {
                    fusedLocationClient.locationAvailability.addOnSuccessListener {
                        if (!it.isLocationAvailable) {
                            toast("location not available")
                        }
                    }
                }
            }
        }.addOnFailureListener {
            toast(it.localizedMessage)
        }.addOnCanceledListener {
            toast("cancelled")
        }.addOnCompleteListener {
            lifecycleScope.launch(Dispatchers.Main) {
                timer.cancel()
                timer.start()
                view?.findViewById<MaterialButton>(R.id.btn_update_location)?.isEnabled = true
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}