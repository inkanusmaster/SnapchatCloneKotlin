package mfuryw.snapchatclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_snaps.*

class SnapsActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var snapsListView: ListView? = null // lista ze snapami
    var emails: ArrayList<String> = ArrayList() // to są osoby które wysłały nam snapa
    var snaps: ArrayList<DataSnapshot> = ArrayList() // lista typu DataSnapshot zawiera info o message, o image name o image url... Potrzebne dane do odebrania snapa


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
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) { // wyciągamy info i wrzucamy do arraylist ze snapami
                emails.add(p0.child("from").value as String)
                snaps.add(p0) // dodajemy do listy snaps wszystko co w snaps! (bo to nasza ścieżka)
                adapter.notifyDataSetChanged()
            }

            // jeśli usunięto cokolwiek, child (czyli po wciśnięciu back, jak z bazy zostanie usunięty snap)? Trzeba usunąć z listy usera od którego go dostaliśmy
            // czyli usuwamy z arraylisty email, która zawierała usera który wysłał oraz z arraylisty snaps, któa zawierała wszystkie dane o snapie (typ DataSnapshot)
            override fun onChildRemoved(p0: DataSnapshot) {
                // przyda nam się indeks usuwanego snapa
                // kotlin sam zrobił withIndex! Widocznie zaczyna pętlę for od 0 i inkrementuja przy każdej pętli
                for ((index, snap: DataSnapshot) in snaps.withIndex()) { // loopujemy po wszystkich snapach w snaps (DataSnapshot - pełne info o snapie)
                    if (snap.key == p0?.key) { // loopujemy. Jeśli trafimy na dany key snapshota, który jest równy keyowi p0 (czyli usuniętego bo w metodzie onChildRemoved)...
                        snaps.removeAt(index) // usuwamy z arraylist - emails i snaps
                        emails.removeAt(index)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        })

        // pobieranie snapa. Pamiętamy że w liście snaps mamy wszystko odnośnie snapa.
        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val snapshot = snaps[i] // zmienna snapshot posiada całe info o snapie (bo całe p0 dodaliśmy) w którego kliknęliśmy (indeks i)
            val intent = Intent(this, ViewSnapsActivity::class.java) // otwieramy aktywność ze snapem, tj. ta co zawiera wiadomość i obraz
            intent.putExtra("imageName", snapshot.child("imageName").value as String) // wysyłamy obraz, który będzie wyświetlany w snapie
            intent.putExtra("imageURL", snapshot.child("imageURL").value as String) // jw. url.
            intent.putExtra("message", snapshot.child("message").value as String) // jw. wiadomość
            intent.putExtra("snapKey", snapshot.key) // odpowiedni klucz do snapshota (ten losowy ciąg znaków snapa) dzięki któremu będziemy mogli go usunąć po obejrzeniu
            startActivity(intent)
        }
    }
}
