package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import frsf.isi.dam.gtm.miagenda.R;

public class HistoriaClinicaAdapter extends RecyclerView.Adapter<HistoriaClinicaHolder> {

    private List<Turno> listaHistoriaClinica;

    public HistoriaClinicaAdapter(List<Turno> list){
        //TODO ordenar lista por fecha
        listaHistoriaClinica = list;
    }

    @NonNull
    @Override
    public HistoriaClinicaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_historia_clinica,parent,false);
        HistoriaClinicaHolder historiaClinicaHolder = new HistoriaClinicaHolder(v);
        return historiaClinicaHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriaClinicaHolder historiaClinicaHolder, int position) {
        final Turno t = listaHistoriaClinica.get(position);
        historiaClinicaHolder.fechaLbl.setText("Fecha: "+t.getFecha());
        historiaClinicaHolder.descripcionLbl.setText("Descripcion: "+t.getDescripcion());
        historiaClinicaHolder.historiaClinicaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(view.getContext(), R.style.MaterialAlertDialog_MaterialComponents_Title_Icon_CenterStacked)
                        .setTitle(t.getFecha())
                        .setMessage(t.getDescripcion())
                        .setPositiveButton(R.string.ok_option, null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaHistoriaClinica.size();
    }
}
