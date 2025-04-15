
package com.example.novaliora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.novaliora.ui.theme.NovaLioraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovaLioraTheme {
                  App()
            }
        }
    }
}

