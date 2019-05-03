package com.example.reezkyillma.kotlinsepuluh

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import android.widget.ListView
import android.widget.Toast
import com.example.reezkyillma.kotlinsepuluh.AdapterC.Adapter
import com.example.reezkyillma.kotlinsepuluh.Add_Data.Add_Data_Act
import com.example.reezkyillma.kotlinsepuluh.Add_Data.Users
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.show_data.*

class ShowData : AppCompatActivity() {
    lateinit var refDb : DatabaseReference
    lateinit var list : MutableList<Users>
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_data)

        val Fab: View = findViewById(R.id.addFab)

      Fab.setOnClickListener {
            Toast.makeText(this, "Please Add Your Data", Toast.LENGTH_SHORT).show()

          startActivity(Intent(this, Add_Data_Act::class.java))
        }

        refDb = FirebaseDatabase.getInstance().getReference("USERS")
        list = mutableListOf()
        listView = findViewById(R.id.listView)

        refDb.addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0!!.exists()){
                        list.clear()
                        for (h in p0.children){
                            val user = h.getValue(
                                Users::class.java
                            )
                            list.add(user!!)
                        }

                        val adapter = Adapter(this@ShowData, R.layout.show_user, list)
                        listView.adapter = adapter
                    }
                }
            }
        )
    }
}