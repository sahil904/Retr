package com.example.texting

import android.arch.lifecycle.Transformations.map
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.HashMap

class Register : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var refrence : DatabaseReference
    lateinit var  username:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()

        setContentView(R.layout.activity_register)
        register.setOnClickListener(){
            register(name_signup.text.toString(),email_signup.text.toString(),password_signup.text.toString())
        }
    }
    fun register(user:String, email:String,password:String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            var user: FirebaseUser
            user= auth.currentUser!!;
            var userid:String=user.uid
            refrence=FirebaseDatabase.getInstance().getReference("user").child(userid)
            val map = HashMap<String, String>()
map.put("id",userid)
            map.put("username",email_signup.text.toString())
            refrence.setValue(map).addOnCompleteListener(){
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
            }

        }


    }
}
