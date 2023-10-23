package maurit.carovna_polika

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.google.gson.Gson
import maurit.carovna_polika.ui.login.LoginActivity2


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn) {
                val mainIntent = Intent(this, ProfileActivity::class.java)
                this.startActivity(mainIntent)
            } else {
                val settings = applicationContext.getSharedPreferences("LOGIN", 0)
                val login = settings.getBoolean("LOGIN", false)
                if (login) {
                    val json = settings.getString("USER", "")
                    val mainIntent = Intent(this, ProfileActivity::class.java)
                    mainIntent.putExtra("LOGIN", "EMAIL")
                    mainIntent.putExtra("USER", json)
                    this.startActivity(mainIntent)
                } else {
                    this.startActivity(Intent(this, LoginActivity2::class.java))
                }
            }
            this.finish()
        }, 1000)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}