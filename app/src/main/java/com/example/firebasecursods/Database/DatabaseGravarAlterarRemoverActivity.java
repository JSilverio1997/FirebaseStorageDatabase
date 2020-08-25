package com.example.firebasecursods.Database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebasecursods.R;
import com.example.firebasecursods.Utilidades.DialogAlerta;
import com.example.firebasecursods.Utilidades.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DatabaseGravarAlterarRemoverActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextNome_Pasta;
    private EditText editTextNome;
    private EditText editTextIdade;

    private Button button_Salvar;
    private Button button_Alterar;
    private Button button_Remover;

    private FirebaseDatabase database;
    private boolean firebaseOffline  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_gravar_alterar_remover_activity);

        editTextNome_Pasta = (EditText)findViewById(R.id.editTextNomePasta);
        editTextNome = (EditText)findViewById(R.id.editTextNome);
        editTextIdade = (EditText)findViewById(R.id.editTextIdade);

        button_Salvar = (Button)findViewById(R.id.buttonSalvar);
        button_Alterar = (Button)findViewById(R.id.buttonAlterar);
        button_Remover = (Button)findViewById(R.id.buttonRemover);

        button_Salvar.setOnClickListener(this);
        button_Alterar.setOnClickListener(this);
        button_Remover.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();

        //ativarFirebaseOffline();

    }

    @Override
    public void onClick(View v)
    {

        switch(v.getId())
        {

            case R.id.buttonSalvar:
                salvarDados();
                break;

            case  R.id.buttonAlterar:
                alterarDados();
                break;

            case  R.id.buttonRemover:
                removerDados();
                break;
        }

    }

    private void salvarDados()
    {
        String nome = editTextNome.getText().toString();
        String aux_idade = editTextIdade.getText().toString();

        if(Utilidades.verificarCampos(getBaseContext(), nome, aux_idade))
        {
            int idade = Integer.parseInt(aux_idade);

            DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

            Gerente gerente = new Gerente(nome, idade, false);

            reference.push().setValue(gerente).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    boolean cadastrado = task.isSuccessful();

                    if(cadastrado)
                    {
                        Toast.makeText(getBaseContext(), "Cadastrado com Sucessso. ",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Erro ao cadastrar. ",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void alterarDados()
    {
        String nome_pasta = editTextNome_Pasta.getText().toString();

        if(!nome_pasta.isEmpty())
        {
            String nome = editTextNome.getText().toString();
            String aux_idade = editTextIdade.getText().toString();

            if (Utilidades.verificarCampos(getBaseContext(), nome, aux_idade))
            {
                int idade = Integer.parseInt(aux_idade);

                DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

                Gerente gerente = new Gerente(nome, idade, false);

                Map<String, Object> atualizacao = new HashMap<>();

                atualizacao.put("1", gerente);

                reference.updateChildren(atualizacao).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        boolean alterado = task.isSuccessful();

                        if (alterado)
                        {
                            Toast.makeText(getBaseContext(), "Alterado com Sucessso. ",
                                    Toast.LENGTH_LONG).show();
                        } else
                            {
                            Toast.makeText(getBaseContext(), "Erro ao Alterar. ",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else
        {
            DialogAlerta alerta = new DialogAlerta("Atenção ", "Preencha o Nome da Pasta para fazer a Alteração.");
            alerta.show(getSupportFragmentManager(), "1");
        }
    }

    private void removerDados()
        {
            String nome_pasta = editTextNome_Pasta.getText().toString();

            if(!nome_pasta.isEmpty())
            {
                if(Utilidades.statusInternet(getBaseContext()))
                {
                    DatabaseReference reference = database.getReference().child("BD").child("Gerentes");

                    reference.child(nome_pasta).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            boolean excluido = task.isSuccessful();

                            if (excluido) {
                                Toast.makeText(getBaseContext(), "Excluído com Sucessso. ",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), "Erro ao Excluir Dados. ",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(getBaseContext(), "O Dispositivo não possuí conexão a Internet.",
                            Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                DialogAlerta alerta = new DialogAlerta("Atenção ", "Preencha o Nome da Pasta para fazer a Remoção.");
                alerta.show(getSupportFragmentManager(), "1");
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
