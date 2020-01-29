package mfuryw.snapchatclonekotlin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ViewSnapsActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null

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
            println("PO KLIKNIECIU W USERA: "+intent.getStringExtra("imageURL"))
            myImage = task.execute(intent.getStringExtra("imageURL")).get()!!
            snapImageView?.setImageBitmap(myImage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            println("WYWALILO BLAD PO KLIKNIECIU W USERA!!!")
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
}
