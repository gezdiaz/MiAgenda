package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ProgressBar;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.LoginActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.NuevoPacienteActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MisPacientesFragment extends Fragment {
    private static final String TAG = "MisPacientesFragment";

    private RecyclerView recyclerView;
    private MisPacientesAdapter adapter;
    private FloatingActionButton fabMisPacientes;
    private ProgressBar progressBar;
    private Snackbar avisoSeleccion;
    private TextInputEditText buscarEdit;
    private Spinner buscarSpinner;
    private int posicionItemSpinerSeleccionada = 0;
    private String categoriaBusqueda;
;
    private FirestoreRecyclerOptions<Paciente> firestoreRecyclerOptions;

    public MisPacientesFragment(){
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_pacientes, container, false);

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_mis_pacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Paciente>()
                                    .setQuery(DatosFirestore.getInstance().getAllPacientesQuery(),Paciente.class)
                                    .build();

        Log.d(TAG, "Arguments recibidos: "+getArguments());

        boolean modoseleccionar = false;
        Bundle arguments = getArguments();
        if(arguments != null && arguments.getBoolean("seleccionarPaciente", false)){
            //Tiene que seleccionar un paciente
            modoseleccionar = true;
        }

        adapter = new MisPacientesAdapter(firestoreRecyclerOptions, modoseleccionar, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        fabMisPacientes = ((PrincipalActivity) getActivity()).fabPrincipal;//view.findViewById(R.id.fab_mis_pacientes);
        fabMisPacientes.setColorFilter(getResources().getColor(R.color.colorTextSecondary));
        fabMisPacientes.setImageDrawable(getActivity().getDrawable(R.drawable.ic_add_24px));
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
        //Para evitar que la aplicación siempre este escuchando cambios de la base de datos (por ser de tiempo real) aunque no este en esta actividad.
        adapter.stopListening();
    }

    public void responderPaciente(Paciente p) {
        ((PrincipalActivity) getActivity()).responderPaciente(p);
    }

    private androidx.appcompat.app.AlertDialog buildBuscarDialog(){
        final androidx.appcompat.app.AlertDialog buscarDialog;
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        LayoutInflater inflater =  (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogo_buscar, null);
        builder.setView(dialogView);

        buscarEdit = dialogView.findViewById(R.id.buscar_edit_text);
        buscarSpinner = dialogView.findViewById(R.id.buscar_spinner);

        final ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.buscar_array, android.R.layout.simple_spinner_item);
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

                if(buscarEdit.getText().toString().isEmpty()){
                    mostrarListaPaciente(null, null,null);
                }
                else {
                    String busquedaUsuario;
                    char finalChar = buscarEdit.getText().toString().charAt(buscarEdit.getText().toString().length() - 1);
                    finalChar++;

                    String busquedaMax;
                    if (buscarEdit.getText().toString().length() > 1) {
                        busquedaUsuario = buscarEdit.getText().toString().substring(0,1).toUpperCase().concat(buscarEdit.getText().toString().substring(1));
                        busquedaMax = busquedaUsuario.substring(0, buscarEdit.getText().toString().length() - 1) + finalChar;
                    } else {
                        busquedaUsuario = buscarEdit.getText().toString().toUpperCase();
                        busquedaMax = String.valueOf(finalChar).toUpperCase();
                    }
                    //Llamar a la funcion con categoriaBusqueda, buscarEdit.getText().toSring() y busquedaMax
                    mostrarListaPaciente(categoriaBusqueda, busquedaUsuario,busquedaMax);
                }

            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //No hago nada
            }
        });

        buscarDialog = builder.create();

        buscarDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                buscarDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorCancelar));
            }
        });
        return buscarDialog;
    }

    private void mostrarListaPaciente(String categoriaBusqueda, String busquedaUsuario, String busquedaMax) {

        if(busquedaUsuario!=null){
            Log.d(TAG,"lo que el usuario escribio: " +  busquedaUsuario);
            Log.d(TAG, "categoria busqueda: " +categoriaBusqueda);
            Log.d(TAG,"hasta donde tiene que buscar: " +  busquedaMax);
        }

        if(categoriaBusqueda == null && busquedaUsuario == null && busquedaMax == null){

            firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<Paciente>().setQuery(DatosFirestore.getInstance().getAllPacientesQuery(),Paciente.class).build();
            adapter.updateOptions(firestoreRecyclerOptions);

        }
        else{
            firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<Paciente>().setQuery(DatosFirestore.getInstance().getPacientesPorBusqueda(categoriaBusqueda,busquedaUsuario,busquedaMax),Paciente.class).build();
            adapter.updateOptions(firestoreRecyclerOptions);
        }

    }
}