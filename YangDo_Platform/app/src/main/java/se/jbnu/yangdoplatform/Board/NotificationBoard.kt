package se.jbnu.yangdoplatform.Board

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.model.BoardModel

class NotificationBoard : AppCompatActivity() {

    private var title_array_Notification: ArrayList<BoardModel?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_board)

        val notificationBoard_board_back_button : ImageButton = findViewById(R.id.notification_board_back_button)
        notificationBoard_board_back_button.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


        val rv_board = findViewById<RecyclerView>(R.id.fqa_notification_recyclerview)
        val boardAdapter = NotificationBoardAdapter()

        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }


    internal inner class NotificationBoardAdapter() :
        RecyclerView.Adapter<NotificationBoard.NotificationBoardAdapter.NotificationBoardViewHolder>() {


        var selectedNotificationName: String = ""
        val database = FirebaseDatabase.getInstance().reference
        val myRef = database.child("FAQ")


        init {

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (item in dataSnapshot.children) {
                        title_array_Notification.add(item.getValue(BoardModel::class.java))
                    }
                    // View를 새로고침해준다
                    notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }



        private var notificationBoardTitle = ""

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NotificationBoard.NotificationBoardAdapter.NotificationBoardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fqa_board, parent, false)
            return NotificationBoardViewHolder(view)
        }



        override fun onBindViewHolder(
            holder: NotificationBoard.NotificationBoardAdapter.NotificationBoardViewHolder,
            position: Int
        ) {
            val friendViewHolder = holder as NotificationBoardViewHolder

            holder.notification_title.text = title_array_Notification[position]?.title
            Log.v("TAGININ",position.toString())

            //아이템을 클릭 시 -  채팅방으로 이동(MessageActivity.class)
            friendViewHolder.itemView.setOnClickListener { view ->
                notificationBoardTitle = title_array_Notification[position]?.title.toString()
//                fqaBoardTitle = itemList[position].title
                Log.v("TAGININ",notificationBoardTitle)

                // 일단 FAQ랑 공지사항이랑 같이 처리
                val intent = Intent(view.context, Board_From_FAQ_Activity::class.java)
                intent.putExtra("clickedNotificationTitle", notificationBoardTitle)
                val activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.from_right, R.anim.to_left)
                startActivity(intent, activityOptions.toBundle())
            }

        }


        override fun getItemCount(): Int {
            return title_array_Notification.size
        }


        inner class NotificationBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val notification_title = itemView.findViewById<TextView>(R.id.fqa_board_title_item)
        }

    }

}