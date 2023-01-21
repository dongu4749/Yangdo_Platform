package se.jbnu.yangdoplatform.Board

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import se.jbnu.yangdoplatform.Fragment.Fragment_Chat
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.chat.MessageActivity
import se.jbnu.yangdoplatform.model.BoardModel
import se.jbnu.yangdoplatform.model.ChatModel
import se.jbnu.yangdoplatform.model.UserModel

data class FQABoardItem(val time:String, val title: String, val content: String)


class FQABoard : AppCompatActivity() {
    // 로그에 사용할 TAG 변수 선언
    private val TAG = javaClass.simpleName


    // 사용할 컴포넌트 선언
    var boardModel = BoardModel("sendingTest","TestForSending")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fqa_board)


        val fqa_board_back_button : ImageButton = findViewById(R.id.fqa_board_back_button)
        fqa_board_back_button.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })


        // 고객센터에 문의하기 클릭 시 화면 전환
        val fqa_text_for_oneToneQnA : TextView = findViewById(R.id.fqa_text_for_oneToneQnA)

        fqa_text_for_oneToneQnA.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, OneToOneBoard::class.java)
            val activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.from_right, R.anim.to_left)
            startActivity(intent, activityOptions.toBundle())
        })


        val rv_board = findViewById<RecyclerView>(R.id.fqa_category_recyclerview)
        val boardAdapter = FQABoardAdapter()

        boardAdapter.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    internal inner class FQABoardAdapter() :
        RecyclerView.Adapter<FQABoardAdapter.FQABoardViewHolder>() {

        private val title_array_FAQ: MutableList<BoardModel?> = ArrayList()
        val database = FirebaseDatabase.getInstance().reference
        val myRef = database.child("FAQ")

        init {

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (item in dataSnapshot.children) {
                        title_array_FAQ.add(item.getValue(BoardModel::class.java))
                    }
                    //ListView를 새로고침해준다
                    notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }



        private var fqaBoardTitle = ""


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FQABoardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fqa_board, parent, false)
            return FQABoardViewHolder(view)
        }

        override fun onBindViewHolder(holder: FQABoardViewHolder, position: Int) {
            val friendViewHolder = holder as FQABoardViewHolder

            holder.fqa_title.text = title_array_FAQ[position]?.title
            Log.v("TAGININ",position.toString())

            //아이템을 클릭 시 -  채팅방으로 이동(MessageActivity.class)
            friendViewHolder.itemView.setOnClickListener { view ->
                fqaBoardTitle = title_array_FAQ[position]?.title.toString()
//                fqaBoardTitle = itemList[position].title
                Log.v("TAGININ",fqaBoardTitle)
                val intent = Intent(view.context, Board_From_FAQ_Activity::class.java)
                intent.putExtra("clickedFQATitle", fqaBoardTitle)
                val activityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.from_right, R.anim.to_left)
                startActivity(intent, activityOptions.toBundle())
            }

        }

        override fun getItemCount(): Int {
            return title_array_FAQ.size
        }


        inner class FQABoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fqa_title = itemView.findViewById<TextView>(R.id.fqa_board_title_item)
        }
    }



}



