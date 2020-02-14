package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda.TurnoLibreHolder;

public class HistoriaClinicaAdapter extends FirestoreRecyclerAdapter<frsf.isi.dam.gtm.miagenda.entidades.Turno, HistoriaClinicaHolder> {

    private List<Turno> listaHistoriaClinica;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HistoriaClinicaAdapter(@NonNull FirestoreRecyclerOptions<frsf.isi.dam.gtm.miagenda.entidades.Turno> options) {
        super(options);

    }

    @NonNull
    @Override
    public HistoriaClinicaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_historia_clinica, parent, false);
        HistoriaClinicaHolder historiaClinicaHolder = new HistoriaClinicaHolder(v);
        return historiaClinicaHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull HistoriaClinicaHolder historiaClinicaHolder, int position, @NonNull final frsf.isi.dam.gtm.miagenda.entidades.Turno turno) {
       Log.d(HistoriaClinicaActivity.TAG, turno.toString());

        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(turno.getFecha());
        historiaClinicaHolder.fechaLbl.setText("Fecha: " + cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR));
        historiaClinicaHolder.descripcionLbl.setText("Descripcion: " + turno.getDescripcion());
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
