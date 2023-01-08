package se.jbnu.yangdoplatform;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;


import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AlertDialog;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class GalleryService extends ComponentActivity
{
    private final int SELECT_PICTURE = 1;
    private int REQUEST_CODE = 1000;


    public void navigateActivity(Activity activity){
        Permission galleryPermission = checkPermission(activity);
//        Log.v("TESTININ",galleryPermission.toString());
        switch(galleryPermission){
            // 권한 수락시
            case GRANTED:
                navigateGalleryActivity(activity);
                break;
            // 권한 한번 거부시
            case DENIED:
                showPermissionContextPopup(activity);
                break;
            // 처음 실행 시
            case ISNOTCHECKED:
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
                break;
        }
    }


    public void navigateFragment(Activity activity, Fragment activityFagment){
        Permission galleryPermission = checkPermission(activity);
//        Log.v("TESTININ",galleryPermission.toString());
        switch(galleryPermission){
            // 권한 수락시
            case GRANTED:
                navigateGalleryFragment(activityFagment);
                break;
            // 권한 한번 거부시
            case DENIED:
                showPermissionContextPopup(activity);
                break;
            // 처음 실행 시
            case ISNOTCHECKED:
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);
                break;
        }
    }


    // 갤러리 오픈 메서드
    private void navigateGalleryFragment(Fragment activityFagment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityFagment.startActivityForResult(intent, SELECT_PICTURE);
    }

    // 갤러리 오픈 메서드
    private void navigateGalleryActivity(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, SELECT_PICTURE);
    }



    // 갤러리 오픈 전 동의구하기 메서드
    private void showPermissionContextPopup(Activity activity) {
        Log.v("TESTIN","INININ");
        AlertDialog.Builder galleryPermissionDialog = new AlertDialog.Builder(activity)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.\n설정에서 권한을 변경해주세요.")
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 허용 안함을 2번 누르면 SDK에서 영구적으로 안된다고 설정하기 때문에 한번 허용 안함을 했을 때 권한 설정으로 넘어가게 해야한다.
                        // 다음은 설정으로 가기 위한 Intent 이다.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });
        galleryPermissionDialog.show();
    }


    private Permission checkPermission(Activity activity){
        Permission galleryStoragePermission = Permission.ISNOTCHECKED;;

        boolean isGrantedPermission = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==  PackageManager.PERMISSION_GRANTED;
        boolean isDeniedPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (isGrantedPermission)
        {
            galleryStoragePermission = Permission.GRANTED;
        }
        else if(isDeniedPermission)
        {
            galleryStoragePermission = Permission.DENIED;
        }
        return galleryStoragePermission;
    }
}
