package mfuryw.snapchatclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class SnapsActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var snapsListView: ListView? = null // lista ze snapami
    var emails: ArrayList<String> = ArrayList() // to są osoby które wysłały nam snapa

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // tworzymy menu opcje trzy kropki
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // co będzie jak klikniemy na item z menu
        if (item?.itemId == R.id.addsnap) {
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)

        } else if (item?.itemId == R.id.logout) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() { // jak user kliknie strzałkę back
        super.onBackPressed()
        auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        snapsListView = findViewById(R.id.snapsListView) // będziemy zapisywać snapy na tę listę. Będziemy mieć listę emaili od ludzi którzy przesłali nam snapa
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        // wyciągamy info z users -> JA -> snaps. Czyli snapy, które my otrzymaliśmy. Zaimplementowane różne metody
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) { // wyciągamy info i wrzucamy do arraylist ze snapami
                emails.add(p0.child("from").value as String)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }
}
