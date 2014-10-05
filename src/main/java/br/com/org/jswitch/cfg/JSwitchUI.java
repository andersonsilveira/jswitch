package br.com.org.jswitch.cfg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

import br.com.org.jswitch.model.JDK;

public class JSwitchUI {

	private JFrame window;
	private JPanel mainPanel;
	private JTable table;
	private JTabbedPane jTabbedPane;
	private JScrollPane tableScroll;
	private JTextWrapPane jTextPane;
	private JScrollPane resultScroll;

	// main e montaTela

	public void montaTela() {
		prepareWindow();
		prepareMainPanel();
		prepareTabbed();
		prepareTabela();
		prepareConsole();
		prepareInstallButton();
		prepareLoadButton();
		prepareExitButton();
		showWindow();
	}
	
	private void prepareConsole() {
		jTextPane = new JTextWrapPane();
		jTextPane.setLineWrap(false);
		resultScroll = new JScrollPane(jTextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		resultScroll.setPreferredSize(new Dimension(450, 450));
		jTabbedPane.addTab("Resultado", resultScroll);
		
	}

	private void prepareTabbed() {
		jTabbedPane =	new JTabbedPane(JTabbedPane.TOP);
		mainPanel.add(jTabbedPane);
	}

	private void prepareMainPanel() {
		  mainPanel = new JPanel();
		  window.add(mainPanel);
	}

	private void prepareWindow() {
		window = new JFrame("JSwitch");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// outros metodos prepara...

	private void showWindow() {
		window.pack();
		window.setSize(540, 560);
		window.setVisible(true);
		Dimension maximumSize = new Dimension(540, 560);
		window.setMaximumSize(maximumSize);
		window.setResizable(false);
		jdks = new JDKLoader().load();
		JSwitchTableModel model = new JSwitchTableModel(jdks);
		table.setModel(model);
	}
	
	private void prepareTabela(){
		table = new JTable();

		// por padrão, vem sem bordas, então colocamos:
		table.setBorder(new LineBorder(Color.black));
		table.setGridColor(Color.black);
		table.setShowGrid(true);
		
		tableScroll = new JScrollPane(); 
		tableScroll.getViewport().setBorder(null);
		tableScroll.getViewport().add(table); 
		tableScroll.setSize(450, 450);
		jTabbedPane.addTab("JDK", tableScroll);
	}

	private List<JDK> jdks = new ArrayList<JDK>();
	private void prepareLoadButton() {
		JButton botaoCarregar = new JButton("Carregar...");
		botaoCarregar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				jdks = new JDKDirectoryChooser().choose();
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
		mainPanel.add(botaoCarregar);
	}

	private void prepareExitButton() {
		JButton botaoSair = new JButton("Sair");
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainPanel.add(botaoSair);
	}
	
	private void prepareInstallButton() {
		JButton botaoInstalar = new JButton("Instalar");
		botaoInstalar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MenuContextExecutor().execute(jdks,jTextPane);
				jTabbedPane.setSelectedComponent(resultScroll);
			}
		});
		mainPanel.add(botaoInstalar);
		
	}
}