package Database_Lista_Funcionarios;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.firebasecursods.Database.Gerente;
import com.example.firebasecursods.R;
import com.example.firebasecursods.Utilidades.DialogAlerta;
import com.example.firebasecursods.Utilidades.Utilidades;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseListaFuncionariosDadosActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView_Database_Dados_Funcionario;
    private ProgressBar progressBar_Database_Dados_Funcionario;

    private EditText  editText_Database_Dados_Funcionario_Nome;
    private EditText editText_Database_Dados_Funcionario_Idade;

    private Button button_Database_Dados_Funcionario_Alterar;
    private Button button_Database_Dados_Funcionario_Remover;

    private Funcionario funcionario_Selecionado;

    private Uri uri_imagem;
    private boolean imagem_alterada;

    private FirebaseDatabase database;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_funcionarios_dados_activity);

        imageView_Database_Dados_Funcionario = (ImageView)findViewById(R.id.imageView_Database_Dados_Funcionario);
        progressBar_Database_Dados_Funcionario = (ProgressBar)findViewById(R.id.progressBar_Database_Dados_Funcionario);

        editText_Database_Dados_Funcionario_Nome = (EditText)findViewById(R.id.editText_Database_Dados_Funcionario_Nome);
        editText_Database_Dados_Funcionario_Idade = (EditText)findViewById(R.id.editText_Database_Dados_Funcionario_Idade);

        button_Database_Dados_Funcionario_Alterar = (Button)findViewById(R.id.button_Database_Dados_Funcionario_Alterar);
        button_Database_Dados_Funcionario_Remover = (Button)findViewById(R.id.button_Database_Dados_Funcionario_Remover);

        imageView_Database_Dados_Funcionario.setOnClickListener(this);
        button_Database_Dados_Funcionario_Alterar.setOnClickListener(this);
        button_Database_Dados_Funcionario_Remover.setOnClickListener(this);

        funcionario_Selecionado = getIntent().getParcelableExtra("funcionario");

        editText_Database_Dados_Funcionario_Nome.setText(funcionario_Selecionado.getNome());
        editText_Database_Dados_Funcionario_Idade.setText(funcionario_Selecionado.getIdade() + "");

        carregarDadosFuncionario();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

    }

    private void carregarDadosFuncionario()
    {
        progressBar_Database_Dados_Funcionario.setVisibility(View.VISIBLE);

        Picasso.with(getBaseContext()).load(funcionario_Selecionado.getUrlimagem()).into(imageView_Database_Dados_Funcionario, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                progressBar_Database_Dados_Funcionario.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar_Database_Dados_Funcionario.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
      switch (v.getId())
      {
          case R.id.imageView_Database_Dados_Funcionario:
              obterImagem_Galeria();
              break;

          case R.id.button_Database_Dados_Funcionario_Alterar:
              alterarFuncionario();
             break;

          case  R.id.button_Database_Dados_Funcionario_Remover:
              removerFuncionario();
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

    private void obterImagem_Galeria()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(intent, "Escolha a Imagem"), 0);
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
                            imagem_alterada = true;
                            return false;
                        }
                    }).into(imageView_Database_Dados_Funcionario);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Erro ao Carregar Imagem", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void alterarFuncionario()
    {
        String nome = editText_Database_Dados_Funcionario_Nome.getText().toString();
        String aux_idade = editText_Database_Dados_Funcionario_Idade.getText().toString();
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());

        if(conexao_internet)
        {
            if (Utilidades.verificarCampos(getBaseContext(), nome, aux_idade))
            {
                int idade = Integer.parseInt(aux_idade);

                if (!nome.equals(funcionario_Selecionado.getNome()) || idade != funcionario_Selecionado.getIdade() || imagem_alterada)
                {

                    if (imagem_alterada)
                    {
                        removerImagem(nome, idade);
                    }
                    else
                    {
                        alterarDados(nome, idade, funcionario_Selecionado.getUrlimagem());
                    }
                }
            }
            else
            {
                DialogAlerta alerta = new DialogAlerta("Atenção", "Por favor preencha os campos para alterar os dados.");
                alerta.show(getSupportFragmentManager(), "2");
            }
        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro", "O Dispositivo não possuí acesso a rede, por favor verifique sua conexão.");
            alerta.show(getSupportFragmentManager(), "2");
        }

    }

    private void alterarDados(String nome, int idade, String url_imagem)
    {

       DatabaseReference reference = database.getReference().child("BD").child("Empresas")
                                             .child(funcionario_Selecionado.getId_empresa())
                                             .child("Funcionarios");

       Funcionario funcionario = new Funcionario(nome, idade, url_imagem);

       Map<String, Object> atualizacao = new HashMap<>();

       atualizacao.put(funcionario_Selecionado.getId(), funcionario);

       reference.updateChildren(atualizacao).addOnCompleteListener(new OnCompleteListener<Void>() {
       @Override
       public void onComplete(@NonNull Task<Void> task)
       {
         boolean alterado = task.isSuccessful();

         if (alterado)
         {
           Toast.makeText(getBaseContext(), "Alterado com Sucessso. ",
                          Toast.LENGTH_LONG).show();
           finish();
         }
         else
         {
            Toast.makeText(getBaseContext(), "Erro ao Alterar. ",
                           Toast.LENGTH_LONG).show();
         }

       }});
     }

    private void removerImagem(final String nome, final int idade) {
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());
        if (conexao_internet)
        {
            String url = funcionario_Selecionado.getUrlimagem();

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    boolean resposta = task.isSuccessful();

                    if (resposta)
                    {
                        salvarDadosStorage(nome, idade);
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Erro ao tentar excluir a  imagem.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            DialogAlerta alerta = new DialogAlerta("Erro de Conexão", "Verifique se o dispositivo possui acesso a Internet.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void salvarDadosStorage(final String nome, final int idade)
    {
        StorageReference reference = storage.getReference().child("BD").child("Empresas").child(funcionario_Selecionado.getId_empresa());

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
                                    Uri uri = (Uri) task.getResult();

                                    String url_imagem = uri.toString();

                                    alterarDados(nome, idade, url_imagem);

                                    Toast.makeText(getBaseContext(), "Sucesso ao fazer Upload. ",
                                            Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(getBaseContext(), "Erro ao fazer Upload. ",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        return false;
                    }
                }).submit();

    }

    private void removerFuncionario()
    {
        boolean conexao_internet = Utilidades.statusInternet(getBaseContext());

        if (conexao_internet)
        {
            String url = funcionario_Selecionado.getUrlimagem();

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    boolean resposta = task.isSuccessful();

                    if (resposta)
                    {
                        removerDadosFuncionario();
                        finish();
                    }
                    else
                    {
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

    private void removerDadosFuncionario()
    {
        if(Utilidades.statusInternet(getBaseContext()))
        {
          DatabaseReference reference = database.getReference().child("BD").child("Empresas")
                                        .child(funcionario_Selecionado.getId_empresa()).child("Funcionarios");

            reference.child(funcionario_Selecionado.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
          {
          @Override
          public void onComplete(@NonNull Task<Void> task)
          {
              boolean excluido = task.isSuccessful();

              if (excluido)
              {
                 Toast.makeText(getBaseContext(), "O Funcionário foi excluído com Sucessso. ",
                                Toast.LENGTH_LONG).show();
                 finish();
              }
              else
               {
                 Toast.makeText(getBaseContext(), "Erro ao Excluir os Dados do Funcionário. ",
                                Toast.LENGTH_LONG).show();
               }
          }});

        }
        else
        {
          Toast.makeText(getBaseContext(), "O Dispositivo não possuí conexão a Internet.",
                        Toast.LENGTH_LONG).show();
        }
    }

    private void compartilhar()
    {
        if(imageView_Database_Dados_Funcionario.getDrawable() != null)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");

            BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView_Database_Dados_Funcionario.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Curso Firebase Storage e com.example.firebasecursods.Database", null);

            Uri uri = Uri.parse(path);

            intent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(Intent.createChooser(intent, "Compartilhar Imagem do Funcionário."));

        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Erro ao Compartilhar", "Não possui nenhuma imagem para compartilhamento.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void gerarPDF()
    {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(25f);

        try
        {

            if(imageView_Database_Dados_Funcionario.getDrawable() != null)
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

                Paragraph paragraph = new Paragraph("Dados do Funcionario -  " + funcionario_Selecionado.getNome(), font);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                item.add(paragraph);
                document.add(paragraph);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView_Database_Dados_Funcionario.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                Image image = Image.getInstance(bytes.toByteArray());
                image.scaleAbsolute(200f, 200f);
                image.setAlignment(Element.ALIGN_CENTER);
                item.add(image);
                table.addCell(image);

                Font font_dados = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
                String dados = "Nome: " + funcionario_Selecionado.getNome() + "\n\n"
                             + "Idade: " + funcionario_Selecionado.getIdade();

                PdfPCell cell = new PdfPCell(new Paragraph(dados, font_dados));
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(PdfPCell.NO_BORDER);

                table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
                table.addCell(cell);

                document.add(table);
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
