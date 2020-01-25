package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import frsf.isi.dam.gtm.miagenda.R;

public class TurnoLibreHolder extends RecyclerView.ViewHolder {

    public TextView horaTurno;
    public MaterialButton reservarTurnoBtn;

    public TurnoLibreHolder(@NonNull View fila) {
        super(fila);

        horaTurno = fila.findViewById(R.id.hora_turno_libre_txt);
        reservarTurnoBtn = fila.findViewById(R.id.reservar_turno_btn);
    }
}
