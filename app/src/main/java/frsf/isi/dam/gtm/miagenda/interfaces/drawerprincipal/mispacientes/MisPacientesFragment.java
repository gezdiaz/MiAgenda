package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
import frsf.isi.dam.gtm.miagenda.interfaces.NuevoPacienteActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MisPacientesFragment extends Fragment {
    private static final String TAG = "MisPacientesFragment";

    RecyclerView recyclerView;
    MisPacientesAdapter adapter;
    FloatingActionButton fabMisPacientes;
    Snackbar avisoSeleccion;
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


        recyclerView = view.findViewById(R.id.recycler_mis_pacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //TODO obtener los datos de firestore

        DatosFirestore datosFirestore = DatosFirestore.getInstance();
        FirestoreRecyclerOptions<Paciente> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Paciente>().setQuery(datosFirestore.getAllPacientesQuery(),Paciente.class).build();

        Log.d(TAG, "Arguments recibidos: "+getArguments());

        boolean modoseleccionar = false;
        Bundle arguments = getArguments();
        if(arguments != null && arguments.getBoolean("seleccionarPaciente", false)){
            //Tiene que seleccionar un paciente
            modoseleccionar = true;
            avisoSeleccion = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), "Seleccionando paciente para turno", Snackbar.LENGTH_INDEFINITE);
                    avisoSeleccion.show();
        }

        adapter = new MisPacientesAdapter(firestoreRecyclerOptions, modoseleccionar, this);
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

    public void responderPaciente(Paciente p) {
        if(avisoSeleccion.isShown()) {
            avisoSeleccion.dismiss();
        }
        ((PrincipalActivity) getActivity()).responderPaciente(p);
    }
}