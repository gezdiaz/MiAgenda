package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import frsf.isi.dam.gtm.miagenda.R;

public class TurnoOcupadoHolder extends RecyclerView.ViewHolder {

    TextView horaTurno;
   // MaterialButton modificarTurnoBtn, quitarTurnoBtn;
    ConstraintLayout turnoOcupadoConstraint;

    public TurnoOcupadoHolder(@NonNull View fila) {
        super(fila);

        horaTurno = fila.findViewById(R.id.hora_turno_ocupado_txt);
        turnoOcupadoConstraint = fila.findViewById(R.id.fila_turno_ocupado_layout);
//        modificarTurnoBtn = fila.findViewById(R.id.modificar_turno_btn);
//        quitarTurnoBtn = fila.findViewById(R.id.quitar_turno_btn);

    }
}
