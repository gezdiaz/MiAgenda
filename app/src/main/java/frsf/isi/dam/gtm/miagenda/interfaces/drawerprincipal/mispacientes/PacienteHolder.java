package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import frsf.isi.dam.gtm.miagenda.R;

public class PacienteHolder extends RecyclerView.ViewHolder {

    ImageView pacienteImg;
    TextView nombrePacienteTxt;

    public PacienteHolder(@NonNull View fila) {
        super(fila);

        pacienteImg = fila.findViewById(R.id.paciente_img);
        nombrePacienteTxt = fila.findViewById(R.id.nombre_paciente_txt);

    }
}
