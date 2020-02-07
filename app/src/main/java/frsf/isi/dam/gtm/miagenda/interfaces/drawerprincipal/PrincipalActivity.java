package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.logging.LogRecord;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.entidades.Turno;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;

public class PrincipalActivity extends AppCompatActivity {

    public static final String LOGIN = "login";
    public static final String LOGINGOOGLE = "loginGoogle";
    public static final String NEWUSER = "newUser";
    private static final String TAG = "PrincipalActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private TextView userNameTxt, userEmailTxt;
    private ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //verificar si hay una sesión iniciada.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            //No hay cuenta iniciada
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //Hay una cuenta iniciada
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);

            //Seteo el perfil del usuario logueado en el header del drawer
            View headerView = navigationView.getHeaderView(0);
            userNameTxt = headerView.findViewById(R.id.user_name_txt);
            userEmailTxt = headerView.findViewById(R.id.user_email_txt);
            userImageView = headerView.findViewById(R.id.user_image_view);

            //nombre se usuario
            String userName = user.getDisplayName();
            if (userName != null && !userName.isEmpty()) {
                userNameTxt.setText(userName);
            } else {
                //Si el usuario no tiene nombre (inicio con mail)
                userNameTxt.setText(R.string.usuario_default);
            }
            //Email siempre tiene
            userEmailTxt.setText(user.getEmail());
            //Foto de perfil
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                //Si tiene foto la cargo con Glide
                Glide.with(headerView)
                        .load(photoUrl)
                        .into(userImageView);
            } else {
                //Si no tiene foto pongo la default
                userImageView.setImageResource(R.drawable.perfil_default);
            }

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_mi_agenda, R.id.nav_mis_pacientes)
                    .setDrawerLayout(drawer)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        //Mostrar mensaje de inicio de sesión si recién ingresa.
        Intent i = getIntent();
        if (i.getBooleanExtra(LOGIN, false)) {
            Snackbar s;
            if(i.getBooleanExtra(NEWUSER, false)){
                s = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.exito_creacion_cuenta, Snackbar.LENGTH_LONG);
            }else {
                if(i.getBooleanExtra(LOGINGOOGLE, false)){
                    s = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.exito_inicio_sesion_google, Snackbar.LENGTH_LONG);
                }else {
                    s = Snackbar.make(findViewById(R.id.coordinator_layout), R.string.exito_inicio_sesion, Snackbar.LENGTH_LONG);
                }
            }
            s.setBackgroundTint(getResources().getColor(R.color.colorPrimary));
            s.show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opcion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion_option_item: {
                Intent i1 = new Intent(this, LoginActivity.class);
                //Le digo a LogInActivity que cierre sesión
                i1.putExtra(LoginActivity.SignOut, true);
                startActivity(i1);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            //No hay cuenta iniciada
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
