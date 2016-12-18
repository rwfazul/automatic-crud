/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import banco.BD;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import model.Classe;
import txtsFixos.ClassesFixasUtil;
import view.MainWindowView;



public class MainWindowController extends MainWindowView {

    public MainWindowController() {
        super();
    }

    @Override
    public void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    @Override
    public void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File diretorio = fc.getSelectedFile();
            txtDiretorio.setText(diretorio.getPath());
        } else {
            JOptionPane.showConfirmDialog(null, "Nenhum diretório selecionado.",
                    "Diretório não selecionado", JOptionPane.DEFAULT_OPTION);      
            btnSelecionar.requestFocus();
        }
    }

    @Override
    public void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {
        //long inicio = System.currentTimeMillis();

        if (!verifica_campos()) {
            return;
        }

        BD bd = new BD();
        bd.setUsario(txtUser.getText());
        bd.setSenha(new String(txtSenha.getPassword()));
        bd.setDatabase(txtDatabase.getText());
        
        MapeamentoController map = new MapeamentoController(bd);
        
        criarClassesModel(map);
        criarClassesDAO(map);
        criarClassesFixas(bd);
        criarClassesExemplo(map);
             
        JOptionPane.showMessageDialog(null, "As classes foram criadas com sucesso.");
        
        //long fim  = System.currentTimeMillis();
        //System.out.println( fim - inicio );

    }
    
    public void criarClassesModel(MapeamentoController map) {
        ClasseEntidadeController model = new ClasseEntidadeController(map, "model");
        
        String caminho = txtDiretorio.getText() + "/" + "model";
        new File(caminho).mkdir();
        
        Collection<Classe> classesModel = model.getClassesModel();
        for (Classe cl : classesModel) {
            try {
                FileWriter arquivo;      
                arquivo = new FileWriter(new File(caminho + "/" + cl.getNome() + ".java"));
                arquivo.write(cl.toString());       
                arquivo.close();
            } catch (IOException ex) {
                JOptionPane.showConfirmDialog(null, "Falha ao criar arquivo da classe model "
                    + cl.getNome() + ".", "Erro de arquivo", JOptionPane.DEFAULT_OPTION);
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void criarClassesDAO(MapeamentoController map) {
        ClasseDaoController modelDao = new ClasseDaoController(map, "model");
        
        String caminho = txtDiretorio.getText() + "/" + "model";
        
        Collection<Classe> classesModelDao= modelDao.getClassesDao();
        for (Classe cl : classesModelDao) {
            try {
                FileWriter arquivo;      
                arquivo = new FileWriter(new File(caminho + "/" + cl.getNome() + "DAO" + ".java"));
                arquivo.write(cl.toString());       
                arquivo.close();
            } catch (IOException ex) {
                JOptionPane.showConfirmDialog(null, "Falha ao criar arquivo da classe model.dao "
                    + cl.getNome() + ".", "Erro de arquivo", JOptionPane.DEFAULT_OPTION);
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void criarClassesFixas(BD bd) {
        ClassesFixasUtil leitor = new ClassesFixasUtil();
        String caminho = txtDiretorio.getText() + "/" + "banco";
        new File(caminho).mkdir();
        
        try {
            FileWriter arquivo;      
            arquivo = new FileWriter(new File(caminho + "/" + "Registros.java"));
            arquivo.write(leitor.getTxt("Registros"));       
            arquivo.close();
            arquivo = new FileWriter(new File(caminho + "/" + "Registro.java"));
            arquivo.write(leitor.getTxt("Registro"));
            arquivo.close();
            arquivo = new FileWriter(new File(caminho + "/" + "BD.java"));
            arquivo.write(leitor.getClasseBD(bd));
            arquivo.close();
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(null, "Falha ao criar arquivos da classe banco.",
                "Erro de arquivo", JOptionPane.DEFAULT_OPTION);
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void criarClassesExemplo(MapeamentoController map) {
        ClasseExemploController exemplo = new ClasseExemploController(map, "exemplo");
        
        String caminho = txtDiretorio.getText() + "/" + "exemplo";
        new File(caminho).mkdir();
        
        Collection<Classe> classesExemplo= exemplo.getClassesExemplo();
        for (Classe cl : classesExemplo) {
            try {
                FileWriter arquivo;      
                arquivo = new FileWriter(new File(caminho + "/" + cl.getNome() + ".java"));
                arquivo.write(cl.toString());       
                arquivo.close();
            } catch (IOException ex) {
                JOptionPane.showConfirmDialog(null, "Falha ao criar arquivo da classe exemplo "
                    + cl.getNome() + ".", "Erro de arquivo", JOptionPane.DEFAULT_OPTION);
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }
    
    public boolean txtVazio(JTextField txt, String msg) {
        if (txt.getText().isEmpty()) {
            JOptionPane.showConfirmDialog(null, "Informe " + msg + " do banco.",
                    "Campo não preenchido", JOptionPane.DEFAULT_OPTION);
            txt.requestFocus();
            return true;
        }

        return false;
    }

    public Boolean verifica_campos() {
        return !(txtVazio(txtUser, "o usuario") || txtVazio(txtSenha, "a senha")
                || txtVazio(txtDatabase, "a database") || txtVazio(txtDiretorio, "o "
                        + "diretório para salvar as clases mapeadas do banco."));
    }

}
