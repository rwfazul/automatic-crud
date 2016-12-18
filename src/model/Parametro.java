/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


public class Parametro {
    
    private String tipo;
    private String nome;

    public Parametro() {
        
    }
    
    public Parametro(String tipo, String nome) {
        setTipo(tipo);
        setNome(nome);
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
        b.append(getTipo()).append(" ");
        b.append(getNome());
        return b.toString();
    }
    
}
