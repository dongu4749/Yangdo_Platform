package se.jbnu.yangdoplatform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.jbnu.yangdoplatform.Fragment.Fragment_Chat
import se.jbnu.yangdoplatform.Fragment.Fragment_Home
import se.jbnu.yangdoplatform.Fragment.Fragment_MyInfo
import se.jbnu.yangdoplatform.Fragment.Fragment_People
import java.util.*

class HomeActivity : AppCompatActivity() {
    private var mBottomNV: BottomNavigationView? = null
    private val mGoogleSignInClient: GoogleSignInClient? = null
    private var fragmentStack: Stack<Fragment?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mBottomNV = findViewById(R.id.navigation_view)
        mBottomNV!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->

            //NavigationItemSelecte
            bottomNavigate(menuItem.itemId)
            true
        })
        mBottomNV!!.setSelectedItemId(R.id.navigation_home)
    }

    fun replaceFragment(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_layout, fragment!!).addToBackStack(null).commit()
    }

    private fun bottomNavigate(id: Int) {  //BottomNavigation 페이지 변경
        val tag = id.toString()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment_Home = Fragment_Home()
        val fragment_people = Fragment_People()
        val fragment_Chat = Fragment_Chat()
        val fragment_MyInfo = Fragment_MyInfo()
        fragmentStack = Stack()
        fragmentStack!!.push(fragment_Home)
        val currentFragment = fragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }
        var fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            if (id == R.id.navigation_home) {
                fragment = Fragment_Home()
            } else if (id == R.id.navigation_people) {
                fragment = Fragment_People()
            } else if (id == R.id.navigation_chat) {
                fragment = Fragment_Chat()
            } else if (id == R.id.navigation_myinfo) {
                fragment = Fragment_MyInfo()
            }
            fragmentTransaction.add(R.id.content_layout, fragment!!, tag)
        } else {
            fragmentTransaction.show(fragment)
        }
        fragmentStack!!.push(fragment)
        fragmentTransaction.replace(R.id.content_layout, fragment, tag)
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNow()
    }
}