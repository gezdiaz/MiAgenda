package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.broadcastreceiver.NotificacionDiariaReceiver;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda.MiAgendaFragment;

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

    private NavController navController;
    public boolean seleccionPacienteEnviada = false;
    private Calendar horaTurnoPendiente;
    private Bundle datosGuardados;
    private AlertDialog dialogoReservar;
    public Snackbar avisoSeleccion;
    private Snackbar avisoTurnoCancelado;
    public FloatingActionButton fabPrincipal;

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

            //Registrar NotificacionDiariaReceiver

            NotificacionDiariaReceiver br = new NotificacionDiariaReceiver();
            IntentFilter filtro = new IntentFilter();
            filtro.addAction(NotificacionDiariaReceiver.ALARMNOTIFICATION);
            IntentFilter filtro3 = new IntentFilter();
            filtro3.addAction(NotificacionDiariaReceiver.BOOTNOTIFICATION);
            getApplication().getApplicationContext().registerReceiver(br, filtro);
            getApplication().getApplicationContext().registerReceiver(br,filtro3);

            //Inicializar alarma
            iniciarNotificacionesDiarias(getApplicationContext());

            //Hay una cuenta iniciada
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            fabPrincipal = findViewById(R.id.fab_principal);

            CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
            avisoSeleccion = Snackbar.make(coordinatorLayout, R.string.seleccionando_paciente, Snackbar.LENGTH_INDEFINITE);
            avisoSeleccion.setActionTextColor(getResources().getColor(R.color.colorCancelar));
            avisoSeleccion.setAction(R.string.x, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(avisoSeleccion.isShown()){
                        avisoSeleccion.dismiss();
                    }
                }
            });

            avisoTurnoCancelado = Snackbar.make(coordinatorLayout, R.string.aviso_sin_paciente, Snackbar.LENGTH_LONG);
            avisoTurnoCancelado.setBackgroundTint(getResources().getColor(R.color.colorCancelar));


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

            navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                @Override
                public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                    Log.d(TAG, "Llama a onDestinationChanged");
                    Log.d(TAG, "args: " + arguments);
                    Log.d(TAG, "SeleccionPacienteEnviada es: "+seleccionPacienteEnviada);
                    switch (destination.getId()) {
                        case R.id.nav_mi_agenda:
                            Log.d(TAG, "Va a MiAgenda");
                            if (avisoSeleccion != null && avisoSeleccion.isShown()) {
                                avisoSeleccion.dismiss();
                            }
                            if (seleccionPacienteEnviada) {

                                if (arguments == null || !arguments.getBoolean("respuestaPaciente", false)) {
                                    //TODO Avisar que si cambia de pantalla se cancela la creación de turno
                                    avisoTurnoCancelado.show();
                                    Log.d(TAG, "Vuelve a MiAgenda sin seleccionar un paciente");
                                    seleccionPacienteEnviada = false;
                                }
                            } else {
                                if (arguments != null && arguments.getBoolean("respuestaPaciente", false)) {
                                    //está respondiendo con un paciente pero no le había pedido esa respuesta
                                    arguments.putBoolean("respuestaPaciente", false);
                                    Log.d(TAG, "Se recibió una respuestaPaciente no solicitada");
                                }
                            }
                            break;
                        case R.id.nav_mis_pacientes:
                            Log.d(TAG, "Va a MisPacientes");
                            if (avisoSeleccion != null) {
                                if (avisoSeleccion.isShown()) {
                                    avisoSeleccion.dismiss();
                                    seleccionPacienteEnviada = false;
                                    avisoTurnoCancelado.show();
                                } else {
                                    if(seleccionPacienteEnviada) {
                                        avisoSeleccion.show();
                                    }
                                }
                            }
                            break;
                        default:
                            Log.wtf(TAG, "No va a ningún lado");
                    }
                }
            });

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);


            //Mostrar mensaje de inicio de sesión si recién ingresa.
            Intent i = getIntent();
            if (i.getBooleanExtra(LOGIN, false)) {
                Snackbar s;
                if (i.getBooleanExtra(NEWUSER, false)) {
                    s = Snackbar.make(coordinatorLayout, R.string.exito_creacion_cuenta, Snackbar.LENGTH_LONG);
                } else {
                    if (i.getBooleanExtra(LOGINGOOGLE, false)) {
                        s = Snackbar.make(coordinatorLayout, R.string.exito_inicio_sesion_google, Snackbar.LENGTH_LONG);
                    } else {
                        s = Snackbar.make(coordinatorLayout, R.string.exito_inicio_sesion, Snackbar.LENGTH_LONG);
                    }
                }
                s.setBackgroundTint(getResources().getColor(R.color.colorPrimary));
                s.show();
            }

        }

    }

    public void seleccionarPaciente(Calendar horaTurno, Bundle datosAGuardar, AlertDialog dialogoReservar) {
        seleccionPacienteEnviada = true;
        horaTurnoPendiente = horaTurno;
        datosGuardados = datosAGuardar;
        this.dialogoReservar = dialogoReservar;
        Bundle args = new Bundle();
        args.putBoolean("seleccionarPaciente", true);
        navController.navigate(R.id.nav_mis_pacientes, args);
    }

    public AlertDialog getDialogoReservar() {
        AlertDialog d = dialogoReservar;
        dialogoReservar = null;
        return d;
    }

    public void responderPaciente(Paciente p) {
        Log.d(TAG, "Respondió con paciente" + p);
        Bundle args = new Bundle();
        args.putBoolean("respuestaPaciente", true);
        args.putSerializable("paciente", p);
        args.putSerializable("horaTurno", horaTurnoPendiente);
        args.putBundle("datos", datosGuardados);
        //Uso una acción en vez de navegar directamente al fragment para eliminar todos los fragments anteriores de la pila (con popUpTo)
        navController.navigate(R.id.respuesta_paciente, args);
        horaTurnoPendiente = null;
        datosGuardados = null;
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


    public void recargarFragment(Fragment fragment, Calendar fechaMostrada) {
        if (fragment instanceof MiAgendaFragment) {
            Bundle args = new Bundle();
            args.putSerializable("fechaMostrar", fechaMostrada);
            navController.navigate(R.id.recargar_agenda, args);
        }
    }

    public void mostrarCanceloTurno() {
        avisoTurnoCancelado.show();
    }
}
