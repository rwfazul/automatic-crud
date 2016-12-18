/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;



public class BDConexao {
    
    public static Connection getConexao(BD bd) {
        Connection c = null;
        
        try {
            Class.forName(bd.JBDC_DRIVER);
        } catch (ClassNotFoundException e) {
            JOptionPane.showConfirmDialog(null, "Impossível carregar driver " + bd.JBDC_DRIVER +".",
            "Driver nao encontrado.", JOptionPane.DEFAULT_OPTION);        }
        try {
            c = DriverManager.getConnection(bd.getUrlConexao(), bd.getUsario(), bd.getSenha());
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(null, "Impossível estabelecer conexão com o banco de dados.",
            "Conexão inválida.", JOptionPane.DEFAULT_OPTION);
        }
        return c;
    }
    
}
