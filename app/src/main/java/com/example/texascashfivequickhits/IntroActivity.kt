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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top portion: Video Player filling upper portion
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.72f)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidView(
                            factory = { context ->
                                VideoView(context).apply {
                                    scaleX = 1.25f
                                    scaleY = 1.25f
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
                    }

                    // Bottom portion: Branding
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.28f)
                            .padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "TX CASH 5 SMART PICKS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGold,
                            letterSpacing = 1.5.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Based on Texas Cash 5 Lotto Numbers · Analyzing 31 Years of Data!",
                            fontSize = 12.sp,
                            color = CyberCyan,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Not affiliated with or endorsed by the Texas Lottery Commission",
                            fontSize = 9.sp,
                            color = Color.Gray,
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