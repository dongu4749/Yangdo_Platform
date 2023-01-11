package se.jbnu.yangdoplatform.Fragment;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import se.jbnu.yangdoplatform.HomeActivity;
import se.jbnu.yangdoplatform.R;
import se.jbnu.yangdoplatform.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_MyInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_MyInfo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //사용자 프로필 불러오기 위한 변수
    private FirebaseAuth mAuth;

    public Fragment_MyInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_MyInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_MyInfo newInstance(String param1, String param2) {
        Fragment_MyInfo fragment = new Fragment_MyInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static Fragment_MyInfo newInstance() {
        return new Fragment_MyInfo();
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
        View v = inflater.inflate(R.layout.fragment__my_info, container, false);
        Toolbar myToolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(myToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        //파이어베이스에 저장된 정보로부터 사용자 불러오기
        //프로필 이름 넣기 위한 변수
        TextView user_nickname = (TextView) v.findViewById(R.id.user_nickname);
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //프로필 이미지 넣기 위한 변수
        ImageView imageView = (ImageView) v.findViewById(R.id.user_img);

        //파이어베이스에 있는 이미지 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //프로필 수정 버튼
        Button modifyingBtn = (Button) v.findViewById(R.id.profile_edit);



        //닉네임 파이어베이스에서 받아와서 넣기
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("TestForChecking","SuccessfulIn");
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                user_nickname.setText(userModel.userName);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        modifyingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("userName", String.valueOf(user_nickname.getText()));
                //프래그먼트간 이동
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment_Profile_Modifying fragment_profile_modifying = new Fragment_Profile_Modifying();

                fragment_profile_modifying.setArguments(bundle);


                transaction.replace(R.id.content_layout, fragment_profile_modifying);
                //꼭 commit을 해줘야 바뀐다.
                transaction.commitNow();


            }
        });


        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        StorageReference imageRef = storageRef.child(myUid + "profile.jpg");
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                if(uri != null && getActivity() != null) {
                    String downloadUrl = uri.toString();
                    Glide.with(getActivity())
                            .load(downloadUrl)
                            .into(imageView);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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
}