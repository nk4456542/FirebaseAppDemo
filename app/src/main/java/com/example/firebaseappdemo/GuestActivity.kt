package com.example.firebaseappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.net.URL

class GuestActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    lateinit var cv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
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
                    .inflate(R.layout.list_guest, group, false)
                return ProductHolder(view)
            }



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
                    Glide.with(this@GuestActivity).load(model.url).placeholder(R.drawable.prod).error(R.drawable.prod).override(600,600).into(imageView);
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

}