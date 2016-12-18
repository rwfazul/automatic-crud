/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package txtsFixos;

import banco.BD;
import java.util.Scanner;
import model.Atributo;
import model.Classe;


public class ClassesFixasUtil {
    
    public String getTxt(String nome) {
        Scanner s = new Scanner(this.getClass().getResourceAsStream((nome + ".txt")));
        StringBuilder txt = new StringBuilder();
        
        while (s.hasNextLine()) 
            txt.append(s.nextLine()).append("\n");
 
        return txt.toString();  
    }
    
    public String getClasseBD(BD bd) {
        Atributo driver = new Atributo("public static final", "String",
            "JDBC_DRIVER = \"org.postgresql.Driver\"");
        Atributo user = new Atributo("static final", "String", "USUARIO = \"" 
            + bd.getUsario() + "\"");
        Atributo senha = new Atributo("static final", "String", "SENHA = \"" 
            + bd.getSenha() + "\"");
        Atributo url = new Atributo("static final", "String", "URL_CONEXAO = \""
            + bd.getUrlConexao() + "\"");
        
        
        Classe banco = new Classe("BD");
        banco.setPacote("banco");
        banco.getAtributos().add(driver);
        banco.getAtributos().add(user);
        banco.getAtributos().add(senha);
        banco.getAtributos().add(url);
        
        
        return banco.toString();
    }
    
}


