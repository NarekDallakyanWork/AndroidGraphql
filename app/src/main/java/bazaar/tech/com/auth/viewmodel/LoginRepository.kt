package bazaar.tech.com.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import bazaar.tech.com.AppApplication
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.CustomerAccessTokenCreateMutation
import com.example.CustomertByTokenQuery
import com.example.type.CustomerAccessTokenCreateInput
import bazaar.tech.com.auth.model.LoginResponseModel
import bazaar.tech.com.auth.model.PhoneModel
import bazaar.tech.com.auth.model.State
import bazaar.tech.com.base.extention.decodeBase64
import bazaar.tech.com.constant.Network
import bazaar.tech.com.helper.SharedHelper
import bazaar.tech.com.network.apollo.client.ApolloClient
import bazaar.tech.com.network.retrofit.RetrofitClient
import ithd.bazaar.app.R
import kotlinx.coroutines.CoroutineScope

class LoginRepository(private var viewModelScope: CoroutineScope) {

    lateinit var loginLiveData: MutableLiveData<LoginResponseModel>

    private suspend fun getEmailByPhoneNumber(phone: PhoneModel) =
        RetrofitClient.instance?.baseUrl(Network.BASE_URL_BACKEND)?.api?.getEmailByPhoneNumber(phone)

    private fun loginByEmail(email: String, password: String) {
        val apolloQuery = CustomerAccessTokenCreateMutation.builder()
            .input(
                CustomerAccessTokenCreateInput.builder()
                    .email(email)
                    .password(password)
                    .build()
            )
            .build()
        ApolloClient.apolloClient
            .mutate(apolloQuery)
            .enqueue(object :
                ApolloCall.Callback<CustomerAccessTokenCreateMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    loginMessage(State.FAILURE, false, AppApplication.appContext.getString(R.string.login_failure_not_found_message))
                }

                override fun onResponse(response: Response<CustomerAccessTokenCreateMutation.Data>) {
                    if (!response.hasErrors()) {
                        val accessToken = response.data()
                            ?.customerAccessTokenCreate()
                            ?.customerAccessToken()
                            ?.accessToken()
                        accessToken?.let {
                            SharedHelper.putKey("accessToken", it)
                            getCustomer(accessToken)
                            loginMessage(State.SUCCESS)
                        } ?: run {
                            loginMessage(State.FAILURE, false, AppApplication.appContext.getString(R.string.login_failure_not_found_message))
                        }
                    } else {
                        loginMessage(State.FAILURE, false, AppApplication.appContext.getString(R.string.login_failure_not_found_message))
                    }
                }
            })
    }

    private fun getCustomer(accessToken: String) {

        val query = CustomertByTokenQuery.builder()
            .accessToken(accessToken)
            .build()

        ApolloClient.apolloClient
            .query(query)
            .enqueue(object : ApolloCall.Callback<CustomertByTokenQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    loginMessage(State.FAILURE, false, AppApplication.appContext.getString(R.string.login_failure_message))
                }

                override fun onResponse(response: Response<CustomertByTokenQuery.Data>) {
                    if (!response.hasErrors()) {

                        val customer = response.data()?.customer()
                        customer?.let {
                            val firstName = customer.firstName()
                            val lastName = customer.lastName()
                            val phone = customer.phone()
                            val company = customer.defaultAddress()?.company()
                            val id = customer.id()
                            val email = customer.email()
                            val decodedId = customer.id().decodeBase64()
                            val customerId = decodedId.substring(decodedId.lastIndexOf('/').plus(1))
                            SharedHelper.putKey("userId", customerId)
                            SharedHelper.putKey("firstName", firstName!!)
                            SharedHelper.putKey("lastName", lastName!!)
                            SharedHelper.putKey("phone", phone!!)
                            SharedHelper.putKey("companyName", company!!)
                            SharedHelper.putKey("email", email!!)
                        }
                    }
                }

            })

    }

    fun login(phone: PhoneModel, password: String?) {


        loginByEmail(phone.phone!!, password!!)
        loginMessage(State.IN_PROGRESS)
    }

    private fun loginMessage(state: State, isSuccess: Boolean = true, message: String = "") {
        val loginResponse = LoginResponseModel()
        loginResponse.state = state
        loginResponse.success = isSuccess
        loginResponse.message = message
        loginLiveData.postValue(loginResponse)
    }
}