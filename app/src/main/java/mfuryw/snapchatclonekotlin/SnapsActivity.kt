package mfuryw.snapchatclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class SnapsActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

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
    }
}
