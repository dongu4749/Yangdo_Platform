package se.jbnu.yangdoplatform.Board;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import se.jbnu.yangdoplatform.R;
import se.jbnu.yangdoplatform.model.BoardModel;

public class Board_RegisterActivity extends AppCompatActivity {

    // 로그에 사용할 TAG 변수 선언
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    EditText title_et, content_et;
    Button reg_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_register);

        // 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);
        // 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 게시물 등록 함수
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                BoardModel boardModel = new BoardModel(title_et.getText().toString(),content_et.getText().toString());
                database.child("board").push().setValue(boardModel);
            }
        });

    }


}