package com.example.texting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.HashMap

class Login : AppCompatActivity() {
lateinit var auth:FirebaseAuth
    lateinit var refrence :DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth= FirebaseAuth.getInstance()
        login.setOnClickListener(){

            auth.signInWithEmailAndPassword(email_login.text.toString(),password_login.text.toString()).addOnCompleteListener(){
//                    var user: FirebaseUser
//                    user= auth.currentUser!!;
//                    var userid:String=user.uid
//                    refrence= FirebaseDatabase.getInstance().getReference("user").child(userid)
//                    val map = HashMap<String, String>()
//                    map.put("id",userid)
//                    map.put("username",email_signup.text.toString())
//                    refrence.setValue(map).addOnCompleteListener(){
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
            }

        }

    }

}
