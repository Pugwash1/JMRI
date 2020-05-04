package jmri.jmrit.dispatcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jmri.Throttle;
import jmri.ThrottleListener;
import jmri.implementation.SignalSpeedMap;
import jmri.jmrit.catalog.NamedIcon;
import jmri.jmrit.roster.RosterEntry;
import jmri.jmrit.roster.RosterIconFactory;
import jmri.util.JmriJFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AutoTrainsFrame provides a user interface to trains that are running
 * automatically under Dispatcher.
 * <p>
 * There is only one AutoTrains window. AutoTrains are added and deleted from
 * this window as they are added or terminated.
 * <p>
 * This file is part of JMRI.
 * <p>
 * JMRI is open source software; you can redistribute it and/or modify it under
 * the terms of version 2 of the GNU General Public License as published by the
 * Free Software Foundation. See the "COPYING" file for a copy of this license.
 * <p>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * @author Dave Duchamp Copyright (C) 2010
 */
public class AutoTrainsFrame extends jmri.util.JmriJFrame {
    
    

    public AutoTrainsFrame() {
        super(false, true);
        initializeAutoTrainsWindow();
    }

    public void addAutoActiveTrain(AutoActiveTrain aat, RosterEntry re) {
        if (aat != null) {
            AutoEngineerMicro atn = new AutoEngineerMicro(this,aat, re);
            //atn.setMaximumSize(new Dimension(64,4*64));
            //atn.setPreferredSize(new Dimension(64,4*64));
            contentPane.add(atn);
            pack();
        }
    }

    protected JmriJFrame autoTrainsFrame = null;
    private Container contentPane  = null ;
    
    private void initializeAutoTrainsWindow() {
        autoTrainsFrame = this;
        autoTrainsFrame.setTitle(Bundle.getMessage("TitleAutoTrains"));
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        autoTrainsFrame.addHelpMenu("package.jmri.jmrit.dispatcher.AutoTrains", true);
        contentPane = autoTrainsFrame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(new JSeparator());
        contentPane.add(new JSeparator());
        JPanel pB = new JPanel();
        pB.setLayout(new FlowLayout());
        JButton stopAllButton = new JButton(Bundle.getMessage("StopAll"));
        pB.add(stopAllButton);
        stopAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAllPressed(e);
            }
        });
        stopAllButton.setToolTipText(Bundle.getMessage("StopAllButtonHint"));
        contentPane.add(pB);
        autoTrainsFrame.pack();
        autoTrainsFrame.setVisible(true);
    }

    private void newSeparator() {
        JSeparator sep = new JSeparator();
        contentPane.add(sep);
    }
    
    public void stopAllPressed(ActionEvent e) {
        firePropertyChange("STOPALLAUTOTRAINS", false, true);
    }

    private final static Logger log = LoggerFactory.getLogger(AutoTrainsFrame.class);

}


