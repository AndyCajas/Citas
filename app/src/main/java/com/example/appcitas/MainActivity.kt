package com.example.appcitas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcitas.databinding.ActivityMainBinding
import com.example.appcitas.databinding.ActivityRegistrarUsuariosBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    var email = ""
    var password = ""

    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference

    // Google Sign-In client
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvNoCuenta.setOnClickListener {
            startActivity(Intent(this, RegistrarUsuarios::class.java))
        }
        binding.btnIniciarSesion.setOnClickListener {
            loginUser()
        }

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
        usuarioactual()

    }

    private fun usuarioactual() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, GestionarCitas::class.java))
            finish()
        }
    }

    private val RC_SIGN_IN = 9001 // Código para identificar la actividad

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Google sign-in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User()
                    val currentUser = auth.currentUser
                    currentUser?.let {
                        user.id = it.uid
                        user.nombre = it.displayName ?: ""
                        user.email = it.email ?: ""
                        user.imagen = it.photoUrl?.toString() ?: ""
                    }

                    val userRef = databaseReference.child("users").child(user.id)
                    userRef.setValue(user).addOnSuccessListener {
                        Toast.makeText(this, "usuario guardado con exito", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, GestionarCitas::class.java))
                        finish()

                    }

                } else {
                    Log.w("GoogleSignIn", "Sign-in failed", task.exception)
                }
            }
    }


    fun loginUser() {
        if (getCredentials()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, GestionarCitas::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al inciar sesión", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Error complete los campos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getCredentials(): Boolean {
        email = binding.nombreUsuarioCuenta.text.toString().trim()
        password = binding.password.text.toString().trim()
        return if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            true
        } else {
            false
        }
    }

}