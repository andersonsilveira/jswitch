package br.com.org.jswitch.cfg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

import br.com.org.jswitch.model.JDK;

public class JSwitchUI {

	private JFrame janela;
	private JPanel painelPrincipal;
	private JTable table;

	// main e montaTela

	public void montaTela() {
		preparaJanela();
		preparaPainelPrincipal();
		preparaBotaoCarregar();
		preparaBotaoSair();
		preparaTabela();
		preparaBotaoInstalar();
		mostraJanela();
	}
	
	private void preparaPainelPrincipal() {
		  painelPrincipal = new JPanel();
		  janela.add(painelPrincipal);
	}

	private void preparaJanela() {
		janela = new JFrame("JSwitch");
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// outros metodos prepara...

	private void mostraJanela() {
		janela.pack();
		janela.setSize(540, 540);
		janela.setVisible(true);
		Dimension maximumSize = new Dimension(540, 540);
		janela.setMaximumSize(maximumSize);
		
		List<JDK> jdks = new Loader().load();
		JSwitchTableModel model = new JSwitchTableModel(jdks);
		table.setModel(model);
	}
	
	private void preparaTabela(){
		table = new JTable();

		// por padrão, vem sem bordas, então colocamos:
		table.setBorder(new LineBorder(Color.black));
		table.setGridColor(Color.black);
		table.setShowGrid(true);
		
		JScrollPane scroll = new JScrollPane(); 
		scroll.getViewport().setBorder(null);
		scroll.getViewport().add(table); 
		scroll.setSize(450, 450);
		painelPrincipal.add(scroll);
	}

	private void preparaBotaoCarregar() {
		JButton botaoCarregar = new JButton("Carregar...");
		botaoCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<JDK> jdks = new JDKDirectoryChooser().choose();
				TableModel modelOld = table.getModel();
				if(modelOld.getRowCount()>0){
					List<JDK> dataRows = ((JSwitchTableModel) modelOld).getDataRows();
					dataRows.addAll(jdks);
					JSwitchTableModel model = new JSwitchTableModel(dataRows);
					table.setModel(model);
				}else{
					JSwitchTableModel model = new JSwitchTableModel(jdks);
					table.setModel(model);
				}
				
			}
		});
		painelPrincipal.add(botaoCarregar);
	}

	private void preparaBotaoSair() {
		JButton botaoSair = new JButton("Sair");
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		painelPrincipal.add(botaoSair);
	}
	
	private void preparaBotaoInstalar() {
		JButton botaoInstalar = new JButton("Instalar");
		botaoInstalar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MenuContextExecutor().execute();
			}
		});
		painelPrincipal.add(botaoInstalar);
	}
}