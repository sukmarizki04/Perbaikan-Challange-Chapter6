package com.example.challenge6.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.challenge6.data.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val dataStoreManager: DataStoreManager): ViewModel() {
    fun getUsername(): LiveData<String> {
        return dataStoreManager.getUsername().asLiveData()
    }
}