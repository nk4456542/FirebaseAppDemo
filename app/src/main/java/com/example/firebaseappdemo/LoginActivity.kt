package com.example.firebaseappdemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth=FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val sharedUser = sharedPreferences.getString("username","defaultname")
        if(sharedUser.equals("defaultname"))
        {

        }
        else
        {
            var i=Intent(applicationContext,OrderActivity::class.java)
            i.putExtra("uname",sharedUser)
            startActivity(i)
        }
    }
    fun login(v: View?)
    {
        var u=findViewById<View>(R.id.userName) as EditText
        var p=findViewById<View>(R.id.passWord) as EditText
        if(u.text.toString()=="admin" && p.text.toString()=="jnnce")
        {
            var i= Intent(applicationContext,MainActivity::class.java)
            startActivity(i)
        }
        else
        {
            var flag=0
            try {
                mFirebaseDatabaseInstances?.collection("users")!!.document(u.text.toString())
                    .get().addOnSuccessListener { result ->

                        val uu = result.toObject(User::class.java) as User?
                        if(uu==null)
                        {
                            Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show()
                        }
                        else {
                            if (uu.password.equals(p.text.toString())) {
                                val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
                                    Context.MODE_PRIVATE)
                                val editor:SharedPreferences.Editor =  sharedPreferences.edit()

                                editor.putString("username",u.text.toString())
                                editor.apply()
                                editor.commit()
                                // Toast.makeText(this, "user exists", Toast.LENGTH_LONG).show()
                                var i=Intent(applicationContext,OrderActivity::class.java)
                                i.putExtra("uname",u.text.toString())
                                startActivity(i)

                            } else {
                                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }

                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show()
                    }
            }
            catch(e:Exception)
            {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show()
            }


        }
    }
    fun guest(v: View?)
    {
        var i= Intent(applicationContext,GuestActivity::class.java)
        startActivity(i)
    }
    fun register(v:View?)
    {
        var i= Intent(applicationContext,RegisterActivity::class.java)
        startActivity(i)
    }
}