package maurit.carovna_polika

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton


class LoginActivity : AppCompatActivity() {

    private val context = this

    private lateinit var loginButton: Button
    private lateinit var fbLoginButton: LoginButton

    private lateinit var callbackManager: CallbackManager

    private lateinit var accessTokenTracker: AccessTokenTracker
    private lateinit var profileTracker: ProfileTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar))

        loginButton = findViewById(R.id.btn_login)
        fbLoginButton = findViewById(R.id.login_button)

        callbackManager = CallbackManager.Factory.create()
        fbLoginButton.setReadPermissions(listOf("email", "public_profile"))

        LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        println("Check: onSuccess")
                        // App code
                    }

                    override fun onCancel() {
                        println("Check: onCancel")
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        println("Check: onError")
                        println(exception)
                        // App code
                    }
                })

        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                    oldAccessToken: AccessToken?,
                    currentAccessToken: AccessToken?
            ) {
                println("Check: onCurrentAccessTokenChanged")
                if (currentAccessToken != null){
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            }

        }

        profileTracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(
                    oldProfile: Profile?,
                    currentProfile: Profile?) {
                // App code
            }
        }

        loginButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.input_email)
            val email:String = emailEditText.text.toString()
            if (email.trim().isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                //podmienka pre databazu loginu
                val settings = applicationContext.getSharedPreferences("LOGIN", 0)
                val editor = settings.edit()
                editor.putBoolean("LOGIN", true)
                editor.putString("EMAIL", email)
                editor.apply()

                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("login", "email")
                intent.putExtra("email", email)
                this.startActivity(intent)
            } else {
                emailEditText.setError("Napíš správne email!")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker.stopTracking()
        profileTracker.stopTracking()
    }
}