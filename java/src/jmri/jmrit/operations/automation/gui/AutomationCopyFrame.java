package jmri.jmrit.operations.automation.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.*;

import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsFrame;
import jmri.jmrit.operations.automation.*;
import jmri.jmrit.operations.setup.Control;
import jmri.util.swing.JmriJOptionPane;

/**
 * Frame for making a new copy of a automation.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2016
 */
public class AutomationCopyFrame extends OperationsFrame {

    AutomationManager automationManager = InstanceManager.getDefault(AutomationManager.class);

    // labels
    // text field
    javax.swing.JTextField automationNameTextField = new javax.swing.JTextField(Control.max_len_string_automation_name);

    // major buttons
    javax.swing.JButton copyButton = new javax.swing.JButton(Bundle.getMessage("ButtonCopy"));

    // combo boxes
    JComboBox<Automation> automationBox = InstanceManager.getDefault(AutomationManager.class).getComboBox();

    public AutomationCopyFrame(Automation automation) {
        // general GUI config

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Set up the panels
        // Layout the panel by rows
        // row 1
        JPanel pName = new JPanel();
        pName.setLayout(new GridBagLayout());
        pName.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Name")));
        addItem(pName, automationNameTextField, 0, 0);

        // row 2
        JPanel pCopy = new JPanel();
        pCopy.setLayout(new GridBagLayout());
        pCopy.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("SelectAutomation")));
        addItem(pCopy, automationBox, 0, 0);

        automationBox.setSelectedItem(automation);

        // row 4
        JPanel pButton = new JPanel();
        pButton.add(copyButton);

        getContentPane().add(pName);
        getContentPane().add(pCopy);
        getContentPane().add(pButton);

        // add help menu to window
        addHelpMenu("package.jmri.jmrit.operations.Operations_CopyAutomation", true); // NOI18N

        setTitle(Bundle.getMessage("TitleAutomationCopy"));

        // setup buttons
        addButtonAction(copyButton);
        
        initMinimumSize(new Dimension(Control.panelWidth400, Control.panelHeight200));
    }

    @Override
    public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == copyButton) {
            log.debug("copy automation button activated");
            if (!checkName()) {
                return;
            }

            Automation newAutomation = automationManager.getAutomationByName(automationNameTextField.getText());
            if (newAutomation != null) {
                reportAutomationExists();
                return;
            }
            if (automationBox.getSelectedItem() == null) {
                reportAutomationDoesNotExist();
                return;
            }
            Automation oldAutomation = (Automation) automationBox.getSelectedItem();
            if (oldAutomation == null) {
                reportAutomationDoesNotExist();
                return;
            }

            // now copy
            newAutomation = automationManager.copyAutomation(oldAutomation, automationNameTextField.getText());
            new AutomationTableFrame(newAutomation);
        }
    }

    private void reportAutomationExists() {
        JmriJOptionPane.showMessageDialog(this, Bundle.getMessage("ReportExists"), Bundle
                .getMessage("CanNotCopyAutomation"), JmriJOptionPane.ERROR_MESSAGE);
    }

    private void reportAutomationDoesNotExist() {
        JmriJOptionPane.showMessageDialog(this, Bundle.getMessage("SelectAutomation"), Bundle
                .getMessage("CanNotCopyAutomation"), JmriJOptionPane.ERROR_MESSAGE);
    }

    /**
     *
     * @return true if name isn't too long
     */
    private boolean checkName() {
        if (automationNameTextField.getText().trim().isEmpty()) {
            JmriJOptionPane.showMessageDialog(this, Bundle.getMessage("EnterAutomationName"), Bundle
                    .getMessage("CanNotCopyAutomation"), JmriJOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (automationNameTextField.getText().length() > Control.max_len_string_automation_name) {
            JmriJOptionPane.showMessageDialog(this,
                    Bundle.getMessage("AutomationNameLengthMax", Control.max_len_string_automation_name),
                    Bundle.getMessage("CanNotCopyAutomation"), JmriJOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AutomationCopyFrame.class);

}
