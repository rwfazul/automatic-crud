/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collection;


public class Banco {
    
    private String nome;
    private Collection<Tabela> tabelas = new ArrayList<>();
    
    public Banco() {
        
    }
    
    public Banco(String nome) {
        setNome(nome);
    }
      
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Collection<Tabela> getTabelas() {
        return tabelas;
    }

    public void setTabelas(Collection<Tabela> tabelas) {
        this.tabelas = tabelas;
    }
    
}
