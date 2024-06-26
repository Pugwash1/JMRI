package jmri.jmris;

import jmri.InstanceManager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Frame displaying start/stop buttons for the JMRI server.
 *
 * @author Paul Bender Copyright (C) 2010
 */
public class JmriServerFrame extends jmri.util.JmriJFrame {

    public JmriServerFrame() {
        this("Jmri Server Starter");
    }

    public JmriServerFrame(String FrameName) {
        super(FrameName);
        getContentPane().setLayout(new BoxLayout(getContentPane(),
                BoxLayout.Y_AXIS));

        JPanel pane0 = new JPanel();
        pane0.add(startButton);
        pane0.add(stopButton);
        pane0.add(closeButton);
        getContentPane().add(pane0);

        // and prep for display
        pack();

        // install start button handler
        startButton.addActionListener(a -> startServer());

        // install stop button handler
        stopButton.addActionListener(a -> stopServer());

        // install close button handler
        closeButton.addActionListener(a -> {
            setVisible(false);
            dispose();
        });

    }

    JToggleButton startButton = new JToggleButton("Start Server");
    JToggleButton stopButton = new JToggleButton("Stop Server");
    JToggleButton closeButton = new JToggleButton("Close Server");

    // Close the window when the close box is clicked
    void thisWindowClosing(java.awt.event.WindowEvent e) {
        setVisible(false);
        dispose();
    }

    @Override
    public void dispose() {
        // take apart the JFrame
        super.dispose();
    }

    public void startServer() {
        InstanceManager.getDefault(JmriServer.class).start();
    }

    public void stopServer() {
        InstanceManager.getDefault(JmriServer.class).stop();
    }

}
