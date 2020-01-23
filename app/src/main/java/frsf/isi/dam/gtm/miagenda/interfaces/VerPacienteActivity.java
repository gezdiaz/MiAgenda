package frsf.isi.dam.gtm.miagenda.interfaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.textview.MaterialTextView;

import frsf.isi.dam.gtm.miagenda.R;

public class VerPacienteActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialTextView dniPacienteLbl, edadPacienteLbl, obraSocialPacienteLbl, telefonoPacienteLbl;
    //Variables para probar los datos del paciente
    private String dni = "40905305", edad="21", obraSocial="IAPOS", telefono="15431193";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_paciente);

        toolbar = findViewById(R.id.ver_paciente_toolbar);
        toolbar.setTitle("Nombre paciente");
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dniPacienteLbl = findViewById(R.id.dni_paciente_lbl);
        edadPacienteLbl = findViewById(R.id.edad_paciente_lbl);
        obraSocialPacienteLbl = findViewById(R.id.obra_social_paciente_lbl);
        telefonoPacienteLbl = findViewById(R.id.telefono_paciente_lbl);

        dniPacienteLbl.append(" " +dni);
        edadPacienteLbl.append(" " +edad + " años");
        obraSocialPacienteLbl.append(" " +obraSocial);
        telefonoPacienteLbl.append(" " +telefono);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo_paciente, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion_option_item:{
                Intent i1 = new Intent(this, LoginActivity.class);
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
}
