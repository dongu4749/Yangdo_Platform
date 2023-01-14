package se.jbnu.yangdoplatform.Fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import se.jbnu.yangdoplatform.HomeActivity
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.SigninActivity
import se.jbnu.yangdoplatform.SigninActivity.Companion.AccountDelete
import se.jbnu.yangdoplatform.SigninActivity.Companion.signOut
import se.jbnu.yangdoplatform.SplashActivity

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Setting.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Setting : PreferenceFragmentCompat() {
    var prefs: SharedPreferences? = null
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.settings_preference)
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val buttonLogout = findPreference("Logout") as Preference?
        val buttonAccountDelete = findPreference("AccountDelete") as Preference?
        buttonLogout!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { //open browser or intent here
            val signinActivity = SigninActivity()
            signOut()
            val intent = Intent(requireActivity().applicationContext, SplashActivity::class.java)
            startActivity(intent)
            true
        }
        buttonAccountDelete!!.onPreferenceClickListener = Preference.OnPreferenceClickListener { //open browser or intent here
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("회원탈퇴")
            builder.setMessage("정말 회원탈퇴를 진행하시겠습니까?")
            builder.setPositiveButton("OK") { dialog, which ->
                val signinActivity = SigninActivity()
                AccountDelete()
                val intent = Intent(requireActivity().applicationContext, SplashActivity::class.java)
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, which -> (activity as HomeActivity?)!!.replaceFragment(newInstance()) }
            builder.create().show()
            true
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): Fragment_Setting {
            return Fragment_Setting()
        }
    }
}