package mfuryw.snapchatclonekotlin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ViewSnapsActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    private val auth = FirebaseAuth.getInstance() //potrzebujemy aby usunąć snapa po wciśnięciu back temu konkretnemu użytkownikowi (zalogowanemu)

    // ta aktywność otwiera się po odebraniu snapa
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snaps)

        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message") // odbieramy wiadomość

        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()!!
            snapImageView?.setImageBitmap(myImage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    // Pobieranie obrazów jest nieco bardziej skomplikowane. Wykorzystamy tutaj imagedownloader z innego projektu. Przerobiony automatycznie na kotlin
    class ImageDownloader : AsyncTask<String?, Void?, Bitmap?>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {
            return try {
                val url = URL(urls[0])
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()
                val `in`: InputStream = urlConnection.inputStream
                BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // usuwamy snapa po kliknięciu back. Całkowicie. I z listy, i z bazy, i ze storagu
    override fun onBackPressed() {
        super.onBackPressed()

        // usuwamy z bazy. Idziemy po drzewku do snapa. czyli users -> uid zalogowanego usera -> snaps -> klucz konkretnego snapa przesłany z intenta SnapsActivity. Na koniec removeValue.
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()

        // usuwamy ze storagu. Folder images. DLATEGO PRZESYLAMY TEŻ IMAGE NAME Z INTENTU SNAPSACTIVITY!!!
        FirebaseStorage.getInstance().reference.child("images").child(intent.getStringExtra("imageName")).delete()

        // Z LISTY BĘDZIEMY USUWAC W SNAPSACTIVITY W METODZIE ONCHILDREMOVED!
    }
}
