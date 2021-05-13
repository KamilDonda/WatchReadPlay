package com.example.watchreadplay.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.watchreadplay.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_up_button.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            val repeated_password = repeatPassword.text.toString()

            if (password.isNullOrEmpty()) {
                Snackbar.make(
                    it,
                    getString(R.string.empty_password),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else
                if (password == repeated_password) {
                    createAccount(email, password)
                } else {
                    Snackbar.make(
                        it,
                        getString(R.string.different_passwords),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.account_created),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        requireView(),
                        task.exception?.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }
}