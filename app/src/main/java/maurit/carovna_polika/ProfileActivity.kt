package maurit.carovna_polika

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import maurit.carovna_polika.data.model.LoggedInUser
import maurit.carovna_polika.ui.login.LoginActivity2
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {

    private lateinit var userImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbar))

        userImageView = findViewById(R.id.user_image)
        userNameTextView = findViewById(R.id.user_name)
        userEmailTextView = findViewById(R.id.user_email)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            loadUserProfile(accessToken)
        } else {
            if (intent.getStringExtra("LOGIN").equals("EMAIL")) {
                val gson = Gson()
                val json: String = intent.getStringExtra("USER") ?: ""
                val loggedInUser = gson.fromJson(json, LoggedInUser::class.java)
                userNameTextView.visibility = View.GONE
                //alebo vytiahnut z databaze user name
                userEmailTextView.text = loggedInUser.email
            }
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val scanIntegrator = IntentIntegrator(this)
            scanIntegrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        val scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        if (scanningResult != null) {
            val scanFormat = scanningResult.formatName
            val scanContent = scanningResult.contents
            // mozno aj  || scanFormat == "EAN-8"
            if (scanFormat != null && scanFormat.equals("EAN_13", ignoreCase = true)) {
                val intent = Intent(this, BookActivity::class.java)
                intent.putExtra("scanFormat", scanFormat)
                intent.putExtra("scanContent", scanContent)
                this.startActivity(intent)
            } else {
                val intent = Intent(this, this::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(intent)
                var toast: Toast = Toast.makeText(applicationContext,
                        "No scan data received!", Toast.LENGTH_SHORT)
                toast.show()
            }
        } else {
            var toast: Toast = Toast.makeText(applicationContext,
                    "No scan data received!", Toast.LENGTH_SHORT)
            toast.show()
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    private fun loadUserProfile(newAccessToken: AccessToken){
        var request: GraphRequest = GraphRequest.newMeRequest(newAccessToken, object :
                GraphRequest.GraphJSONObjectCallback {
            override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                var firstName = `object`?.getString("first_name")
                var lastName = `object`?.getString("last_name")
                var email = `object`?.getString("email")
                var id = `object`?.getString("id")
                var imageUrl = "https://graph.facebook.com/" + id + "/picture?type=large"
                userNameTextView.text = firstName + " " + lastName
                userEmailTextView.text = email
                loadImage(imageUrl, userImageView)
            }
        })

        var parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun loadImage(imageUrl: String, userImageView: ImageView){
        Picasso.get().load(imageUrl).into(userImageView, object : Callback {
            override fun onSuccess() {
            }

            override fun onError(e: Exception?) {
                val updatedImageUrl: String
                updatedImageUrl = if (imageUrl.contains("https")) {
                    imageUrl.replace("https", "http")
                } else {
                    imageUrl.replace("http", "https")
                }
                loadImage(updatedImageUrl, userImageView)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            val nightModeFlags: Int = this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (nightModeFlags == UI_MODE_NIGHT_YES) {
                spanString.setSpan(ForegroundColorSpan(Color.parseColor("#d2dadb")), 0, spanString.length, 0) //fix the color to white
                item.title = spanString
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                val settings = applicationContext.getSharedPreferences("LOGIN", 0)
                val editor = settings.edit()
                editor.putBoolean("LOGIN", false)
                editor.apply()
                FacebookSdk.sdkInitialize(applicationContext)
                LoginManager.getInstance().logOut()
                val intent = Intent(this, LoginActivity2::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(Intent(intent))
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}