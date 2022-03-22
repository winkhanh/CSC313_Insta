package winkhanh.com.insta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.LogInCallback
import com.parse.ParseException
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val etUsername : EditText = findViewById(R.id.etUsername)
        val etPassword : EditText = findViewById(R.id.etPassword)
        val btLogin: Button= findViewById(R.id.btLogin)
        val btRegister: Button = findViewById(R.id.btSignUp)
        if (ParseUser.getCurrentUser()!=null)
            goToMain()
        btLogin.setOnClickListener {
            val userName = etUsername.text.toString()
            val password = etPassword.text.toString()
            onLogin(userName,password)
        }
        btRegister.setOnClickListener {
            val userName = etUsername.text.toString()
            val password = etPassword.text.toString()
            onRegister(userName,password)
        }
    }

    private fun onRegister(userName: String, password: String){
        val user: ParseUser = ParseUser()
        user.username = userName
        user.setPassword(password)
        user.signUpInBackground {
            if (it==null){
                onLogin(userName,password)
            }
        }
    }
    private fun onLogin(userName: String, password:String){
        ParseUser.logInInBackground(userName,password,object:LogInCallback{
            override fun done(user: ParseUser?, e: ParseException?) {
                if (e != null){
                    Log.e("Login","Issue with login",e)
                    Toast.makeText(this@LoginActivity,"Wrong Username or Password",Toast.LENGTH_LONG).show()
                    return
                }
                goToMain()
            }
        })
    }
    fun goToMain(){
        val i : Intent = Intent(this,MainActivity::class.java)
        startActivity(i)
    }
}