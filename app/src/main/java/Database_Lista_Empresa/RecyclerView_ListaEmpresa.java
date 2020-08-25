package Database_Lista_Empresa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasecursods.R;

import java.util.List;

public class RecyclerView_ListaEmpresa extends RecyclerView.Adapter<RecyclerView_ListaEmpresa.ViewHolder> {

    private Context context;
    private List<Empresa> empresas;
    private ClickEmpresa clickEmpresa;

    public RecyclerView_ListaEmpresa(Context context, List<Empresa> empresas, ClickEmpresa clickEmpresa)
    {
        this.context = context;
        this.empresas = empresas;
        this.clickEmpresa = clickEmpresa;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.database_lista_empresa_conteudo_recycle_view,
                                        parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Empresa empresa = empresas.get(position);

        holder.textView_Nome.setText(empresa.getNome());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             clickEmpresa.click_Empresa(empresa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public interface ClickEmpresa{
        void click_Empresa(Empresa empresa);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView textView_Nome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.cardView_ListaEmpresa_Item_CardView);
            textView_Nome = (TextView)itemView.findViewById(R.id.textView_ListaEmpresa_Nome);
        }
    }
}
