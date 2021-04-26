package bazaar.tech.com.auth.model

import bazaar.tech.com.base.model.BaseResponseModel

class LoginResponseModel : BaseResponseModel() {
    var state: State? = null
}

enum class State {
    IN_PROGRESS,
    FAILURE,
    SUCCESS
}