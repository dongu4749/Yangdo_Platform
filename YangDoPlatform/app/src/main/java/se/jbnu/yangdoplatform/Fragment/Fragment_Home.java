package se.jbnu.yangdoplatform.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import se.jbnu.yangdoplatform.Board.Board_RegisterActivity;
import se.jbnu.yangdoplatform.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Home extends Fragment implements View.OnClickListener{

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab_main, fab_home, fab_gym,fab_register;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Home() {
        // Required empty public constructor
    }
    public static Fragment_Home newInstance() {
        return new Fragment_Home();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Home newInstance(String param1, String param2) {
        Fragment_Home fragment = new Fragment_Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View v = inflater.inflate(R.layout.fragment__home, container, false);
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab_main = (FloatingActionButton) v.findViewById(R.id.fab_main);
        fab_home = (FloatingActionButton) v.findViewById(R.id.fab_home);
        fab_gym = (FloatingActionButton) v.findViewById(R.id.fab_gym);
        fab_register = (FloatingActionButton) v.findViewById(R.id.fab_register);
        fab_main.setOnClickListener(this);
        fab_home.setOnClickListener(this);
        fab_gym.setOnClickListener(this);
        fab_register.setOnClickListener(this);
        return v;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_main:
                anim();
                break;
            case R.id.fab_home:
                anim();
                break;
            case R.id.fab_gym:
                anim();
                break;
            case R.id.fab_register: {
                anim();
                Intent intent = new Intent(getActivity().getApplicationContext(), Board_RegisterActivity.class);
                startActivity(intent);
            }
                break;
        }
    }
    public void anim() {

        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_baseline_add_24);
            fab_home.startAnimation(fab_close);
            fab_gym.startAnimation(fab_close);
            fab_register.startAnimation(fab_close);
            fab_home.setClickable(false);
            fab_gym.setClickable(false);
            fab_register.setClickable(false);
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_baseline_close_24);
            fab_home.startAnimation(fab_open);
            fab_gym.startAnimation(fab_open);
            fab_register.startAnimation(fab_open);
            fab_home.setClickable(true);
            fab_gym.setClickable(true);
            fab_register.setClickable(true);
            isFabOpen = true;
        }
    }

}