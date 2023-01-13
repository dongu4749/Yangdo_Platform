package se.jbnu.yangdoplatform

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class GalleryService : ComponentActivity() {
    private val SELECT_PICTURE = 1
    private val REQUEST_CODE = 1000
    fun navigateActivity(activity: Activity) {
        val galleryPermission = checkPermission(activity)
        when (galleryPermission) {
            Permission.GRANTED -> navigateGalleryActivity(activity)
            Permission.DENIED -> showPermissionContextPopup(activity)
            Permission.ISNOTCHECKED -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }

    fun navigateFragment(activity: Activity, activityFagment: Fragment) {
        val galleryPermission = checkPermission(activity)
        when (galleryPermission) {
            Permission.GRANTED -> navigateGalleryFragment(activityFagment)
            Permission.DENIED -> showPermissionContextPopup(activity)
            Permission.ISNOTCHECKED -> ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }

    // 갤러리 오픈 메서드
    private fun navigateGalleryFragment(activityFagment: Fragment) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityFagment.startActivityForResult(intent, SELECT_PICTURE)
    }

    // 갤러리 오픈 메서드
    private fun navigateGalleryActivity(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activity.startActivityForResult(intent, SELECT_PICTURE)
    }

    // 갤러리 오픈 전 동의구하기 메서드
    private fun showPermissionContextPopup(activity: Activity) {
        Log.v("TESTIN", "INININ")
        val galleryPermissionDialog = AlertDialog.Builder(activity)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.\n설정에서 권한을 변경해주세요.")
                .setPositiveButton("설정") { dialogInterface, i -> // 허용 안함을 2번 누르면 SDK에서 영구적으로 안된다고 설정하기 때문에 한번 허용 안함을 했을 때 권한 설정으로 넘어가게 해야한다.
                    // 다음은 설정으로 가기 위한 Intent 이다.
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null)
                    intent.data = uri
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    activity.startActivity(intent)
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    // Do nothing
                }
        galleryPermissionDialog.show()
    }

    private fun checkPermission(activity: Activity): Permission {
        var galleryStoragePermission = Permission.ISNOTCHECKED
        val isGrantedPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val isDeniedPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (isGrantedPermission) {
            galleryStoragePermission = Permission.GRANTED
        } else if (isDeniedPermission) {
            galleryStoragePermission = Permission.DENIED
        }
        return galleryStoragePermission
    }
}