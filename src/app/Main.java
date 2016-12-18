/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import control.MainWindowController;
import javax.swing.JOptionPane;
import view.MainWindowView;


public class Main {
    
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : 
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException 
                | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showConfirmDialog(null, "Não foi possível inicializar os componentes.",
                    "Erro Swing", JOptionPane.DEFAULT_OPTION);
            java.util.logging.Logger.getLogger(MainWindowView.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }

        MainWindowController window = new MainWindowController();
        window.setVisible(true);
    }
    
}
