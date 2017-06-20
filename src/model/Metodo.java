/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



public class Metodo {

    private String notacao;
    private String visibilidade;
    private String retorno;
    private String nome;
    private Collection<Parametro> parametros = new ArrayList<>();
    private String extra;
    private String txt;

    public Metodo() {

    }
    
    public Metodo(String visibilidade, String retorno, String nome) {
        setVisibilidade(visibilidade);
        setRetorno(retorno);
        setNome(nome);
    }

    public String getNotacao() {
        return notacao;
    }

    public void setNotacao(String notacao) {
        this.notacao = notacao;
    }
    
    
    public String getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(String visibilidade) {
        this.visibilidade = visibilidade;
    }

    public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Collection<Parametro> getParametros() {
        return parametros;
    }

    public void setParametros(Collection<Parametro> parametros) {
        this.parametros = parametros;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
    
    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (notacao != null)
            b.append(getNotacao()).append("\n\t");
        if (visibilidade != null)
            b.append(getVisibilidade()).append(" ");
        if (retorno != null)
            b.append(getRetorno()).append(" ");
        b.append(getNome()).append("(");
        for (Iterator<Parametro> iterator = parametros.iterator(); iterator.hasNext();) {
            Parametro p = iterator.next();
            b.append(p.toString());
            if (iterator.hasNext())
                b.append(", ");
        }
     
        b.append(")");
        if (extra != null)
            b.append(" ").append(getExtra());
        b.append(" {\n").append(getTxt()).append("\n\t}");
        return b.toString();
    }

}
