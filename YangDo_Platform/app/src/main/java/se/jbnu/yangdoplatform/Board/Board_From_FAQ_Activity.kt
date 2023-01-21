package se.jbnu.yangdoplatform.Board

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import se.jbnu.yangdoplatform.R

class Board_From_FAQ_Activity : AppCompatActivity() {
    var title_tv: TextView? = null
    var content_tv: TextView? = null
    var date_tv: TextView? = null
    var comment_layout: LinearLayout? = null
    var comment_et: EditText? = null
    var reg_button: Button? = null
    private var clickedFQATitle: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)
        // 컴포넌트 초기화
        title_tv = findViewById(R.id.title_tv)
        content_tv = findViewById(R.id.content_tv)
        date_tv = findViewById(R.id.date_tv)
        comment_layout = findViewById(R.id.comment_layout)
        comment_et = findViewById(R.id.comment_et)
        reg_button = findViewById(R.id.reg_button)

        clickedFQATitle = intent.getStringExtra("clickedFQATitle")


        // 등록하기 버튼을 눌렀을 때 댓글 등록 함수 호출

    }
}