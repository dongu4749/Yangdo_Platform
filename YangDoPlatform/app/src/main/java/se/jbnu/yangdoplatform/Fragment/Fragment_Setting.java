package se.jbnu.yangdoplatform.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import se.jbnu.yangdoplatform.HomeActivity;
import se.jbnu.yangdoplatform.R;
import se.jbnu.yangdoplatform.SigninActivity;
import se.jbnu.yangdoplatform.SplashActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Setting extends PreferenceFragmentCompat {
    SharedPreferences prefs;

    public static Fragment_Setting newInstance() {
        return new Fragment_Setting();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        androidx.preference.Preference buttonLogout = (androidx.preference.Preference) findPreference("Logout");
        androidx.preference.Preference buttonAccountDelete = (androidx.preference.Preference) findPreference("AccountDelete");


        buttonLogout.setOnPreferenceClickListener(new androidx.preference.Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(androidx.preference.Preference preference) {
                //open browser or intent here
                SigninActivity signinActivity = new SigninActivity();
                signinActivity.signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                return true;
            }
        });

        buttonAccountDelete.setOnPreferenceClickListener(new androidx.preference.Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("회원탈퇴");
                builder.setMessage("정말 회원탈퇴를 진행하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            SigninActivity signinActivity = new SigninActivity();
                            signinActivity.AccountDelete();
                            Intent intent = new Intent(getActivity().getApplicationContext(), SplashActivity.class);
                            startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((HomeActivity)getActivity()).replaceFragment(Fragment_Setting.newInstance());
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }


}