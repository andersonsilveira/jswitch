package br.com.org.jswitch.ui;

import java.awt.TrayIcon;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;

import br.com.org.jswitch.control.OperationSystemManager;
/**
 * 
 * @author Anderson
 *
 */
public class CheckboxMenuItemGroupListener implements ItemListener {

    public CheckboxMenuItemGroupListener(OperationSystemManager operationSystemManager,
			TrayIcon icon) {
		super();
		this.operationSystemManager = operationSystemManager;
		this.icon = icon;
	}

	private Set<JCheckBoxMenuItem>   items = new HashSet<JCheckBoxMenuItem>();
    
    OperationSystemManager operationSystemManager;
    
    TrayIcon icon;

    public void add(JCheckBoxMenuItem cbmi) {
        cbmi.addItemListener(this);
        cbmi.setSelected(false);
        items.add(cbmi);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
	JCheckBoxMenuItem itemAffected = (JCheckBoxMenuItem) e.getItem();
        if (e.getStateChange() == ItemEvent.SELECTED) {
            try {
				operationSystemManager.changeJDKOnSelectedFile(itemAffected.getText());
			} catch (Exception e1) {
				icon.displayMessage("Atenção", itemAffected +" ocorreu um erro durante a configuração", 
						TrayIcon.MessageType.ERROR);
			}
   
        }
    }
    
    

    public void selectItem(JCheckBoxMenuItem itemToSelect) {
        for (JCheckBoxMenuItem item : items) {
            item.setState(item == itemToSelect);
        }
    }
    
    public void selectItem(String itemToSelect) {
    	 for (JCheckBoxMenuItem item : items) {
             if (item.getText().equals(itemToSelect)){
            	 item.setSelected(true);
             }
    	 }
    	 for (JCheckBoxMenuItem item : items) {
             if (!item.getText().equals(itemToSelect)) item.setState(false);
         }
    }

    public JCheckBoxMenuItem getSelectedItem() {
        for (JCheckBoxMenuItem item : items) {
            if (item.getState()) return item;
        }
        return null;
    }
}