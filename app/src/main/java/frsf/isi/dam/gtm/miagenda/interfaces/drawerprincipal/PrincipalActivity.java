package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.LogRecord;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.broadcastreceiver.NotificacionDiariaReceiver;
import frsf.isi.dam.gtm.miagenda.broadcastreceiver.NotificacionDiariaService;
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
    public static AlarmManager alarmManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextSecondary));
        setSupportActionBar(toolbar);

        //verificar si hay una sesión iniciada.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            //No hay cuenta iniciada
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {

            //Registrar Notificacion diaria Receiver

            NotificacionDiariaReceiver br = new NotificacionDiariaReceiver();
            IntentFilter filtro = new IntentFilter();
            filtro.addAction(NotificacionDiariaReceiver.ALARMNOTIFICATION);
//            IntentFilter filtro2 = new IntentFilter();
//            filtro2.addAction(NotificacionDiariaReceiver.SCREENONNOTIFICATION);
            IntentFilter filtro3 = new IntentFilter();
            filtro3.addAction(NotificacionDiariaReceiver.BOOTNOTIFICATION);
            getApplication().getApplicationContext().registerReceiver(br, filtro);
//            getApplication().getApplicationContext().registerReceiver(br, filtro2);
            getApplication().getApplicationContext().registerReceiver(br,filtro3);

            //Inicializar alarma
            iniciarNotificacionesDiarias(getApplicationContext());

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

                Intent intent = new Intent(this, NotificacionDiariaReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(this, 1, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(sender);

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

    public static void iniciarNotificacionesDiarias(Context context){
        //Crear notificacion diaria
        Intent broadcastIntent = new Intent(context, NotificacionDiariaReceiver.class);
        broadcastIntent.setAction(NotificacionDiariaReceiver.ALARMNOTIFICATION);
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(context,1,broadcastIntent,0);

        Calendar ahora = Calendar.getInstance();
        ahora.setTimeZone(TimeZone.getDefault());
        //TODO usar las variables de los turnos
        Calendar horaAlarmaNotificacion = Calendar.getInstance();
        horaAlarmaNotificacion.setTimeZone(TimeZone.getDefault());
        horaAlarmaNotificacion.set(Calendar.HOUR_OF_DAY,6);
        horaAlarmaNotificacion.set(Calendar.MINUTE,30);
        horaAlarmaNotificacion.set(Calendar.SECOND,0);


        if(horaAlarmaNotificacion.getTimeInMillis()< ahora.getTimeInMillis()){
            horaAlarmaNotificacion.set(Calendar.DATE, horaAlarmaNotificacion.get(Calendar.DATE)+1);
        }

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, horaAlarmaNotificacion.getTimeInMillis(),AlarmManager.INTERVAL_DAY,broadcastPendingIntent);
    }

}
