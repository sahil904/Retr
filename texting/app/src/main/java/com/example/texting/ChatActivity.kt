package com.example.texting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import com.google.firebase.database.DataSnapshot



class ChatActivity : AppCompatActivity() {

    lateinit var firebaseDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        firebaseDatabase = FirebaseDatabase.getInstance().reference

        val user = User()
        user.name = "xyz"
        user.emaail = "xyz@gmail.com"
user.emaail="lkl"
        firebaseDatabase.child("user").setValue(user)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)


                val demo = user!!.emaail
                Log.d("dfdf",demo)
                //email fetched from database
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
    }
}
