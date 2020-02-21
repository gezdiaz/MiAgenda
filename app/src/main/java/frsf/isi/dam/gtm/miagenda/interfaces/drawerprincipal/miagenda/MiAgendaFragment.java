package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.datos.DatosFirestore;
import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.entidades.Turno;
import frsf.isi.dam.gtm.miagenda.interfaces.VerPacienteActivity;
import frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.PrincipalActivity;


public class MiAgendaFragment extends Fragment {

    protected static final int tiempoTurno = 30; //Tiempo entre turnos en minutos
    protected static final int horaInicio = 7; //Hora del primer turno
    protected static final int horaFin = 20; //Hora del último turno;

    private final String TAG = "MiAgendaFragment";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MiAgendaAdapter adapter;
    private FloatingActionButton verCalendarioFAB;
    private MaterialDatePicker.Builder<Long> builder;
    private MaterialDatePicker<Long> datePicker;
    private Calendar fechaMostrar;
    private TextView fechaTxt;
    private List<Turno> listTurnos;
    private boolean nuevoTurno = false;

    protected Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case DatosFirestore.GETALL_TURNOS:
                    List<Turno> turnos = (List<Turno>) msg.obj;
                    actualizarTurnos(turnos);
                    Log.d(TAG, "Se recibieron: " + turnos.size()+" turnos");
                    break;
                case DatosFirestore.ERROR_GETALL_TURNOS:
                    Log.d(TAG, "Error al obtener los turnos de la base de datos");
                    Snackbar.make(getView().findViewById(R.id.mi_agenda_layout), "No se pudieron cargar los turnos", BaseTransientBottomBar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.colorCancelar))
                            .show();
                    break;
                case DatosFirestore.SAVE_TURNO:
                    Log.d(TAG, "Se gaurdó el turno");
                    if(nuevoTurno) {
                        nuevoTurno = false;
                        ((PrincipalActivity) getActivity()).recargarFragment(MiAgendaFragment.this, fechaMostrar);
                    }else{
                        mostrarFechaSeleccionada(fechaMostrar);
                    }
                    break;
                case DatosFirestore.ERROR_SAVE_TURNO:
                    Log.d(TAG, "Error al guardar el turno");
                    Snackbar.make(getView().findViewById(R.id.mi_agenda_layout), "Se produjo un error al guardar el turno", BaseTransientBottomBar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.colorCancelar))
                            .show();
                    break;

                case DatosFirestore.ELIMINACION_TURNO:
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), R.string.elimino_turno, BaseTransientBottomBar.LENGTH_SHORT).show();
                    mostrarFechaSeleccionada(fechaMostrar);
                    break;
                case DatosFirestore.ERROR_ELIMINACION_TURNO:
                    Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), R.string.error_eliminar_turno, BaseTransientBottomBar.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void actualizarTurnos(List<Turno> turnos) {
        int cantTurnos = listTurnos.size();
        listTurnos = new ArrayList<>();
        for (int i = 0; i < cantTurnos; i++) {
            listTurnos.add(new Turno());
        }
        for (Turno t : turnos) {
            listTurnos.set(t.getPosicion(), t);
        }
        adapter.setListaTurnos(listTurnos);
        adapter.setFecha(fechaMostrar);
        adapter.notifyDataSetChanged();
        fechaTxt.setText(getString(R.string.fecha_miagenda,
                fechaMostrar.get(Calendar.DATE),
                fechaMostrar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                fechaMostrar.get(Calendar.YEAR)));

        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_agenda, container, false);
        boolean actualizoLista = false;

        fechaTxt = view.findViewById(R.id.txt_fecha_hoy);

        fechaMostrar = Calendar.getInstance(TimeZone.getDefault());
        //pongo la hora coincidente con el primer turno para después poder sumar minutos.
        fechaMostrar.set(Calendar.HOUR_OF_DAY, horaInicio);
        fechaMostrar.set(Calendar.MINUTE, 0);
        fechaMostrar.set(Calendar.SECOND, 0);
        fechaMostrar.set(Calendar.MILLISECOND, 0);

        recyclerView = view.findViewById(R.id.recycler_turnos);
        progressBar = view.findViewById(R.id.mi_agenda_progress);

        recyclerView.setVisibility(View.INVISIBLE);
        listTurnos = new ArrayList<>();


        adapter = new MiAgendaAdapter(listTurnos, fechaMostrar, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Bundle arguments = getArguments();
        boolean seleccionEnviada = ((PrincipalActivity) getActivity()).seleccionPacienteEnviada;
        Log.d(TAG, "Arguments recibidos en MiAgenda: " + arguments);
        if(arguments != null){
            if(arguments.getBoolean("respuestaPaciente", false)) {
                if (seleccionEnviada) {
                    //Si se había enviado una selección de paciente vuelvo a crear el diálogo, sino no

                    Paciente p = (Paciente) getArguments().get("paciente");
                    Calendar hora = (Calendar) getArguments().get("horaTurno");
                    if (!estaMostrandoFecha(hora)) {
                        fechaMostrar = (Calendar) hora.clone();
                        fechaMostrar.set(Calendar.HOUR_OF_DAY, horaInicio);
                        fechaMostrar.set(Calendar.MINUTE, 0);
                        fechaMostrar.set(Calendar.SECOND, 0);
                        fechaMostrar.set(Calendar.MILLISECOND, 0);
                        mostrarFechaSeleccionada(fechaMostrar);
                        actualizoLista = true;
                    }
                    Log.d(TAG, "Datos guardados recibidos: " + arguments.getBundle("datos"));

                    //"consumo" la selección del paciente
                    ((PrincipalActivity) getActivity()).seleccionPacienteEnviada = false;
                    adapter.setFecha(fechaMostrar);
                    adapter.retomarCreacionTurno(p, arguments.getBundle("datos"));
                }else{
                    Log.d(TAG, "Seleccion Paciente es false");
                }
            }else{
                if(seleccionEnviada){
                    //Había pedido un paciente pero no respondió
                    Log.d(TAG, "No hubo respuesta en a la petición de paciente, pero sí hubo argumentos");
                    ((PrincipalActivity) getActivity()).seleccionPacienteEnviada = false;
                    ((PrincipalActivity) getActivity()).mostrarCanceloTurno();
                }
                Calendar f = (Calendar) arguments.get("fechaMostrar");
                if(f != null){
                    mostrarFechaSeleccionada(f);
                    actualizoLista = true;
                }
            }
        }else{
            if(seleccionEnviada){
                //Había enviado la selección de paciente pero le llegaron argumentos nulos, probablemente porque volvió a MiAgneda por el drawer
                Log.d(TAG, "No hubo respuesta en a la petición de paciente");
                ((PrincipalActivity) getActivity()).seleccionPacienteEnviada = false;
                ((PrincipalActivity) getActivity()).mostrarCanceloTurno();
            }
        }

        listTurnos = new ArrayList<>();
        Calendar h = (Calendar) fechaMostrar.clone();
        while (h.get(Calendar.HOUR_OF_DAY) <= horaFin) {
            listTurnos.add(new Turno());
            h.add(Calendar.MINUTE, tiempoTurno);
        }
        h = null;

        if (!actualizoLista) {
            mostrarFechaSeleccionada(fechaMostrar);
        }

        buildDatePicker();

        verCalendarioFAB = ((PrincipalActivity) getActivity()).fabPrincipal;//view.findViewById(R.id.fab_mi_agenda);
        verCalendarioFAB.setColorFilter(getResources().getColor(R.color.colorTextSecondary));
        verCalendarioFAB.setImageDrawable(getActivity().getDrawable(R.drawable.ic_calendar_24px));
        verCalendarioFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getParentFragmentManager(), datePicker.toString());
            }
        });

        return view;
    }

    private boolean estaMostrandoFecha(Calendar fecha) {
        return fecha.get(Calendar.DAY_OF_MONTH) == fechaMostrar.get(Calendar.DAY_OF_MONTH) &&
                fecha.get(Calendar.MONTH) == fechaMostrar.get(Calendar.MONTH) &&
                fecha.get(Calendar.YEAR) == fechaMostrar.get(Calendar.YEAR);
    }


    private void buildDatePicker() {

        builder = MaterialDatePicker.Builder.datePicker();
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);
        builder.setTitleText(R.string.fecha_ver_turno);
        builder.setSelection(Calendar.getInstance().getTimeInMillis() + TimeZone.getDefault().getRawOffset());
        datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                //Le resto el offset de la time zone del celular para que la fecha sea correcta.
                fechaMostrar.setTimeInMillis(selection - TimeZone.getDefault().getRawOffset());
                //pongo la hora coincidente con el primer turno para después poder sumar minutos.
                fechaMostrar.set(Calendar.HOUR_OF_DAY, horaInicio);
                fechaMostrar.set(Calendar.MINUTE, 0);
                fechaMostrar.set(Calendar.SECOND, 0);
                fechaMostrar.set(Calendar.MILLISECOND, 0);

                mostrarFechaSeleccionada(fechaMostrar);
            }
        });

    }

    private void mostrarFechaSeleccionada(Calendar fechaSeleccionada) {
        Log.d(TAG, "Fecha seleccionada: " + fechaSeleccionada.getTime() + " TimeZone: " + fechaSeleccionada.getTimeZone().getDisplayName());
        Log.d(TAG, "Fecha mostrar: " + fechaMostrar.getTime() + " Milis: " + fechaMostrar.getTimeInMillis());
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        fechaMostrar = fechaSeleccionada;
        //buscar en firebase los turnos de esta fecha.
        DatosFirestore.getInstance().getTurnosEnFecha(fechaSeleccionada.getTime(), handler);
    }

    void seleccionarPaciente(Calendar hora, Bundle datosAguardar){
        ((PrincipalActivity) getActivity()).seleccionarPaciente(hora, datosAguardar);
    }

    public void guardarTurno(Turno t, String idPaciente) {
        recyclerView.setVisibility(View.INVISIBLE);
        nuevoTurno = true;
        DatosFirestore.getInstance().saveTurno(t, idPaciente, handler);
    }

    public void verPaciente(String idPaciente) {
        Intent i = new Intent(getActivity(), VerPacienteActivity.class);
        i.putExtra("idPaciente", idPaciente);
        getActivity().startActivity(i);
    }

    public void borrarTurno(Turno turno) {
        DatosFirestore.getInstance().borrarTurno(turno, handler);
    }

    public void actualizarTurno(Turno turno) {
        Log.d(TAG, "Turno a actualizado: "+turno);
        DatosFirestore.getInstance().saveTurno(turno, turno.getIdPaciente(), handler);
    }
}