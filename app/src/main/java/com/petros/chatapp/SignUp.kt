package com.petros.chatapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.nio.channels.AsynchronousFileChannel.open

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnUploadPhoto: Button
    private lateinit var btnSignUp: Button

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var photoBase64: String

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        edtName = findViewById(R.id.edt_name)
        edtUsername = findViewById(R.id.edt_username)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnUploadPhoto = findViewById(R.id.btn_uploadphoto)
        btnSignUp = findViewById(R.id.btn_signup)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(result.data)
                }
            }

        btnUploadPhoto.setOnClickListener {
            //intent to open camera app
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }

        btnSignUp.setOnClickListener {

            //TODO add verification. if the edittexts are not empty , parola min6 caractere
            var name = edtName.text.toString()
            var username = edtUsername.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(email, password, name, username)
        }
    }

    private fun signUp(email: String, password: String, name: String, username: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    addUserToDb(email, password, name, username, mAuth.currentUser?.uid.toString())
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SignUp, "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun addUserToDb(email: String, password: String, name: String, username: String, uid: String){
        mDbRef.child("users").child(uid).setValue(User(username, name, email, uid, photoBase64))
    }

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        photoBase64 = encodeImage(bitmap).toString()
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}