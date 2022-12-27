package se.jbnu.yangdoplatform.Fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import se.jbnu.yangdoplatform.R;
import se.jbnu.yangdoplatform.chat.MessageActivity;
import se.jbnu.yangdoplatform.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_People#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_People extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_People() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Transaction_history.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_People newInstance(String param1, String param2) {
        Fragment_People fragment = new Fragment_People();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static Fragment_People newInstance() {
        return new Fragment_People();
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
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment__people, container, false);
        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new Fragment_Transaction_historyRecyclerViewAdapter());


        return v;
    }
    class Fragment_Transaction_historyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<UserModel> userModels;

        public Fragment_Transaction_historyRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear();

                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if(userModel.uid.equals(myUid))
                        {
                            continue;
                        }
                        userModels.add(userModel);
                    }

                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends,parent,false);
            return new PeopleViewHolder(view);
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {




            FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("TestForChecking","SuccessfulIn");
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    showTheImageOntheScreen(userModels.get(position).uid, ((PeopleViewHolder)holder).imageView);
                    ((PeopleViewHolder)holder).textView.setText(userModels.get(position).userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //수정 전 코드
//            Glide.with
//                    (holder.itemView.getContext())
//                    .load(userModels.get(position).profileImageUrl)
//                    .apply(new RequestOptions().circleCrop())
//                    .into(((PeopleViewHolder)holder).imageView);
//            ((PeopleViewHolder)holder).textView.setText(userModels.get(position).userName);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",userModels.get(position).uid);
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.from_right,R.anim.to_left);
                        startActivity(intent,activityOptions.toBundle());
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class PeopleViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public PeopleViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
//                showTheImageOntheScreen(myUid, imageView);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
            }
        }
    }

    private void showTheImageOntheScreen(String myUid, ImageView userProfileImage) {
        //파이어베이스에 있는 이미지 가져오기
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //파이어베이스에서 이미지 다운 받고 화면에 표시하기
        StorageReference imageRef = storageRef.child(myUid + "profile.jpg");
        if (imageRef == null){
            Log.v("TagTagTag",imageRef.toString());
        } else {
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    //액티비티와 프래그먼트 생명주기 차이로 발생하는 getActivity-null 값 방지
                    if(getActivity() == null){
                        Log.v("SUPERTAG", "ININININININ");
                    }
                    else{
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
                    Log.v("TagTagTag","FailTag");
                }
            });

        }

    }
}