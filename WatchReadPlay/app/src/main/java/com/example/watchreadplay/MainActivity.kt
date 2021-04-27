package com.example.watchreadplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.watchreadplay.ui.main.MainFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }

    override fun onBackPressed() {}
}