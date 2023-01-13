package se.jbnu.yangdoplatform

import android.app.Application
import com.kakao.sdk.common.KakaoSdk.init

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        // 네이티브 앱 키로 초기화
        init(this, "2873251a3f1f1480797ffdc3c64d4042")
    }

    companion object {
        private var instance: GlobalApplication? = null
    }
}