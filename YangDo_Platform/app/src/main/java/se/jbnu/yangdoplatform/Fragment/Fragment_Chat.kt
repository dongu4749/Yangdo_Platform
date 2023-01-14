package se.jbnu.yangdoplatform.Fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import se.jbnu.yangdoplatform.model.ChatModel
import se.jbnu.yangdoplatform.model.UserModel
import java.text.SimpleDateFormat
import java.util.*

class Fragment_Chat : Fragment() {
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment__chat, container, false)
        val recyclerView = view.findViewById<View>(R.id.chatfragment_recyclerview) as RecyclerView
        recyclerView.adapter = ChatRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        return view
    }

    internal inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val chatModels: MutableList<ChatModel?> = ArrayList()
        private val uid: String
        private val destinationUsers = ArrayList<String?>()

        init {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    chatModels.clear()
                    for (item in dataSnapshot.children) {
                        chatModels.add(item.getValue(ChatModel::class.java))
                    }
                    //ListView를 새로고침해준다
                    notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
            return FriendViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val friendViewHolder = holder as FriendViewHolder
            var destinationUid: String? = null

            //챗방에 있는 유저를 체크한다. 일일이2
            for (user in chatModels[position]!!.users.keys) {
                if (user != uid) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }

            //profile 이름 받아오기
            showTheImageOntheScreen(destinationUid, friendViewHolder.imageView)
            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    friendViewHolder.textView_title.text = userModel!!.userName
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            val commentMap: MutableMap<String, ChatModel.Comment> = TreeMap(Collections.reverseOrder())
            commentMap.putAll(chatModels[position]!!.comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            friendViewHolder.textView_last_message.text = chatModels[position]!!.comments[lastMessageKey]!!.message


            //아이템을 클릭 시 -  채팅방으로 이동(MessageActivity.class)
            friendViewHolder.itemView.setOnClickListener { view ->
                val intent = Intent(view.context, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    val activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.from_right, R.anim.to_left)
                    startActivity(intent, activityOptions.toBundle())
                }
            }
            //Time 찍기
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val unixTime = chatModels[position]!!.comments[lastMessageKey]!!.timestamp as Long
            val date = Date(unixTime)
            friendViewHolder.textView_timestamp.text = simpleDateFormat.format(date)
        }

        override fun getItemCount(): Int {
            return chatModels.size
        }

        private inner class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView
            var textView_title: TextView
            var textView_last_message: TextView
            var textView_timestamp: TextView

            init {
                imageView = view.findViewById<View>(R.id.chatitem_imageview) as ImageView
                textView_title = view.findViewById<View>(R.id.chatitem_textview_title) as TextView
                textView_last_message = view.findViewById<View>(R.id.chatitem_textview_lastmessage) as TextView
                textView_timestamp = view.findViewById<View>(R.id.chatitem_textview_timestamp) as TextView
            }
        }
    }

    private fun showTheImageOntheScreen(myUid: String?, userProfileImage: ImageView) {
        //파이어베이스에 있는 이미지 가져오기
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        val imageRef = storageRef.child(myUid + "profile.jpg")
        imageRef.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for 'users/me/profile.png'
            val downloadUrl = uri.toString()
            Glide.with(requireActivity())
                    .load(downloadUrl)
                    .into(userProfileImage)
        }.addOnFailureListener {
            // Handle any errors
        }
    }
}