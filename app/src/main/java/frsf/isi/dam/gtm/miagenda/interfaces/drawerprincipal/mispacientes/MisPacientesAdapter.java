package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.VerPacienteActivity;

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
        holder.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), VerPacienteActivity.class);
                Calendar pruebaCalendario = Calendar.getInstance();
                pruebaCalendario.set(1997, 8,6);
                i.putExtra("paciente", new Paciente("Intent ", "Ver paciente Btn", "Sancor Seguros", pruebaCalendario.getTime(), "40905", 123456789L, "Argentina", "Santa fe","Escalada", "S/N", "493", "Sin dpto"));
                view.getContext().startActivity(i);
            //TODO mostrar datos de paciente
            }
        });
    }

    @Override
    public int getItemCount() {
        return cant;
    }
}
