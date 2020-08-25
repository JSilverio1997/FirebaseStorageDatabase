package Database_Lista_Funcionarios;

import android.os.Parcel;
import android.os.Parcelable;

public class Funcionario implements Parcelable {

    private String nome;
    private String id;
    private String urlimagem;
    private int idade;
    private String id_empresa;

    public Funcionario() {
    }

    public Funcionario(String nome, int idade, String urlimagem) {
        this.nome = nome;
        this.urlimagem = urlimagem;
        this.idade = idade;
    }

    public Funcionario(String id, String nome, int idade, String urlimagem) {
        this.nome = nome;
        this.id = id;
        this.urlimagem = urlimagem;
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlimagem() {
        return urlimagem;
    }

    public void setUrlimagem(String urlimagem) {
        this.urlimagem = urlimagem;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }


    public String getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(String id_empresa) {
        this.id_empresa = id_empresa;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nome);
        dest.writeString(this.id);
        dest.writeString(this.urlimagem);
        dest.writeInt(this.idade);
        dest.writeString(this.id_empresa);
    }

    protected Funcionario(Parcel in) {
        this.nome = in.readString();
        this.id = in.readString();
        this.urlimagem = in.readString();
        this.idade = in.readInt();
        this.id_empresa = in.readString();
    }

    public static final Creator<Funcionario> CREATOR = new Creator<Funcionario>() {
        @Override
        public Funcionario createFromParcel(Parcel source) {
            return new Funcionario(source);
        }

        @Override
        public Funcionario[] newArray(int size) {
            return new Funcionario[size];
        }
    };
}
