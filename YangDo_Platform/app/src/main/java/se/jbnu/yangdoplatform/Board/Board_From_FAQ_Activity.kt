package se.jbnu.yangdoplatform.Board

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.model.BoardModel
import se.jbnu.yangdoplatform.model.ChatModel

class Board_From_FAQ_Activity : AppCompatActivity() {

    private var clickedFQATitle: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fqa_detail_board)

        var fqa_detail_board_title: TextView = findViewById(R.id.fqa_detail_board_title)
        var fqa_detail_board_content: TextView = findViewById(R.id.fqa_detail_board_content)

        val fqa_board_back_button : ImageButton = findViewById(R.id.fqa_detail_board_back_button)
        fqa_board_back_button.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        //클릭으로 받아온 title
        clickedFQATitle = intent.getStringExtra("clickedFQATitle")

        FirebaseDatabase.getInstance().reference.child("FAQ").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    var title: String = item.getValue(BoardModel::class.java)?.title.toString()
                    if(title.equals(clickedFQATitle)){
                        fqa_detail_board_content.setText(item.getValue(BoardModel::class.java)?.content)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })



        fqa_detail_board_title.setText(clickedFQATitle)



    }
}