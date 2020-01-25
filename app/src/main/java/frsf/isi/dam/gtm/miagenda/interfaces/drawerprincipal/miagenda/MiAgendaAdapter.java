package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import frsf.isi.dam.gtm.miagenda.R;

public class MiAgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TURNO_LIBRE = 0;
    public static final int TURNO_OCUPADO = 2;

    private String TAG = "MiAgendaActivity.MiAgendaAdapter";

    //TODO tiene que tener una lista de turnos
    int cant = 0;

    public MiAgendaAdapter(){
        //TODO recibe una lista de turnos
        cant = 10;
    }

    @Override
    public int getItemViewType(int position) {
        //TODO usar tipo de turno para definir tipo de view
        return position % 2 * 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TURNO_LIBRE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_turno_libre, parent, false);
                return new TurnoLibreHolder(view);
            case TURNO_OCUPADO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_turno_ocupado, parent, false);
                return new TurnoOcupadoHolder(view);
            default:
                Log.d(TAG, "Opción no válida en MiAgenda adapter.");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TURNO_LIBRE:
                //TODO mostrar turno libre
                break;
            case TURNO_OCUPADO:
                //TODO mostrar turno ocupado
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cant;
    }

}
