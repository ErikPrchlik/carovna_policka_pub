package maurit.carovna_polika.data.model

import android.graphics.Bitmap

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
        val userId: String,
        val email: String,
        //moze to byt asi aj url
        val picture: Bitmap?
)