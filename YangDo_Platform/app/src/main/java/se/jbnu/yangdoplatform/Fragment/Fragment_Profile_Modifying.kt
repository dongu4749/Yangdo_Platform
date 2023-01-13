package se.jbnu.yangdoplatform.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import se.jbnu.yangdoplatform.GalleryService
import se.jbnu.yangdoplatform.HomeActivity
import se.jbnu.yangdoplatform.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.Inflater

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Profile_Modifying.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Profile_Modifying : Fragment() {
    private var myUid: String? = null

    private var userProfileImage: ImageView ?= null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("앱 종료")
                builder.setMessage("정말 앱을 종료하시겠습니까?")
                builder.setPositiveButton("OK") { dialog, which -> activity!!.finish() }
                builder.setNegativeButton("Cancel") { dialog, which -> }
                builder.create().show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment__profile__modifying, container, false)
        val myToolbar = v.findViewById<View>(R.id.modifying_toolbar) as Toolbar
        setHasOptionsMenu(true)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(myToolbar)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        userProfileImage = v.findViewById(R.id.userProfileImage)
        val user_nicknameInModifying = v.findViewById<EditText>(R.id.user_nicknameInModifying)
        val complete_modifying = v.findViewById<Button>(R.id.complete_modifying)

        // userName을 이미지에 저장하기 위한 몸부림
        myUid = FirebaseAuth.getInstance().currentUser!!.uid
        userProfileImage!!.setOnClickListener(View.OnClickListener {
            val galleryService = GalleryService()
            galleryService.navigateFragment(activity, this@Fragment_Profile_Modifying)
        })
        showTheImageOntheScreen(myUid!!, userProfileImage)


        // Fragment_MyInfo에서 닉네임 받아오기
        val arguments = arguments
        if (arguments != null) {
            val userName = arguments.getString("userName")
            user_nicknameInModifying.setText(userName)
        }

        // nickname 클릭 시 ""으로 set
        user_nicknameInModifying.setOnClickListener { user_nicknameInModifying.setText("") }


        // 버튼 클릭시 다시 내 정보로 이동
        complete_modifying.setOnClickListener { // 수정한 닉네임 파이어베이스에 저장
            val modifiedUser_nickname: String
            // 아무것도 입력하지 않았을 때 방지
            modifiedUser_nickname = if (user_nicknameInModifying.text == null) {
                val randomDefaultName = (Random().nextInt(11171) + 44032).toChar()
                randomDefaultName.toString()
            } else {
                user_nicknameInModifying.text.toString()
            }
            // 닉네임 파이어베이스에 넣기
            FirebaseDatabase.getInstance().reference.child("users").child(myUid!!).child("userName").setValue(modifiedUser_nickname)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment_myInfo = Fragment_MyInfo()
            transaction.replace(R.id.content_layout, fragment_myInfo)
            // 꼭 commit을 해줘야 바뀐다.
            transaction.commitNow()
        }
        return v
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item);
        when (item.itemId) {
            R.id.action_settings -> {
                // User chose the "Settings" item, show the app settings UI...
                (activity as HomeActivity?)!!.replaceFragment(Fragment_Setting.newInstance())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // handle the result of the image selection:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("TESTIN", "onActivityResultIN")
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            try {
                Log.v("TESTIN", "tryIN")
                // Get the selected image as a Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
                // Save the Bitmap to Firebase
                uploadImageToFirebase(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {

        //이미지 파일명을 구분하기 위함
        val myUid = FirebaseAuth.getInstance().currentUser!!.uid
        val storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        val storageRef = storage.reference
        // Create a reference to "mountains.jpg"
        val profileRef = storageRef.child(myUid + "profile.jpg")

        // Get the data from an ImageView as bytes
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = profileRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { showTheImageOntheScreen(myUid, userProfileImage) }
    }

    private fun showTheImageOntheScreen(myUid: String, userProfileImage: ImageView?) {
        //파이어베이스에 있는 이미지 가져오기
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        val imageRef = storageRef.child(myUid + "profile.jpg")
        imageRef.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for 'users/me/profile.png'
            if (uri != null) {
                val downloadUrl = uri.toString()
                Glide.with(requireActivity())
                        .load(downloadUrl)
                        .into(userProfileImage!!)
            }
        }.addOnFailureListener { exception: Exception? -> }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val SELECT_PICTURE = 1

        // for imagePicker(using user's galley)
        private const val PICK_IMAGE_REQUEST_CODE = 2352

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment_Profile_Modifying.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment_Profile_Modifying {
            val fragment = Fragment_Profile_Modifying()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment_Profile_Modifying {
            return Fragment_Profile_Modifying()
        }
    }
}