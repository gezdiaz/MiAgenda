package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.NuevoPacienteActivity;

public class MisPacientesFragment extends Fragment {

    private static final String TAG = "MisPacientesFragment";

    public MisPacientesFragment(){
        setHasOptionsMenu(true);
    }

    RecyclerView recyclerView;
    MisPacientesAdapter adapter;
    FloatingActionButton fabMisPacientes;
    DatosFirestore datosFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_pacientes, container, false);

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_mis_pacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        datosFirestore = DatosFirestore.getInstance();
        FirestoreRecyclerOptions<Paciente> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Paciente>()
                .setQuery(datosFirestore.getAllPacientesQuery(),Paciente.class)
                .build();


        adapter = new MisPacientesAdapter(firestoreRecyclerOptions);
        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
        fabMisPacientes = view.findViewById(R.id.fab_mis_pacientes);
        fabMisPacientes.setColorFilter(getResources().getColor(R.color.colorTextSecondary));
        fabMisPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), NuevoPacienteActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_paciente,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.cerrar_sesion_option_item:{
                Intent i1 = new Intent(getContext(), LoginActivity.class);
                i1.putExtra(LoginActivity.SignOut, true);
                startActivity(i1);
                //aca iria el finish
                break;
            }
            case R.id.search_option_item:{
                //TODO Hacer la busqueda
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Para evitar que la aplicación siempre este escuchando cambios de la base de datos (por ser de tiempo real) aunque no este en esta actividad.
        adapter.stopListening();
    }
}