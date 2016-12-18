package banco;


public class BD {
    
    public final String JBDC_DRIVER = "org.postgresql.Driver";
    public final String URL_BANCO = "jdbc:postgresql://localhost/";
    private String usario;
    private String senha;
    private String database;
    
    public String getUsario() {
        return usario;
    }

    public void setUsario(String usario) {
        this.usario = usario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
    
    public String getUrlConexao() {
       return URL_BANCO + getDatabase();
    }
       
}