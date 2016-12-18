/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collection;


public class Classe {
    
    private String pacote;
    private Collection<String> imports = new ArrayList<>();
    private Boolean importOk;
    private String nome;
    private String nomeExtra;
    private Collection<Atributo> atributos = new ArrayList<>();
    private Collection<Metodo> metodos = new ArrayList<>();

    public Classe() {
        importOk = false;
    }
    
    public Classe(String nome) {
        setNome(nome);
        importOk = false;
    }

    public String getPacote() {
        return pacote;
    }

    public void setPacote(String pacote) {
        this.pacote = pacote;
    }

    public Collection<String> getImports() {
        return imports;
    }

    public void setImports(Collection<String> imports) {
        this.imports = imports;
    }

    public Boolean getImportOk() {
        return importOk;
    }

    public void setImportOk(Boolean importOk) {
        this.importOk = importOk;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeExtra() {
        return nomeExtra;
    }

    public void setNomeExtra(String nomeExtra) {
        this.nomeExtra = nomeExtra;
    }
    
    public Collection<Atributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(Collection<Atributo> atributos) {
        this.atributos = atributos;
    }

    public Collection<Metodo> getMetodos() {
        return metodos;
    }

    public void setMetodos(Collection<Metodo> metodos) {
        this.metodos = metodos;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("package ").append(getPacote()).append(";\n\n");
        for (String i : getImports())
            b.append("import ").append(i).append(";\n");
        b.append("\n");
        b.append("public class" + " ");
        if (nomeExtra != null)
            b.append(getNomeExtra()).append(" {\n");
        else
            b.append(getNome()).append(" {\n\n");
        for (Atributo a : getAtributos()) 
            b.append("\t").append(a.toString()).append(";\n");
        b.append("\n");
        for (Metodo m : getMetodos()) 
            b.append("\t").append(m.toString()).append("\n\n");
        b.append("}");
                
        return b.toString();
    }
    
}
