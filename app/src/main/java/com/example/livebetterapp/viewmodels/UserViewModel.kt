package com.example.livebetterapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livebetterapp.data.models.User
import com.example.livebetterapp.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData

class UserViewModel(private val repository: AppRepository) : ViewModel() {
    val loginResult = MutableLiveData<User?>()

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.loginUser(username, password)
            loginResult.postValue(user)
        }
    }

    fun register(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.registerUser(user)
        }
    }
}
