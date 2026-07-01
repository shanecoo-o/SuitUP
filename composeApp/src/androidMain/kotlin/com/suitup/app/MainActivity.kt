package com.suitup.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.suitup.app.data.remote.auth.AndroidEncryptedTokenStore

class MainActivity : ComponentActivity() {
    private val tokenStore by lazy { AndroidEncryptedTokenStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(tokenStore)
        }
    }
}
