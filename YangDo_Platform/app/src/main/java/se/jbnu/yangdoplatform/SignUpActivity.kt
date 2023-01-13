package se.jbnu.yangdoplatform

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import se.jbnu.yangdoplatform.model.UserModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class SignUpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private val name: EditText? = null
    private var signUp_userProfileImage: ImageView ?= null
    private var filePath: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        mAuth = FirebaseAuth.getInstance()
        signUp_userProfileImage = findViewById(R.id.signUp_userProfileImage)
        findViewById<View>(R.id.check).setOnClickListener(onClickListener)
        // nullPoint Error 방지코드_/초기 이미지 넣기 실패
        val firstProfileimageFile = File("\\src\\main\\res\\drawable-v24\\user_img.png")
        val absolutePath = firstProfileimageFile.absolutePath
        filePath = Uri.parse(absolutePath)
        Log.v("filepath_TAG", filePath.toString())
        signUp_userProfileImage!!.setOnClickListener(signUp_userProfileImageOnClickListener)
    }

    //회원가입 시 프로필 이미지 선택
    var signUp_userProfileImageOnClickListener = View.OnClickListener { GalleryService().navigateActivity(this@SignUpActivity) }
    var onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.check -> signUp()
        }
    }

    private fun signUp() {
        val id = (findViewById<View>(R.id.user_id) as EditText).text.toString()
        val password = (findViewById<View>(R.id.user_password) as EditText).text.toString()
        val passwordCheck = (findViewById<View>(R.id.user_password_check) as EditText).text.toString()
        val name = (findViewById<View>(R.id.user_name) as EditText).text.toString()
        if (id.length > 0 && password.length > 0 && passwordCheck.length > 0 && name.length > 0) {
            if (password == passwordCheck) {
                mAuth!!.createUserWithEmailAndPassword(id, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val uid = task.result.user!!.uid
                                val userModel = UserModel()
                                userModel.userName = name
                                userModel.uid = FirebaseAuth.getInstance().currentUser!!.uid
                                FirebaseDatabase.getInstance().reference.child("users").child(uid).setValue(userModel)
                                //프로필 부여 코드
                                try {
                                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                                    uploadImageToFirebase(bitmap)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                Toast.makeText(this@SignUpActivity, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, SigninActivity::class.java)
                                intent.putExtra(userModel.userName, userModel.uid)
                                startActivity(intent)
                            } else {
                                if (task.exception.toString() != null) {
                                    Toast.makeText(this@SignUpActivity, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
            } else {
                Toast.makeText(this@SignUpActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@SignUpActivity, "아아디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 갤러리 오픈 메서드
    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_PICTURE)
    }

    // 갤러리 오픈 전 동의구하기 메서드
    private fun showPermissionContextPopup(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
                .setPositiveButton("동의하기") { dialogInterface, i ->
                    ActivityCompat.requestPermissions(this@SignUpActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                    navigateGallery()
                }
                .setNegativeButton("취소하기") { dialogInterface, i ->
                    // Do nothing
                }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {

        // 이미지 파일명을 구분하기 위함
        val myUid = FirebaseAuth.getInstance().currentUser!!.uid
        val storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        val storageRef = storage.reference
        // Create a reference to "mountains.jpg"
        val profileRef = storageRef.child(myUid + "profile.jpg")
        // Create a reference to 'images/mountains.jpg'
        val profileRefImagesRef = storageRef.child("images/" + myUid + "profile.jpg")

        // While the file names are the same, the references point to different files
        profileRef.name == profileRefImagesRef.name // true
        profileRef.path == profileRefImagesRef.path // false

        // Get the data from an ImageView as bytes
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = profileRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            // 화면에 띄우기
            Glide.with(this@SignUpActivity)
                    .load(filePath)
                    .into(signUp_userProfileImage!!)
            Log.v("filepath_TAG", filePath.toString())
        }
    }

    companion object {
        private const val SELECT_PICTURE = 1
    }
}

private fun ImageView.setOnClickListener() {
    TODO("Not yet implemented")
}
