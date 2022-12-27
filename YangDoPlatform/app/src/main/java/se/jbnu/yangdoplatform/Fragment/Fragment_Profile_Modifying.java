package se.jbnu.yangdoplatform.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import se.jbnu.yangdoplatform.HomeActivity;
import se.jbnu.yangdoplatform.R;
import se.jbnu.yangdoplatform.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Profile_Modifying#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Profile_Modifying extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SELECT_PICTURE = 1;

    private ImageView userProfileImage;
    private String myUid;


    //for imagePicker(using user's galley)
    private static final int PICK_IMAGE_REQUEST_CODE = 2352;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment_Profile_Modifying() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Profile_Modifying.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Profile_Modifying newInstance(String param1, String param2) {
        Fragment_Profile_Modifying fragment = new Fragment_Profile_Modifying();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static Fragment_Profile_Modifying newInstance() {
        return new Fragment_Profile_Modifying();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override

            public void handleOnBackPressed() {

                // Handle the back button event
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("앱 종료");
                builder.setMessage("정말 앱을 종료하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment__profile__modifying, container, false);
        Toolbar myToolbar = (Toolbar) v.findViewById(R.id.modifying_toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        userProfileImage = v.findViewById(R.id.userProfileImage);
        EditText user_nicknameInModifying = v.findViewById(R.id.user_nicknameInModifying);
        Button complete_modifying = v.findViewById(R.id.complete_modifying);

        //userName을 이미지에 저장하기 위한 몸부림
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();



        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    navigateGallery();

                }
                else if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    showPermissionContextPopup();
                }
                else
                {
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            1000
                    );
                }

            }
        });

        showTheImageOntheScreen(myUid, userProfileImage);


        //Fragment_MyInfo에서 닉네임 받아오기
        if(getArguments()!=null){
            String userName = getArguments().getString("userName");
            user_nicknameInModifying.setText(userName);
        }
        user_nicknameInModifying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_nicknameInModifying.setText("");
            }
        });

        //버튼 클릭시 다시 내 정보로 이동(닉네임 변경은 아직 불가)
        complete_modifying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //수정한 닉네임 파이어베이스에 저장
                String modifiedUser_nickname = user_nicknameInModifying.getText().toString();
                // 아무것도 입력하지 않았을 때 방지
                if(modifiedUser_nickname == null){
                    modifiedUser_nickname = String.valueOf((char) ((int) (new Random().nextInt(11171))+44032));
                }
                //닉네임 파이어베이스에 넣기
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("userName").setValue(modifiedUser_nickname);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_MyInfo fragment_myInfo = new Fragment_MyInfo();

                transaction.replace(R.id.content_layout, fragment_myInfo);
                //꼭 commit을 해줘야 바뀐다.
                transaction.commitNow();
            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                ((HomeActivity)getActivity()).replaceFragment(Fragment_Setting.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //권한 수락시 실행되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateGallery();
            } else {
                Toast.makeText(getActivity(), "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //handle the result of the image selection:
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                // Get the selected image as a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                // Save the Bitmap to Firebase
                uploadImageToFirebase(bitmap);



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImageToFirebase(Bitmap bitmap) {

        //이미지 파일명을 구분하기 위함
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
                showTheImageOntheScreen(myUid, userProfileImage);
            }
        });
    }

    private void showTheImageOntheScreen(String myUid, ImageView userProfileImage) {
        //파이어베이스에 있는 이미지 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        StorageReference imageRef = storageRef.child(myUid + "profile.jpg");
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(uri == null){
                    //아무것도 실행하지 않음
                }else {
                    String downloadUrl = uri.toString();
                    Glide.with(getActivity())
                            .load(downloadUrl)
                            .into(userProfileImage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    //갤러리 오픈 메서드
    private void navigateGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
        //아래의 코드를 사용하면 연결이 끊기는 현상 발생 2000을 SLECT_PICTURE로 바꾸면 해결됨 --- 왠지는 모름...
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        /* 가져올 컨텐츠들 중에서 Image 만을 가져온다. */
//        intent.setType("image/*");
//        /* 갤러리에서 이미지를 선택한 후, 프로필 이미지뷰를 수정하기 위해 갤러리에서 수행한 값을 받아오는 startActivityForeResult를 사용한다. */
//        startActivityForResult(intent, 2000);
    }

    //갤러리 오픈 전 동의구하기 메서드
    private void showPermissionContextPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
                .setPositiveButton("동의하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                    }
                })
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });

    }

}