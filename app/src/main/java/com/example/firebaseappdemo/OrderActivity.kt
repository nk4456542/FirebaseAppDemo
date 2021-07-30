package com.example.firebaseappdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    lateinit var cv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        mAuth=FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        cv=findViewById<View>(R.id.rv) as RecyclerView
        loadData()
    }
    fun loadData()
    {
        val query: Query = FirebaseFirestore.getInstance()
            .collection("products")
        val options: FirestoreRecyclerOptions<Product?> = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()
        val adapter: FirestoreRecyclerAdapter<*, *> = object : FirestoreRecyclerAdapter<Product?, RecyclerView.ViewHolder?>(options) {

            override fun onCreateViewHolder(group: ViewGroup, i: Int): ProductHolder {
                // Using a custom layout called R.layout.message for each item, we create a new instance of the viewholder
                val view: View = LayoutInflater.from(group.context)
                    .inflate(R.layout.list_order, group, false)
                return ProductHolder(view)
            }



            @RequiresApi(Build.VERSION_CODES.O)
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Product) {

                (holder.itemView.findViewById<View>(R.id.tpid) as TextView).setText("Product ID: "+model.pid)
                (holder.itemView.findViewById<View>(R.id.tpname) as TextView).setText("Product Name:"+ model.pname)
                (holder.itemView.findViewById<View>(R.id.tpcost) as TextView).setText("Product Cost:"+model.cost.toString())
                (holder.itemView.findViewById<View>(R.id.tptyp) as TextView).setText("Product Category:"+model.typ)
                if(model.url!="") {
                    /*Thread(
                            Runnable {

                                    var url = URL(model.url)
                                runOnUiThread {
                                    var bm = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                                    (holder.itemView.findViewById<View>(R.id.imageView) as ImageView).setImageBitmap(bm)
                                }
                            }

                    ).start();*/
                    var url = URL(model.url)
                    var imageView=(holder.itemView.findViewById<View>(R.id.imageView) as ImageView)
                    Glide.with(this@OrderActivity).load(model.url).placeholder(R.drawable.prod).error(R.drawable.prod).override(600,600).into(imageView);
                }
                (holder.itemView.findViewById<View>(R.id.btnOrd)).setOnClickListener {
                    var pid=model.pid
                    var i=intent
                    var username=i.getStringExtra("uname")
                    val sdf = SimpleDateFormat("dd-MM-yyyyhh:mm:ss")
                    val dt = sdf.format(Date())
                    var o=Order(username!!,pid,dt)
                    val docRef=mFirebaseDatabaseInstances?.collection("orders")?.document(dt!!)
                    docRef?.get()?.addOnSuccessListener { documentSnapshot ->

                        mFirebaseDatabaseInstances?.collection("orders")?.document(dt!!)?.set(o)
                        Toast.makeText(
                            applicationContext,
                            "order placed successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        val resultIntent = Intent(this@OrderActivity, ResultActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            this@OrderActivity,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val notificationID = 101
                        val channelID = "com.example.pfb"
                        val mChannel = NotificationChannel(channelID, "c", NotificationManager.IMPORTANCE_DEFAULT)
                        mChannel.description = "d"
                        var notification = Notification.Builder(this@OrderActivity,"com.example.pfb")
                            .setContentText("Order Notification").setSmallIcon(R.drawable.ic_launcher_background).setChannelId(channelID)
                        notification.setContentIntent(pendingIntent)
                        val myNotification: Notification = notification.build()
                        var mNotifyManager = this@OrderActivity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        mNotifyManager.createNotificationChannel(mChannel)
                        mNotifyManager.notify(notificationID,myNotification)
                    }
                }

            }


        }
//Final step, where "mRecyclerView" is defined in your xml layout as
//the recyclerview
//Final step, where "mRecyclerView" is defined in your xml layout as
//the recyclerview

        cv.layoutManager = LinearLayoutManager(this)
        cv.adapter=adapter
        adapter.startListening()
    }

    class Order
    {
        var uname=""
        var pid=""
        var dt=""

        constructor(uname:String, pid:String,dt:String)
        {
            this.pid=pid
            this.uname=uname
            this.dt=dt

        }
        constructor()
    }
}
