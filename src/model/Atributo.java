/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


public class Atributo {
    
    private String visibilidade;
    private String tipo;
    private String nome;

    public Atributo() {
        
    }
    
    public Atributo(String visibilidade, String tipo, String nome) {
        setVisibilidade(visibilidade);
        setTipo(tipo);
        setNome(nome);
    }
    
    public String getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(String visibilidade) {
        this.visibilidade = visibilidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    @Override 
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (visibilidade != null)
            b.append(getVisibilidade()).append(" ");
        b.append(getTipo()).append(" ");
        b.append(getNome());
        return b.toString();    
    }
    
}
