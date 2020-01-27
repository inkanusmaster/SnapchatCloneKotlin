package mfuryw.snapchatclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_snaps.*

class ChooseUserActivity : AppCompatActivity() {

    var chooseUserListView: ListView? = null
    var emails: ArrayList<String> = ArrayList() // emaile czyli usery
    var keys: ArrayList<String> = ArrayList() // lista będzie zawierała uuidy userów

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)
        chooseUserListView = findViewById(R.id.chooseUserListView)

        // będziemy importować userów czyli emaile z bazy do listview
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        chooseUserListView?.adapter = adapter

        // robimy coś z users. różne metody się implementuje z automatu
        FirebaseDatabase.getInstance().reference.child("users").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) { // będziemy dodawać emaile do array listy
                val email = p0.child("email").value as String // p0 typu datasnapshot używamy to wyciągnięcia value z emaila. jakoś tak
                if (email != FirebaseAuth.getInstance().currentUser?.email.toString()) { // nie chcemy na listę dodawać obecnie zalogowanego usera bo wysyłanie snapów do siebie ssie.
                    emails.add(email) // dodajemy email
                    keys.add(p0.key!!) // dodajemy uuid
                    adapter.notifyDataSetChanged() // update adapter
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })

        // robimy listener dla listview coby ogarnąć co się dzieje jak się kogoś zaznaczy
        // wysyłając komuś coś, robimy mu w bazie dodatkowe childy np snaps, który zawiera from z value od kogo
        // każdy snap powinien zawierać unikatową nazwę uuid. Snap poza polem from  i nazwą snapa powinien mieć pole url z urlem snapa i pole message z wiadomością
        // do tego robimy mapę String key i String value
        // wartości które wpisujemy zostały przesłane z intenta createsnapactivity, dlatego pobieramy je przez intent.getStringExtra
        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapMap: Map<String, String> = mapOf(
                "from" to FirebaseAuth.getInstance().currentUser!!.email!!.toString(), "imageName" to intent.getStringExtra("imageName"), "imageURL" to intent.getStringExtra("imageURL"), "message" to intent.getStringExtra("message")
            ) // deklarujemy mapkę

            // dodajemy do bazy do kogo wysyłamy snapa przy pomocy uuid. keys[i] wskaze nam usera na którego klikniemy.
            // dodatkowo do grupy snaps dodajemy snap o losowym uuid (ten snam będzie zawierał ww. 4 pola)
            // pod snaps wrzuamy cała nasza mapę przy pomocy .push().setValue(snapMap). push zrobi to nam też losowy uuid dla snapaa setvalue(snapMap) uzupełni polami
            FirebaseDatabase.getInstance().reference.child("users").child(keys[i]).child("snaps").push().setValue(snapMap)

            // po wysłaniu wracamy do SnapsActivity
            val intent = Intent(this, SnapsActivity::class.java)

            // zamiast uruchamiać od nowa ten intent, zamyka inne aktywności, które są nad nią i ten intent teraz będzie na topie
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
