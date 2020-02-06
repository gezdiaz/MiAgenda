package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import frsf.isi.dam.gtm.miagenda.R;
import frsf.isi.dam.gtm.miagenda.entidades.Turno;

public class MiAgendaFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FloatingActionButton verCalendarioFAB;
    private MaterialDatePicker.Builder<Long> builder;
    private MaterialDatePicker<Long> datePicker;
    private Calendar fechaMostrar;
    private TextView fecha;
    private MaterialButton reservarBtn, modificarBtn, quitarBtn;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_agenda, container, false);

        fechaMostrar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        fecha = view.findViewById(R.id.txt_fecha_hoy);
        mostrarFechaSeleccionada(fechaMostrar);

        recyclerView = view.findViewById(R.id.recycler_turnos);
        //TODO setear lista de turnos

        adapter = new MiAgendaAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buildDatePicker();


        verCalendarioFAB = view.findViewById(R.id.fab_mi_agenda);
//        reservarBtn = view.findViewById(R.id.reservar_turno_btn);
//        modificarBtn = view.findViewById(R.id.modificar_turno_btn);
//        quitarBtn = view.findViewById(R.id.quitar_turno_btn);


        verCalendarioFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getFragmentManager(),datePicker.toString());
            }
        });
        return view;
    }


    private void buildDatePicker(){

        builder = MaterialDatePicker.Builder.datePicker();
        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);
        builder.setTitleText(R.string.fecha_ver_turno);
        builder.setSelection(Calendar.getInstance().getTimeInMillis());
        datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                fechaMostrar.setTimeInMillis(selection);
                mostrarFechaSeleccionada(fechaMostrar);
            }
        });

    }

    private void mostrarFechaSeleccionada(Calendar fechaSeleccionada) {
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String mesSeleccionado = month_date.format(fechaSeleccionada.getTime());
        fecha.setText(fechaSeleccionada.get(Calendar.DATE)+" de " + mesSeleccionado + " de " + fechaSeleccionada.get(Calendar.YEAR));
    }
}