package Database_Lista_Empresa;

import android.os.Parcel;
import android.os.Parcelable;

public class Empresa implements Parcelable {

    private String id;
    private String nome;

    public Empresa()
    {

    }

    public Empresa(String nome, String id)
    {
        this.nome = nome;
        this.id = id;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nome);
    }

    protected Empresa(Parcel in) {
        this.id = in.readString();
        this.nome = in.readString();
    }

    public static final Creator<Empresa> CREATOR = new Creator<Empresa>() {
        @Override
        public Empresa createFromParcel(Parcel source) {
            return new Empresa(source);
        }

        @Override
        public Empresa[] newArray(int size) {
            return new Empresa[size];
        }
    };
}
