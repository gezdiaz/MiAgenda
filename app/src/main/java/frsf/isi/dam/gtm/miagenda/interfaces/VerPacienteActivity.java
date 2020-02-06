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
import android.os.Build;
import android.os.Bundle;
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

    private Toolbar toolbar;
    private MaterialTextView dniPacienteLbl, edadPacienteLbl, obraSocialPacienteLbl, telefonoPacienteLbl;
    private MaterialButton verHistoriaClinicaBtn;
    private GoogleMap googleMap;
    //Variables para probar los datos del pacientes
    private String dni = "40905305", edad="21", obraSocial="IAPOS", telefono="15431193",provincia="Santa fe", ciudad="San justo", calle = "Belgrano", numero="200";
    private boolean permisoLocalizacionAceptado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_paciente);

        toolbar = findViewById(R.id.ver_paciente_toolbar);
        toolbar.setTitle("Nombre paciente");
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.ver_paciente_map);
        mapFragment.getMapAsync(this);

        dniPacienteLbl = findViewById(R.id.dni_paciente_lbl);
        edadPacienteLbl = findViewById(R.id.edad_paciente_lbl);
        obraSocialPacienteLbl = findViewById(R.id.obra_social_paciente_lbl);
        telefonoPacienteLbl = findViewById(R.id.telefono_paciente_lbl);
        verHistoriaClinicaBtn = findViewById(R.id.ver_historia_btn);

        dniPacienteLbl.append(" " +dni);
        edadPacienteLbl.append(" " +edad + " años");
        obraSocialPacienteLbl.append(" " +obraSocial);
        telefonoPacienteLbl.append(" " +telefono);

        verHistoriaClinicaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getApplicationContext(), HistoriaClinicaActivity.class);
                startActivity(i1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opcion, menu);
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
            case 9999: {
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
            }
        }
    }
    private void inicializarMapa(){

        Toast t1 = Toast.makeText(VerPacienteActivity.this, getString(R.string.mapa_mas_info),Toast.LENGTH_LONG);
        t1.show();

        if(permisoLocalizacionAceptado){
            googleMap.setMyLocationEnabled(true);
        }

        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        String ubicacion = "Argentina, " + provincia+", " + ciudad+", " +calle+", "+numero;
        ubicacion = ubicacion.toUpperCase();
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
                                                .title((ciudad+", "+calle + " " +numero).toUpperCase()));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pacienteUbicacionLatLng)
                .zoom(15)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
