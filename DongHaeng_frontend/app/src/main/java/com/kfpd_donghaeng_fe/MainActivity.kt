// MainActivity.kt

package com.kfpd_donghaeng_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingScreen // 1. OngoingScreen을 import 합니다.
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import dagger.hilt.android.AndroidEntryPoint // 2. Hilt를 사용하기 위해 이 어노테이션을 추가합니다.

@AndroidEntryPoint // 3. Hilt 필수!
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KFPD_DongHaeng_FETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. 여기서 OngoingScreen()을 호출하면 됩니다!
                    OngoingScreen()
                }
            }
        }
    }
}