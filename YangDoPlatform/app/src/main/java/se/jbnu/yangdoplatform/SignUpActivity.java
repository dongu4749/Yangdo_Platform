package se.jbnu.yangdoplatform;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import se.jbnu.yangdoplatform.model.UserModel;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText name;
    private ImageView signUp_userProfileImage;
    private static final int SELECT_PICTURE = 1;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.check).setOnClickListener(onClickListener);
        signUp_userProfileImage = findViewById(R.id.signUp_userProfileImage);
        // nullPoint Error 방지코드_/초기 이미지 넣기 실패
        File firstProfileimageFile = new File("\\src\\main\\res\\drawable-v24\\user_img.png");
        String absolutePath = firstProfileimageFile.getAbsolutePath();
        filePath = Uri.parse(absolutePath);
        Log.v("filepath_TAG", filePath.toString());
        signUp_userProfileImage.setOnClickListener(signUp_userProfileImageOnClickListener);
    }


    //회원가입 시 프로필 이미지 선택
    View.OnClickListener signUp_userProfileImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new GalleryService().navigateActivity(SignUpActivity.this);
        }
    };

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
                                    //프로필 부여 코드
                                    try {
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                                        uploadImageToFirebase(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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


    // 갤러리 오픈 메서드
    private void navigateGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
    }


    // 갤러리 오픈 전 동의구하기 메서드
    private void showPermissionContextPopup(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
                .setPositiveButton("동의하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                        navigateGallery();
                    }
                })
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });
    }


    private void uploadImageToFirebase(Bitmap bitmap) {

        // 이미지 파일명을 구분하기 위함
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to "mountains.jpg"
        StorageReference profileRef = storageRef.child(myUid + "profile.jpg");
        // Create a reference to 'images/mountains.jpg'
        StorageReference profileRefImagesRef = storageRef.child("images/"+ myUid +"profile.jpg");

        // While the file names are the same, the references point to different files
        profileRef.getName().equals(profileRefImagesRef.getName());    // true
        profileRef.getPath().equals(profileRefImagesRef.getPath());    // false

        // Get the data from an ImageView as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            // 화면에 띄우기
            Glide.with(SignUpActivity.this)
                    .load(filePath)
                    .into(signUp_userProfileImage);
            Log.v("filepath_TAG", filePath.toString());
        }
    }
}