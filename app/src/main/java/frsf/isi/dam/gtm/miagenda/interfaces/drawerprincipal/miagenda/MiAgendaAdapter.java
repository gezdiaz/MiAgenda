package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.interfaces.VerPacienteActivity;

public class MiAgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TURNO_LIBRE = 0;
    public static final int TURNO_OCUPADO = 2;

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
        //TODO usar tipo de turno para definir tipo de view
        return position % 2 * 2;
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
            case TURNO_LIBRE:
                //TODO mostrar turno libre
                TurnoLibreHolder turnoLibreHolder = (TurnoLibreHolder) holder;
                turnoLibreHolder.reservarTurnoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildModificarDialog(view);
                        verPacienteDialogBtn.setVisibility(View.GONE);
                        seleccionarPacienteDialogBtn.setVisibility(View.VISIBLE);
                        modificarTurnoDialogo.show();
                    }
                });
                break;
            case TURNO_OCUPADO:
                //TODO mostrar turno ocupado
                TurnoOcupadoHolder turnoOcupadoHolder = (TurnoOcupadoHolder) holder;
                turnoOcupadoHolder.modificarTurnoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buildModificarDialog(view);
                        seleccionarPacienteDialogBtn.setVisibility(View.GONE);
                        verPacienteDialogBtn.setVisibility(View.VISIBLE);
                        modificarTurnoDialogo.show();
                    }
                });

                turnoOcupadoHolder.quitarTurnoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext())
                                .setTitle(R.string.titulo_dialogo_quitar_turno)
                                .setMessage(R.string.msg_dialogo_quitar_turno)
                                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //TODO borrar turno
                                    }
                                });

                        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        final AlertDialog quitarTurnoDialog =  builder.create();
                        quitarTurnoDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            View v = view;
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                quitarTurnoDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(v.getResources().getColor(R.color.colorCancelar));
                            }
                        });
                        quitarTurnoDialog.show();
                    }
                });

                break;
        }
    }


    @Override
    public int getItemCount() {
        return cant;
    }

    private void buildModificarDialog(final View view) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
        LayoutInflater inflater =  (LayoutInflater)view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogo_modificar_turno, null);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.listo_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO Guardar nuevo paciente o nuevos cambios.
            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        modificarTurnoDialogo = builder.create();
        verPacienteDialogBtn = dialogView.findViewById(R.id.turno_ver_paciente_btn);
        seleccionarPacienteDialogBtn = dialogView.findViewById(R.id.turno_seleccionar_paciente_btn);
        descripcionEdit = dialogView.findViewById(R.id.tuno_descripcion_edit_text);

        modificarTurnoDialogo.setOnShowListener( new DialogInterface.OnShowListener() {
            View view2 = view;
            @Override
            public void onShow(DialogInterface arg0) {
                modificarTurnoDialogo.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(view2.getResources().getColor(R.color.colorCancelar));
            }
        });

        verPacienteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ir a la pantallar para ver paciente
                Intent i1 = new Intent(view.getContext(),VerPacienteActivity.class);
                Calendar pruebaCalendario = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                pruebaCalendario.set(1998,4,13);
                i1.putExtra("paciente", new Paciente("Intent ", "Ver paciente Btn", "OS", pruebaCalendario.getTime(), "40905", 123456789L, "Argentina", "Santa fe","Escalada", "S/N", "493", "Sin dpto"));
                view.getContext().startActivity(i1);
            }
        });
        seleccionarPacienteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ir a la pantallar para seleccionar un paciente
            }
        });
    }

}
