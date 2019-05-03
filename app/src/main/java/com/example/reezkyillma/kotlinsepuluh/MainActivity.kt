package com.example.reezkyillma.kotlinsepuluh

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.reezkyillma.kotlinsepuluh.Add_Data.Add_Data_Act
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //deklarasi untuk request code
    private val RC_SIGN_IN = 7
    //deklarasi untuk sign in client
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    //deklarasi untuk firebase auth
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(
            R.string.default_web_client_id
        )).requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signinbutton.setOnClickListener{
            signIn()
        }

    }

    private fun signIn(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount
    ){
      Log.d ("LOGIN", "firebaseAuth" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken,null)

        mAuth.signInWithCredential(credential).addOnCompleteListener (this) {
            task ->

            if(task.isSuccessful){
                Log.d("LOGIN", "Sign in Success")
                        val user = mAuth.currentUser
                        updateUI(user)
            }else{
                Log.w("LOGIN", "Sign in Error", task.exception)
                Toast.makeText(this, "Sign In Failure", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Hello ${user.email}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ShowData::class.java))
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(
                    ApiException::class.java
                )
                firebaseAuthWithGoogle(account!!)
            }catch (e: ApiException){
                Log.w("LOGIN", "Login failed", e)
            }
        }
    }

   override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
}
