package se.jbnu.yangdoplatform.chat

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.model.ChatModel
import se.jbnu.yangdoplatform.model.UserModel
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {
    private var destinatonUid: String? = null
    private var button: Button? = null
    private var editText: EditText? = null
    private var uid: String? = null
    private var chatRoomUid: String? = null
    private val username: String? = null
    private var recyclerView: RecyclerView? = null
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    private var destinationUserModel: UserModel? = null
    private val notificationId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        uid = FirebaseAuth.getInstance().currentUser!!.uid //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        destinatonUid = intent.getStringExtra("destinationUid") // 채팅을 당하는 아이디
        button = findViewById<View>(R.id.messageActivity_button) as Button
        editText = findViewById<View>(R.id.messageActivity_editText) as EditText
        recyclerView = findViewById<View>(R.id.messageActivity_recyclerview) as RecyclerView
        button!!.setOnClickListener {
            val chatModel = ChatModel()
            chatModel.users.put(uid, true)
            chatModel.users.put(destinatonUid, true)
            if (chatRoomUid == null) {
                button!!.isEnabled = false
                FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(chatModel).addOnSuccessListener { //채팅방이 존재하지 않을때 채팅방을 만들고 주고 받는 메세지를 화면에 표시해준다
                    checkChatRoom()
                }
            } else {
                val comment = ChatModel.Comment()
                comment.uid = uid
                comment.message = editText!!.text.toString()
                comment.timestamp = ServerValue.TIMESTAMP
                FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(comment).addOnCompleteListener { editText!!.setText("") }
            }
        }
        //메세지를 보낸 후 모든 메세지를 화면에 표시하기 위해 호출해준다.
        checkChatRoom()
    }

    //채팅방들 중 자신이 있는 지 확인하고 자신이 있으면 채팅할 상대방 id가 포함돼 있을때 채팅방 key를 가져와 저장한다.
    fun checkChatRoom() {
        FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    val chatModel = item.getValue(ChatModel::class.java)
                    if (chatModel!!.users.containsKey(destinatonUid)) {   // 채팅방 중복 검사
                        chatRoomUid = item.key // key 값은 방 id를 뜻함
                        button!!.isEnabled = true
                        // setLayoutManager is to set the layout of the contents
                        recyclerView!!.layoutManager = LinearLayoutManager(this@MessageActivity)
                        recyclerView!!.adapter = RecyclerViewAdapter()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    internal inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var comments: MutableList<ChatModel.Comment?>

        init {
            comments = ArrayList()
            FirebaseDatabase.getInstance().reference.child("users").child(destinatonUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    destinationUserModel = snapshot.getValue(UserModel::class.java)
                    messageList
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        //메세지가 갱신
        val messageList: Unit
            get() {
                FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        comments.clear()
                        for (item in dataSnapshot.children) {
                            comments.add(item.getValue(ChatModel.Comment::class.java))
                        }

                        //메세지가 갱신
                        notifyDataSetChanged()
                        recyclerView!!.scrollToPosition(comments.size - 1)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_msg, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val messageViewHolder = holder as MessageViewHolder


            //내가보낸 메세지
            if (comments[position]!!.uid == uid) {
                messageViewHolder.textView_message.text = comments[position]!!.message
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                messageViewHolder.linearLayout_destination.visibility = View.INVISIBLE
                messageViewHolder.messageItem_linearLayout_main.gravity = Gravity.RIGHT
            } else {
                showTheImageOntheScreen(destinatonUid, messageViewHolder.imageView_profile)
                messageViewHolder.textView_name.text = destinationUserModel!!.userName
                messageViewHolder.linearLayout_destination.visibility = View.VISIBLE
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                messageViewHolder.textView_message.text = comments[position]!!.message
                messageViewHolder.messageItem_linearLayout_main.gravity = Gravity.LEFT
                messageViewHolder.textView_message.textSize = 25f
            }
            val unixTime = comments[position]!!.timestamp as Long
            val date = Date(unixTime)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val time = simpleDateFormat.format(date)
            messageViewHolder.textView_timestamp.text = time
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        //view를 재사용할때 쓰는 클래스
        private inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textView_message: TextView
            var textView_name: TextView
            var imageView_profile: ImageView
            var linearLayout_destination: LinearLayout
            var messageItem_linearLayout_main: LinearLayout
            var textView_timestamp: TextView

            init {
                textView_message = view.findViewById<View>(R.id.messageItem_textView_message) as TextView
                textView_name = view.findViewById<View>(R.id.messageItem_textview_name) as TextView
                imageView_profile = view.findViewById<View>(R.id.messageItem_imageview_profile) as ImageView
                linearLayout_destination = view.findViewById<View>(R.id.messageItem_linearlayout_destination) as LinearLayout
                messageItem_linearLayout_main = view.findViewById<View>(R.id.messageItem_linearlayout_main) as LinearLayout
                textView_timestamp = view.findViewById<View>(R.id.messageItem_textView_timestamp) as TextView
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.from_left, R.anim.to_right)
    }

    private fun showTheImageOntheScreen(myUid: String?, userProfileImage: ImageView) {
        //파이어베이스에 있는 이미지 가져오기
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        val imageRef = storageRef.child(myUid + "profile.jpg")
        imageRef.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for 'users/me/profile.png'
            val downloadUrl = uri.toString()
            Glide.with(this@MessageActivity)
                    .load(downloadUrl)
                    .into(userProfileImage)
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    companion object {
        const val CHANNEL_ID = "channelId"
    }
}