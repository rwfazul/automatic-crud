/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import banco.BD;
import banco.BDConexao;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JOptionPane;
import model.Banco;
import model.Coluna;
import model.ForeignKey;
import model.Tabela;


public class MapeamentoController {

    private BD dados;
    private Banco database;
    private Connection conexao;
    private DatabaseMetaData metadata;
    
    public MapeamentoController(BD dados) {
        setDados(dados);
        database = new Banco(dados.getDatabase());
        mapearBanco();
    }

    public BD getDados() {
        return dados;
    }

    public Banco getDatabase() {
        return database;
    }

    public void setDatabase(Banco database) {
        this.database = database;
    }

    public void setDados(BD dados) {
        this.dados = dados;
    }

    public Connection getConexao() {
        return conexao;
    }

    public void setConexao(Connection conexao) {
        this.conexao = conexao;
    }

    public DatabaseMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(DatabaseMetaData metadata) {
        this.metadata = metadata;
    }

    public void mapearBanco() {
        setConexao(BDConexao.getConexao(getDados()));
        try {
            setMetadata(getConexao().getMetaData());
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(null, "Erro ao obter metadata.",
                    "Erro banco de dados", JOptionPane.DEFAULT_OPTION);
            System.err.println(ex.getMessage());
        }
        try {
            mapearTabelas();
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(null, "Erro ao obter tabelas.",
                    "Erro banco de dados", JOptionPane.DEFAULT_OPTION);
            System.err.println(ex.getMessage());
        }
    }

    public void mapearTabelas() throws SQLException {
        String table[] = {"TABLE"};
        ResultSet rs = null;
        rs = metadata.getTables(null, null, null, table);
        while (rs.next()) 
            database.getTabelas().add(new Tabela(rs.getString("TABLE_NAME")));
        
        Collection<Thread> threadsTabelas = new ArrayList<>();
        Collection<Tabela> tabelas = database.getTabelas();
        for (Tabela tbl : tabelas) {
            Collection<ForeignKey> fks = mapearForeignKey(tbl);
            mapearColunas(tbl, mapearPrimaryKey(tbl), fks, nomesFks(fks));
        }

        referenciaFks();
    }

    public void mapearColunas(Tabela tbl, Collection<String> pks, Collection<ForeignKey> fks, 
            Collection<String> nomesFks) {
        try {
            ResultSet rs = metadata.getColumns(null, null, tbl.getNome(), null);
            while (rs.next()) {
                Coluna col = new Coluna();
                String nome = rs.getString("COLUMN_NAME");
                col.setPrimaryKey(pks.contains(nome));
                if (nomesFks.contains(nome)) {
                    for (ForeignKey fk : fks) {
                        if (fk.getNome().equals(nome)) {
                            col.setInfoFk(fk);
                            break;
                        }
                    }
                    col.setForeignKey(true);
                    col.setTipo(col.getInfoFk().getNomeTblReferenciada().substring(0,1).toUpperCase()
                            + col.getInfoFk().getNomeTblReferenciada().substring(1));
                } else {
                    col.setForeignKey(false);
                    col.setTipo(tipoBancoParaTipoJava(rs.getString("TYPE_NAME")));
                }
                col.setNome(nome);
                col.setNotNull(rs.getString("NULLABLE").equals("0"));
                tbl.getColunas().add(col);
                
            }
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(null, "Erro ao obter colunas da tabela" + 
                    tbl.getNome() + ".", "Erro banco de dados", JOptionPane.DEFAULT_OPTION);
            System.err.println(ex.getMessage());
        }
    }

    public Collection mapearPrimaryKey(Tabela tbl) {
        Collection<String> primaryKeys = new ArrayList<>();
        try {
            ResultSet rs = metadata.getPrimaryKeys(null, null, tbl.getNome());
            while (rs.next())
                primaryKeys.add(rs.getString("COLUMN_NAME"));
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(null, "Erro ao chaves primárias da tabela "
                    + tbl.getNome() + ".", "Erro banco de dados", JOptionPane.DEFAULT_OPTION);
            System.err.println(ex.getMessage());
        }
        
        return primaryKeys;
    }

    public Collection mapearForeignKey(Tabela tbl) {
        Collection<ForeignKey> foreignKeys = new ArrayList<>();
        try {
            ResultSet rs = metadata.getImportedKeys(null, null, tbl.getNome());
            while (rs.next()) {
                ForeignKey fk = new ForeignKey();
                fk.setNome(rs.getString("FKCOLUMN_NAME"));
                fk.setNomeTblReferenciada(rs.getString("PKTABLE_NAME"));
                fk.setColunaReferenciada(rs.getString("PKCOLUMN_NAME"));
                foreignKeys.add(fk);
            }
        } catch (SQLException ex) {
            JOptionPane.showConfirmDialog(null, "Erro ao chaves estrangeiras da tabela "
                    + tbl.getNome() + ".", "Erro banco de dados", JOptionPane.DEFAULT_OPTION);
            System.err.println(ex.getMessage());
        }
        
        return foreignKeys;
    }
    
    public Collection nomesFks(Collection<ForeignKey> fks) {
        Collection<String> nomes = new ArrayList<>();
        for (ForeignKey fk : fks) 
            nomes.add(fk.getNome());
        
        return nomes;
    }
    
    public void referenciaFks() {
        Collection<Tabela> tbls = database.getTabelas();
        for (Tabela tbl : tbls) {
            for (Coluna c : tbl.getColunas()) {
                if (c.isForeignKey()) {
                    for (Tabela busca : tbls) {
                        if (busca.getNome().equals(c.getInfoFk().getNomeTblReferenciada())) {
                            c.getInfoFk().setTblReferenciada(busca);
                            break;
                        }
                    }                    
                }
            }
        }
    }
    
    public void testImpressao() {
        Collection<Tabela> tbls = database.getTabelas();
        if (database.getNome() != null) 
            System.out.println("BANCO DE DADOS: " + database.getNome());
        for (Tabela t : tbls) {
            System.out.println("\nTABELA: " + t.getNome());
            Collection<Coluna> colunas = t.getColunas();
            for (Coluna c : colunas) {
                System.out.println(c.getTipo() + "\t" + c.getNome() + "\tPK:" 
                        + c.isPrimaryKey() + "\tFK: " + c.isForeignKey() 
                        +"\tNot Null:" + c.isNotNull());
                if (c.isForeignKey()) {
                    ForeignKey fk = c.getInfoFk();
                    System.out.println("\tFOREIGN KEY: " + fk.getNome());
                    Tabela tabelaReferencia = fk.getTblReferenciada();
                    System.out.println("\tREFERENCIA: Tabela " + tabelaReferencia.getNome()
                            + "\tColuna " + fk.getColunaReferenciada());
                    Collection<Coluna> colunasReferencia = tabelaReferencia.getColunas();
                    System.out.print("\tCOLUNAS DA TABELA REFERENCIADA: ");
                    for (Coluna colunasRef : colunasReferencia) {
                        System.out.print(colunasRef.getNome() + " ");
                    }
                    System.out.println("");
                }
            }
            System.out.println("\n-------------------------");
        }
    }

    private String tipoBancoParaTipoJava(String tipo) {
        String tipoJava = null;
        
        switch (tipo) {
            case "varchar":
            case "char":
            case "text":
                tipoJava = "String";
                break;
            case "bool":
                tipoJava = "Boolean";
                break;
            case "float4":
            case "real":
                tipoJava = "Float";
                break;
            case "float8":
                tipoJava = "Double";
                break;
            case "int":
            case "serial":
            case "int2":
            case "int4":
            case "int8":
            case "integer":
            case "serial4":
            case "serial8":
                tipoJava = "Integer";
                break;
            case "date":
            case "timestamp":
            case "time":
                tipoJava = "Calendar";
                break;
        }
        
        return tipoJava;
    }

}
