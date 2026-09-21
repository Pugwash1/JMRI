package jmri.configurexml.swing;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jmri.configurexml.ShutdownPreferences;
import jmri.util.JmriJFrame;

/**
 * Swing dialog notify that there is un-stored PanelPro data changes.
 *
 * @author Dave Sand Copyright (c) 2022
 */
public class StoreAndCompareDialog {

    private static ShutdownPreferences _preferences = jmri.InstanceManager.getDefault(ShutdownPreferences.class);

    public static JmriJFrame getTopFrame() {
        String fTitle = jmri.Application.getApplicationName();
        for ( JmriJFrame f: jmri.util.JmriJFrame.getFrameList()) {
            log.info("f.name[{}]",f.getName());
            if (fTitle == f.getTitle()) {
                log.info("found [{}] for null parent",f.getTitle());
                return f;
            }
        }
        log.info("Not FOund [{}] for null parent using [{}]", fTitle,jmri.util.JmriJFrame.getFrameList().get(1));
        return jmri.util.JmriJFrame.getFrameList().get(1);
    }

    public static boolean showAbortShutdownDialogPermissionDenied() {
        AtomicBoolean result = new AtomicBoolean(false);
        try {
            // Provide option to invoke the store process before the shutdown.
            JmriJFrame parent = getTopFrame();
            final JDialog dialog = new JDialog(parent);
            parent.setVisible(true);
            parent.toFront();
            dialog.setTitle(Bundle.getMessage("QuestionTitle"));     // NOI18N
            dialog.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            JPanel container = new JPanel();
            container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            JLabel question = new JLabel(Bundle.getMessage("StoreAndComparePermissionDenied"));  // NOI18N
            question.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(question);

            JButton noButton = new JButton(Bundle.getMessage("ButtonNo"));    // NOI18N
            JButton yesButton = new JButton(Bundle.getMessage("ButtonYes"));      // NOI18N
            JPanel button = new JPanel();
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(noButton);
            button.add(yesButton);
            container.add(button);

            noButton.addActionListener((ActionEvent e) -> {
                dialog.dispose();
                result.set(false);
            });

            yesButton.addActionListener((ActionEvent e) -> {
                dialog.dispose();
                result.set(true);
            });

            container.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.setAlignmentY(Component.CENTER_ALIGNMENT);
            dialog.getContentPane().add(container);
            dialog.pack();

            setDialogLocation(parent, dialog);
            
            dialog.setModal(true);
            dialog.setVisible(true);
            dialog.toFront();

        } catch (HeadlessException ex) {
            // silently do nothig - we can't display a dialog and shutdown continues without a store.
        }
        return result.get();
    }

    public static boolean showDialog() {
        if (_preferences.getDisplayDialog().equals(ShutdownPreferences.DialogDisplayOptions.SkipDialog)) {
            performStore();
            return false;
        }

        AtomicBoolean cancelShutdown = new AtomicBoolean(false);
        try {
            // Provide option to invoke the store process before the shutdown.
            JmriJFrame parent = getTopFrame();
            final JDialog dialog = new JDialog(parent);
            parent.setVisible(true);
            parent.toFront();
            dialog.setTitle(Bundle.getMessage("QuestionTitle"));     // NOI18N
            dialog.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            JPanel container = new JPanel();
            container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            JLabel question = new JLabel(Bundle.getMessage("StoreAndCompareRequest"));  // NOI18N
            question.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(question);

            JButton noButton = new JButton(Bundle.getMessage("ButtonNo"));    // NOI18N
            JButton yesButton = new JButton(Bundle.getMessage("ButtonYes"));      // NOI18N
            JButton canButton = new JButton(Bundle.getMessage("ButtonCancel"));      // NOI18N
            JPanel button = new JPanel();
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.add(noButton);
            button.add(yesButton);
            button.add(canButton);
            container.add(button);

            noButton.addActionListener((ActionEvent e) -> {
                dialog.dispose();
            });

            canButton.addActionListener((ActionEvent e) -> {
                cancelShutdown.set(true);
                dialog.dispose();
            });

            yesButton.addActionListener((ActionEvent e) -> {
                dialog.setVisible(false);
                performStore();
                dialog.dispose();
            });

            container.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.setAlignmentY(Component.CENTER_ALIGNMENT);
            dialog.getContentPane().add(container);
            dialog.pack();
            
            setDialogLocation(parent,dialog);
            
            dialog.setModal(true);
            dialog.setVisible(true);
            dialog.toFront();

        } catch (HeadlessException ex) {
            // silently do nothig - we can't display a dialog and shutdown continues without a store.
        }
        return cancelShutdown.get();
    }
    
    private static void setDialogLocation( @Nonnull JmriJFrame parent, @Nonnull Dialog dialog) {
        log.debug("set dialog position for comp {} dialog {}", parent, dialog.getTitle());
        
        Point topLeft = parent.getLocationOnScreen();
        Dimension size = parent.getSize();
        int    centreWidth = topLeft.x + ( size.width / 2 );
        int    centreHeight = topLeft.y + ( size.height / 2 );
        int centerX = centreWidth - ( dialog.getWidth() / 2 );
        int centerY = centreHeight - ( dialog.getHeight() / 2 );
        dialog.setLocation( new Point(Math.max(0, centerX), Math.max(0, centerY)));
    }


    private static void performStore() {
        new jmri.configurexml.StoreXmlUserAction("").actionPerformed(null);
    }
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoreAndCompareDialog.class);
}
