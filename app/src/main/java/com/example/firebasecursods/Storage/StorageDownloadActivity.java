package com.example.firebasecursods.Storage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.R;
import com.example.firebasecursods.Utilidades.DialogAlerta;
import com.example.firebasecursods.Utilidades.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.crypto.ExemptionMechanism;
import javax.xml.transform.Result;

public class StorageDownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Button button_Download;
    private Button button_Remover;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storage_download_activity);

        imageView  = (ImageView)findViewById(R.id.imageView_StorageDownload);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_StorageDownload);
        button_Download = (Button)findViewById(R.id.button_StorageDownload_Download);
        button_Remover = (Button)findViewById(R.id.button_StorageDownload_Remover);

        imageView.setOnClickListener(this);
        progressBar.setOnClickListener(this);
        button_Download.setOnClickListener(this);
        button_Remover.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();

    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.button_StorageDownload_Download:
               // download_imagem_1();
                download_imagem_2();
                break;

            case  R.id.button_StorageDownload_Remover:
                //remover_imagem_1();
                remover_imagem_2();
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_storage_download, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
       switch(item.getItemId())
       {
           case R.id.item_compartilhar:
               compartilhar();

               return true;

           case R.id.item_criar_pdf:
               gerarPDF();

               return true;

       }

       return super.onOptionsItemSelected(item);
    }

    private void download_imagem_1()
    {
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());
        if(conexao_internet) {
            progressBar.setVisibility(View.VISIBLE);
            String url = "https://firebasestorage.googleapis.com/v0/b/fir-cursods-54bb4.appspot.com/o/Imagens%2F399735_405682076165762_56186950_n%20(1).jpg?alt=media&token=af4847be-0b1f-4e17-973e-c9c589198d39";

            /*Picasso.with(getBaseContext()).load(url).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess()
                {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError()
                {
                    progressBar.setVisibility(View.GONE);
                }
            });
             */

            Glide.with(getBaseContext()).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);
        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se o dispositivo possui acesso a Internet.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void download_imagem_2()
    {
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());
        if(conexao_internet)
        {
           StorageReference storageReference = storage.getReference().child("Imagens")
                                                   .child("8.jpg");

           storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
           {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
               boolean resposta = task.isSuccessful();
               progressBar.setVisibility(View.VISIBLE);

               if(resposta)
               {
                 String url = task.getResult().toString();

                 Picasso.with(getBaseContext()).load(url).into(imageView, new Callback() {
                 @Override
                 public void onSuccess()
                            {
                                progressBar.setVisibility(View.GONE);
                            }

                 @Override
                 public void onError()
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                 });
               }
            }
           });
        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se o dispositivo possui acesso a Internet.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void remover_imagem_1()
    {
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());
        if(conexao_internet)
        {
            String url = "https://firebasestorage.googleapis.com/v0/b/fir-cursods-54bb4.appspot.com/o/Imagens%2F399735_405682076165762_56186950_n%20(1).jpg?alt=media&token=af4847be-0b1f-4e17-973e-c9c589198d39";

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    boolean resposta = task.isSuccessful();

                    if (resposta) {
                        imageView.setImageDrawable(null);
                        Toast.makeText(getBaseContext(), "A Imagem foi excluída com sucesso.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Erro ao tentar excluir a  imagem.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se o dispositivo possui acesso a Internet.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void remover_imagem_2()
    {
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());
        if(conexao_internet)
        {
            String nome = "8.jpg";

            StorageReference storageReference = storage.getReference().child("Imagens").child(nome);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    boolean resposta = task.isSuccessful();

                    if(resposta)
                    {
                        imageView.setImageDrawable(null);
                        Toast.makeText(getBaseContext(), "A Imagem foi excluida com sucesso.",
                                       Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Erro ao tentar excluira a imagem.",
                                      Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se o dispositivo possui acesso a Internet.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void compartilhar()
    {
        if(imageView.getDrawable() != null)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");

            BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Curso Firebase Storage e com.example.firebasecursods.Database", null);

            Uri uri = Uri.parse(path);

            intent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(intent, "Compartilhar Imagem do Curso"));

        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro ao Compartilhar", "Não possui nenhuma imagem para compartilhamento.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void gerarPDF()
    {
        try
        {
           if(imageView.getDrawable() != null)
           {
               ListItem item = new ListItem();

               File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

               String nome_arquivo = diretorio.getPath() + "/" + "FirebaseCursoDS" + System.currentTimeMillis() + ".pdf";

               File pdf = new File(nome_arquivo);

               OutputStream outputStream = new FileOutputStream(pdf);

               Document document = new Document();

               PdfWriter writer = PdfWriter.getInstance(document, outputStream);

               writer.setBoxSize("Firebase", new Rectangle(36, 54, 788));

               document.open();

               Font font = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);

               Paragraph paragraph = new Paragraph("Curso Firebase Módulo ll ", font);
               paragraph.setAlignment(Element.ALIGN_CENTER);

               item.add(paragraph);

               BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
               Bitmap bitmap = bitmapDrawable.getBitmap();

               ByteArrayOutputStream bytes = new ByteArrayOutputStream();
               bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

               Image image = Image.getInstance(bytes.toByteArray());
               image.scaleAbsolute(200f, 200f);
               image.setAlignment(Element.ALIGN_CENTER);
               item.add(image);

               Paragraph paragraph_2 = new Paragraph("Hunter X Hunter é um Mangá excelente. ", font);
               paragraph.setAlignment(Element.ALIGN_CENTER);
               item.add(paragraph_2);

               document.add(item);
               document.close();

               visualizarPdf(pdf);
           }
           else
           {
               DialogAlerta alerta = new DialogAlerta("Erro ao Gerar PDF", "Não possui nenhuma imagem para gravar no Pdf.");
               alerta.show(getSupportFragmentManager(), "1");
           }
        }
        catch(FileNotFoundException e)
        {
            Toast.makeText(getBaseContext(),"Erro Ao Tentar Recuperar diretório ou Caminho.",
                           Toast.LENGTH_LONG).show();
        }
        catch(DocumentException e)
        {
            Toast.makeText(getBaseContext(),"Erro Ao Tentar Abrir, Escrever ou Fechar o Documento.",
                    Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            Toast.makeText(getBaseContext(),"Erro No Fluxo de Entrada ou Saida do documento PDF.",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void visualizarPdf(File pdf)
    {
        try
        {

            PackageManager packageManager = getPackageManager();

            Intent itent = new Intent(Intent.ACTION_VIEW);

            itent.setType("application/pdf");

            List<ResolveInfo> lista = packageManager.queryIntentActivities(itent, PackageManager.MATCH_DEFAULT_ONLY);

            if(lista.size() > 0)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(getBaseContext(), "com.example.firebasecursods", pdf);

                intent.setDataAndType(uri, "application/pdf");

                startActivity(intent);
            }
            else
            {
                DialogAlerta dialogAlerta = new DialogAlerta("Erro ao Abrir PDF", "Seu Dispositivo não possui leitor de PDF.");
                dialogAlerta.show(getSupportFragmentManager(), "3");
            }

        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(), "Erro ao Tentar Visualizar PDF",
                          Toast.LENGTH_LONG).show();
        }
    }
}
