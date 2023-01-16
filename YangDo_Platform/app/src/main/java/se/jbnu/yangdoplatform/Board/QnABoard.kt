package se.jbnu.yangdoplatform.Board

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import se.jbnu.yangdoplatform.R

class QnABoard : AppCompatActivity() {
    // 로그에 사용할 TAG 변수 선언
    private val TAG = javaClass.simpleName

    // 사용할 컴포넌트 선언
    var title_et: EditText? = null
    var content_et: EditText? = null
    var reg_button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qna_register)

        // 컴포넌트 초기화
        title_et = findViewById(R.id.QnA_title_et)
        content_et = findViewById(R.id.QnA_content_et)
        reg_button = findViewById(R.id.QnA_reg_button)

    }
}