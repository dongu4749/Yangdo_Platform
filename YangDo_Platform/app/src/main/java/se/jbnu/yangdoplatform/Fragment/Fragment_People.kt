package se.jbnu.yangdoplatform.Fragment

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.chat.MessageActivity
import se.jbnu.yangdoplatform.model.UserModel

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_People.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_People : Fragment() {
    private val myUid = FirebaseAuth.getInstance().currentUser!!.uid

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
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
                builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                builder.create().show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment__people, container, false)
        val recyclerView = v.findViewById<View>(R.id.peoplefragment_recyclerview) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = Fragment_Transaction_historyRecyclerViewAdapter()
        return v
    }

    internal inner class Fragment_Transaction_historyRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var userModels: MutableList<UserModel?>

        init {
            userModels = ArrayList()
            val myUid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userModels.clear()
                    for (snapshot in dataSnapshot.children) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        if (userModel!!.uid == myUid) {
                            continue
                        }
                        userModels.add(userModel)
                    }
                    notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)
            return PeopleViewHolder(view)
        }

        @SuppressLint("RecyclerView")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            FirebaseDatabase.getInstance().reference.child("users").child(myUid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.v("TestForChecking", "SuccessfulIn")
                    val userModel = dataSnapshot.getValue(UserModel::class.java)
                    showTheImageOntheScreen(userModels[position]!!.uid, (holder as PeopleViewHolder).imageView)
                    holder.textView.text = userModels[position]!!.userName
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            holder.itemView.setOnClickListener { view ->
                val intent = Intent(view.context, MessageActivity::class.java)
                intent.putExtra("destinationUid", userModels[position]!!.uid)
                var activityOptions: ActivityOptions? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.from_right, R.anim.to_left)
                    startActivity(intent, activityOptions.toBundle())
                }
            }
        }

        override fun getItemCount(): Int {
            return userModels.size
        }

        private inner class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView
            var textView: TextView

            init {
                imageView = view.findViewById<View>(R.id.frienditem_imageview) as ImageView
                textView = view.findViewById<View>(R.id.frienditem_textview) as TextView
            }
        }
    }

    private fun showTheImageOntheScreen(myUid: String?, userProfileImage: ImageView) {
        // 파이어베이스에 있는 이미지 가져오기
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // 파이어베이스에서 이미지 다운 받고 화면에 표시하기
        val imageRef = storageRef.child(myUid + "profile.jpg")
        imageRef?.downloadUrl?.addOnSuccessListener { uri -> // Got the download URL for 'users/me/profile.png'
            // 액티비티와 프래그먼트 생명주기 차이로 발생하는 getActivity-null 값 방지
            if (activity == null) {
                Log.v("SUPERTAG", "ININININININ")
            } else {
                val downloadUrl = uri.toString()
                Glide.with(requireActivity())
                        .load(downloadUrl)
                        .into(userProfileImage)
            }
        }?.addOnFailureListener { // Handle any errors
            Log.v("TagTagTag", "FailTag")
        }
                ?: Log.v("TagTagTag", imageRef.toString())
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
         * @return A new instance of fragment Fragment_Transaction_history.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Fragment_People {
            val fragment = Fragment_People()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): Fragment_People {
            return Fragment_People()
        }
    }
}