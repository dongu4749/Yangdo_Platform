package se.jbnu.yangdoplatform;
import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication  extends Application{
    private static GlobalApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // 네이티브 앱 키로 초기화
        KakaoSdk.init(this, "2873251a3f1f1480797ffdc3c64d4042");

    }
}
