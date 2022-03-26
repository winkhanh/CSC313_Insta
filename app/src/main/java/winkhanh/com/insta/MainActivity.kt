package winkhanh.com.insta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.parse.ParseUser
import winkhanh.com.insta.fragments.ComposeFragment
import winkhanh.com.insta.fragments.FeedFragment
import winkhanh.com.insta.fragments.ProfileFragment


class MainActivity : AppCompatActivity() {
    val fragmentManager: FragmentManager = supportFragmentManager
    val PROFILE_PIC_KEY = "profilePicture"

    lateinit var bottomNav : BottomNavigationView
    val fragmentH : Fragment = FeedFragment.newInstance()
    val fragmentC : Fragment = ComposeFragment.newInstance()
    val fragmentP : Fragment = ProfileFragment.newInstance(ParseUser.getCurrentUser().objectId)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_logout->{
                ParseUser.logOut()
                val intent: Intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bnvNav)
        Log.d("CURRENTUSER",ParseUser.getCurrentUser().keySet().toString())
        setSupportActionBar(findViewById(R.id.toolbar))



        bottomNav.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.action_home -> {
                    fragment = fragmentH
                    Log.d("Main","Pick Home")
                }
                R.id.action_compose -> {
                    fragment = fragmentC
                    Log.d("Main","Pick Compose")
                }
                R.id.action_profile -> {
                    fragment = fragmentP
                    Log.d("Main","Pick Profile")
                }
                else -> fragment = fragmentH
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            true
        }
        bottomNav.selectedItemId = R.id.action_home
    }
    fun backToHome(){
        bottomNav.selectedItemId=R.id.action_home
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentH).commit()
    }
    fun goToProfile(userId:String){
        bottomNav.isSelected=false
        val fragmentSP : Fragment = ProfileFragment.newInstance(userId)
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentSP).commit()
    }
}