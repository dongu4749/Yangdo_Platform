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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import se.jbnu.yangdoplatform.GalleryService;
import se.jbnu.yangdoplatform.HomeActivity;
import se.jbnu.yangdoplatform.R;

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


    // for imagePicker(using user's galley)
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

        // userName을 이미지에 저장하기 위한 몸부림
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryService galleryService = new GalleryService();
                galleryService.navigateFragment(activity, Fragment_Profile_Modifying.this);
            }
        });


        showTheImageOntheScreen(myUid, userProfileImage);


        // Fragment_MyInfo에서 닉네임 받아오기
        Bundle arguments = getArguments();
        if(arguments != null){
            String userName = arguments.getString("userName");
            user_nicknameInModifying.setText(userName);
        }

        // nickname 클릭 시 ""으로 set
        user_nicknameInModifying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_nicknameInModifying.setText("");
            }
        });


        // 버튼 클릭시 다시 내 정보로 이동
        complete_modifying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 수정한 닉네임 파이어베이스에 저장
                String modifiedUser_nickname;
                // 아무것도 입력하지 않았을 때 방지
                if(user_nicknameInModifying.getText() == null){
                    char randomDefaultName = (char) (new Random().nextInt(11171) + 44032);
                    modifiedUser_nickname = String.valueOf(randomDefaultName);
                } else{
                    modifiedUser_nickname = user_nicknameInModifying.getText().toString();
                }
                // 닉네임 파이어베이스에 넣기
                FirebaseDatabase.getInstance().getReference().child("users").child(myUid).child("userName").setValue(modifiedUser_nickname);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_MyInfo fragment_myInfo = new Fragment_MyInfo();

                transaction.replace(R.id.content_layout, fragment_myInfo);
                // 꼭 commit을 해줘야 바뀐다.
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


    // handle the result of the image selection:
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("TESTIN", "onActivityResultIN");
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Log.v("TESTIN", "tryIN");
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
                if(uri != null) {
                    String downloadUrl = uri.toString();
                    Glide.with(getActivity())
                            .load(downloadUrl)
                            .into(userProfileImage);
                }
            }
        }).addOnFailureListener(exception -> {});
    }
}