package se.jbnu.yangdoplatform;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Stack;

import se.jbnu.yangdoplatform.Fragment.Fragment_Chat;
import se.jbnu.yangdoplatform.Fragment.Fragment_Home;
import se.jbnu.yangdoplatform.Fragment.Fragment_MyInfo;
import se.jbnu.yangdoplatform.Fragment.Fragment_People;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;
    private GoogleSignInClient mGoogleSignInClient;
    private Stack<Fragment> fragmentStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBottomNV = findViewById(R.id.navigation_view);
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() { //NavigationItemSelecte
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                bottomNavigate(menuItem.getItemId());
                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.navigation_home);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager
                fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, fragment).addToBackStack(null).commit();
    }

    private void bottomNavigate(int id) {  //BottomNavigation 페이지 변경
        String tag = String.valueOf(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment_Home fragment_Home = new Fragment_Home();
        Fragment_People fragment_people = new Fragment_People();
        Fragment_Chat fragment_Chat = new Fragment_Chat();
        Fragment_MyInfo fragment_MyInfo = new Fragment_MyInfo();



        fragmentStack = new Stack<>();
        fragmentStack.push(fragment_Home);

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (id == R.id.navigation_home) {
                fragment = new Fragment_Home();

            } else if (id == R.id.navigation_people){
                fragment = new Fragment_People();
            }
            else if (id == R.id.navigation_chat){
                fragment = new Fragment_Chat();
            }
            else if(id==R.id.navigation_myinfo){
                fragment = new Fragment_MyInfo();
            }



            fragmentTransaction.add(R.id.content_layout, fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentStack.push(fragment);
        fragmentTransaction.replace(R.id.content_layout, fragment, tag);
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();
    }

}