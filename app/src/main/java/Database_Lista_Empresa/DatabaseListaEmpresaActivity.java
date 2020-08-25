package Database_Lista_Empresa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.firebasecursods.Database.Gerente;
import com.example.firebasecursods.R;
import com.example.firebasecursods.Utilidades.Utilidades;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Database_Lista_Funcionarios.DatabaseListaFuncionariosActivity;

public class DatabaseListaEmpresaActivity extends AppCompatActivity implements RecyclerView_ListaEmpresa.ClickEmpresa, Runnable {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private RecyclerView_ListaEmpresa recyclerView_listaEmpresa;
    private List<Empresa> empresas = new ArrayList<Empresa>();

    private ChildEventListener childEventListener;
    private DatabaseReference reference_database;
    private List<String> keys = new ArrayList<String>();

    private boolean firebaseOffline = false;

    private Handler handler;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_lista_empresa_activity);

        recyclerView = (RecyclerView)findViewById(R.id.recycleView_Database_Empresa_Lista);

        database = FirebaseDatabase.getInstance();

        handler = new Handler();
        thread = new Thread(this);

        thread.start();

        ativarFirebaseOffline();
        iniciarRecyclerView();
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(10000);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    conexaoFirebaseBD();
                }
            });
        }
        catch (InterruptedException e)
        {
                e.printStackTrace();
        }
    }

    private void iniciarRecyclerView()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_listaEmpresa = new RecyclerView_ListaEmpresa(getBaseContext(), empresas, this);
        recyclerView.setAdapter(recyclerView_listaEmpresa);

    }

    @Override
    public void click_Empresa(Empresa empresa) {

        Toast.makeText(getBaseContext(), "Nome da Empresa: "+ empresa.getNome() + "\n" +
                       "\n" + "Nome da Pasta: "+ empresa.getId(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getBaseContext(), DatabaseListaFuncionariosActivity.class);
        intent.putExtra("empresa", empresa);

        startActivity(intent);

    }

    private void ouvinte()
    {

        reference_database = database.getReference().child("BD").child("Empresas");

        if(childEventListener == null)
        {
            childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {

                    String key = snapshot.getKey();

                    keys.add(key);

                    Empresa empresa = snapshot.getValue(Empresa.class);
                    empresa.setId(key);

                    empresas.add(empresa);

                    recyclerView_listaEmpresa.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();
                    int index = key.indexOf(key);

                    Empresa empresa = snapshot.getValue(Empresa.class);
                    empresa.setId(key);

                    empresas.set(index, empresa);
                    recyclerView_listaEmpresa.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    String key = snapshot.getKey();
                    int index = keys.indexOf(key);

                    empresas.remove(index);
                    keys.remove(index);

                    recyclerView_listaEmpresa.notifyItemRemoved(index);
                    recyclerView_listaEmpresa.notifyItemChanged(index, empresas.size());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String chave = snapshot.getKey();
                    Gerente gerente = snapshot.getValue(Gerente.class);

                    Log.i("Teste","Gerente ID: "+ chave + "\n" + "Nome do Gerente: "+ gerente.getNome()+ " Ramo Movido");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            reference_database.addChildEventListener(childEventListener);
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

    private void  conexaoFirebaseBD()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(".info/connected");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean conexao =  snapshot.getValue(Boolean.class);
                boolean conexaoInternet = Utilidades.statusInternet(getBaseContext());

                if(conexao)
                {
                    Toast.makeText(getBaseContext(), "Temos Conexão com BD. "
                                  ,Toast.LENGTH_LONG).show();
                }
                else
                {
                  if(conexaoInternet)
                  {
                      Toast.makeText(getBaseContext(), "Bloqueio ao acessar o BD."
                                    ,Toast.LENGTH_LONG).show();
                  }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            reference_database.removeEventListener(childEventListener);
        }
    }

}
