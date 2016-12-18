/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import model.Classe;
import model.Coluna;
import model.Metodo;
import model.Parametro;
import model.Tabela;



public class ClasseExemploController {
    
    private MapeamentoController map;
    private String nomePacote;
    private Collection<Classe> classesExemplo = new ArrayList<>();
    
    public ClasseExemploController(MapeamentoController map, String nomePacote) {
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

    public Collection<Classe> getClassesExemplo() {
        return classesExemplo;
    }

    public void setClassesExemplo(Collection<Classe> classesExemplo) {
        this.classesExemplo = classesExemplo;
    }

    public void criarClasses() {
        for (Tabela t : map.getDatabase().getTabelas()) {
            String nome = t.getNome();
            Classe c = new Classe((t.getNome() + "Exemplo").substring(0,1).toUpperCase()
                            + (t.getNome() + "Exemplo").substring(1)); 
            c.setPacote(getNomePacote());
            adicionarImports(c);
            criarMain(t, c);
            getClassesExemplo().add(c);
        }
    }

    public void adicionarImports(Classe c) {
        c.getImports().add("java.sql.SQLException");
        c.getImports().add("model.*");
    }

    public void criarMain(Tabela t, Classe c) {
        Metodo m = new Metodo("public static", "void", "main");
        m.getParametros().add(new Parametro("String[]", "args"));
        m.setExtra("throws ClassNotFoundException, SQLException");
        String nomeDao = t.getNome() + "DAO";
        String nomeObjDao =  nomeDao.toLowerCase();
        String nomeEntidade = t.getNome().substring(0,1).toUpperCase()
                            + t.getNome().substring(1);
        StringBuilder txt = new StringBuilder();
        txt.append("\n\t\t").append(nomeDao.substring(0,1).toUpperCase()
                            + nomeDao.substring(1));
        txt.append(" ").append(nomeObjDao);
        txt.append(" = new ").append(nomeDao.substring(0,1).toUpperCase()
                            + nomeDao.substring(1)).append("();\n\n\t\t");
        txt.append(listarTodos(t, nomeEntidade, nomeObjDao, false));
        txt.append(inserir(t, nomeEntidade, nomeObjDao));
        txt.append(listarTodos(t, nomeEntidade, nomeObjDao, true));
        txt.append(getAlterar(t, nomeObjDao, nomeEntidade));
        txt.append(getExcluir(t, nomeObjDao, nomeEntidade));
        m.setTxt(txt.toString());
        c.getMetodos().add(m);
    }

    public String listarTodos(Tabela t, String nomeEntidade, String nomeObjDao, Boolean again) {
        StringBuilder b = new StringBuilder();
        b.append("System.out.println(\"\\nListando ").append(t.getNome());
        if (again)
            b.append(" novamente");
        b.append("...\");\n\t\t");
        b.append("for (").append(nomeEntidade).append(" temp : ");
        b.append(nomeObjDao).append(".buscarTodos())\n\t\t");
        b.append(getGetters(t, nomeObjDao));
        
        return b.toString();
    }

    public String getGetters(Tabela t, String nomeObjDao) {
        Collection<String> listaGet = new ArrayList<>();
        Integer qt = 0;
        for (Coluna c : t.getColunas()) {
            String tipo = c.getTipo();
            if (!c.isForeignKey() && !c.isPrimaryKey() &&
                    (tipo.equals("Integer") || tipo.equals("String"))) {
                listaGet.add(".get" + c.getNome().substring(0,1).toUpperCase()
                            + c.getNome().substring(1));
                qt++;
                if (qt.equals(2))
                   break;
            }
        }
        StringBuilder b = new StringBuilder();
        
        b.append("\tSystem.out.println(");
        for (Iterator<String> iterator = listaGet.iterator(); iterator.hasNext();) {
            String p = iterator.next();
            b.append("temp").append(p).append("()");
            if (iterator.hasNext())
                b.append(" + \" - \" + ");
        }
        b.append(");\n\n\t\t");
        
        return b.toString();
    }

    public String inserir(Tabela t, String nomeEntidade, String nomeObjDao) {
        StringBuilder b = new StringBuilder();
        b.append("System.out.println(\"\\nInserindo ").append(t.getNome());
        b.append("...\");\n\t\t");
        String obj = "obj1";
        b.append(nomeEntidade).append(" ").append(obj);
        b.append(" = new ").append(nomeEntidade).append("();\n\t\t");
        b.append(getSettes(t, nomeEntidade, obj));
        b.append(nomeObjDao).append(".inserir(").append(obj).append(");\n\n\t\t");
        obj = "obj2";
        b.append(nomeEntidade).append(" ").append(obj);
        b.append(" = new ").append(nomeEntidade).append("();\n\t\t");
        b.append(getSettes(t, nomeEntidade, obj));
        b.append(nomeObjDao).append(".inserir(").append(obj).append(");");
        b.append("\n\n\t\t");
        
        return b.toString();
    }
    
    public String getSettes(Tabela t, String nomeEntidade, String nomeObj) {
        StringBuilder b = new StringBuilder();
        for (Coluna c : getSetsObjeto(t)) {
            b.append(nomeObj).append(".set").append(c.getNome().substring(0,1).toUpperCase()
                            + c.getNome().substring(1));
            b.append("(");
            String tipo = c.getTipo();
            if (tipo.equals("Integer")) {
                if (nomeObj.equals("obj1"))
                    b.append("1234);");
                else
                    b.append("567);");
            } else if (tipo.equals("String")) {
                if (nomeObj.equals("obj1"))
                    b.append("\"umaPalavraQualquer\");");
                else
                   b.append("\"outraPalavraQualquer\");");
            } else if (tipo.equals("Float")) {
                b.append("3.14);");
            } else if (tipo.equals("Double")) {
                b.append("3.1415);");
            } else if (tipo.equals("Boolean")) {
                b.append("true);");
            } else
                b.append("null);");
            b.append("\n\t\t");
        }
        
        return b.toString();
    }
    
    public Collection<Coluna> getSetsObjeto(Tabela t) {
        Collection<Coluna> colunas = new ArrayList<>();
         // Integer qt = 0;
        // Boolean flag = false;
        for (Coluna c : t.getColunas()) {
            String tipo = c.getTipo();
            if (c.isNotNull() && !c.isPrimaryKey()) {
                colunas.add(c);
            }
            else if (!c.isForeignKey() && !c.isPrimaryKey()) {
                colunas.add(c);
                //  qt++;
                // if (qt.equals(2))
                //   flag = true;
            }
        }
        
        return colunas;
    }
    
    public String getAlterar(Tabela t, String nomeObjDao, String nomeEntidade) {
        StringBuilder b = new StringBuilder();
        b.append("for (").append(nomeEntidade).append(" temp : ");
        b.append(nomeObjDao).append(".buscarTodos()) { \n\t\t");
        b.append("\tobj1 = temp;\n\t\t\tbreak;\n\t\t}\n\t\t");
               
        for (Coluna c : t.getColunas()) {
            String tipo = c.getTipo();
            if (!c.isForeignKey() && !c.isPrimaryKey() && tipo.equals("String")) {
                b.append("System.out.println(\"\\nAlterando obj1...\");\n\t\t");
                b.append("obj1.set" + c.getNome().substring(0,1).toUpperCase()
                            + c.getNome().substring(1));
                b.append("(\"novaPalavra\");\n\t\t");
                b.append(nomeObjDao).append(".alterar(obj1);\n\n\t\t");
                
                b.append(listarTodos(t, nomeEntidade, nomeObjDao, true));
                break;
            }
        }
        
        return b.toString();
    }

    public String getExcluir(Tabela t, String nomeObjDao, String nomeEntidade) {
        StringBuilder b = new StringBuilder();
        b.append("/*\n\t\t// Cuidado, isso ira deletar todos os dados do banco\n\t\t");
        b.append("System.out.println(\"\\nExcluindo todos dados");
        b.append("...\");\n\t\t");
	b.append("for (").append(nomeEntidade).append(" temp : ").append(nomeObjDao);
        b.append(".buscarTodos())\n\t\t\t");
        b.append(nomeObjDao).append(".excluir(temp);\n");
        b.append("\t\t*/\n");
        
        return b.toString();
    } 
       
}

