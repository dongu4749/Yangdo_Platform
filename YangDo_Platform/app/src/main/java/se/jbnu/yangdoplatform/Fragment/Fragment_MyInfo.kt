package se.jbnu.yangdoplatform.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import se.jbnu.yangdoplatform.HomeActivity
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.model.UserModel

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_MyInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_MyInfo : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    //사용자 프로필 불러오기 위한 변수
    private val mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
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
        val v = inflater.inflate(R.layout.fragment__my_info, container, false)
        val myToolbar = v.findViewById<View>(R.id.my_toolbar) as Toolbar
        setHasOptionsMenu(true)
        val activity = activity as AppCompatActivity?
        activity!!.setSupportActionBar(myToolbar)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)

        //파이어베이스에 저장된 정보로부터 사용자 불러오기
        //프로필 이름 넣기 위한 변수
        val user_nickname = v.findViewById<View>(R.id.user_nickname) as TextView
        val myUid = FirebaseAuth.getInstance().currentUser!!.uid

        //프로필 이미지 넣기 위한 변수
        val imageView = v.findViewById<View>(R.id.user_img) as ImageView

        //파이어베이스에 있는 이미지 가져오기
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        //프로필 수정 버튼
        val modifyingBtn = v.findViewById<View>(R.id.profile_edit) as Button


        //닉네임 파이어베이스에서 받아와서 넣기
        FirebaseDatabase.getInstance().reference.child("users").child(myUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.v("TestForChecking", "SuccessfulIn")
                val userModel = dataSnapshot.getValue(UserModel::class.java)
                user_nickname.text = userModel!!.userName
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        modifyingBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userName", user_nickname.text.toString())
            //프래그먼트간 이동
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment_profile_modifying = Fragment_Profile_Modifying()
            fragment_profile_modifying.arguments = bundle
            transaction.replace(R.id.content_layout, fragment_profile_modifying)
            //꼭 commit을 해줘야 바뀐다.
            transaction.commitNow()
        }


        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        val imageRef = storageRef.child(myUid + "profile.jpg")
        imageRef.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for 'users/me/profile.png'
            if (uri != null && getActivity() != null) {
                val downloadUrl = uri.toString()
                Glide.with(requireActivity())
                        .load(downloadUrl)
                        .into(imageView)
            }
        }.addOnFailureListener {
            // Handle any errors
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

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment_MyInfo.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment_MyInfo {
            val fragment = Fragment_MyInfo()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment_MyInfo {
            return Fragment_MyInfo()
        }
    }
}