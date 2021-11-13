package com.findme.app

import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findme.app.model.Location
import com.findme.app.model.Sensor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.Instant

class MainViewModel : ViewModel() {

    companion object {
        const val MAX_LAST_LOCATIONS_SIZE = 5
    }

    init {
        viewModelScope.launch {
            initDb()
        }
    }

    var resultLauncher: ActivityResultLauncher<Array<String>>? = null
    var fid: String? = null
    private lateinit var ref: DatabaseReference

    private fun initDb() {
        val database = Firebase.database
        ref = database.getReference("sensors")
    }

    val data: LiveData<Sensor> get() = _data
    private val _data: MutableLiveData<Sensor> = MutableLiveData()

    fun getLocations() {
        viewModelScope.launch {
            ref.child(fid!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.getValue(Sensor::class.java)
                    if (result != null) {
                        _data.value = result!!
                    } else {
                        val deviceData =
                            Sensor(emptyList(), "Sensor_$fid", Instant.now().toString())
                        ref.setValue(fid).onSuccessTask {
                            ref.child(fid!!).setValue(deviceData)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    fun updateLocation(lat: Double, lng: Double, locationList: List<Location>) {
        viewModelScope.launch {
            val newList = locationList.toMutableList()
            val newLocation = Location(lat, lng, Instant.now().toString())

            if (locationList.size >= MAX_LAST_LOCATIONS_SIZE) {
                newList.removeLast()
                newList.add(0, newLocation)
            } else {
                newList.add(0, newLocation)
            }

            val newMap = newList.mapIndexed { index, location ->
                "/locations/$index" to location
            }.toMap()

            ref.child(fid!!).updateChildren(newMap)
        }
    }

    override fun onCleared() {
        resultLauncher?.unregister()
        super.onCleared()
    }

}