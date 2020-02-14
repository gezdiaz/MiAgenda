package frsf.isi.dam.gtm.miagenda.datos;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import frsf.isi.dam.gtm.miagenda.R;

public class ArchivosCloudStorage {

    public static final int CARGA_IMAGEN = 1;
    public static final int ERROR_CARGA_IMAGEN = -1;


    private static final String TAG = "ArchivosCloudStorage";
    private static ArchivosCloudStorage instance;


    private String imagenPerfilString = "ImagenPerfil";
    StorageReference archivosUsuario;

    private ArchivosCloudStorage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            archivosUsuario = FirebaseStorage.getInstance().getReference().child(user.getUid());
        } else {
            Log.wtf(TAG, "Usuario no logueado, no debería pasar esto.");
        }
    }

    public static ArchivosCloudStorage getInstance(){
        if(instance == null){
            instance = new ArchivosCloudStorage();
        }
        return instance;
    }

    public static void resetIntance(){
        instance = null;
    }

    public void saveImageEnPaciente(final String dniPaciente, File imagen, final Handler handler){
        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(imagen);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "No se encontró la imagen a cargar", e);
            return;
        }
        StorageReference carpetaPaciente = archivosUsuario.child(dniPaciente);
        StorageReference rutaImagen = carpetaPaciente.child(imagenPerfilString);
        rutaImagen.putStream(fileInputStream)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Se cargó la imagen correctamente en el paciente con dni: "+dniPaciente);
                        Message m = Message.obtain();
                        m.what = CARGA_IMAGEN;
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error al cargar la imagen en el paciente con dni: "+dniPaciente, e);
                        Message m = Message.obtain();
                        m.what = ERROR_CARGA_IMAGEN;
                        handler.sendMessage(m);
                    }
                });
    }
    public void saveImageEnPaciente(final String dniPaciente, Bitmap imagen, final Handler handler, Context context){


        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("carga_imagen",
                    "Carga imagen",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canal de notificaciones de carga de imágenes de pacientes");
            notificationManager.createNotificationChannel(channel);
        }

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "carga_imagen");
        notificationBuilder.setContentTitle("Cargando imagen")
                .setContentText("Cargando imagen de perfil del paciente con dni: "+dniPaciente)
                .setSmallIcon(R.drawable.ic_calendar_24px)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setProgress(100, 0, false);
        final int notificationID = 123;
        notificationManager.notify(notificationID, notificationBuilder.build());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        StorageReference carpetaPaciente = archivosUsuario.child(dniPaciente);
        final StorageReference rutaImagen = carpetaPaciente.child(imagenPerfilString);
        rutaImagen.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Se cargó la imagen correctamente en el paciente con dni: "+dniPaciente);
                        rutaImagen.getDownloadUrl();
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            DatosFirestore.getInstance().actualizarImagenDePaciente(task.getResult().toString(), dniPaciente);
                                        }
                                    }
                                });
                        Message m = Message.obtain();
                        m.what = CARGA_IMAGEN;
                        notificationBuilder.setContentText("Se completó la carga de la imagen")
                                .setProgress(0,0,false)
                                .setOngoing(false);
                        notificationManager.notify(notificationID, notificationBuilder.build());
                        handler.sendMessage(m);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error al cargar la imagen en el paciente con dni: "+dniPaciente, e);
                        Message m = Message.obtain();
                        m.what = ERROR_CARGA_IMAGEN;
                        notificationBuilder.setContentText("Error al cargar la imagen")
                                .setProgress(0,0,false)
                                .setOngoing(false);
                        notificationManager.notify(notificationID, notificationBuilder.build());
                        handler.sendMessage(m);
                        handler.sendMessage(m);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int)((taskSnapshot.getBytesTransferred()*1.0 / taskSnapshot.getTotalByteCount()*1.0) * 100);
                        notificationBuilder.setProgress(100, progress, false);
                        notificationManager.notify(notificationID, notificationBuilder.build());
                    }
                });
    }

}
