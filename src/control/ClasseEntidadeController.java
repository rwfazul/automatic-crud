/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.ArrayList;
import java.util.Collection;
import model.Atributo;
import model.Classe;
import model.Coluna;
import model.Metodo;
import model.Parametro;
import model.Tabela;



public class ClasseEntidadeController {
    
    private MapeamentoController map;
    private String nomePacote;
    Collection<Classe> classesModel = new ArrayList<>();
    
    public ClasseEntidadeController(MapeamentoController map, String nomePacote) {
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

    public Collection<Classe> getClassesModel() {
        return classesModel;
    }

    public void setClassesModel(Collection<Classe> classesModel) {
        this.classesModel = classesModel;
    }
    
    public void criarClasses() {
        Collection<Tabela> tbls = map.getDatabase().getTabelas();
        Collection<Thread> threadsTabelas = new ArrayList<>();

        for (Tabela t : tbls) {
            Classe c = new Classe(t.getNome().substring(0,1).toUpperCase()
                            + t.getNome().substring(1)); 
            c.setNomeExtra(t.getNome().substring(0,1).toUpperCase()
                            + t.getNome().substring(1) + " extends Registro");
            c.getImports().add("banco.Registro");
            c.setPacote(getNomePacote());
            
            Runnable r = () -> {      
                criarAtributos(t, c);
                criarGettersSetters(c);
                getClassesModel().add(c);
            };
            Thread threadTabela = new Thread(r);
            threadsTabelas.add(threadTabela);   
            
        }
        
        for (Thread threadTabela : threadsTabelas) 
            threadTabela.run();
    }
    
    public void criarAtributos(Tabela tbl, Classe c) {
        Collection<Coluna> colunas = tbl.getColunas();
        Collection<String> imports = new ArrayList<>();
        for (Coluna coluna : colunas) {
            Atributo a = new Atributo();
            a.setVisibilidade("private");
            a.setTipo(coluna.getTipo());
            a.setNome(coluna.getNome());
            imports.add(coluna.getTipo());
            c.getAtributos().add(a);
        }
        
        if (imports.contains("Calendar")) {
            c.getImports().add("java.util.Calendar");
            c.getImports().add("java.text.SimpleDateFormat");
            c.getImports().add("java.beans.Transient");
        }
    }
    
    public void criarGettersSetters(Classe c) {
        Collection<Atributo> atributos = c.getAtributos();
        
        for (Atributo atributo : atributos) {
            Metodo get = new Metodo();
            get.setVisibilidade("public");
            get.setRetorno(atributo.getTipo());
            get.setNome("get" + atributo.getNome().substring(0,1).toUpperCase()
                            + atributo.getNome().substring(1));
            get.setTxt("\t\treturn " + atributo.getNome() + ";");
            c.getMetodos().add(get);
            
            if (atributo.getTipo().equals("Calendar")) {
                Metodo formatado = new Metodo();
                formatado.setNotacao("@Transient");
                formatado.setVisibilidade("public");
                formatado.setRetorno("String");
                formatado.setNome("get" + atributo.getNome().substring(0,1).toUpperCase()
                            + atributo.getNome().substring(1) + "Formatado");
                StringBuilder b = new StringBuilder();
                b.append("\t\tSimpleDateFormat sdf = new SimpleDateFormat(\"dd/MM/yyyy\");");
                b.append("\n\t\treturn sdf.format(").append(atributo.getNome());
                b.append(".getTime());");
                formatado.setTxt(b.toString());
                c.getMetodos().add(formatado);         
            }


            Metodo set = new Metodo();
            set.setVisibilidade("public");
            set.setRetorno("void");
            set.setNome("set" + atributo.getNome().substring(0,1).toUpperCase()
                            + atributo.getNome().substring(1));
            Collection<Parametro> parametros = new ArrayList<>();
            Parametro p = new Parametro(atributo.getTipo(), atributo.getNome());
            parametros.add(p);
            set.getParametros().add(p);
            set.setTxt("\t\tthis." + atributo.getNome() + " = " + p.getNome() + ";");
            c.getMetodos().add(set);      
        }
    }
     
}
