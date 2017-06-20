/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collection;



public class Tabela {
    
    private String nome;
    private Collection<Coluna> colunas = new ArrayList<>();

    public Tabela() {
        
    }
    
    public Tabela(String nome) {
        this.nome = nome;
    }
     
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Collection<Coluna> getColunas() {
        return colunas;
    }

    public void setColunas(Collection<Coluna> colunas) {
        this.colunas = colunas;
    }

}
