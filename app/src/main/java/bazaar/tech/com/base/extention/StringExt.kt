package bazaar.tech.com.base.extention
import android.util.Base64.DEFAULT
import android.util.Base64.decode

/**
 * Extension method to check if String is Phone Number.
 */
fun String.isPhone(): Boolean {
    val p = "^0\\d{10,10}\$".toRegex()
    return matches(p)
}

/**
 * Extension method to check if String is Password.
 */
fun String.isPassword(): Boolean {
    val p = "[a-z, A-Z,0-9,/,*,&,^,%,$,#,@,(,),_,+]{3,}\$".toRegex()
    return matches(p)
}

/**
 * Extension method to check if String is Email.
 */
fun String.isEmail(): Boolean {
    val p = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}".toRegex()
    return matches(p)
}

/**
 * Extension method to check if String is Email.
 */
fun String.decodeBase64(): String {
    return String(decode(this, DEFAULT))
}