/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


public class ForeignKey {
    
    private String nome;
    private Tabela tblReferenciada;
    private String nomeTblReferenciada;
    private String colunaReferenciada;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Tabela getTblReferenciada() {
        return tblReferenciada;
    }

    public void setTblReferenciada(Tabela tblReferenciada) {
        this.tblReferenciada = tblReferenciada;
    }

    public String getNomeTblReferenciada() {
        return nomeTblReferenciada;
    }

    public void setNomeTblReferenciada(String nomeTblReferenciada) {
        this.nomeTblReferenciada = nomeTblReferenciada;
    }
    
    public String getColunaReferenciada() {
        return colunaReferenciada;
    }

    public void setColunaReferenciada(String colunaReferenciada) {
        this.colunaReferenciada = colunaReferenciada;
    }
    
}
