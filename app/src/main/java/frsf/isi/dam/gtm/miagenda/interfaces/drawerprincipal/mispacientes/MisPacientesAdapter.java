package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.mispacientes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestFutureTarget;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Calendar;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.VerPacienteActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;

public class MisPacientesAdapter extends FirestoreRecyclerAdapter<Paciente, PacienteHolder> {

    private boolean modoSeleccionar = false;
    MisPacientesFragment fragment;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MisPacientesAdapter(@NonNull FirestoreRecyclerOptions<Paciente> options, boolean modoSeleccionar, MisPacientesFragment fragment) {
        super(options);
        this.modoSeleccionar = modoSeleccionar;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public PacienteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_paciente, parent, false);

        return new PacienteHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PacienteHolder holder, int position, @NonNull final Paciente paciente) {

//        //Foto de perfil
        if(paciente.getFotoURL() != null){
            Uri photoUrl = Uri.parse(paciente.getFotoURL());

                if (photoUrl != null) {
                //Si tiene foto la cargo con Glide
                Glide.with(holder.itemCardView)
                        .load(photoUrl)
                        .into(holder.pacienteImg);
            }
        }
         else {
            //Si no tiene foto pongo la default
            holder.pacienteImg.setImageResource(R.drawable.perfil_default);
        }

        holder.nombrePacienteTxt.setText( paciente.getApellido() + " " + paciente.getNombre());

        holder.itemCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modoSeleccionar){
                    //TODO Seleccionar un paciente y volver a mi agenda
                    fragment.responderPaciente(paciente);
                }else{
                    //Ir a ver paciente;
                    Intent i = new Intent(view.getContext(), VerPacienteActivity.class);
//                Calendar pruebaCalendario = Calendar.getInstance();
//                pruebaCalendario.set(1997, 8,6);
//                i.putExtra("paciente", new Paciente("Intent ", "Ver paciente Btn", "Sancor Seguros", pruebaCalendario.getTime(), 40905, 123456789L, "Argentina", "Santa fe","Escalada", "S/N", "493", "Sin dpto"));

                    //TODO mostrar datos de paciente
                    i.putExtra("paciente", paciente);
                    view.getContext().startActivity(i);
                }

            }
        });

    }
}
