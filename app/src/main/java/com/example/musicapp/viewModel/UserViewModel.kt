package com.example.musicapp.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.musicapp.model.UserModel
import com.example.musicapp.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel (val repo : UserRepository) {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun signup(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.signup(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgotPassword(email, callback)
    }

    fun addUserToDatabase(
        userId: String, userModel: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUserToDatabase(userId, userModel, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    var _userData = MutableLiveData<UserModel?>()
    var userData = MutableLiveData<UserModel?>()
        get() = _userData

    fun getUSerFromDatabase(userId: String) {
        repo.getUSerFromDatabase(userId) { userModel, success, message ->
            if (success) {
                _userData.value = userModel
            } else {
                _userData.value = null
            }
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }

    fun editProfile(
        userId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        repo.editProfile(userId, data, callback)
    }
}