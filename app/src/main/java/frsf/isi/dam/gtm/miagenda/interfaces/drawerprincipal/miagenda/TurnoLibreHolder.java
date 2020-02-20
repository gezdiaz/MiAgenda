package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import frsf.isi.dam.gtm.miagenda.R;

public class TurnoLibreHolder extends RecyclerView.ViewHolder {

    public TextView horaTurno;
    public ConstraintLayout turnoLibreConstraintLayout;

    public TurnoLibreHolder(@NonNull View fila) {
        super(fila);

        horaTurno = fila.findViewById(R.id.hora_turno_libre_txt);
        turnoLibreConstraintLayout = fila.findViewById(R.id.fila_turno_libre_layout);
    }
}
