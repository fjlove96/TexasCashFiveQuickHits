package com.example.texascashfivequickhits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.texascashfivequickhits.ui.CashFiveScreen
import com.example.texascashfivequickhits.ui.theme.TexasCashFiveQuickHitsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TexasCashFiveQuickHitsTheme {
                CashFiveScreen()
            }
        }
    }
}
