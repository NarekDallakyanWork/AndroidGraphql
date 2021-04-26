package bazaar.tech.com.auth.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bazaar.tech.com.auth.model.LoginResponseModel
import bazaar.tech.com.auth.model.PhoneModel

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    var email = ObservableField<String>()
    var password = ObservableField<String>()

    var phoneIsValid = false
    var passwordIsValid = false

    val fieldValidationLiveData = MutableLiveData<Boolean>()

    private val authRepository = LoginRepository(viewModelScope)
    var loginLiveData = MutableLiveData<LoginResponseModel>()

    fun login() {

        authRepository.loginLiveData = loginLiveData
        authRepository.login(PhoneModel(email.get()), password.get())
    }
}