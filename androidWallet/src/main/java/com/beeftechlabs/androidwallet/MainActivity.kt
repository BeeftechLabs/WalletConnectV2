package com.beeftechlabs.androidwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beeftechlabs.androidwallet.ui.main.MainWalletFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainWalletFragment.newInstance(intent?.data?.toString()))
                .commitNow()
        }
    }
}