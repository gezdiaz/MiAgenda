package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;

public class HistoriaClinicaActivity extends AppCompatActivity {

    private List<Turno> mockTurnos;
    private RecyclerView historiaClinicaRecyclerView;
    private RecyclerView.Adapter historiaClinicaAdapter;
    private  RecyclerView.LayoutManager historiaClinicaLayoutManager;

    private Toolbar toolbar;
    private MaterialTextView pacienteLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historia_clinica);

        toolbar = findViewById(R.id.historia_clinica_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //prueba con turnos falsos
        mockTurnos= new ArrayList<>();
        for(int i=0; i<20; i++){
            mockTurnos.add(new Turno(((i+1)+"/2/2020"),"Problema x"+i,"Sin nombre"));
        }
        mockTurnos.add(new Turno("21/2/2020","Problema xasmdlkasmndlkas nd klnas  kldnasklndkalsndklasnldnasklndlaksndklasndlkasnlkdnsklanqkndoqsndoqind","Sin nombre"));
        historiaClinicaRecyclerView = findViewById(R.id.historia_clinica_recyclerview);
        historiaClinicaLayoutManager = new LinearLayoutManager(this);
        historiaClinicaRecyclerView.setLayoutManager(historiaClinicaLayoutManager);
        historiaClinicaAdapter = new HistoriaClinicaAdapter(mockTurnos);
        historiaClinicaRecyclerView.setAdapter(historiaClinicaAdapter);

        pacienteLbl = findViewById(R.id.paciente_hist_clin_lbl);

        pacienteLbl.append(": "+mockTurnos.get(0).getPaciente());
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
}

