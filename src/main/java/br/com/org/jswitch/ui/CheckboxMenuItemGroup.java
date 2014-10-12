package br.com.org.jswitch.ui;

import java.awt.CheckboxMenuItem;
import java.awt.TrayIcon;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import br.com.org.jswitch.cfg.SystemTrayConfig;

public class CheckboxMenuItemGroup implements ItemListener {

    public CheckboxMenuItemGroup(SystemTrayConfig systemTrayConfig,
			TrayIcon icon) {
		super();
		this.systemTrayConfig = systemTrayConfig;
		this.icon = icon;
	}

	private Set<CheckboxMenuItem>   items = new HashSet<CheckboxMenuItem>();
    
    SystemTrayConfig systemTrayConfig;
    
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
				systemTrayConfig.change(itemAffected);
				icon.displayMessage("JSwitch", itemAffected +" foi selecionado!", 
						TrayIcon.MessageType.INFO);
			} catch (IOException e1) {
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

    public CheckboxMenuItem getSelectedItem() {
        for (CheckboxMenuItem item : items) {
            if (item.getState()) return item;
        }
        return null;
    }
}