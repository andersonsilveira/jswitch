package br.com.org.jswitch.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import br.com.org.jswitch.cfg.exception.DirectoryChooseNotValidException;
import br.com.org.jswitch.cfg.exception.InstallationDirectoryFaultException;
import br.com.org.jswitch.cfg.exception.InstallationFailException;
import br.com.org.jswitch.cfg.exception.PermissionOperatingSystemExpection;
import br.com.org.jswitch.control.OperationSystemManager;
import br.com.org.jswitch.model.JDK;
import br.com.org.jswitch.ui.ShowWaitAction.RESOURCE;
/**
 * UI class to interface of program
 * 
 * @author Anderson
 *
 */
public class JSwitchUI {

    	private static final ResourceBundle bundle = MessagesHelp.getBundle();
        
        private static final String CONFIG_LABEL_BUTTON = bundle.getString("button.config"); //"Configurar";
        private static final String CLOSE_LABEL_BUTTON = bundle.getString("button.close"); //"Sair";
        private static final String ADD_LABEL_BUTTON = bundle.getString("button.add"); //"Adicionar...";
	private static final String CARREGANDO_JDK_INSTALADAS = bundle.getString("info.load.jdk"); //"Carregando JDK instaladas...";
	private static final String RESULT_LOG = bundle.getString("tab.result");//"Resultado";
	private static final String ERRO_DURANTE_A_INSTALACAO_DO_APLICATIVO = bundle.getString("error.instalation"); //"Erro durante a instalação do aplicativo";
	private static final String VERIFIQUE_SE_VOCA_TEM_PERMISSAO_NECESSARIA_PARA_INSTALACAO_DO_APLICATIVO = bundle.getString("warn.permission.needle");//"Verifique se você tem permissão necessária para instalação do aplicativo";
	private static final String SELECIONE_PELO_MENOS_UM_DIRETORIO_DE_INSTALACAO = bundle.getString("info.select.one.directory"); //"Selecione pelo menos um diretório de instalação";
	
	
	private OperationSystemManager operationSystemManager;
	private List<JDK> jdks = new ArrayList<JDK>();
	private JFrame window;
	private JPanel mainPanel;
	private JTable table;
	private JTabbedPane jTabbedPane;
	private JScrollPane tableScroll;
	private JTextWrapPane jTextPane;
	private JScrollPane resultScroll;
	private JButton installButton;
	private JTrayIconUI jTrayIconUI;
	private JButton botaoSair;
	private boolean wasUpdate = false;
	

	
	private MODE mode = MODE.INSTALL;
	private JDKTableModel model;

	public JSwitchUI() {
		super();
		operationSystemManager = new OperationSystemManager();
		jTrayIconUI = new JTrayIconUI();
	}


	// main e montaTela

	public JSwitchUI(JTrayIconUI jTrayIconUI) {
	    	operationSystemManager = new OperationSystemManager();
		this.jTrayIconUI = jTrayIconUI;
	}

	public void show(MODE mode) throws Exception{
	    this.mode = mode;
	    if(MODE.INSTALL.equals(mode)){
		showForInstall();
	    }else if (MODE.UPDATE.equals(mode)){
		showForUpdate();
	    }
	    
	}

	public void showForInstall() {
		prepareWindow();
		prepareMainPanel();
		prepareTabbed();
		prepareTable();
		prepareConsole();
		prepareInstallButton();
		prepareLoadButton();
		prepareExitButton(false);
		showWindow();
		loadDefaultJDKs(RESOURCE.INSTALLED_ON_SYSTEM);
		initColumnSizes(table);
		
	}
	
	public void showForUpdate() throws Exception {
	    	jdks.clear();
	    	wasUpdate = false;
		prepareWindow();
		prepareMainPanel();
		prepareTabbed();
		prepareTable();
		prepareConsole();
		prepareInstallButton();
		prepareLoadButton();
		//prepareExitButton(false);
		showWindow();
		loadDefaultJDKs(RESOURCE.CONFIGURED);
		initColumnSizes(table);
		configureSystemTray();
		window.setResizable(false);
		installButton.setEnabled(false);
		window.setDefaultCloseOperation(JFrame.ICONIFIED);
		window.setExtendedState(JFrame.NORMAL);
		window.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent windowEvent) {
			window.setExtendedState(JFrame.ICONIFIED); 
			    }
			});
		open();
		jTrayIconUI.removeIconTray(window);
	}
	
	private void prepareConsole() {
		jTextPane = new JTextWrapPane();
		jTextPane.setLineWrap(false);
		resultScroll = new JScrollPane(jTextPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		resultScroll.setPreferredSize(new Dimension(450, 450));
		jTabbedPane.addTab(RESULT_LOG, resultScroll);
		
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
		//Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		//window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		//window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
	}


	private void configureSystemTray() {
	    window.addWindowStateListener(new WindowStateListener() {

	            public void windowStateChanged(WindowEvent e) {
	                if (e.getNewState() == JFrame.ICONIFIED) {
	                    try {
	                        jTrayIconUI.addIconTray(window,wasUpdate);
	                        System.out.println("added to SystemTray");
	                    } catch (Exception ex) {
	                        System.out.println("unable to add to tray");
	                    }
	                }
	                if(e.getNewState() == WindowEvent.WINDOW_CLOSING){
	                   try {
	            	   jTrayIconUI.addIconTray(window,wasUpdate);
	                        System.out.println("added to SystemTray");
	                    } catch (Exception ex) {
	                        System.out.println("unable to add to system tray");
	                    }
	                }
	                if (e.getNewState() == 7) {
	                    try {
	                        jTrayIconUI.addIconTray(window,wasUpdate);
	                        System.out.println("added to SystemTray");
	                    } catch (Exception ex) {
	                        System.out.println("unable to add to system tray");
	                    }
	                }
	                if (e.getNewState() == JFrame.MAXIMIZED_BOTH) {
	                    jTrayIconUI.removeIconTray(window);
	                    System.out.println("Tray icon removed");
	                }
	                if (e.getNewState() == JFrame.NORMAL) {
	            	 jTrayIconUI.removeIconTray(window);
	                    System.out.println("Tray icon removed");
	                }
	            }
	        });
	}


	private void loadDefaultJDKs(RESOURCE resource) {
	    ShowWaitAction waitAction = new ShowWaitAction(CARREGANDO_JDK_INSTALADAS, mainPanel,resource);
	    waitAction.executeLoader(operationSystemManager);
	    jdks = waitAction.getLoadJDKInstalled();
	    for (JDK jdk2 : jdks) {
		model.addRow(jdk2);
	    }
	}
	
	private void prepareTable(){
	    	model = new JDKTableModel(jdks);
		table = new JTable(model);

		// por padr�o, vem sem bordas, ent�o colocamos:
		table.setBorder(new LineBorder(Color.black));
		table.setGridColor(Color.black);
		table.setShowGrid(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		
		tableScroll = new JScrollPane(); 
		tableScroll.getViewport().setBorder(null);
		tableScroll.getViewport().add(table); 
		tableScroll.setSize(450, 450);
		jTabbedPane.addTab("JDK", tableScroll);
	}
	
	private void initColumnSizes(JTable table) {
	        JDKTableModel model = (JDKTableModel)table.getModel();
	        TableColumn column = null;
	        Component comp = null;
	        int headerWidth = 0;
	        int cellWidth = 0;
	        Object[] longValues = model.longValues;
	        TableCellRenderer headerRenderer =
	            table.getTableHeader().getDefaultRenderer();
	 
	        for (int i = 0; i < 3; i++) {
	            column = table.getColumnModel().getColumn(i);
	 
	            comp = headerRenderer.getTableCellRendererComponent(
	                                 table, column.getHeaderValue(),
	                                 false, false, 0, 0);
	            headerWidth = comp.getPreferredSize().width;
	 
	            comp = table.getDefaultRenderer(model.getColumnClass(i)).
	                             getTableCellRendererComponent(
	                                 table, longValues[i],
	                                 false, false, 0, i);
	            cellWidth = comp.getPreferredSize().width;
	 
	           
	 
	            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
	        }
	    }

	private void prepareLoadButton() {
		JButton botaoCarregar = new JButton(ADD_LABEL_BUTTON);
		botaoCarregar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
				    JDK jdk = operationSystemManager.chooseDirectory();
				    if(jdk!=null){
					jdks.add(jdk);
					model.addRow(jdk);
					jTabbedPane.setSelectedComponent(tableScroll);
					installButton.setEnabled(true);
					initColumnSizes(table);
				    }
				} catch (DirectoryChooseNotValidException e1) {
				    JOptionPane.showMessageDialog(null,
						"O diretorio escolhido não é um diretório válido!", "JSwitch",
						JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		mainPanel.add(botaoCarregar);
	}

	private void prepareExitButton(final boolean onlyHide) {
		botaoSair = new JButton(CLOSE_LABEL_BUTTON);
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if(onlyHide){
				window.setVisible(false);
			    }else{
				System.exit(0);
			    }
			}
		});
		mainPanel.add(botaoSair);
	}
	
	private void prepareInstallButton() {
		installButton = new JButton(CONFIG_LABEL_BUTTON);
		installButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
				    if(MODE.INSTALL.equals(mode)){
					operationSystemManager.install(new ArrayList<JDK>( jdks ),jTextPane);
					mainPanel.remove(botaoSair);
					prepareExitButton(true);
					jTrayIconUI.show();
				    }else if(MODE.UPDATE.equals(mode)){
					operationSystemManager.update(new ArrayList<JDK>( jdks ),jTextPane);
					wasUpdate = true;
				    }
				    
				} catch (InstallationFailException e1) {
				    JOptionPane.showMessageDialog(null, ERRO_DURANTE_A_INSTALACAO_DO_APLICATIVO,
					    "JSwitch", JOptionPane.ERROR_MESSAGE);
				} catch (InstallationDirectoryFaultException e1) {
				    JOptionPane.showMessageDialog(null,
					SELECIONE_PELO_MENOS_UM_DIRETORIO_DE_INSTALACAO, "JSwitch",
					JOptionPane.ERROR_MESSAGE);
				   
				} catch (PermissionOperatingSystemExpection e1) {
				    JOptionPane.showMessageDialog(null, VERIFIQUE_SE_VOCA_TEM_PERMISSAO_NECESSARIA_PARA_INSTALACAO_DO_APLICATIVO,
					    "JSwitch", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
				    JOptionPane.showMessageDialog(null, ERRO_DURANTE_A_INSTALACAO_DO_APLICATIVO,
					    "JSwitch", JOptionPane.ERROR_MESSAGE);
				}
				jTabbedPane.setSelectedComponent(resultScroll);
				((JButton)e.getSource()).setEnabled(false);
			}
		});
		mainPanel.add(installButton);
		
	}


	public void open() throws Exception {
		window.setVisible(true);
		window.setExtendedState(JFrame.NORMAL);
	    
	}


	public void initTray() {
	   try {
	    jTrayIconUI.addIconTray(window,false);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	    
	}
	
	public enum MODE{
	    INSTALL,
	    UPDATE
	}
}