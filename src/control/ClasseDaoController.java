/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import model.Atributo;
import model.Classe;
import model.Coluna;
import model.Metodo;
import model.Parametro;
import model.Tabela;



public class ClasseDaoController {
    
    private MapeamentoController map;
    private String nomePacote;
    private Collection<Classe> classesDao = new ArrayList<>();
    private Integer inicioPadrao;
    
    public ClasseDaoController(MapeamentoController map, String nomePacote) {
        setMap(map);
        setNomePacote(nomePacote);
        criarClasses();
    }

    public MapeamentoController getMap() {
        return map;
    }

    public void setMap(MapeamentoController map) {
        this.map = map;
    }

    public String getNomePacote() {
        return nomePacote;
    }

    public void setNomePacote(String nomePacote) {
        this.nomePacote = nomePacote;
    }

    public Collection<Classe> getClassesDao() {
        return classesDao;
    }

    public void setClassesDao(Collection<Classe> classesDao) {
        this.classesDao = classesDao;
    }

    public Integer getInicioPadrao() {
        return inicioPadrao;
    }

    public void setInicioPadrao(Integer inicioPadrao) {
        this.inicioPadrao = inicioPadrao;
    }
    
    public void criarClasses() {
        Collection<Tabela> tbls = map.getDatabase().getTabelas();
        Collection<Thread> threadsTabelas = new ArrayList<>();
        for (Tabela t : tbls) {
            String nome = t.getNome();
            Classe c = new Classe(t.getNome().substring(0,1).toUpperCase() + t.getNome().substring(1)); 
            c.setNomeExtra(t.getNome().substring(0,1).toUpperCase() + t.getNome().substring(1) + "DAO" 
                    + " extends Registros<" + t.getNome().substring(0,1).toUpperCase() 
                            + t.getNome().substring(1) + ">");
            c.setPacote(getNomePacote());
            
            Runnable r = () -> {
                adicionarImports(c);
                criarSqls(t, c);
                setInicioPadrao(1);
                criarMetodos(t, c);
                getClassesDao().add(c);          
            };
            Thread threadTabela = new Thread(r);
            threadsTabelas.add(threadTabela);           
        }        
        
        for (Thread threadTabela : threadsTabelas) 
            threadTabela.run();
    }
    
    public void adicionarImports(Classe c) {
        c.getImports().add("banco.Registros");
        c.getImports().add("java.sql.PreparedStatement");
        c.getImports().add("java.sql.ResultSet");
        c.getImports().add("java.sql.SQLException");
        c.getImports().add("java.util.ArrayList");
        c.getImports().add("java.util.Collection");
    }
    
    public void criarSqls(Tabela t, Classe c){
        Metodo construtor = new Metodo();
        construtor.setVisibilidade("public");
        construtor.setRetorno(null);
        construtor.setNome(t.getNome().substring(0,1).toUpperCase() + t.getNome().substring(1) + "DAO");

        String nomePk = getNomePk(t);
        StringBuilder allSql = new StringBuilder();
        allSql.append(getSqlInsercao(t));
        allSql.append(getSqlAlteracao(t, nomePk));
        allSql.append(getSqlExclusao(t, nomePk));
        allSql.append(getSqlBuscaPk(t, nomePk));
        allSql.append(getSqlBuscaTodos(t, nomePk));
        construtor.setTxt(allSql.toString());
        c.getMetodos().add(construtor);
    }
    
    public String getSqlInsercao(Tabela t) {
        StringBuilder b = new StringBuilder();
        String nomePk = getNomePk(t);
        b.append("\t\tsetSqlInsercao(\"INSERT INTO ").append(t.getNome());
        b.append(" (");
        for (Iterator<Coluna> iterator = t.getColunas().iterator(); iterator.hasNext();) {
            Coluna c = iterator.next();
            if (!nomePk.equals(c.getNome())) {
                b.append(c.getNome());
                if (iterator.hasNext())
                    b.append(", ");
            }
        }
        b.append(") VALUES (");
        Integer qt = t.getColunas().size() - 1;
        for (int i = 0; i < qt; i++) {
            b.append("?");
            if (i + 1 < qt)
                b.append(", ");
        }
        b.append(")\");\n\t\t");
            
        return b.toString();
    }
            
    public String getSqlAlteracao(Tabela t, String nomePk) {
        StringBuilder b = new StringBuilder();
        b.append("setSqlAlteracao(\"UPDATE ").append(t.getNome()).append(" SET ");
        
        for (Iterator<Coluna> iterator = t.getColunas().iterator(); iterator.hasNext();) {
            Coluna c = iterator.next();
            if (!nomePk.equals(c.getNome())) {
                b.append(c.getNome()).append(" = ?");
                
                if (iterator.hasNext())
                    b.append(", ");
            }
        }
        b.append(" WHERE ").append(nomePk).append(" = ?\");\n\t\t");
            
        return b.toString();
    }
    
    public String getSqlExclusao(Tabela t, String nomePk) {
        StringBuilder b = new StringBuilder();
        b.append("setSqlExclusao(\"DELETE FROM ").append(t.getNome());
        b.append(" WHERE ").append(nomePk).append(" = ?\");\n\t\t");        
 
        return b.toString();
    }
    
    public String getSqlBuscaPk(Tabela t, String nomePk) {
        StringBuilder b = new StringBuilder();
        b.append("setSqlBuscaChavePrimaria(\"SELECT * FROM ").append(t.getNome());
        b.append(" WHERE ").append(nomePk).append(" = ?\");\n\t\t");        
 
        return b.toString();
    }

    public String getSqlBuscaTodos(Tabela t, String nomePk) {
        return "setSqlBuscaTodos(\"SELECT * FROM " + t.getNome() + "\");";
    }
    
    public String getNomePk(Tabela t) {
        String nomePk = null;
        for (Coluna col : t.getColunas()) {
            if (col.isPrimaryKey()) {
                nomePk = col.getNome();
                break;
            }
        }
        
        return nomePk;
    }
    
    public Coluna getPk(Tabela t) {
        Coluna pk = null;
        for (Coluna col : t.getColunas()) {
            if (col.isPrimaryKey()) {
                pk= col;
                break;
            }
        }
        
        return pk;
    }

    public void criarMetodos(Tabela t, Classe c) {        
        String nomeObj = Character.toString(c.getNome().charAt(0)).toLowerCase();
        c.getMetodos().add(getPreencherInsercao(t, c, nomeObj));
        c.getMetodos().add(getPreencherAlteracao(t, c, nomeObj));
        c.getMetodos().add(getPreencherExclusao(t, c, nomeObj));
        c.getMetodos().add(getPreencher(t, c, nomeObj));
        c.getMetodos().add(getPreencherColecao(t, c, nomeObj));
    }
    
    public Metodo getPreencherInsercao(Tabela t, Classe c, String nomeObj) {
        Metodo insercao = new Metodo();
        prototipo(insercao, c, "preencherInsercao", nomeObj);

        Integer pos = 1;
        StringBuilder b = new StringBuilder();
        b.append("\t\t");
        for (Coluna col : t.getColunas()) {
            if (!col.isPrimaryKey()) {
                if (pos > 1)
                    b.append("\n\t\t");
                b.append("ps.set");
                String tipo = col.getTipo();
                if (col.isForeignKey()) {
                    String ref = col.getInfoFk().getColunaReferenciada();
                    for (Coluna r : col.getInfoFk().getTblReferenciada().getColunas()) {
                        if (r.getNome().equals(ref)) 
                           tipo = r.getTipo();
                    }
                    if ("Integer".equals(tipo))
                        b.append("Int(");
                    else if ("Calendar".equals(tipo))
                        b.append("Date(");
                    else
                        b.append(tipo).append("(");
                    
                    b.append(pos.toString()).append(", ").append(nomeObj).append(".get");
                    b.append(col.getNome().substring(0,1).toUpperCase() 
                            + col.getNome().substring(1)).append("()" + ".get");
                    b.append(col.getInfoFk().getColunaReferenciada().substring(0,1).toUpperCase() 
                            + col.getInfoFk().getColunaReferenciada().substring(1));
                    b.append("());");
                }
                else {
                    if ("Integer".equals(tipo))
                        b.append("Int(");
                    else if ("Calendar".equals(tipo))
                        b.append("Date(");
                    else
                        b.append(tipo).append("(");

                    if (!"Calendar".equals(tipo)) {
                        b.append(pos.toString()).append(", ").append(nomeObj).append(".get");
                        b.append(col.getNome().substring(0,1).toUpperCase() 
                                + col.getNome().substring(1)).append("());");
                    } else {
                        b.append(pos.toString()).append(", " + "new java.sql.Date(");
                        b.append(nomeObj).append(".get").append(
                            col.getNome().substring(0,1).toUpperCase() 
                                    + col.getNome().substring(1)).append("().getTimeInMillis()));");

                        if (!c.getImportOk()) {
                            c.getImports().add("java.util.Calendar");
                            c.setImportOk(true);
                        }
                    }
                }
                pos++;
            }
        }
        
        insercao.setTxt(b.toString());
        
        return insercao;
    }
    
    public Metodo getPreencherAlteracao(Tabela t, Classe c, String nomeObj) {
        Metodo alteracao = new Metodo();
        prototipo(alteracao, c, "preencherAlteracao", nomeObj);
        Integer pos = 1;
        Integer tam = t.getColunas().size();
        StringBuilder b = new StringBuilder();
        b.append("\t\t");
        for (Coluna col : t.getColunas()) {
            if (!col.isPrimaryKey()) {
                if (pos > 1)
                    b.append("\n\t\t");
                b.append("ps.set");
                String tipo = col.getTipo();
                if (col.isForeignKey()) {
                    String ref = col.getInfoFk().getColunaReferenciada();
                    for (Coluna r : col.getInfoFk().getTblReferenciada().getColunas()) {
                        if (r.getNome().equals(ref)) 
                           tipo = r.getTipo();
                    }
                    
                    if ("Integer".equals(tipo))
                        b.append("Int(");
                    else if ("Calendar".equals(tipo))
                        b.append("Date(");
                    else
                        b.append(tipo).append("(");
                    
                    b.append(pos.toString()).append(", ").append(nomeObj).append(".get");
                    b.append(col.getNome().substring(0,1).toUpperCase() 
                            + col.getNome().substring(1)).append("()" + ".get");
                    b.append(col.getInfoFk().getColunaReferenciada().substring(0,1).toUpperCase() 
                            + col.getInfoFk().getColunaReferenciada().substring(1));
                    b.append("());");
                }
                else {
                    if ("Integer".equals(tipo))
                        b.append("Int(");
                    else if ("Calendar".equals(tipo))
                        b.append("Date(");
                    else
                        b.append(tipo).append("(");


                    if (!"Calendar".equals(tipo)) {
                        b.append(pos.toString()).append(", ").append(nomeObj).append(".get");
                        b.append(col.getNome().substring(0,1).toUpperCase() 
                                + col.getNome().substring(1)).append("());");
                    } else {
                        b.append(pos.toString()).append(", " + "new java.sql.Date(");
                        b.append(nomeObj).append(".get").append(
                                col.getNome().substring(0,1).toUpperCase() + col.getNome().substring(1));
                        b.append("().getTimeInMillis()));");
                    }
                }
                pos++;
            } 
        }
        
        Coluna pk = getPk(t);
        b.append("\n\t\tps.set");
        String tipo = pk.getTipo();
        if ("Integer".equals(tipo))
            b.append("Int(");
        else if ("Calendar".equals(tipo))
            b.append("Date(");
        else
            b.append(tipo).append("(");
        b.append(tam.toString()).append(", ").append(nomeObj).append(".get");
        b.append(pk.getNome().substring(0,1).toUpperCase() + pk.getNome().substring(1)).append("());"); 
        alteracao.setTxt(b.toString());
        
        return alteracao;    
    }

    
    public Metodo getPreencherExclusao(Tabela t, Classe c, String nomeObj) {
        Metodo exclusao = new Metodo();
        prototipo(exclusao, c, "preencherExclusao", nomeObj);
        StringBuilder b = new StringBuilder();
        Coluna pk = getPk(t);
        b.append("\t\tps.set");
        String tipo = pk.getTipo();
        if ("Integer".equals(tipo))
            b.append("Int(");
        else if ("Calendar".equals(tipo))
            b.append("Date(");
        else
            b.append(tipo).append("(");
        b.append("1" + ", ").append(nomeObj).append(".get");
        b.append(pk.getNome().substring(0,1).toUpperCase() + pk.getNome().substring(1)).append("());"); 
        exclusao.setTxt(b.toString());
        
        return exclusao;    
    }
    
    public Metodo getPreencher(Tabela t, Classe c, String nomeObj) {
        Metodo preencher = new Metodo();
        prototipo(preencher, c.getNome(), "preencher");
        
        StringBuilder b = new StringBuilder();
        b.append("\t\t").append(c.getNome()).append(" ").append(nomeObj);
        b.append(" = new ").append(c.getNome()).append("();\n\t\t");
        for (Coluna col : t.getColunas()) {
            String tipo = col.getTipo();
            if (col.isForeignKey()) {
                String novoDao = col.getInfoFk().getNomeTblReferenciada() + "dao";
                Atributo adao = new Atributo(null, col.getTipo() + "DAO", novoDao + " = new "
                    + col.getTipo() + "DAO()");
                c.getAtributos().add(adao);
                b.append(nomeObj).append(".set").append(col.getNome().substring(0,1).toUpperCase()
                            + col.getNome().substring(1));
                b.append("((").append((col.getInfoFk().getNomeTblReferenciada()).substring(0,1).toUpperCase()
                            + col.getInfoFk().getNomeTblReferenciada().substring(1)).append(") ");
                b.append(novoDao).append(".buscarChavePrimaria(rs.get");
                String ref = col.getInfoFk().getColunaReferenciada();
                for (Coluna r : col.getInfoFk().getTblReferenciada().getColunas()) {
                    if (r.getNome().equals(ref)) 
                       tipo = r.getTipo();
                }
                if ("Integer".equals(tipo))
                    b.append("Int(");
                else if ("Calendar".equals(tipo))
                    b.append("Date(");
                else
                    b.append(tipo).append("(");
                b.append("\"").append(col.getInfoFk().getNome()).append("\")));\n\t\t");
                
                
            }
            else {
                if ("Calendar".equals(tipo)) {
                    String nomeDate = "dt" + getInicioPadrao().toString();
                    String nomeCal = "cal" + getInicioPadrao().toString();
                    b.append("java.sql.Date ").append(nomeDate).append(" = rs.getDate(\"").append(col.getNome());
                    b.append("\");\n\t\t");
                    b.append("Calendar ").append(nomeCal).append(" = Calendar.getInstance();\n\t\t");
                    b.append(nomeCal).append(".setTime(").append(nomeDate).append(");\n\t\t");
                    b.append(nomeObj).append(".set").append(col.getNome().substring(0,1).toUpperCase()
                            + col.getNome().substring(1));
                    b.append("(").append(nomeCal).append(");\n\t\t");
                    setInicioPadrao(getInicioPadrao() + 1 );
                }
                else {
                    b.append(nomeObj).append(".set").append(col.getNome().substring(0,1).toUpperCase()
                            + col.getNome().substring(1));
                    b.append("(rs.get");
                    if ("Integer".equals(tipo))
                        b.append("Int(");
                    else
                        b.append(tipo).append("(");
                    b.append("\"").append(col.getNome()).append("\"));\n\t\t");
                }
            }
        }
        
        
        b.append("return ").append(nomeObj).append(";");
        preencher.setTxt(b.toString());
        
        return preencher;    
    }
        
        
    public Metodo getPreencherColecao(Tabela t, Classe c, String nomeObj) {
        Metodo preencher = new Metodo();
        String retornoMetodo = "Collection<" + c.getNome() + ">";
        prototipo(preencher, retornoMetodo, "preencherColecao");
        StringBuilder b = new StringBuilder();
        b.append("\t\tCollection<").append(c.getNome());
        b.append("> retorno = new ArrayList<>();");
        b.append("\n\t\twhile(rs.next())");
        b.append("\n\t\t\tretorno.add(preencher(rs));");
        b.append("\n\t\treturn retorno;");
        preencher.setTxt(b.toString());
        
        return preencher;    
    }

    public void prototipo(Metodo m, Classe c, String nomeMetodo, String nomeObj) {
        m.setNotacao("@Override");
        m.setVisibilidade("protected");
        m.setRetorno("void");
        m.setNome(nomeMetodo);
        m.getParametros().add(new Parametro("PreparedStatement", "ps"));
        m.getParametros().add(new Parametro(c.getNome(), nomeObj));
        m.setExtra("throws SQLException");
    }
    
    public void prototipo(Metodo m, String retorno, String nomeMetodo) {
        m.setNotacao("@Override");
        m.setVisibilidade("protected");
        m.setRetorno(retorno);
        m.setNome(nomeMetodo);
        m.getParametros().add(new Parametro("ResultSet", "rs"));
        m.setExtra("throws SQLException");
    }      

}
