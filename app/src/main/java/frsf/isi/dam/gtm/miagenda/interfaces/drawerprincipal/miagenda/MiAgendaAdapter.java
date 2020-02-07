package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.VerPacienteActivity;
import io.opencensus.resource.Resource;

public class MiAgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TURNO_LIBRE = 0;
    public static final int TURNO_OCUPADO = 1;

    private final String cancelarDialogo = "x       ";

    private ArrayList<Integer> pruebaLista = new ArrayList<>();

    private MaterialButton verPacienteDialogBtn, seleccionarPacienteDialogBtn;
    private AlertDialog modificarTurnoDialogo;
    private TextInputEditText descripcionEdit;

    private String TAG = "MiAgendaActivity.MiAgendaAdapter";

    //TODO tiene que tener una lista de turnos
    int cant = 0;

    public MiAgendaAdapter(){
        //TODO recibe una lista de turnos
        cant = 10;
    }

    @Override
    public int getItemViewType(int position) {
        //TODO usar tipo de turno para definir tipo de view. Para esto hay que saber que tipo de turno esta en la posicion position de la lista de turnos.
        int tipo = (int)((Math.random()*10)%2);
        pruebaLista.add(tipo);
        return pruebaLista.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TURNO_LIBRE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_turno_libre, parent, false);
                return new TurnoLibreHolder(view);
            case TURNO_OCUPADO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_turno_ocupado, parent, false);
                return new TurnoOcupadoHolder(view);
            default:
                Log.d(TAG, "Opción no válida en MiAgenda adapter.");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TURNO_LIBRE:{
                //TODO mostrar turno libre

                TurnoLibreHolder turnoLibreHolder = (TurnoLibreHolder) holder;
                turnoLibreHolder.turnoLibreConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog reservarDialog = buildReservarDialog(view);
                        reservarDialog.show();
                    }
                });
                break;
            }

            case TURNO_OCUPADO:
                //TODO mostrar turno ocupado
                TurnoOcupadoHolder turnoOcupadoHolder = (TurnoOcupadoHolder) holder;
                turnoOcupadoHolder.turnoOcupadoConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog verTurnoDialog = buildVerTurnoDialogo(view);
                        verTurnoDialog.show();
                    }
                });
                break;
        }
    }



    @Override
    public int getItemCount() {
        return cant;
    }


    private AlertDialog buildReservarDialog(final View view) {

        final AlertDialog reservarDialogo;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        LayoutInflater inflater =  (LayoutInflater)view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogo_modificar_turno, null);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.listo_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO Reservar turno
            }
        });

        builder.setNeutralButton(cancelarDialogo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        reservarDialogo = builder.create();

        descripcionEdit = dialogView.findViewById(R.id.tuno_descripcion_edit_text);
        seleccionarPacienteDialogBtn = dialogView.findViewById(R.id.turno_seleccionar_paciente_btn);
        seleccionarPacienteDialogBtn.setVisibility(View.VISIBLE);
        verPacienteDialogBtn = dialogView.findViewById(R.id.turno_ver_paciente_btn);
        verPacienteDialogBtn.setVisibility(View.GONE);

        reservarDialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                reservarDialogo.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(view.getResources().getColor(R.color.colorCancelar));
            }
        });

        seleccionarPacienteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ir a pantalla para seleccionar paciente
            }
        });
        return reservarDialogo;
    }

    private AlertDialog buildVerTurnoDialogo(final View view){

        final AlertDialog verTurnoDialogo;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());

        LayoutInflater inflater =  (LayoutInflater)view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialogo_modificar_turno, null);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.listo_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO Modificar turno
            }
        });

        builder.setNegativeButton(R.string.quitar_turno, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO quitar turno
            }
        });

        builder.setNeutralButton(cancelarDialogo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        verTurnoDialogo = builder.create();

        descripcionEdit = dialogView.findViewById(R.id.tuno_descripcion_edit_text);
        seleccionarPacienteDialogBtn = dialogView.findViewById(R.id.turno_seleccionar_paciente_btn);
        seleccionarPacienteDialogBtn.setVisibility(View.GONE);
        verPacienteDialogBtn = dialogView.findViewById(R.id.turno_ver_paciente_btn);
        verPacienteDialogBtn.setVisibility(View.VISIBLE);

        verTurnoDialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                verTurnoDialogo.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(view.getResources().getColor(R.color.colorCancelar));
                verTurnoDialogo.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(view.getResources().getColor(R.color.colorCancelar));
            }
        });

        verPacienteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ir a la pantallar para ver paciente
                Intent i1 = new Intent(view.getContext(), VerPacienteActivity.class);
                Calendar pruebaCalendario = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                pruebaCalendario.set(1998,4,13);
                i1.putExtra("paciente", new Paciente("Intent ", "Ver paciente Btn", "OS", pruebaCalendario.getTime(), 40905, 123456789L, "Argentina", "Santa fe","Escalada", "S/N", "493", "Sin dpto"));
                view.getContext().startActivity(i1);
            }
        });

        return verTurnoDialogo;
    }

}
