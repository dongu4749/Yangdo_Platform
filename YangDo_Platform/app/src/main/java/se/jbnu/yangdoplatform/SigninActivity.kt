package se.jbnu.yangdoplatform

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import se.jbnu.yangdoplatform.SignUpActivity

class SigninActivity : AppCompatActivity() {
    private var signup: Button? = null
    private var login: Button? = null
    private var email_login: EditText? = null
    private var pwd_login: EditText? = null
    var firebaseAuth: FirebaseAuth? = null
    private val firebaseAuthListener: AuthStateListener? = null
    private val RC_SIGN_IN = 123
    private val TAG = "mainTag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase_login_check()
        setContentView(R.layout.activity_signin)
        signup = findViewById<View>(R.id.btn_signup) as Button
        login = findViewById<View>(R.id.btn_login) as Button
        email_login = findViewById<View>(R.id.user_id_input) as EditText
        pwd_login = findViewById<View>(R.id.user_password_input) as EditText
        firebaseAuth = FirebaseAuth.getInstance()
        login!!.setOnClickListener {
            val email = email_login!!.text.toString().trim { it <= ' ' }
            val pwd = pwd_login!!.text.toString().trim { it <= ' ' }
            if (email.length > 0 && pwd.length > 0) {
                firebaseAuth!!.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(this@SigninActivity) { task ->
                            Log.v("SUPERTAG", "successIn")
                            if (task.isSuccessful) { //성공했을때
                                Toast.makeText(this@SigninActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SigninActivity, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else { //실패했을때
                                Toast.makeText(this@SigninActivity, "로그인 오류", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
        signup!!.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    fun firebase_login_check() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this@SigninActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun signOut() {
            FirebaseAuth.getInstance().signOut()
        }

        @JvmStatic
        fun AccountDelete() {
            val user = FirebaseAuth.getInstance().currentUser
            user!!.delete()
        }
    }
}