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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    //TODO eliminar esto
    public void reestucturarBaseDeDatos() {
        final CollectionReference coleccionPacientes = datosUsuario.collection(idColeccionPacientes);

        Log.d("ActualizacionBD", "Empieza la actualizacion de la base de datos");
//        db.collectionGroup(idColeccionTurnos).whereEqualTo("propietario", datosUsuario.getId()).get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()) {
//                            Turno t = document.toObject(Turno.class);
//                            Map<String, Object> updates = new HashMap<>();
//                            updates.put("dniPaciente", FieldValue.delete());
//                            datosUsuario.collection(idColeccionPacientes).document(t.getIdPaciente()).collection(idColeccionTurnos).document(t.getId()).update(updates);
//                        }
//                    }
//                });
//        coleccionPacientes.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> taskPacientes) {
//                        if (taskPacientes.isSuccessful()) {
//                            for (DocumentSnapshot document : taskPacientes.getResult()) {
//                                Paciente p = document.toObject(Paciente.class);
//                                if (p.getId() == null) {
//                                    p.setId(p.getDni());
//                                    Log.d("ActualizacionBD", "Se actualiza el paciente: "+p.getDni());
//                                    coleccionPacientes.document(p.getId()).set(p);
//                                    final CollectionReference coleccionTurnos = coleccionPacientes.document(p.getId()).collection(idColeccionTurnos);
//                                    coleccionTurnos.get()
//                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<QuerySnapshot> taskTrunos) {
//                                                    if(taskTrunos.isSuccessful()){
//                                                        for(DocumentSnapshot document: taskTrunos.getResult()){
//                                                            Turno t = document.toObject(Turno.class);
//                                                            if(t.getIdPaciente() == null){
//                                                                t.setIdPaciente(t.getDniPaciente());
//                                                            }
//                                                            Log.d("ActualizacionBD", "Se actualiza el turno: "+t.getId()+" del paciente: "+t.getIdPaciente());
//                                                            coleccionTurnos.document(t.getId()).set(t);
//                                                        }
//                                                    }
//                                                }
//                                            });
//                                }
//
//                            }
//                        }
//                    }
//                });
    }


    public void getAllPacientes(final Handler handler) {
        //obtiene todos los pacientes del usuario actual
        CollectionReference pacientesCollection = datosUsuario.collection(idColeccionPacientes);
        pacientesCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Paciente> listaPacientes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Paciente p = document.toObject(Paciente.class);
                            listaPacientes.add(p);
                            Log.d(TAG, "Paciente obtenido: " + p.toString());
                        }
                        Message m = Message.obtain();
                        m.what = GETALL_PACIENTES;
                        m.obj = listaPacientes;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al obtener los pacientes.", e);
                        Message m = Message.obtain();
                        m.what = ERROR_GETALL_PACIENTES;
                        handler.sendMessage(m);
                    }
                });
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

//    public void savePaciente(final Paciente p, String dniSinEditar, final Handler handler) {
//        //Guarda el paciente en la base de datos
//        final CollectionReference collectionPacientes = datosUsuario.collection(idColeccionPacientes);
//
//
//        if(!dniSinEditar.isEmpty()){
//            collectionPacientes.document(dniSinEditar).delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            guardarPaciente(p, handler, collectionPacientes);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d(TAG, "savePaciente: Error al eliminar el paciente.", e);
//                            Message m = Message.obtain();
//                            m.what = ERROR_SAVE_PACIENTE;
//                            handler.sendMessage(m);
//                        }
//                    });
//        }
//        else{
//           collectionPacientes.document(p.getDni()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//               @Override
//               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                   if(task.isSuccessful()){
//                       if(task.getResult().exists()){
//                        // mostrar error
//                           Log.d(TAG, "El paciente ya existe");
////                           guardarPaciente(p, handler, collectionPacientes);
//                       }
//                       else {
//                           guardarPaciente(p, handler, collectionPacientes);
//                       }
//                   }
//                   else {
//                       Message m = Message.obtain();
//                       m.what = ERROR_SAVE_PACIENTE;
//                       handler.sendMessage(m);
//                   }
//                   }
//               });
//           }
//    }

    public void guardarPaciente(Paciente p, final Handler handler) {
        CollectionReference collectionPacientes = datosUsuario.collection(idColeccionPacientes);

        if (p.getId() == null || p.getId().isEmpty()) {
            p.setId(collectionPacientes.document().getId());
        }

        collectionPacientes.document(p.getId()).set(p)
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
                        Log.d(TAG, "guardarPaciente: Error al guardar el paciente.", e);
                        Message m = Message.obtain();
                        m.what = ERROR_SAVE_PACIENTE;
                        handler.sendMessage(m);
                    }
                });
    }

    public void getAllTurnosDePaciente(String idPaciente, final Handler handler) {
        //Obtiene todos los turnos de un paciente específico;
        final DocumentReference documentPaciente = datosUsuario.collection(idColeccionPacientes).document(idPaciente);
        final CollectionReference collectionTurnos = documentPaciente.collection(idColeccionTurnos);

        collectionTurnos.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Turno> listaTurnos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Turno t = document.toObject(Turno.class);
                            listaTurnos.add(t);
                            Log.d(TAG, "getAllTurnosDePaciente: Turno obtenido: " + t.toString());
                        }
                        Message m = Message.obtain();
                        m.what = GET_TURNOS_PACIENTE;
                        m.obj = listaTurnos;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "getAllTurnosDePaciente: Error", e);
                        Message m = Message.obtain();
                        m.what = ERROR_GET_TURNOS_PACIENTE;
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

    public void actualizarImagenDePaciente(String urlImagen, final String idPaciente) {
        CollectionReference collectionPacientes = datosUsuario.collection(idColeccionPacientes);

        Map<String, Object> map = new HashMap<>();
        map.put("fotoURL", urlImagen);

        collectionPacientes.document(idPaciente).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Se actualizó la url de la imagen del paciente " + idPaciente);
                        } else {
                            Log.d(TAG, "Se produjo un error al actualizar la url de la imagen del paciente " + idPaciente);
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

        Query query = datosUsuario.collection(idColeccionPacientes).orderBy("apellido", Query.Direction.ASCENDING).orderBy("nombre", Query.Direction.ASCENDING);

        return query;
    }

    public Query getPacientesPorBusqueda(String categoriaBusqueda, String busquedaUsuario, String busquedaMax) {
        Query query;

        query = datosUsuario.collection(idColeccionPacientes).whereGreaterThanOrEqualTo(categoriaBusqueda, busquedaUsuario).whereLessThan(categoriaBusqueda, busquedaMax);

        Log.d(TAG, "En getPAcientesPOrBusqueda");
        if (categoriaBusqueda == "apellido") {
            query.orderBy(categoriaBusqueda).orderBy("nombre");
        }
        return query;
    }

    public Query getAllTurnosDePacienteQuery(String idPaciente) {
        final DocumentReference documentPaciente = datosUsuario.collection(idColeccionPacientes).document(idPaciente);
        final CollectionReference collectionTurnos = documentPaciente.collection(idColeccionTurnos);

        return collectionTurnos.orderBy("fecha", Query.Direction.DESCENDING);
    }
}
