package se.jbnu.yangdoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import se.jbnu.yangdoplatform.model.UserModel;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.check).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    signUp();
                    break;
            }
        }
    };

    private void signUp(){
        String id=((EditText)findViewById(R.id.user_id)).getText().toString();
        String password=((EditText)findViewById(R.id.user_password)).getText().toString();
        String passwordCheck=((EditText)findViewById(R.id.user_password_check)).getText().toString();
        String name=((EditText)findViewById(R.id.user_name)).getText().toString();

        if(id.length()>0 && password.length()>0 && passwordCheck.length()>0 && name.length()>0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(id, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String uid = task.getResult().getUser().getUid();
                                    UserModel userModel = new UserModel();
                                    userModel.userName = name;
                                    userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                    Toast.makeText(SignUpActivity.this, "회원가입에 성공했습니다." ,Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                                    intent.putExtra(userModel.userName,userModel.uid);
                                    startActivity(intent);
                                } else {
                                    if(task.getException().toString() !=null){
                                        Toast.makeText(SignUpActivity.this, "회원가입에 실패했습니다." ,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
            else{
                Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(SignUpActivity.this, "아아디와 비밀번호를 확인해주세요." ,Toast.LENGTH_SHORT).show();
        }
    }
}