package br.com.org.jswitch.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.model.JDK;
/**
 * UI class to interface of program
 * 
 * @author Anderson
 *
 */
public class JSwitchUI {

	private OperationSystemManager operationSystemManager;
	private List<JDK> jdks = new ArrayList<JDK>();
	private JFrame window;
	private JPanel mainPanel;
	private JTable table;
	private JTabbedPane jTabbedPane;
	private JScrollPane tableScroll;
	private JTextWrapPane jTextPane;
	private JScrollPane resultScroll;
	private JButton botaoInstalar;

	public JSwitchUI() {
		super();
		operationSystemManager = new OperationSystemManager();
		
	}


	// main e montaTela

	public void show() {
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
		ShowWaitAction waitAction = new ShowWaitAction("Carregando JDK instaladas...", mainPanel,table);
		waitAction.executeLoader(operationSystemManager);
		jdks = waitAction.getLoadJDKInstalled();
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

	private void prepareLoadButton() {
		JButton botaoCarregar = new JButton("Carregar...");
		botaoCarregar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JDK jdk = operationSystemManager.chooseDirectory();
				if(jdk!=null){
					TableModel modelOld = table.getModel();
					if(modelOld.getRowCount()>0){
						jdks = ((JDKTableModel) modelOld).getDataRows();
					}
					jdks.add(jdk);
					JDKTableModel model = new JDKTableModel(jdks);
					table.setModel(model);
					jTabbedPane.setSelectedComponent(tableScroll);
					botaoInstalar.setEnabled(true);
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
		botaoInstalar = new JButton("Instalar");
		botaoInstalar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
				    operationSystemManager.install(new ArrayList<JDK>( jdks ),jTextPane);
				} catch (InstallationFailException e1) {
				    JOptionPane.showMessageDialog(null, "Erro durante a instalação do aplicativo",
					    "JSwitch", JOptionPane.ERROR_MESSAGE);
				} catch (InstallationDirectoryFaultException e1) {
				    JOptionPane.showMessageDialog(null,
					"Selecione pelo menos um diretório de instalação", "JSwitch",
					JOptionPane.ERROR_MESSAGE);
				   
				} catch (PermissionOperatingSystemExpection e1) {
				    JOptionPane.showMessageDialog(null, "Verifique se você tem permissão necessária para instalação do aplicativo",
					    "JSwitch", JOptionPane.ERROR_MESSAGE);
				}
				jTabbedPane.setSelectedComponent(resultScroll);
				((JButton)e.getSource()).setEnabled(false);
			}
		});
		mainPanel.add(botaoInstalar);
		
	}
}