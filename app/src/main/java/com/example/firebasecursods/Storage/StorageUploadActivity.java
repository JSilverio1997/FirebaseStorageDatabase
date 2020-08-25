package com.example.firebasecursods.Storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.Utilidades.DialogAlerta;
import com.example.firebasecursods.Utilidades.DialogProgress;
import com.example.firebasecursods.Utilidades.Permissao;
import com.example.firebasecursods.Utilidades.Utilidades;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class StorageUploadActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private Button button_Enviar;
    private Uri uri_imagem;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_upload_activity);

        imageView = (ImageView)findViewById(R.id.imageView_StorageUpload);
        button_Enviar = (Button)findViewById(R.id.button_StorageUpload_Enviar);

        button_Enviar.setOnClickListener(this);

        permissao();

        storage = FirebaseStorage.getInstance();
    }

    private void permissao()
    {
        String permissoes [] = new String[]{
                Manifest.permission.CAMERA,
        };

        Permissao.permissao(this, 0, permissoes);
    }

    @Override
    public void onClick(View v)
    {
      switch(v.getId())
      {
          case R.id.button_StorageUpload_Enviar:
             //upload_Imagem_1();
              //upload_Imagem_2();
              //upload_Imagem_3();
              button_Upload();
              break;
      }
    }

    private void button_Upload()
    {
        boolean conexaoInternet = Utilidades.statusInternet(getBaseContext());

        if(conexaoInternet)
        {
            if(imageView.getDrawable() != null)
            {
                upload_Imagem_3();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Não Existe Nnehuma Imagem ainda para realizar o Upload.  ",
                               Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getBaseContext(), "Erro - Conexão - Verifique se você possuí  acesso a Internet. ",
                           Toast.LENGTH_LONG).show();
        }
    }

    private void item_camera()
    {
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            DialogAlerta  dialogAlertaPermissionCamera =  new DialogAlerta("Permissão Necessária", "Acesse as configurações do aplicativo.");
            dialogAlertaPermissionCamera.show(getSupportFragmentManager(),"1");

        }
        else
        {
            obterImagem_Camera();
        }
    }

    private void upload_Imagem_1()
    {
        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        StorageReference storageReference = storage.getReference("Upload").child("Imagens");

        StorageReference nome_imagem = storageReference.child("CursoFirebaseDS"+ System.currentTimeMillis()+ ".jpg");

        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        UploadTask uploadTask = nome_imagem.putBytes(bytes.toByteArray());

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                boolean resposta = task.isSuccessful();

                if(resposta)
                {
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Sucesso ao realizar upload",
                                   Toast.LENGTH_LONG).show();
                }
                else
                {
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao realizar Upload. ",
                                   Toast.LENGTH_LONG).show();

                }            }
        });
    }

    private void upload_Imagem_2()
    {
        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        StorageReference reference = storage.getReference().child("Upload").child("Imagens");

        final StorageReference nome_imagem = reference.child("CursoFirebaseUpload"+ System.currentTimeMillis()+ ".jpg");

        BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        UploadTask uploadTask = nome_imagem.putBytes(bytes.toByteArray());

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                return nome_imagem.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                boolean resposta = task.isSuccessful();

                if (resposta)
                {
                    dialogProgress.dismiss();

                    Uri uri = (Uri) task.getResult();

                    String url_imagem = uri.toString();

                    DialogAlerta alerta = new DialogAlerta("URL_IMAGEM", url_imagem);
                    alerta.show(getSupportFragmentManager(), "3");

                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Sucesso ao fazer Upload. ",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao fazer Upload. ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void upload_Imagem_3()
    {
        final DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        StorageReference reference = storage.getReference().child("Upload").child("Imagens");

        final StorageReference nome_imagem = reference.child("CursoFirebaseUpload"+ System.currentTimeMillis()+ ".jpg");

        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource)
                    {
                        Toast.makeText(getBaseContext(), "Erro ao Trnasformar Imagem. ",
                                       Toast.LENGTH_LONG).show();

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource)
                    {

                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                            resource.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

                            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes.toByteArray());

                        try {
                              bytes.close();
                            }
                            catch(Exception e)
                           {
                            e.printStackTrace();
                           }

                            UploadTask uploadTask = nome_imagem.putStream(inputStream);
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                            {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                                {
                                    return nome_imagem.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task)
                                {
                                    boolean resposta = task.isSuccessful();

                                    if (resposta)
                                    {
                                        dialogProgress.dismiss();

                                        Uri uri = (Uri) task.getResult();

                                        String url_imagem = uri.toString();

                                        DialogAlerta alerta = new DialogAlerta("URL_IMAGEM", url_imagem);
                                        alerta.show(getSupportFragmentManager(), "3");

                                        dialogProgress.dismiss();
                                        Toast.makeText(getBaseContext(), "Sucesso ao fazer Upload. ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        dialogProgress.dismiss();
                                        Toast.makeText(getBaseContext(), "Erro ao fazer Upload. ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            return false;
                    }
                }).submit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_storage_upload, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_galeria:
                obterImagem_Galeria();
                break;

            case R.id.item_camera:
                item_camera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void obterImagem_Galeria()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(intent, "Escolha a Imagem"), 0);
    }

   private void obterImagem_Camera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String nomeImagem = diretorio.getPath()+ "/" + "CursoImagem"+System.currentTimeMillis()+".jpg";

        File file = new File(nomeImagem);

        String autorizacao = "com.example.firebasecursods";

        uri_imagem = FileProvider.getUriForFile(getBaseContext(), autorizacao, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_imagem);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode ==  RESULT_OK)
        {
            if(requestCode == 0)
            {
                if(data != null)
                {
                    uri_imagem = data.getData();

                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            Toast.makeText(getBaseContext(), "Erro ao Carregar Imagem", Toast.LENGTH_LONG).show();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    }).into(imageView);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Erro ao Carregar Imagem", Toast.LENGTH_LONG).show();
                }
            }
            else if(requestCode == 1)
            {
                if(uri_imagem != null)
                {
                    Glide.with(getBaseContext()).asBitmap().load(uri_imagem).listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                            Toast.makeText(getBaseContext(), "Erro ao Carregar Imagem", Toast.LENGTH_LONG).show();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                            return false;
                        }
                    }).into(imageView);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Erro ao Tirar Foto.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int result : grantResults)
        {
            if(result == PackageManager.PERMISSION_DENIED)
            {
                Toast.makeText(this, "Aceite as permissões para o aplicativo acessar a sua câmera.",
                        Toast.LENGTH_LONG).show();
                finish();

                break;
            }
        }
    }
}
