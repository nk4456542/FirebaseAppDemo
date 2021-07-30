package com.example.firebaseappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth= FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
    }
    fun register(v: View?)
    {
        var username=(findViewById<View>(R.id.rUserName) as EditText).text.toString()
        var password=(findViewById<View>(R.id.rPassword) as EditText).text.toString()
        var email=(findViewById<View>(R.id.rEmail) as EditText).text.toString()
        var pno=(findViewById<View>(R.id.rPhone) as EditText).text.toString()
        var address=(findViewById<View>(R.id.rAddress) as EditText).text.toString()
        val docRef=mFirebaseDatabaseInstances?.collection("users")?.document(username!!)
        /*  if(docRef!=null)
          {
              Toast.makeText(this,"User Already exists",Toast.LENGTH_LONG).show()
              return
          }*/
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            val user=documentSnapshot.toObject(User::class.java) as User?
            if(user!=null)
            {
                Toast.makeText(this,"User Already exists",Toast.LENGTH_LONG).show()
            }
            else {
                var u = User(username, password, email, pno, address)
                mFirebaseDatabaseInstances?.collection("users")?.document(username!!)?.set(u)
                Toast.makeText(this, "User added successfully", Toast.LENGTH_LONG).show()
            }
        }


    }
}
class User
{
    var username=""
    var password=""
    var email=""
    var pno=""
    var address=""

    constructor(username:String,password:String,email:String,pno:String,address:String)
    {
        this.username=username
        this.password=password
        this.email=email
        this.pno=pno
        this.address=address

    }
    constructor()
}
