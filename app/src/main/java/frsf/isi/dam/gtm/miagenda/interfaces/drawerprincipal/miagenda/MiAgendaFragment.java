package frsf.isi.dam.gtm.miagenda.interfaces.drawerprincipal.miagenda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import frsf.isi.dam.gtm.miagenda.R;

public class MiAgendaFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mi_agenda, container, false);
    }
}