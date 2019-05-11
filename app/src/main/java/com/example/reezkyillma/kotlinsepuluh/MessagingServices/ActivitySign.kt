package com.example.reezkyillma.kotlinsepuluh.MessagingServices

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.example.reezkyillma.kotlinsepuluh.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider

class ActivitySign : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private val TAG = "ActivitySign"
    private val RC_SIGN_IN = 9001
    private var mSignInButton : SignInButton? = null
    private  var mGoogleApiClient : GoogleApiClient? = null
    //Firebase instance variables
    private var mFirebaseAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mSignInButton = findViewById<View>(R.id.sign_in_button) as SignInButton
        mSignInButton!!.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this/* Fragment Actifiry*/, this/*OnConnectionFailedListener*/)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        //initialize firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance()
    }
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + "$connectionResult")
        Toast.makeText(this, "Google Play" + "Services error",
            Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.sign_in_button -> signIn()
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess){
                //Google Sign In was Successfull
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            }else {
                //Google sign in failed
                Log.e(TAG, "Google Sign In Failed")

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d (TAG, "firebaseAuthWithGoogle : " + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        mFirebaseAuth!!.signInWithCredential(credential).addOnCompleteListener(this){
            task ->
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
            if (!task.isSuccessful){
                Log.w(TAG, "signInWithCredential", task.exception)
                Toast.makeText(
                    this@ActivitySign,
                    "Authentication Failed.",Toast.LENGTH_SHORT
                ).show()
            } else {
                startActivity(Intent(this@ActivitySign, MainMessage::class.java))
                finish()
            }
        }

    }

}