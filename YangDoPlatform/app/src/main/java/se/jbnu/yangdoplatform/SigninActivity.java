package se.jbnu.yangdoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SigninActivity extends AppCompatActivity {
    private Button signup;
    private Button login;
    private EditText email_login;
    private EditText pwd_login;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private int RC_SIGN_IN = 123;
    private String TAG = "mainTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebase_login_check();
        setContentView(R.layout.activity_signin);

        signup = (Button) findViewById(R.id.btn_signup);
        login = (Button) findViewById(R.id.btn_login);

        email_login = (EditText) findViewById(R.id.user_id_input);
        pwd_login = (EditText) findViewById(R.id.user_password_input);
        firebaseAuth = FirebaseAuth.getInstance();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();

                if (email.length() > 0 && pwd.length() > 0) {
                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {//성공했을때
                                        Toast.makeText(SigninActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {//실패했을때
                                        Toast.makeText(SigninActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void AccountDelete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete();
    }

    public void firebase_login_check(){
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
        Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    }
}