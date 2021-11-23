package com.beeftechlabs.androiddapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beeftechlabs.androiddapp.ui.main.MainDappFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainDappFragment.newInstance())
                .commitNow()
        }
    }
}