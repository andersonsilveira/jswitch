package br.com.org.jswitch.ui;

import java.awt.CheckboxMenuItem;
import java.awt.TrayIcon;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.org.jswitch.cfg.FileChangeListener;
import br.com.org.jswitch.control.OperationSystemManager;
/**
 * 
 * @author Anderson
 *
 */
public class CheckboxMenuItemGroupItemListener implements ItemListener,FileChangeListener {

    public CheckboxMenuItemGroupItemListener(OperationSystemManager operationSystemManager,
			TrayIcon icon) {
		super();
		this.operationSystemManager = operationSystemManager;
		this.icon = icon;
	}

    private Set<CheckboxMenuItem>   items = new HashSet<CheckboxMenuItem>();
    
    OperationSystemManager operationSystemManager;
    
    TrayIcon icon;

    public void add(CheckboxMenuItem cbmi) {
        cbmi.addItemListener(this);
        cbmi.setState(false);
        items.add(cbmi);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String itemAffected = (String) e.getItem();
            try {
				operationSystemManager.changeJDKOnSelectedFile(itemAffected);
			} catch (Exception e1) {
				icon.displayMessage("Attention", itemAffected +" ocorreu um erro durante a configuração", 
						TrayIcon.MessageType.ERROR);
			}
            for (CheckboxMenuItem item : items) {
                // Use this line to allow user to toggle the selected item off
                if (!item.getLabel().equals(itemAffected)) item.setState(false);
                // Use this line to force one of the items to always be selected
                // item.setState(item.getLabel().equals(itemAffected));
            }
        }
    }
    
    

    public void selectItem(CheckboxMenuItem itemToSelect) {
        for (CheckboxMenuItem item : items) {
            item.setState(item == itemToSelect);
        }
    }
    
    public void selectItem(String itemToSelect) {
    	 for (CheckboxMenuItem item : items) {
             if (item.getLabel().equals(itemToSelect)){
            	 item.setState(true);
             }
    	 }
    	 for (CheckboxMenuItem item : items) {
             if (!item.getLabel().equals(itemToSelect)) item.setState(false);
         }
    }

    public CheckboxMenuItem getSelectedItem() {
        for (CheckboxMenuItem item : items) {
            if (item.getState()) return item;
        }
        return null;
    }

    @Override
    public void fileChanged(File file) {
	List<String> jdkInstalled = operationSystemManager.getJDKInstalled();
	for (final String jdk : jdkInstalled) {
	    if(!exists(jdk)){
		CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(jdk);
		add(checkboxMenuItem);
	    }
		
	}
	
    }

    public Set<CheckboxMenuItem> getItems() {
        return items;
    }

    private boolean exists(final String jdk) {
	for (CheckboxMenuItem item : items) {
	    if (item.getLabel().equals(jdk)){
	         return true;
	    }
	}
	return false;
    }
}