package jmri.jmrit.dispatcher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import jmri.jmrit.catalog.NamedIcon;

public class AutoEngineerJButton extends JButton {
    private NamedIcon currentOriginalImage = null;
    public AutoEngineerJButton(NamedIcon namedIcon) {
        super();
        currentOriginalImage = namedIcon;
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // keeps the image in line with the button.
                reSizeIcon();
            }
        });
    }
    
    /**
     * Resizes the baseimage to the button size
     */
    private void reSizeIcon() {
        ImageIcon icon = null;
        if (getWidth() > 0 && getHeight() > 0) {
            icon = new ImageIcon(
                    currentOriginalImage.getOriginalImage().getScaledInstance(getWidth(), getHeight(),
                            java.awt.Image.SCALE_FAST));
            super.setIcon(icon);
        } 
    }
    
    /**
     * Set the Named Icon base Image
     *
     * @param namedIcon Base image for building icon.
     */
    public void setIcon(NamedIcon namedIcon) {
        currentOriginalImage = namedIcon;
        reSizeIcon();
    }
}
