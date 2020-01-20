package mfuryw.snapchatclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null // ? pozwala na nullable
    var passwordEditText: EditText? = null
    private lateinit var auth: FirebaseAuth // deklarujemy instancję FirebaseAuth (z dokumentacji firebase ta linijka)

    fun signupLogin(view: View) {
        // sprawdzamy czy możemy zalogować się
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                logIn()
                Toast.makeText(this, "Login success.", Toast.LENGTH_SHORT).show()
            } else {
                // zakładamy konto
                auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // dodanie usera do bazy i zalogowanie się
                        logIn()
                        Toast.makeText(this, "User created.", Toast.LENGTH_SHORT).show() // zakładanie usera
                    } else {
                        Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    fun logIn() {
        val intent = Intent(this, SnapsActivity::class.java) // nie wiem czemu .java
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance() // inicjalizujemy instancję Firebase (z dokumentacji firebase ta linijka)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        if (auth.currentUser != null) {
            logIn()
        }
    }
}
