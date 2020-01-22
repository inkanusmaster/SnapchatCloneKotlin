package mfuryw.snapchatclonekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
//                        val database = FirebaseDatabase.getInstance() // te zakomentowane wpisy z dokumentacji
//                        val myRef = database.getReference("message")
//                        myRef.setValue("Hello, World!")

//                        FirebaseDatabase.getInstance().getReference() to nasz główny element w bazie od którego idziemy chyba dalej (ten z nullem).
//                        Dodajemy users do którego wrzucamy uid usera a dalej email, któremu już ustawiamy value konkretne. Wykrzykniki bo nie może być null.
//                        UWAGA. Dobrze żeby w bazie coś było już recznie utworzone bo Firebase nie chce mi jakoś pustej bazy zapisać.

                        FirebaseDatabase.getInstance().reference.child("users").child(task.result!!.user!!.uid).child("email").setValue(emailEditText?.text.toString())
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
