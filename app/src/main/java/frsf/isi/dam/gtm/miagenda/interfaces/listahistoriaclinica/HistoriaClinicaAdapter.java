package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.entidades.Turno;

public class HistoriaClinicaAdapter extends FirestoreRecyclerAdapter<Turno, HistoriaClinicaHolder> {


    public HistoriaClinicaAdapter(@NonNull FirestoreRecyclerOptions<Turno> options) {
        super(options);

    }

    @NonNull
    @Override
    public HistoriaClinicaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_historia_clinica, parent, false);
        return new HistoriaClinicaHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull HistoriaClinicaHolder historiaClinicaHolder, int position, @NonNull final Turno turno) {
       Log.d(HistoriaClinicaActivity.TAG, turno.toString());

        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(turno.getFecha());
        String fecha = historiaClinicaHolder.fechaLbl.getContext().getString(R.string.fecha_estandar, cal.get(Calendar.DATE), (cal.get(Calendar.MONTH)+1), cal.get(Calendar.YEAR));
        historiaClinicaHolder.fechaLbl.setText(fecha);
        historiaClinicaHolder.descripcionLbl.setText(R.string.descripcion_lbl);
        historiaClinicaHolder.descripcionLbl.append(": "+turno.getDescripcion());
        historiaClinicaHolder.historiaClinicaCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(view.getContext(), R.style.MaterialAlertDialog_MaterialComponents_Title_Icon_CenterStacked)
                        .setTitle(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR))
                        .setMessage(turno.getDescripcion())
                        .setPositiveButton(R.string.ok_option, null)
                        .show();
            }
        });
    }


}
