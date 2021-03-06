package frsf.isi.dam.gtm.miagenda.datos;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import frsf.isi.dam.gtm.miagenda.entidades.Paciente;
import frsf.isi.dam.gtm.miagenda.entidades.Turno;

public class DatosFirestore {

    public static final int GETALL_PACIENTES = 1;
    public static final int ERROR_GETALL_PACIENTES = -1;
    public static final int GET_PACIENTE = 2;
    public static final int ERROR_GET_PACIENTE = -2;
    public static final int GET_TURNOS_PACIENTE = 3;
    public static final int ERROR_GET_TURNOS_PACIENTE = -3;
    public static final int SAVE_PACIENTE = 4;
    public static final int ERROR_SAVE_PACIENTE = -4;
    public static final int SAVE_TURNO = 5;
    public static final int ERROR_SAVE_TURNO = -5;
    public static final int GETALL_TURNOS = 6;
    public static final int ERROR_GETALL_TURNOS = -6;
    public static final int ELIMINACION_TURNO = 7;
    public static final int ERROR_ELIMINACION_TURNO = -7;

    private static DatosFirestore instance;
    private static final String TAG = "DatosFirestore";

    public static final String idColeccionUsuarios = "usuarios";
    public static final String idColeccionPacientes = "pacientes";
    public static final String idColeccionTurnos = "turnos";

    private DocumentReference datosUsuario;
    private FirebaseFirestore db;

    public static DatosFirestore getInstance() {
        if (instance == null) {
            instance = new DatosFirestore();
        }
        Log.d(TAG, "id usuario actual: " + (FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "Error usuario no logueado"));
        Log.d(TAG, "datosUsuario Actual: " + instance.datosUsuario.getId());
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private DatosFirestore() {
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual != null) {
            db = FirebaseFirestore.getInstance();
            datosUsuario = db.collection(idColeccionUsuarios).document(usuarioActual.getUid());
            Log.d(TAG, "Base de datos inicializada para el usuario: " + usuarioActual.getEmail());
            Log.d(TAG, "datosUsuario: " + datosUsuario.getId());
        } else {
            Log.wtf(TAG, "Error, usuario no logueado. No debería llegar hasta acá");
        }
    }

    public void getPacienteById(String idPaciente, final Handler handler) {
        //Obtiene un paciente específico dado su id
        final CollectionReference pacientesCollection = datosUsuario.collection(idColeccionPacientes);
        pacientesCollection.document(idPaciente).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Paciente p = documentSnapshot.toObject(Paciente.class);
                        Log.d(TAG, "getPacienteById: Paciente obtenido: " + p.toString());
                        Message m = Message.obtain();
                        m.what = GET_PACIENTE;
                        m.obj = p;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "getPacienteById: Error al obtener el paciente", e);
                        Message m = Message.obtain();
                        m.what = ERROR_GET_PACIENTE;
                        handler.sendMessage(m);
                    }
                });
    }


    public void guardarPaciente(Paciente p, final boolean actualizarNombre, final Handler handler) {
        final CollectionReference collectionPacientes = datosUsuario.collection(idColeccionPacientes);

        if (p.getId() == null || p.getId().isEmpty()) {
            p.setId(collectionPacientes.document().getId());
        }
        final String idPaciente = p.getId();
        final String nombrePaciente = p.getApellido()+", "+p.getNombre();
        collectionPacientes.document(idPaciente).set(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(actualizarNombre) {
                            final CollectionReference collectionTurnos = collectionPacientes.document(idPaciente).collection(idColeccionTurnos);
                            collectionTurnos.get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            WriteBatch writeBatch = db.batch();
                                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                                DocumentReference documentTurno = collectionTurnos.document(document.getId());
                                                Map<String, Object> updates = new HashMap<>();
                                                updates.put("nombrePaciente", nombrePaciente);
                                                writeBatch.update(documentTurno, updates);
                                            }
                                            writeBatch.commit()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Message m = Message.obtain();
                                                            m.what = SAVE_PACIENTE;
                                                            handler.sendMessage(m);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "guardarPaciente: Error al actualizar los turnos con el WriteBatch.", e);
                                                            Message m = Message.obtain();
                                                            m.what = ERROR_SAVE_PACIENTE;
                                                            handler.sendMessage(m);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "guardarPaciente: Error obtener los turnos para actualizar.", e);
                                            Message m = Message.obtain();
                                            m.what = ERROR_SAVE_PACIENTE;
                                            handler.sendMessage(m);
                                        }
                                    });
                        }else{
                            Message m = Message.obtain();
                            m.what = SAVE_PACIENTE;
                            handler.sendMessage(m);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "guardarPaciente: Error al guardar el paciente.", e);
                        Message m = Message.obtain();
                        m.what = ERROR_SAVE_PACIENTE;
                        handler.sendMessage(m);
                    }
                });
    }

    public void saveTurno(Turno t, String idPaciente, final Handler handler) {
        //Guarda un turno en la colección turnos del paciente especificado.
        final DocumentReference documentPaciente = datosUsuario.collection(idColeccionPacientes).document(idPaciente);
        //La colección turnos de cada paciente tiene como ID "turnos" más el id del usuario al que pertenecen, por elemplo: "turnosByZQ2Xkt1sMpujQd25hDKedlWZa2"
        //Esto es para poder hacer un collectionGroup con todos los turnos de los pacientes de un usuario.
        final CollectionReference collectionTurnos = documentPaciente.collection(idColeccionTurnos);

        if (t.getPropietario() == null || t.getPropietario().isEmpty()) {
            t.setPropietario(datosUsuario.getId());
        }

        if (t.getId() == null || t.getId().isEmpty()) {
            t.setId(collectionTurnos.document().getId());
        }

        if (t.getIdPaciente() == null || t.getIdPaciente().isEmpty()) {
            t.setIdPaciente(idPaciente);
        }

        collectionTurnos.document(t.getId()).set(t)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Message m = Message.obtain();
                        m.what = SAVE_TURNO;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "saveTurno: Error al guardar el turno.", e);
                        Message m = Message.obtain();
                        m.what = ERROR_SAVE_TURNO;
                        handler.sendMessage(m);
                    }
                });

    }

    public void getTurnosEnFecha(Date fecha, final Handler handler) {
        //Obtiene todos los turnos de todos los pacientes del usuario actual en la fecha proporcionada

        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        inicio.setTime(fecha);
        inicio.set(inicio.get(Calendar.YEAR), inicio.get(Calendar.MONTH), inicio.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        fin.setTime(fecha);
        fin.set(fin.get(Calendar.YEAR), fin.get(Calendar.MONTH), fin.get(Calendar.DAY_OF_MONTH), 24, 59, 59);

        Date fechaInicio = inicio.getTime();
        Date fechaFin = fin.getTime();

        db.collectionGroup(idColeccionTurnos)
                .whereEqualTo("propietario", datosUsuario.getId())
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Turno> listaTurnos = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Turno t = document.toObject(Turno.class);
                            listaTurnos.add(t);
                            Log.d(TAG, "Turno obtenido: " + t.toString());
                        }
                        Message m = Message.obtain();
                        m.what = GETALL_TURNOS;
                        m.obj = listaTurnos;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error al obtener todos los turnos", e);
                        Message m = Message.obtain();
                        m.what = ERROR_GETALL_TURNOS;
                        handler.sendMessage(m);
                    }
                });
    }

    public void actualizarImagenDePaciente(String urlImagen, final String idPaciente, final Handler handler) {
        CollectionReference collectionPacientes = datosUsuario.collection(idColeccionPacientes);

        Map<String, Object> map = new HashMap<>();
        map.put("fotoURL", urlImagen);

        collectionPacientes.document(idPaciente).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //En este momento debo mandar un mensaje al handler para que termine la actividad editar paciente porque es cuando el paciente tiene la nueva url de la foto.
                            Message m = Message.obtain();
                            m.what = ArchivosCloudStorage.CARGA_IMAGEN;
                            handler.sendMessage(m);
                            Log.d(TAG, "Se actualizó la url de la imagen del paciente "+idPaciente);
                        }else{
                            Log.d(TAG, "Se produjo un error al actualizar la url de la imagen del paciente "+idPaciente);
                        }
                    }
                });

    }

    public void borrarTurno(final Turno t, final Handler handler) {
        DocumentReference documentTurno = datosUsuario.collection(idColeccionPacientes)
                .document(t.getIdPaciente())
                .collection(idColeccionTurnos)
                .document(t.getId());

        documentTurno.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Message m = Message.obtain();
                        m.what = ELIMINACION_TURNO;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Message m = Message.obtain();
                        m.what = ERROR_ELIMINACION_TURNO;
                        handler.sendMessage(m);
                    }
                });

    }

    public Query getAllPacientesQuery() {

        return datosUsuario.collection(idColeccionPacientes).orderBy("apellido", Query.Direction.ASCENDING).orderBy("nombre", Query.Direction.ASCENDING);
    }

    public Query getPacientesPorBusqueda(String categoriaBusqueda, String busquedaUsuario, String busquedaMax) {
        Query query;

        query = datosUsuario.collection(idColeccionPacientes).whereGreaterThanOrEqualTo(categoriaBusqueda, busquedaUsuario).whereLessThan(categoriaBusqueda, busquedaMax);

        Log.d(TAG, "En getPAcientesPOrBusqueda");
        if(categoriaBusqueda == "apellido"){
            query.orderBy(categoriaBusqueda, Query.Direction.ASCENDING).orderBy("nombre", Query.Direction.ASCENDING);
        }
        else{
            query.orderBy(categoriaBusqueda);
        }
        return query;
    }

    public Query getAllTurnosDePacienteQuery(String idPaciente) {
        final DocumentReference documentPaciente = datosUsuario.collection(idColeccionPacientes).document(idPaciente);
        final CollectionReference collectionTurnos = documentPaciente.collection(idColeccionTurnos);

        return collectionTurnos.orderBy("fecha", Query.Direction.DESCENDING);
    }
}
