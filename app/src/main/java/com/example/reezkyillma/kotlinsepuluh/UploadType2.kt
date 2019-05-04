package com.example.reezkyillma.kotlinsepuluh

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.upload_photo.*
import java.io.IOException
import java.util.*
import java.util.jar.Manifest

class UploadType2 : AppCompatActivity() {
    lateinit var btn_Choose : Button
    lateinit var btn_Upload : Button
    lateinit var imgView : ImageView
    val PICK_IMAGE_REQUEST = 71
    val PERMISSION_REQUEST_CODE = 1001
    var value = 0.0
    lateinit var filepath : Uri
    lateinit var storage : FirebaseStorage
    lateinit var storageReference: StorageReference


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_type_2)

        btn_Choose = findViewById(R.id.btn_Choose)
        btn_Upload = findViewById(R.id.btn_Upload)

        imgView = findViewById(R.id.imgView)
        storage = FirebaseStorage.getInstance()

        storageReference = storage.reference


        btn_Choose.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this@UploadType2, android.Manifest
                                .permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE)
                    }else{
                        chooseImage()
                    }
                }
                else -> chooseImage()
            }
        }

        btn_Upload.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        val progress = ProgressDialog(this)
            .apply {
                setTitle("Uploading Picture....")
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                show()
            }
        var ref : StorageReference = storageReference.child("images/"
                +UUID.randomUUID().toString())
        ref.putFile(filepath)
            .addOnSuccessListener {
                taskSnapshot -> progress.dismiss()
                Toast.makeText(this@UploadType2, "Uploaded", Toast.LENGTH_SHORT).show();
            }.addOnFailureListener{
                e -> progress.dismiss()
                Toast.makeText(this@UploadType2, "Failed" + e.message, Toast.LENGTH_SHORT).show();
            }.addOnProgressListener {
                taskSnapshot ->
                value = (100.0 * taskSnapshot
                    .getBytesTransferred()/ taskSnapshot
                    .getTotalByteCount())

                progress.setMessage("Uploaded.."
                        + value.toInt() + "%")
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0]
                    == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this@UploadType2,
                        "Oops! Permission Denied!!"
                        ,Toast.LENGTH_SHORT).show()
                else
                    chooseImage()
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent,
                "Select Picture"),
            PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && data.data !=null) {
            filepath = data.data

            try {
                var bitmap : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
                imgView.setImageBitmap(bitmap)
            }catch (e :IOException){
                e.printStackTrace()
            }
        }

    }
}
