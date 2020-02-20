package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica.HistoriaClinicaActivity;

public class VerPacienteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "VerPacienteActivity";
    public static final int REQUESTEDITARPACIENTE = 1;
    private Toolbar toolbar;
    private MaterialTextView dniPacienteLbl, edadPacienteLbl, obraSocialPacienteLbl, telefonoPacienteLbl, nombrePacienteLbl;
    private TextView masInfoMapaLbl;
    private MaterialButton verHistoriaClinicaBtn;
    private GoogleMap googleMap;
    private boolean permisoLocalizacionAceptado;
    private Paciente p;
    private Intent intentLlamada;
    private ProgressDialog progressDialog;
    private String idPaciente;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case DatosFirestore.GET_PACIENTE:
                    Log.d(TAG, "Paciente guardado correctamente");

                    p = (Paciente) msg.obj;

                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ver_paciente_map);
                    mapFragment.getMapAsync(VerPacienteActivity.this);

                    inicializarTextViews();

                    break;
                case DatosFirestore.ERROR_GET_PACIENTE:
                    Log.d(TAG, "Error al obtener el paciente");
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Snackbar.make(findViewById(R.id.ver_paciente_linear_layout), "Se produjo un error al obtener el paciente", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.colorCancelar))
                            .show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_paciente);
        idPaciente = getIntent().getStringExtra("idPaciente");


        toolbar = findViewById(R.id.ver_paciente_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextSecondary));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_llamada, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.llamada_option_item:{
                intentLlamada = new Intent(Intent.ACTION_CALL);
                intentLlamada.setData(Uri.parse("tel:" + p.getTelefono()));
                //Esto lo pongo porque necesita API 23 y estamos usando 22
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(VerPacienteActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 8888);
                        Log.d(TAG, "Necesita Permisos");
                    }
                    else{
                        startActivity(intentLlamada);
                    }
                }

                break;
            }
            case R.id.editar_option_item:{

                Intent editarPacienteIntent = new Intent(getApplicationContext(), NuevoPacienteActivity.class);
                editarPacienteIntent.setAction(NuevoPacienteActivity.EDITAR_ACTION);
                editarPacienteIntent.putExtra("paciente",p);
                startActivity(editarPacienteIntent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESTEDITARPACIENTE){
           DatosFirestore.getInstance().getPacienteById(getIntent().getStringExtra("idPaciente"),handler);
        }
    }

    private void inicializarTextViews() {

        dniPacienteLbl = findViewById(R.id.dni_paciente_lbl);
        dniPacienteLbl.setText(R.string.dni_paciente_lbl);
        edadPacienteLbl = findViewById(R.id.edad_paciente_lbl);
        edadPacienteLbl.setText(R.string.edad_paciente_lbl);
        obraSocialPacienteLbl = findViewById(R.id.obra_social_paciente_lbl);
        obraSocialPacienteLbl.setText(R.string.obra_social_paciente_lbl);
        telefonoPacienteLbl = findViewById(R.id.telefono_paciente_lbl);
        telefonoPacienteLbl.setText(R.string.telefono_paciente_lbl);
        verHistoriaClinicaBtn = findViewById(R.id.ver_historia_btn);
        nombrePacienteLbl = findViewById(R.id.nombre_paciente_lbl);
        nombrePacienteLbl.setText(R.string.nombre_ver_paciente);
        masInfoMapaLbl = findViewById(R.id.mas_info_mapa_lbl);

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
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},9999);
        }
        else{
            inicializarMapa();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 9999:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoLocalizacionAceptado = true;
                }
                else{
                   permisoLocalizacionAceptado = false;
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


        if(addressList != null && addressList.size() != 0) {

            masInfoMapaLbl.setText(R.string.mapa_mas_info);

            Address pacienteAddress = addressList.get(0);

            LatLng pacienteUbicacionLatLng = new LatLng(pacienteAddress.getLatitude(),pacienteAddress.getLongitude());

            googleMap.addMarker(new MarkerOptions()
                    .position(pacienteUbicacionLatLng)
                    .title((p.getDireccion().getStringToShow()).toUpperCase()));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pacienteUbicacionLatLng)
                    .zoom(15)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
        else{
            masInfoMapaLbl.setText(getString(R.string.ubicacion_no_encontrada, p.getDireccion().getStringToShow()));
            masInfoMapaLbl.setTextColor(Color.RED);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(idPaciente != null && !idPaciente.isEmpty()){
            progressDialog = ProgressDialog.show(VerPacienteActivity.this, getString(R.string.por_favor_espere), getString(R.string.buscando_paciente));
            progressDialog.setCancelable(false);
            DatosFirestore.getInstance().getPacienteById(idPaciente, handler);
        }
    }
}




