package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.NuevoPacienteActivity;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MisPacientesFragment extends Fragment {
    private static final String TAG = "MisPacientesFragment";

    private TextInputEditText buscarEdit;
    private Spinner buscarSpinner;
    private int posicionItemSpinerSeleccionada = 0;
    private String categoriaBusqueda;

    public MisPacientesFragment(){
        setHasOptionsMenu(true);
    }

    RecyclerView recyclerView;
    MisPacientesAdapter adapter;
    FloatingActionButton fabMisPacientes;
    DatosFirestore datosFirestore;
//    private Handler handler = new Handler(Looper.myLooper()){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            switch (msg.what){
//                case DatosFirestore.GETALL_PACIENTES:{
//                    List<Paciente> pacientesRecibidos = (List<Paciente>) msg.obj;
//                    ((MisPacientesAdapter) adapter).setPlatoViewDataSet(pacientesRecibidos);
//                    adapter.notifyDataSetChanged();
//                    if(progressDialog.isShowing()){
//                        progressDialog.cancel();
//                    }
//                    break;
//                }
//                case DatosFirestore.ERROR_GETALL_PACIENTES:{
//                    Toast t = Toast.makeText(DishViewActivity.this, R.string.databaseGetAllDishesError, Toast.LENGTH_LONG);
//                    t.show();
//                    finish();
//                    break;
//                }
//            }
//        }
//    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_pacientes, container, false);

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_mis_pacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        datosFirestore = DatosFirestore.getInstance();
        FirestoreRecyclerOptions<Paciente> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Paciente>().setQuery(datosFirestore.getAllPacientesQuery(),Paciente.class).build();

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

                AlertDialog buscarDialog = buildBuscarDialog();
                buscarDialog.show();

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
        //Para evitar que la aplicacion siempre este escuchando cambios de la base de datos (por ser de tiempo real) aunque no este en esta actividad.
        adapter.stopListening();
    }

    private androidx.appcompat.app.AlertDialog buildBuscarDialog(){
        final androidx.appcompat.app.AlertDialog buscarDialog;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        LayoutInflater inflater =  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogo_buscar, null);
        builder.setView(dialogView);
        buscarDialog = builder.create();

        buscarEdit = dialogView.findViewById(R.id.buscar_edit_text);
        buscarSpinner = dialogView.findViewById(R.id.buscar_spinner);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.buscar_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buscarSpinner.setAdapter(adapterSpinner);


        buscarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posicionItemSpinerSeleccionada = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                posicionItemSpinerSeleccionada = 0;
            }
        });

        builder.setPositiveButton(R.string.buscar_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (posicionItemSpinerSeleccionada){
                    case 0:{
                        categoriaBusqueda = "apellido";
                        break;
                    }
                    case 1:{
                        categoriaBusqueda = "nombre";
                        break;
                    }
                    case 2:{
                        categoriaBusqueda = "dni";
                        break;
                    }
                    default:{
                        Log.wtf(TAG, "Entro al default del Switch del Spinner");
                    }
                }
                //Llamar metodo para actualizar el RecyclerView con categoriaBusqueda y buscarEdit.getText().toString()
            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //No hago nada
            }
        });







        return buscarDialog;
    }
}