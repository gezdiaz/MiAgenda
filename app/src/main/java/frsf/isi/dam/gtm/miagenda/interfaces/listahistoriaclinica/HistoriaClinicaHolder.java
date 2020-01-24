package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import frsf.isi.dam.gtm.miagenda.R;

public class HistoriaClinicaHolder extends RecyclerView.ViewHolder {

    public MaterialTextView fechaLbl;
    public MaterialTextView descripcionLbl;
    public MaterialCardView historiaClinicaCardView;

    public HistoriaClinicaHolder (View base) {

        super((base));
        fechaLbl = base.findViewById(R.id.fecha_atencion_paciente_lbl);
        descripcionLbl = base.findViewById(R.id.descripcion_atencion_paciente_lbl);
        historiaClinicaCardView =base.findViewById(R.id.historia_clinica_card_view);
    }
}
