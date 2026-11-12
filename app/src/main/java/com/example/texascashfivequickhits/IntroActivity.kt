package com.example.texascashfivequickhits

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.texascashfivequickhits.ui.theme.CyberCyan
import com.example.texascashfivequickhits.ui.theme.CyberGold
import com.example.texascashfivequickhits.ui.theme.TexasCashFiveQuickHitsTheme

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(android.R.color.black)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContent {
            TexasCashFiveQuickHitsTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.intro_video}")
                                setVideoURI(videoUri)
                                setOnCompletionListener {
                                    navigateToMain()
                                }
                                setOnErrorListener { _, _, _ ->
                                    navigateToMain()
                                    true
                                }
                                start()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Title at bottom of launch screen (Matching TX Lotto)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TX CASH 5",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGold,
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SMART PICKS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 4.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        if (!isFinishing) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}