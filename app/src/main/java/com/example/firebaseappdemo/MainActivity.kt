package com.example.firebaseappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    lateinit var cv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Get Firebase Instances
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth=FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        cv=findViewById<View>(R.id.rv) as RecyclerView
        loadData()

    }
    fun add(v: View?)
    {
        var pidA=findViewById<View>(R.id.pid) as EditText
        var pnameA=findViewById<View>(R.id.pname) as EditText
        var pcostA=findViewById<View>(R.id.cost) as EditText
        var ptypA=findViewById<View>(R.id.ptyp) as Spinner
        var pid=pidA.text.toString()
        var pname=pnameA.text.toString()
        var pcost=pcostA.text.toString().toDouble()
        var ptyp=ptypA.selectedItem.toString()
        val docRef=mFirebaseDatabaseInstances?.collection("products")?.document(pid!!)
        var url=""
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            val prod:Product?=documentSnapshot.toObject(Product::class.java)
            if(prod!=null)
                url=prod?.url

            //Toast.makeText(this,url,Toast.LENGTH_LONG).show()
            var p=Product(pid,pname,pcost,url,ptyp)
            mFirebaseDatabaseInstances?.collection("products")?.document(pid!!)?.set(p)
            Toast.makeText(this,"Product added successfully",Toast.LENGTH_LONG).show()
        }
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
                    .inflate(R.layout.list_data, group, false)
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
                    Glide.with(this@MainActivity).load(model.url).placeholder(R.drawable.prod).error(R.drawable.prod).override(600,600).into(imageView);
                }
                (holder.itemView.findViewById<View>(R.id.delBtn) as ImageButton).setOnClickListener {
                    mFirebaseDatabaseInstances!!.collection("products").document(model.pid!!).delete()
                        .addOnSuccessListener { Toast.makeText(applicationContext, "Successfully deleted ", Toast.LENGTH_SHORT).show() }
                        .addOnFailureListener { Toast.makeText(applicationContext, "Unable to delete", Toast.LENGTH_SHORT).show() }
                }


                (holder.itemView.findViewById<View>(R.id.uploadBtn) as ImageButton).setOnClickListener {
                    var i= Intent(applicationContext,UploadActivity::class.java)
                    i.putExtra("pid",model.pid)
                    i.putExtra("pname",model.pname)
                    i.putExtra("cost",model.cost.toString())
                    i.putExtra("typ",model.typ)
                    startActivity(i)
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
class Product
{
    var pid=""
    var pname=""
    var cost=0.0
    var typ=""
    var url=""

    constructor(pid:String,pname:String,cost:Double,url:String,typ:String)
    {
        this.pid=pid
        this.pname=pname
        this.cost=cost
        this.url=url
        this.typ=typ

    }
    constructor()
}
class ProductHolder(itemView: View):RecyclerView.ViewHolder(itemView)  {
    val tpid: TextView
    val tpname: TextView
    val tpcost: TextView
    val tptyp:TextView
    init {
        tpid=itemView.findViewById(R.id.tpid)

        tpname=itemView.findViewById(R.id.tpname)
        tpcost=itemView.findViewById(R.id.tpcost)
        tptyp=itemView.findViewById(R.id.tptyp)
    }


}