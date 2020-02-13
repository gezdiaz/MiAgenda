package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.entidades.Turno;
import frsf.isi.dam.gtm.miagenda.interfaces.VerPacienteActivity;

public class MiAgendaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TURNO_LIBRE = 0;
    public static final int TURNO_OCUPADO = 1;

    private final String cancelarDialogo = "x       ";

    private MiAgendaFragment fragment;

    private MaterialButton verPacienteDialogBtn, seleccionarPacienteDialogBtn;
    private TextInputEditText descripcionEdit;
    private TextView nombrePaceinteTxt, fechaTurnoTxt;

    private String TAG = "MiAgendaActivity.MiAgendaAdapter";

    private List<Turno> listTurnos;
    private Calendar fechaSeleccionada;

    public MiAgendaAdapter(List<Turno> listTurnos, Calendar fechaSeleccionada, MiAgendaFragment fragment) {
        this.listTurnos = listTurnos;
        this.fechaSeleccionada = fechaSeleccionada;
        this.fragment = fragment;
    }

    public void setFecha(Calendar fecha) {
        fechaSeleccionada = fecha;
    }

    public void setListaTurnos(List<Turno> listTurnos) {
        this.listTurnos = listTurnos;
    }

    @Override
    public int getItemViewType(int position) {
        if (listTurnos.get(position).isDisponible()) {
            return TURNO_LIBRE;
        } else {
            return TURNO_OCUPADO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TURNO_LIBRE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_turno_libre, parent, false);
                return new TurnoLibreHolder(view);
            case TURNO_OCUPADO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_turno_ocupado, parent, false);
                return new TurnoOcupadoHolder(view);
            default:
                Log.wtf(TAG, "Opción no válida en MiAgenda adapter.");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TURNO_LIBRE: {
                //TODO mostrar turno libre
                TurnoLibreHolder turnoLibreHolder = (TurnoLibreHolder) holder;
                turnoLibreHolder.turnoLibreConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog reservarDialog = buildReservarDialog(position, null, null);
                        reservarDialog.show();
                    }
                });
                Calendar hora = (Calendar) fechaSeleccionada.clone();
                hora.add(Calendar.MINUTE, MiAgendaFragment.tiempoTurno * position);
                turnoLibreHolder.horaTurno.setText(hora.get(Calendar.HOUR_OF_DAY) + ":" + (hora.get(Calendar.MINUTE) == 0 ? "00" : hora.get(Calendar.MINUTE)) + " hs.");
                break;
            }

            case TURNO_OCUPADO:
                //TODO mostrar turno ocupado
                TurnoOcupadoHolder turnoOcupadoHolder = (TurnoOcupadoHolder) holder;
                turnoOcupadoHolder.turnoOcupadoConstraint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog verTurnoDialog = buildVerTurnoDialogo(position);
                        verTurnoDialog.show();
                    }
                });
                Calendar hora = Calendar.getInstance();
                hora.setTime(listTurnos.get(position).getFecha());
                turnoOcupadoHolder.horaTurno.setText(hora.get(Calendar.HOUR_OF_DAY) + ":" + (hora.get(Calendar.MINUTE) == 0 ? "00" : hora.get(Calendar.MINUTE)) + " hs.");
                turnoOcupadoHolder.nombrePaciente.setText(listTurnos.get(position).getNombrePaciente());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listTurnos.size();
    }


    public void retomarCreacionTurno(Paciente p, Bundle datosIngresados){
        Calendar horaSeleccionada = (Calendar) datosIngresados.get("horaSeleccionada");
        String descripcionIngresada = datosIngresados.getString("descripcionIngresada","");
        int pos = getPosicionDeHora(horaSeleccionada);
        Log.d(TAG, "Posicion calculada: "+pos);
        buildReservarDialog(pos, descripcionIngresada, p).show();
    }

    private int getPosicionDeHora(Calendar horaSeleccionada) {
        //hora = horaInicio + (pos*timepoTurno minutos)
        //hora = horaInicio + (pos*timepoTurno minutos)/60 horas
        //pos = (hora - horaInicio)*60/tiempoTurno
        Calendar inicio = (Calendar) horaSeleccionada.clone();
        inicio.set(Calendar.HOUR_OF_DAY, MiAgendaFragment.horaInicio);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.SECOND, 0);
        inicio.set(Calendar.MILLISECOND, 0);
        long difMilis = horaSeleccionada.getTimeInMillis() - inicio.getTimeInMillis();
        double difHoras = difMilis/3.6e+6;
        Log.d(TAG, "Diferencia en horas: "+difHoras);
        int pos = (int)((difHoras*60)/MiAgendaFragment.tiempoTurno);
        Log.d(TAG, "pos en funcion: "+pos);
        return pos;
    }

    private AlertDialog buildReservarDialog(final int turnoPos, String descripcionIngresada, final Paciente pacienteSeleccionado) {

        final AlertDialog reservarDialogo;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getContext());
        LayoutInflater inflater = (LayoutInflater) fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialogo_modificar_turno, null);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.listo_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO Reservar turno
                //Paciente falso para probar
//                Paciente p = new Paciente("Gastón", "Díaz", "OSECAC", new Date(), "40267721", 155046210l, "Argentina", "fdgeg", "sgerg", "sfger", "fdbe", "fge");
                Calendar fechaTurno = (Calendar)fechaSeleccionada.clone();
                fechaTurno.add(Calendar.MINUTE, MiAgendaFragment.tiempoTurno*turnoPos);
//                Turno t = new Turno(descripcionEdit.getText().toString(), p.getApellido()+", "+p.getNombre(), fechaTurno.getTime());
//                t.setDisponible(false);
//                t.setPosicion(turnoPos);
//                DatosFirestore.getInstance().saveTurno(t, p.getDni(), fragment.handler);
                String descripcion = descripcionEdit.getText().toString();
                guardarTurno(turnoPos, descripcion, fechaTurno, pacienteSeleccionado);
//                fragment.recargarFragment();
            }
        });

        builder.setNeutralButton(cancelarDialogo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        reservarDialogo = builder.create();

        descripcionEdit = dialogView.findViewById(R.id.tuno_descripcion_edit_text);
        fechaTurnoTxt = dialogView.findViewById(R.id.fecha_turno_editado_txt);
        nombrePaceinteTxt = dialogView.findViewById(R.id.turno_nombre_paciente_txt);
        seleccionarPacienteDialogBtn = dialogView.findViewById(R.id.turno_seleccionar_paciente_btn);
        seleccionarPacienteDialogBtn.setVisibility(View.VISIBLE);
        verPacienteDialogBtn = dialogView.findViewById(R.id.turno_ver_paciente_btn);
        verPacienteDialogBtn.setVisibility(View.GONE);

        if(descripcionIngresada != null){
            descripcionEdit.setText(descripcionIngresada);
        }

        Calendar hora = (Calendar) fechaSeleccionada.clone();
        hora.add(Calendar.MINUTE, 30 * turnoPos);
        fechaTurnoTxt.setText("Fecha: "
                + hora.get(Calendar.DAY_OF_MONTH)
                + "/" + (hora.get(Calendar.MONTH)+1)
                + "/" + hora.get(Calendar.YEAR)
                + " - " + hora.get(Calendar.HOUR_OF_DAY)
                + ":" + (hora.get(Calendar.MINUTE) == 0 ? "00" : hora.get(Calendar.MINUTE))
                + " hs.");
        hora = null;

        reservarDialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                reservarDialogo.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(fragment.getResources().getColor(R.color.colorCancelar));
            }
        });

        if(pacienteSeleccionado != null){
            seleccionarPacienteDialogBtn.setText(R.string.cambiar_paciente);
            nombrePaceinteTxt.setText(pacienteSeleccionado.getApellido()+", "+pacienteSeleccionado.getNombre());
        }

        seleccionarPacienteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ir a pantalla para seleccionar paciente
                String descripcionIngresada = descripcionEdit.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("'Fecha: 'dd/MM/yyyy' - 'HH:mm' hs.'");
                Calendar horaSeleccionada = Calendar.getInstance();
                try {
                    horaSeleccionada.setTime(sdf.parse(fechaTurnoTxt.getText().toString()));
                    horaSeleccionada.set(Calendar.SECOND, 0);
                    horaSeleccionada.set(Calendar.MILLISECOND, 0);
                } catch (ParseException e) {
                    Log.d(TAG, "Error al parsear la fecha", e);
                }
                Log.d(TAG, "hora seleccionada al pedir paciente: "+horaSeleccionada);
                Bundle guardar = new Bundle();
                guardar.putSerializable("horaSeleccionada", horaSeleccionada);
                guardar.putString("descripcionIngresada", descripcionIngresada);
                fragment.seleccionarPaciente(horaSeleccionada, guardar, reservarDialogo);
                reservarDialogo.dismiss();
            }
        });
        return reservarDialogo;
    }

    private void guardarTurno(int turnoPos, String descripcion, Calendar fechaTurno, Paciente p) {
        if(p == null){
            //No seleccionó paciente, se descarta el turno
            Snackbar.make(fragment.getActivity().findViewById(R.id.coordinator_layout), "No se seleccionó ningún paciente, se descarta el turno", BaseTransientBottomBar.LENGTH_LONG)
                    .setBackgroundTint(fragment.getResources().getColor(R.color.colorCancelar))
                    .show();
        }else{
            Turno t = new Turno(descripcion, p.getApellido()+", "+p.getNombre(), p.getDni(), fechaTurno.getTime());
            t.setPosicion(turnoPos);
            t.setDisponible(false);
            fragment.guardarTurno(t, p.getDni());
        }
    }

    private AlertDialog buildVerTurnoDialogo(int turnoPos) {

        final AlertDialog verTurnoDialogo;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getContext());
        final Turno turno = listTurnos.get(turnoPos);

        LayoutInflater inflater = (LayoutInflater) fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialogo_modificar_turno, null);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.listo_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO Modificar turno
                String nuevaDescripcion = descripcionEdit.getText().toString();
                if(!nuevaDescripcion.equals(turno.getDescripcion())){
                    turno.setDescripcion(nuevaDescripcion);
                    fragment.actualizarTurno(turno);
                }
            }
        });

        builder.setNegativeButton(R.string.quitar_turno, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO quitar turno
                fragment.borrarTurno(turno);
            }
        });

        builder.setNeutralButton(cancelarDialogo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        verTurnoDialogo = builder.create();

        descripcionEdit = dialogView.findViewById(R.id.tuno_descripcion_edit_text);
        nombrePaceinteTxt = dialogView.findViewById(R.id.turno_nombre_paciente_txt);
        fechaTurnoTxt = dialogView.findViewById(R.id.fecha_turno_editado_txt);
        seleccionarPacienteDialogBtn = dialogView.findViewById(R.id.turno_seleccionar_paciente_btn);
        seleccionarPacienteDialogBtn.setVisibility(View.GONE);
        verPacienteDialogBtn = dialogView.findViewById(R.id.turno_ver_paciente_btn);
        verPacienteDialogBtn.setVisibility(View.VISIBLE);

        descripcionEdit.setText(turno.getDescripcion());
        nombrePaceinteTxt.setText(turno.getNombrePaciente());
        //No puedo usar la fecha del turno porque se guarda mal la hora ¯\_(ツ)_/¯
//        SimpleDateFormat sdf = new SimpleDateFormat("'Fecha: 'dd/MM/yy' - 'HH:MM' hs.'");
//        fechaTurnoTxt.setText(sdf.format(listTurnos.get(turnoPos).getFecha()));
        Calendar hora = (Calendar) fechaSeleccionada.clone();
        hora.add(Calendar.MINUTE, 30 * turnoPos);
        fechaTurnoTxt.setText("Fecha: "
                + hora.get(Calendar.DAY_OF_MONTH)
                + "/" + (hora.get(Calendar.MONTH)+1)
                + "/" + hora.get(Calendar.YEAR)
                + " - " + hora.get(Calendar.HOUR_OF_DAY)
                + ":" + (hora.get(Calendar.MINUTE) == 0 ? "00" : hora.get(Calendar.MINUTE))
                + " hs.");
        hora = null;


        verTurnoDialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                verTurnoDialogo.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(fragment.getResources().getColor(R.color.colorCancelar));
                verTurnoDialogo.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(fragment.getResources().getColor(R.color.colorCancelar));
            }
        });
        verPacienteDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ir a la pantallar para ver paciente
//                Intent i1 = new Intent(view.getContext(), VerPacienteActivity.class);
//                Calendar pruebaCalendario = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//                pruebaCalendario.set(1998, 4, 13);
//                i1.putExtra("paciente", new Paciente("Intent ", "Ver paciente Btn", "OS", pruebaCalendario.getTime(), "40905", 123456789L, "Argentina", "Santa fe", "Escalada", "S/N", "493", "Sin dpto"));
//                view.getContext().startActivity(i1);
                fragment.verPaciente(turno.getDniPaciente());
            }
        });

        return verTurnoDialogo;
    }

}
