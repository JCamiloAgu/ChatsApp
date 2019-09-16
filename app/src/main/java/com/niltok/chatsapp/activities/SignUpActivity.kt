package com.niltok.chatsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.niltok.chatsapp.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonGoLogIn.setOnClickListener {
            goToActivity<LoginActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        buttonSignUp.setOnClickListener{
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            if(isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password, confirmPassword))
                signUpByEmail(email, password)
            else
                toast("Please make sure all the data is correct.")


        }

        editTextEmail.validate{ editTextEmail.error = if(isValidEmail(it)) null else "Email is not valid" }

        editTextPassword.validate{ editTextPassword.error = if(isValidPassword(it)) null else "The password should contain 1 lowercase, 1 uppercase, 1 number, 1 special character and 4 characters length at least." }

        editTextConfirmPassword.validate{ editTextConfirmPassword.error = if(isValidConfirmPassword(editTextPassword.text.toString(),it)) null else "Confirm Password does not match with password" }


    }

    private fun signUpByEmail(email: String, password: String)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
                if (task.isSuccessful)
                {
                    mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                        toast("An email has been sent to you. Please, confirm before sign in")

                        goToActivity<LoginActivity> { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK}
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                else
                {
                    toast("An unexpected error occurred, please try again")
                }
            }
    }

}
