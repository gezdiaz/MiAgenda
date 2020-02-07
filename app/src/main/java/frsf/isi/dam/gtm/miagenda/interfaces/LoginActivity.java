package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String SignOut = "hasToSignOut";
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN_GOOGLE = 1;
    private TextInputEditText correoEdit;
    private TextInputEditText claveEdit;
    private MaterialButton iniciarSesionBtn;
    private MaterialButton iniciarConGoogleBtn;
    private MaterialButton crearCuentaBtn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar iniciarSesionProgress, iniciarConGoogleProgress, crearCuentaProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correoEdit = findViewById(R.id.login_usuario_edit);
        claveEdit = findViewById(R.id.login_password_edit);
        iniciarSesionBtn = findViewById(R.id.iniciar_sesion_btn);
        iniciarConGoogleBtn = findViewById(R.id.iniciar_sesion_google_btn);
        crearCuentaBtn = findViewById(R.id.crear_cuenta_btn);
        iniciarSesionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correoEdit.clearFocus();
                claveEdit.clearFocus();
                String email = correoEdit.getText().toString();
                String clave = claveEdit.getText().toString();
                if (email.isEmpty() || clave.isEmpty()) {
                    Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.datos_login_no_validos, Snackbar.LENGTH_LONG);
                    s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                    s.show();
                } else {
                    Intent i1 = new Intent(getApplicationContext(), PrincipalActivity.class);
                    //iniciar sesión con mail y contraseña
                    iniciarSesion(email, clave);
                }
            }
        });
        iniciarConGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //iniciar sesión con google
                iniciarSesionConGoogle();
            }
        });
        crearCuentaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correoEdit.clearFocus();
                claveEdit.clearFocus();
                if (correoEdit.getText().toString().isEmpty() || claveEdit.getText().toString().isEmpty()) {
                    Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.datos_login_no_validos, Snackbar.LENGTH_LONG);
                    s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                    s.show();
                } else {
                    if (claveEdit.getText().length() < 6) {
                        Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.error_clave_6_caracteres, Snackbar.LENGTH_LONG);
                        s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                        s.show();
                    } else {
                        crearCuenta(correoEdit.getText().toString(), claveEdit.getText().toString());
                    }
                }
            }
        });

        iniciarSesionProgress = findViewById(R.id.iniciar_sesion_progress);
        iniciarConGoogleProgress = findViewById(R.id.iniciar_sesion_google_progress);
        crearCuentaProgress = findViewById(R.id.crear_cuenta_progress);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Verificar si ya hay una cuenta iniciada.
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            Log.d(TAG, "Cuenta ya inicada: " + user.getEmail());
//            startActivity(new Intent(this, PrincipalActivity.class));
//            finish();
//        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent().getBooleanExtra(SignOut, false)) {
            mAuth.signOut();
            mGoogleSignInClient.signOut();
            DatosFirestore.resetInstance();
        }

    }

    private void iniciarSesionConGoogle() {
        iniciarConGoogleProgress.setVisibility(View.VISIBLE);
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWhitGoogle(account);
            } catch (ApiException e) {
                Log.d(TAG, "Google sign in failed", e);
                Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.error_inicio_sesion_google, Snackbar.LENGTH_LONG);
                s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                s.show();iniciarConGoogleProgress.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void firebaseAuthWhitGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithCredential:success");
                    Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
                    i.putExtra(PrincipalActivity.LOGIN, true);
                    i.putExtra(PrincipalActivity.LOGINGOOGLE, true);
                    iniciarConGoogleProgress.setVisibility(View.INVISIBLE);
                    startActivity(i);
                    finish();
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.error_inicio_sesion_google, Snackbar.LENGTH_LONG);
                    s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                    s.show();
                    iniciarConGoogleProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void iniciarSesion(String email, String clave) {
        iniciarSesionProgress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                            Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.exito_inicio_sesion, Snackbar.LENGTH_LONG);
//                            s.setBackgroundTint(getResources().getColor(R.color.colorAceptar));
//                            s.show();
                            Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
                            i.putExtra(PrincipalActivity.LOGIN, true);
                            iniciarSesionProgress.setVisibility(View.INVISIBLE);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.error_inicio_sesion, Snackbar.LENGTH_LONG);
                            s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                            s.show();
                            iniciarSesionProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void crearCuenta(String email, String clave) {
        crearCuentaProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
                            i.putExtra(PrincipalActivity.LOGIN, true);
                            i.putExtra(PrincipalActivity.NEWUSER, true);
                            crearCuentaProgress.setVisibility(View.INVISIBLE);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar s = Snackbar.make(findViewById(R.id.login_activity_layout), R.string.error_creacion_cuenta, Snackbar.LENGTH_LONG);
                            s.setBackgroundTint(getResources().getColor(R.color.colorCancelar));
                            s.show();
                            crearCuentaProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });

    }

}
