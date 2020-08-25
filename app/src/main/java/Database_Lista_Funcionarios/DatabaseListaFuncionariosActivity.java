package Database_Lista_Funcionarios;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.firebasecursods.Utilidades.DialogProgress;
import com.example.firebasecursods.Utilidades.PdfCreator;
import com.example.firebasecursods.Utilidades.Utilidades;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import Database_Lista_Empresa.Empresa;

public class DatabaseListaFuncionariosActivity extends AppCompatActivity implements View.OnClickListener, RecyclerView_ListaFuncionarios.ClickFuncionario {

    private CardView cardView_Funcionario;
    private LinearLayout linearLayout_Funcionario;
    private ImageView imageView_Funcionario_LimparCampos;
    private EditText editText_NomeFuncionario;
    private EditText editText_IdadeFuncionario;
    private Button button_SalvarFuncionario;
    private ImageView imageView_Funcionario_Galeria;
    private RecyclerView recyclerView_Funcionario;
    private RecyclerView_ListaFuncionarios recyclerViewListaFuncionarios;

    private List<Funcionario> funcionarios = new ArrayList<Funcionario>();

    private FirebaseDatabase database;
    private FirebaseStorage storage;

   private  Uri uri_imagem = null;

    private ChildEventListener childEventListener;
    private DatabaseReference reference_database;
    private List<String> keys = new ArrayList<String>();

    private Empresa empresa;
    private DialogProgress dialogProgress;
    private boolean imagem_selecionada = false;

    private boolean firebaseOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_funcionarios_activity);

        cardView_Funcionario = (CardView)findViewById(R.id.cardView_Database_ListaFuncionario);
        linearLayout_Funcionario = (LinearLayout)findViewById(R.id.linearLayout_Database_ListaFuncionario);
        imageView_Funcionario_LimparCampos = (ImageView)findViewById(R.id.imageView_Database_Funcionario_LimparCampos);
        editText_NomeFuncionario = (EditText)findViewById(R.id.editText_Database_Lista_Funcionarios_Nome);
        editText_IdadeFuncionario = (EditText)findViewById(R.id.editText_Database_Lista_Funcionarios_Idade);
        button_SalvarFuncionario = (Button)findViewById(R.id.button_Database_Lista_Funcionarios_Salvar);
        imageView_Funcionario_Galeria = (ImageView)findViewById(R.id.imageView_Database_ListaFuncionario_galeria);
        recyclerView_Funcionario =(RecyclerView)findViewById(R.id.recycleView_Database_ListaFuncionario);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        imageView_Funcionario_LimparCampos.setOnClickListener(this);
        button_SalvarFuncionario.setOnClickListener(this);
        imageView_Funcionario_Galeria.setOnClickListener(this);

        empresa = getIntent().getParcelableExtra("empresa");

        ativarFirebaseOffline();
        iniciarRecyclerView();

    }

    private void iniciarRecyclerView()
    {
        recyclerView_Funcionario.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewListaFuncionarios = new RecyclerView_ListaFuncionarios(getBaseContext(), funcionarios, this);
        recyclerView_Funcionario.setAdapter(recyclerViewListaFuncionarios);
    }

    @Override
    public void onClick(View v)
    {
            switch(v.getId())
            {
                case R.id.imageView_Database_Funcionario_LimparCampos:
                    limparCampos();
                    break;

                case R.id.button_Database_Lista_Funcionarios_Salvar:
                    mainSalvar();
                    break;

                case R.id.imageView_Database_ListaFuncionario_galeria:
                    obterImagem_Galeria();
                    break;


            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_database_lista_funcionario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.item_mostrar_layout:
                linearLayout_Funcionario.setVisibility(View.VISIBLE);
                return true;

            case R.id.item_esconder_layout:
                linearLayout_Funcionario.setVisibility(View.GONE);
                return true;

            case R.id.item_criar_pdf_funcionarios:
                itemCriarPdf();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void click_Funcionario(Funcionario funcionario) {
      funcionario.setId_empresa(empresa.getId());
      Intent intent = new Intent(getBaseContext(), DatabaseListaFuncionariosDadosActivity.class);
      intent.putExtra("funcionario", funcionario);

      startActivity(intent);
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
                            imagem_selecionada = true;
                            return false;
                        }
                    }).into(imageView_Funcionario_Galeria);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Erro ao Carregar Imagem", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void limparCampos()
    {
        editText_NomeFuncionario.setText(null);
        editText_IdadeFuncionario.setText(null);
        uri_imagem = null;
        imageView_Funcionario_Galeria.setImageResource(R.drawable.ic_galeria_24dp);
        editText_NomeFuncionario.requestFocus();
        imagem_selecionada = false;
    }

    private void mainSalvar() {
        String nome = editText_NomeFuncionario.getText().toString();
        String aux_idade = editText_IdadeFuncionario.getText().toString();

        if (Utilidades.verificarCampos(getBaseContext(), nome, aux_idade))
        {
            int idade = Integer.parseInt(aux_idade);

            if(imagem_selecionada)
            {
                salvarDadosStorage(nome, idade);
            }
            else
            {
                DialogAlerta dialogAlerta = new DialogAlerta("Image- Erro", "É obrigatório escolher uma imagem para " +
                                                             "Salvar os dados." );

                dialogAlerta.show(getSupportFragmentManager(), "2");
            }

        }

    }

    private void salvarDadosStorage(final String nome, final int idade)
    {
        dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "1");

        StorageReference reference = storage.getReference().child("BD").child("Empresas").child(empresa.getNome());

        final StorageReference nome_imagem = reference.child("CursoFirebase"+ System.currentTimeMillis()+ ".jpg");

        Glide.with(getBaseContext()).asBitmap().load(uri_imagem).apply(new RequestOptions().override(1024, 768))
                .listener(new RequestListener<Bitmap>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource)
                    {
                        dialogProgress.dismiss();
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

                                    salvarDadosDatabase(nome, idade, url_imagem);

                                    Toast.makeText(getBaseContext(), "Sucesso ao fazer Upload - Storage. ",
                                            Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    dialogProgress.dismiss();
                                    Toast.makeText(getBaseContext(), "Erro ao fazer Upload - Storage. ",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        return false;
                    }
                }).submit();
    }

    private void salvarDadosDatabase(String nome, int idade, String url_imagem)
    {
        dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "1");

        Funcionario funcionario = new Funcionario(nome, idade, url_imagem);

        DatabaseReference reference = database.getReference().child("BD").child("Empresas").child(empresa.getId())
                                      .child("Funcionarios");

        reference.push().setValue(funcionario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                boolean func_salvo = task.isSuccessful();

                if(func_salvo)
                {
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Sucesso ao fazer Upload - Database.",
                                   Toast.LENGTH_LONG).show();
                }
                else
                {
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao fazer Upload - Database.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void ouvinte()
    {
       // Query por um valor especifico
       /* Query query = database.getReference().child("BD").child("Empresas").child(empresa.getId())
               .child("Funcionarios").orderByChild("nome").equalTo("Luizito"); */

       // Buscar por nomes que começa com Z e diante
       /* Query query = database.getReference().child("BD").child("Empresas").child(empresa.getId())
                .child("Funcionarios").orderByChild("nome").startAt("Z"); */

        // Buscar por idade que começa a partir de 48
        /* Query query = database.getReference().child("BD").child("Empresas").child(empresa.getId())
                .child("Funcionarios").orderByChild("idade").startAt(48); */

        // Buscar por idade que começa a partir de 1 até 22 anos
        /* Query query = database.getReference().child("BD").child("Empresas").child(empresa.getId())
                .child("Funcionarios").orderByChild("idade").startAt(1).endAt(22); */

        // Buscar por idade que tem menos ou 22 anos
        /* Query query = database.getReference().child("BD").child("Empresas").child(empresa.getId())
                .child("Funcionarios").orderByChild("idade").endAt(22); */

       reference_database = database.getReference().child("BD").child("Empresas").child(empresa.getId())
                .child("Funcionarios");

        if(childEventListener == null)
        {
            childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {

                    String key = snapshot.getKey();

                    keys.add(key);

                    Funcionario funcionario = snapshot.getValue(Funcionario.class);
                    funcionario.setId(key);

                    funcionarios.add(funcionario);

                    recyclerViewListaFuncionarios.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();
                    int index = key.indexOf(key);

                    Funcionario funcionario = snapshot.getValue(Funcionario.class);
                    funcionario.setId(key);

                    funcionarios.set(index, funcionario);
                    recyclerViewListaFuncionarios.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    String key = snapshot.getKey();
                    int index = keys.indexOf(key);

                    funcionarios.remove(index);
                    keys.remove(index);

                    recyclerViewListaFuncionarios.notifyItemRemoved(index);
                    recyclerViewListaFuncionarios.notifyItemChanged(index, funcionarios.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            // query.addChildEventListener(childEventListener);
            reference_database.addChildEventListener(childEventListener);
        }


    }

    private void itemCriarPdf()
    {
        if(funcionarios.size() > 0)
        {
            GerarPdf gerarPdf = new GerarPdf();
            gerarPdf.execute();
        }
        else
        {
            DialogAlerta dialogAlerta = new DialogAlerta("Erro ao gerar PDF.", "Não existe funcionários cadastrados" +
                                                         " para gerar o relatório. ");
            dialogAlerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void gerarPDF()
    {
        try
        {
            ListItem item = new ListItem();

            File diretorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String nome_arquivo = diretorio.getPath() + "/" + "RelatórioFuncionário" + System.currentTimeMillis() + ".pdf";

            File pdf = new File(nome_arquivo);

            OutputStream outputStream = new FileOutputStream(pdf);

            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            PdfCreator event = new PdfCreator();
            writer.setBoxSize("box_a", new Rectangle(36, 54, 788));
            writer.setPageEvent(event);

            document.open();

            Font font = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Font font_dados = new Font(Font.FontFamily.HELVETICA, 24, Font.NORMAL);

             Paragraph paragraph = new Paragraph("Relatório De Funcionários ", font);
             paragraph.setAlignment(Element.ALIGN_CENTER);

             document.add(paragraph);

             PdfPTable table = new PdfPTable(2);
             table.setWidthPercentage(100);
             table.setSpacingBefore(30f);
             table.setSpacingAfter(30f);

             for(Funcionario func : funcionarios)
             {
               String dados = "Nome: "+func.getNome()+"\n\n"+ "Idade: "+func.getIdade();
               PdfPCell cell = new PdfPCell(new Paragraph(dados, font_dados));
               cell.setPadding(10);
               cell.setBorder(Rectangle.NO_BORDER);
               cell.setBorder(PdfPCell.NO_BORDER);

               table.addCell(cell);
              }

              document.add(table);

              document.close();
              visualizarPdf(pdf);

        }
        catch(FileNotFoundException e)
        {
            Toast.makeText(getBaseContext(),"Erro Ao Tentar Recuperar diretório ou Caminho.",
                    Toast.LENGTH_LONG).show();
        }
        catch(DocumentException e)
        {
            Toast.makeText(getBaseContext(), "Erro Ao Tentar Abrir, Escrever ou Fechar o Documento.",
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

    @Override
    protected void onStart()
    {
        super.onStart();

        ouvinte();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(childEventListener != null)
        {
            // reference_database.removeEventListener(childEventListener);
        }
    }

    private class GerarPdf extends AsyncTask<Void, Void, Void>{

        private DialogProgress dialogProgress;

        @Override
        protected void onPreExecute()
        {
            dialogProgress = new DialogProgress();
            dialogProgress.show(getSupportFragmentManager(), "2");

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            gerarPDF();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            dialogProgress.dismiss();
        }

    }

    private void ativarFirebaseOffline()
    {
        try
        {
            if(!firebaseOffline)
            {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                firebaseOffline = true;
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getBaseContext(), "Erro ao Executar A função para Ativar modo Offline",
                    Toast.LENGTH_LONG).show();

        }

    }

}
