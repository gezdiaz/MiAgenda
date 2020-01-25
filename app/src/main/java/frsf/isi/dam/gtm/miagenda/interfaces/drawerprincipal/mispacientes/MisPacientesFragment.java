package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import frsf.isi.dam.gtm.miagenda.R;

public class MisPacientesFragment extends Fragment {


    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_pacientes, container, false);

        recyclerView = view.findViewById(R.id.recycler_mis_pacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MisPacientesAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }
}