package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import frsf.isi.dam.gtm.miagenda.R;

public class MisPacientesAdapter extends RecyclerView.Adapter<PacienteHolder> {


    //TODO Lista de pacientes
    int cant;

    public MisPacientesAdapter(){
        //TODO recibe una lista de pacientes
        cant = 10;
    }

    @NonNull
    @Override
    public PacienteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_paciente, parent, false);
        return new PacienteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return cant;
    }
}
