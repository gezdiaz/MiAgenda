package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica.HistoriaClinicaActivity;

public class VerPacienteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "VerPacienteActivity";
    private Toolbar toolbar;
    private MaterialTextView dniPacienteLbl, edadPacienteLbl, obraSocialPacienteLbl, telefonoPacienteLbl, nombrePacienteLbl;
    private MaterialButton verHistoriaClinicaBtn;
    private GoogleMap googleMap;
    //Variables para probar los datos del pacientes
    private String dni = "40905305", edad = "21", obraSocial = "IAPOS", telefono = "15431193", provincia = "Santa fe", ciudad = "San justo", calle = "Belgrano", numero = "200";
    private boolean permisoLocalizacionAceptado;
    private Paciente p;
    private Intent intentLlamada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_paciente);

        p = (Paciente) getIntent().getSerializableExtra("paciente");

        toolbar = findViewById(R.id.ver_paciente_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextSecondary));
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ver_paciente_map);
        mapFragment.getMapAsync(this);

        dniPacienteLbl = findViewById(R.id.dni_paciente_lbl);
        edadPacienteLbl = findViewById(R.id.edad_paciente_lbl);
        obraSocialPacienteLbl = findViewById(R.id.obra_social_paciente_lbl);
        telefonoPacienteLbl = findViewById(R.id.telefono_paciente_lbl);
        verHistoriaClinicaBtn = findViewById(R.id.ver_historia_btn);
        nombrePacienteLbl = findViewById(R.id.nombre_paciente_lbl);


        nombrePacienteLbl.append(" " + p.getNombre() + " " + p.getApellido());
        dniPacienteLbl.append(" " + p.getDni());
        edadPacienteLbl.append(" " + p.getEdad() + " años");
        obraSocialPacienteLbl.append(" " + p.getObraSocial());
        telefonoPacienteLbl.append(" " + p.getTelefono());

        verHistoriaClinicaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getApplicationContext(), HistoriaClinicaActivity.class);
                i1.putExtra("paciente", p);
                startActivity(i1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_llamada, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion_option_item:{
                Intent i1 = new Intent(this, LoginActivity.class);
                //Le digo a LogInActivity que cierre sesión
                i1.putExtra(LoginActivity.SignOut, true);
                startActivity(i1);
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.llamada_option_item:{
                intentLlamada = new Intent(Intent.ACTION_CALL);
                intentLlamada.setData(Uri.parse("tel:" + String.valueOf(p.getTelefono())));
                //Esto lo pongo porque necesita API 23 y estamos usando 22
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        ActivityCompat.requestPermissions(VerPacienteActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 8888);
                        Log.d(TAG, "Necesita Permisos");
                        //return;
                    }
                }
                startActivity(intentLlamada);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},9999);
            return;
        }
        else{
            inicializarMapa();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 9999:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoLocalizacionAceptado = true;
                }
                else{
                   permisoLocalizacionAceptado = false;
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(VerPacienteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        //Show permission explanation dialog...
//                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(VerPacienteActivity.this);
//                        alertDialogBuilder.setMessage(R.string.peticion_permiso_localizacion);
//                        alertDialogBuilder.setPositiveButton(R.string.ok_option, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                ActivityCompat.requestPermissions(VerPacienteActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},9999);
//                            }
//                        });
//                    }else{
//                        //Never ask again selected, or device policy prohibits the app from having that permission.
//                        //So, disable that feature, or fall back to another situation...
//                    }
                }
                inicializarMapa();
                break;
            case 8888:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intentLlamada);
                }
        }
    }
    private void inicializarMapa(){

        if(permisoLocalizacionAceptado){
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        String ubicacion = p.getDireccion().getStringForMap();

        try {
            addressList = geocoder.getFromLocationName(ubicacion,1);
        }
        catch (Exception e){
            Toast t = Toast.makeText(VerPacienteActivity.this, getString(R.string.ubicacion_no_encontrada),Toast.LENGTH_LONG);
            t.show();
        }

        Address pacienteAddress = addressList.get(0);

        LatLng pacienteUbicacionLatLng = new LatLng(pacienteAddress.getLatitude(),pacienteAddress.getLongitude());

        Marker marker = googleMap.addMarker(new MarkerOptions()
                                                .position(pacienteUbicacionLatLng)
                                                .title((p.getDireccion().getStringToShow()).toUpperCase()));


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pacienteUbicacionLatLng)
                .zoom(15)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
