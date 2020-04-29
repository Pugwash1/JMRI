package jmri.jmrit.dispatcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import jmri.jmrit.catalog.NamedIcon;
import jmri.jmrit.roster.RosterEntry;
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

    public AutoTrainsFrame(DispatcherFrame disp) {
        super(false, true);
        _dispatcher = disp;
        initializeAutoTrainsWindow();
    }

    /* Get Icons Once 
    private static NamedIcon iconEngineerAuto =
            new NamedIcon("resources/icons/AutoTrainsFrame/EngineerAuto.png", "resources/icons/AutoTrainsFrame/EngineerAuto.png");
    private static NamedIcon iconEngineerManual =
            new NamedIcon("resources/icons/AutoTrainsFrame/EngineerManual.png", "resources/icons/AutoTrainsFrame/EngineerManual.png");
    private static NamedIcon iconStopIcon =
            new NamedIcon("resources/icons/AutoTrainsFrame/StopIcon.png", "resources/icons/AutoTrainsFrame/StopIcon.png");
    private static NamedIcon iconGoIcon =
            new NamedIcon("resources/icons/AutoTrainsFrame/GoIcon.png", "resources/icons/AutoTrainsFrame/GoIcon.png");
    private static java.util.List<NamedIcon> iconSpeeds = 
            Arrays.asList( new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleZero.png", "resources/icons/AutoTrainsFrame/ThrottleZero.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleOne.png", "resources/icons/AutoTrainsFrame/ThrottleOne.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleTwo.png", "resources/icons/AutoTrainsFrame/ThrottleTwo.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleThree.png", "resources/icons/AutoTrainsFrame/ThrottleThree.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleFour.png", "resources/icons/AutoTrainsFrame/ThrottleFour.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleFive.png", "resources/icons/AutoTrainsFrame/ThrottleFive.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleSix.png", "resources/icons/AutoTrainsFrame/ThrottleSix.png"),
            new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleSeven.png", "resources/icons/AutoTrainsFrame/ThrottleSeven.png"));
    */
    private  NamedIcon iconForward = null;
    private  NamedIcon iconReverse = null;
    private  NamedIcon iconEngineerAuto = null;
    private  NamedIcon iconEngineerManual = null;
    private  NamedIcon iconStopIcon = null;
    private  NamedIcon iconGoIcon = null;
    private  NamedIcon iconRestartIcon = null;
    private  java.util.List<NamedIcon> iconSpeeds = null;
     
    // instance variables
    private DispatcherFrame _dispatcher = null;
    private ArrayList<AutoActiveTrain> _autoTrainsList = new ArrayList<AutoActiveTrain>();
    private ArrayList<java.beans.PropertyChangeListener> _listeners
            = new ArrayList<java.beans.PropertyChangeListener>();
    //Keep track of throttle and listeners to update frame with their current state.
    private ArrayList<jmri.Throttle> _throttles = new ArrayList<jmri.Throttle>();
    private ArrayList<java.beans.PropertyChangeListener> _throttleListeners
            = new ArrayList<java.beans.PropertyChangeListener>();

    // accessor functions
    public ArrayList<AutoActiveTrain> getAutoTrainsList() {
        return _autoTrainsList;
    }

    public void addAutoActiveTrain(AutoActiveTrain aat) {
        
         iconForward =
                new NamedIcon("resources/icons/AutoTrainsFrame/Forward.png", "resources/icons/AutoTrainsFrame/Forward.png");
         iconReverse =
                new NamedIcon("resources/icons/AutoTrainsFrame/Reverse.png", "resources/icons/AutoTrainsFrame/Reverse.png");
         iconEngineerAuto =
                new NamedIcon("resources/icons/AutoTrainsFrame/EngineerAuto.png", "resources/icons/AutoTrainsFrame/EngineerAuto.png");
         iconEngineerManual =
                new NamedIcon("resources/icons/AutoTrainsFrame/EngineerManual.png", "resources/icons/AutoTrainsFrame/EngineerManual.png");
         iconStopIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/Stop.png", "resources/icons/AutoTrainsFrame/Stop.png");
         iconGoIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/Go.png", "resources/icons/AutoTrainsFrame/Go.png");
         iconRestartIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/Restart.png", "resources/icons/AutoTrainsFrame/Restart.png");
         iconSpeeds = 
                Arrays.asList( new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleZero.png", "resources/icons/AutoTrainsFrame/ThrottleZero.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleOne.png", "resources/icons/AutoTrainsFrame/ThrottleOne.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleTwo.png", "resources/icons/AutoTrainsFrame/ThrottleTwo.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleThree.png", "resources/icons/AutoTrainsFrame/ThrottleThree.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleFour.png", "resources/icons/AutoTrainsFrame/ThrottleFour.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleFive.png", "resources/icons/AutoTrainsFrame/ThrottleFive.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleSix.png", "resources/icons/AutoTrainsFrame/ThrottleSix.png"),
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleSeven.png", "resources/icons/AutoTrainsFrame/ThrottleSeven.png"));

        if (aat != null) {
            AutoTrainNew atn = new AutoTrainNew(aat);
            contentPane.add(atn);
            pack();
        }
    }

    public void removeAutoActiveTrain(AutoActiveTrain aat) {
        for (int i = 0; i < _autoTrainsList.size(); i++) {
            if (_autoTrainsList.get(i) == aat) {
                removeThrottleListener(aat);
                _autoTrainsList.remove(i);
                ActiveTrain at = aat.getActiveTrain();
                at.removePropertyChangeListener(_listeners.get(i));
                _throttles.remove(i);
                _listeners.remove(i);
                _throttleListeners.remove(i);
                displayAutoTrains();
                return;
            }
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


    private void placeWindow() {
        // get size and placement of Dispatcher Window, screen size, and window size
        Point dispPt = new Point(0, 0);
        Dimension dispDim = new Dimension(0, 0);

        if (_dispatcher.isShowing()) {
            dispPt = _dispatcher.getLocationOnScreen();
            dispDim = _dispatcher.getSize();
        }
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenDim.height - 120;
        int screenWidth = screenDim.width - 20;
        Dimension dim = getSize();
        int width = dim.width;
        int height = dim.height;
        // place AutoTrains window to the right of Dispatcher window, if it will fit.
        int upperLeftX = dispPt.x + dispDim.width;
        int upperLeftY = 0;
        if ((upperLeftX + width) > screenWidth) {
            // won't fit, place it below Dispatcher window, if it will fit.
            upperLeftX = 0;
            upperLeftY = dispPt.y + dispDim.height;
            if ((upperLeftY + height) > screenHeight) {
                // if all else fails, place it at the upper left of the screen, and let the user adjust placement
                upperLeftY = 0;
            }
        }
        setLocation(upperLeftX, upperLeftY);
    }


    public void stopAllPressed(ActionEvent e) {
        for (int i = 0; i < _autoTrainsList.size(); i++) {
            AutoActiveTrain aat = _autoTrainsList.get(i);
            ActiveTrain at = aat.getActiveTrain();
            if ((at.getStatus() != ActiveTrain.STOPPED) && (aat.getAutoEngineer() != null)) {
                aat.getAutoEngineer().setHalt(true);
                aat.saveSpeed();
                aat.setSavedStatus(at.getStatus());
                at.setStatus(ActiveTrain.STOPPED);
            }
        }
    }

    
    public class AutoTrainNew extends JPanel {

        public AutoTrainNew(AutoActiveTrain autoActiveTrain) {
            this(autoActiveTrain, null);
        }
        
        public AutoTrainNew(AutoActiveTrain autoActiveTrain, RosterEntry rosterEntry) {
            
            this.autoActiveTrain = autoActiveTrain;
            if (rosterEntry != null) {
                this.rosterEntry = rosterEntry;
            }
            activeTrain = autoActiveTrain.getActiveTrain();
            activeTrain.addPropertyChangeListener(activeTrainListener = new java.beans.PropertyChangeListener() {
                @Override
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    handleActiveTrainListen(e);
                }
            });
            drawComponent();
            if (this.autoActiveTrain.getThrottle() != null) {
                throttle = autoActiveTrain.getThrottle();
                throttleListener = new java.beans.PropertyChangeListener() {
                    @Override
                    public void propertyChange(java.beans.PropertyChangeEvent e) {
                        handleThrottleListen(e);
                    }
                };
                throttle.addPropertyChangeListener(throttleListener);
                btnNewBbtnStartStop.setIcon(iconGoIcon);
            }
            this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // This is only called when the user releases the mouse button.
                    log.info("componentResized[{}]",e.getSource());
                    ImageIcon icon =  new ImageIcon(
                            iconEngineerAuto.getOriginalImage().getScaledInstance(btnManualAuto.getWidth(),btnManualAuto.getHeight(), java.awt.Image.SCALE_FAST));
                    btnManualAuto.setIcon(icon);
                }
            });
        }
        
        private AutoActiveTrain autoActiveTrain = null;
        private java.beans.PropertyChangeListener activeTrainListener = null;
        private jmri.Throttle throttle = null;
        private java.beans.PropertyChangeListener throttleListener = null;
        private ActiveTrain activeTrain = null;

        private void handleActiveTrainListen(java.beans.PropertyChangeEvent e) {
            if (e.getPropertyName().equals("mode")) {
                log.info("Modeold[{}]new[{}]",e.getNewValue(),e.getOldValue());
                int newValue = ((Integer) e.getNewValue()).intValue();
                int oldValue = ((Integer) e.getOldValue()).intValue();
                if (newValue == ActiveTrain.DISPATCHED) {
                    jmri.InstanceManager.throttleManagerInstance().removeListener(throttle.getLocoAddress(),
                            throttleListener);
                } else if (newValue == ActiveTrain.DONE) {
                    btnNewBbtnStartStop.setIcon(iconRestartIcon);
                } else if (oldValue == ActiveTrain.DISPATCHED) {
                    if (autoActiveTrain.getThrottle() != null) {
                        throttle = autoActiveTrain.getThrottle();
                        throttleListener = new java.beans.PropertyChangeListener() {
                            @Override
                            public void propertyChange(java.beans.PropertyChangeEvent e) {
                                handleThrottleListen(e);
                            }
                        };
                        throttle.addPropertyChangeListener(throttleListener);
                        btnNewBbtnStartStop.setIcon(iconGoIcon);
                    }
                }
            } else if (e.getPropertyName().equals("status") && (int) e.getNewValue() == ActiveTrain.TERMINATED) {
                if (throttle != null && throttleListener != null) {
                    throttle.removePropertyChangeListener(throttleListener);
                    throttle = null;
                }
                activeTrain.removePropertyChangeListener(activeTrainListener);
                Container gp = getParent();
                gp.remove(this);
                gp.revalidate();
                gp.repaint();
            }
        }
        
        private RosterEntry rosterEntry = null;
        private int currentStep = 0;
        private float currentThrottleSetting = 0;
        private float currentThrottlePerHour = 0;

        public void throttleChange(int value) {
            if (activeTrain.getMode() == ActiveTrain.MANUAL) {
                if (currentStep + value > 7 || currentStep + value < 0) {
                    return;
                }
                currentStep += value;
                currentThrottleSetting = (currentStep * 7.0f / 100.0f);
                lblThrottle.setIcon(iconSpeeds.get(currentStep));
                autoActiveTrain.getAutoEngineer().setSpeedImmediate(currentThrottleSetting);
            } else {
                log.debug("Ignoreing speed change click");
            }
        }

        private void handleThrottleListen(java.beans.PropertyChangeEvent e) {
            if (!e.getPropertyName().equals(Throttle.SPEEDSETTING) && !e.getPropertyName().equals(Throttle.ISFORWARD)) {
                log.debug("Property[{}]",e.getPropertyName());
                return; // ignore if not speed or direction
            }
            /* Convert throttle stop seven speed steps */
            if (throttle.getSpeedSetting() <= 0.0) {
                currentStep = 0;
                currentThrottleSetting = 0;
                currentThrottlePerHour = 0;
                lblThrottle.setIcon(iconSpeeds.get(0));
                updatePgEnd();
                return;
            }
            // >0 is 1 <100 is 6, 0=0, 100 = 7
            currentThrottleSetting = throttle.getSpeedSetting();
            currentStep = (int) (throttle.getSpeedSetting()*6.0f) + 1;
            if (rosterEntry != null) {
                currentThrottlePerHour = rosterEntry.getSpeedProfile().getSpeed(currentThrottleSetting, true);
            }
            lblThrottle.setIcon(iconSpeeds.get(currentStep));
            lblReverser.setIcon(throttle.getIsForward() ? iconForward : iconReverse);
            updatePgEnd();
            log.info("Throttle Speed [{}] index [{}]",throttle.getSpeedSetting(),currentStep );
            //lblSpeedNumeric.setText(String.format("%.1f", throttle.getSpeedSetting()));
            //if (throttle.getIsForward()) {
            //    lblSpeedNumeric.setText(lblSpeedNumeric.getText() + "F");
            //} else {
            //    lblSpeedNumeric.setText(lblSpeedNumeric.getText() + "R");
           // }
        }

        private void updatePgEnd() {
            lblPageEndSpeed.setText(Float.toString(currentThrottlePerHour));
            lblPageEndThrottle.setText(Float.toString(currentThrottleSetting));
        }
        
        private void stopResumeTrain() {
            if (activeTrain.getStatus() == ActiveTrain.STOPPED) {
                // resume
                autoActiveTrain.setEngineDirection();
                autoActiveTrain.getAutoEngineer().setHalt(false);
                autoActiveTrain.restoreSavedSpeed();
                activeTrain.setStatus(autoActiveTrain.getSavedStatus());
                if ((activeTrain.getStatus() == ActiveTrain.RUNNING) ||
                        (activeTrain.getStatus() == ActiveTrain.WAITING)) {
                    autoActiveTrain.setSpeedBySignal();
                }
                btnNewBbtnStartStop.setIcon(iconGoIcon);
            } else if (activeTrain.getStatus() == ActiveTrain.DONE) {
                // restart
                activeTrain.allocateAFresh();
                activeTrain.restart();
                btnNewBbtnStartStop.setIcon(iconGoIcon);
            } else {
                // stop
                autoActiveTrain.getAutoEngineer().setHalt(true);
                autoActiveTrain.saveSpeed();
                autoActiveTrain.setSavedStatus(activeTrain.getStatus());
                activeTrain.setStatus(ActiveTrain.STOPPED);
                btnNewBbtnStartStop.setIcon(iconStopIcon);
            }
        }

        public void manualAutoTrain() {
            if (activeTrain.getMode() == ActiveTrain.AUTOMATIC) {
                activeTrain.setMode(ActiveTrain.MANUAL);
                btnManualAuto.setIcon(iconEngineerManual);
                if (autoActiveTrain.getAutoEngineer() != null) {
                    autoActiveTrain.saveSpeed();
                    autoActiveTrain.getAutoEngineer().setHalt(true);
                    autoActiveTrain.setTargetSpeed(0.0f);
                    autoActiveTrain.waitUntilStopped();
                    autoActiveTrain.getAutoEngineer().setHalt(false);
                }
            } else if (activeTrain.getMode() == ActiveTrain.MANUAL) {
                activeTrain.setMode(ActiveTrain.AUTOMATIC);
                btnManualAuto.setIcon(iconEngineerAuto);
                autoActiveTrain.restoreSavedSpeed();
                autoActiveTrain.setForward(!autoActiveTrain.getRunInReverse());
                if ((activeTrain.getStatus() == ActiveTrain.RUNNING) ||
                        (activeTrain.getStatus() == ActiveTrain.WAITING)) {
                    autoActiveTrain.setSpeedBySignal();
                }
            }
        }
        
        JLabel  lblPageStart = null;
        JLabel  lblPageEndDCC   = null;
        JLabel  lblPageEndThrottle   = null;
        JLabel  lblPageEndSpeed   = null;
        JButton btnNewBbtnStartStop = null;
        JLabel  lblReverser = null;
        JButton btnManualAuto = null;
        JLabel  lblThrottle = null;

        private void drawComponent() {
            
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.black));
            setMinimumSize(new Dimension(32,32)); // make sure it cant totally disappear.
            setMaximumSize(new Dimension(1200,5*1200)); // dont want it stupid big or do we
            
            lblPageStart = new JLabel(activeTrain.getActiveTrainName(),SwingConstants.CENTER);
            add(lblPageStart,BorderLayout.PAGE_START);
            
            JPanel pnlPageEnd = new JPanel(new GridLayout(1,3));
            GridBagConstraints constraintPageEnd = new GridBagConstraints();
            constraintPageEnd.weightx = 1.0;
            constraintPageEnd.weighty = 1.0;

            constraintPageEnd.gridx = 0;
            constraintPageEnd.gridy = 0;
            lblPageEndDCC = new JLabel(activeTrain.getDccAddress(),SwingConstants.CENTER);
            pnlPageEnd.add(lblPageEndDCC,constraintPageEnd);
            constraintPageEnd.gridx = 1;
            constraintPageEnd.gridy = 0;
            lblPageEndThrottle = new JLabel(Integer.toString(currentStep),SwingConstants.CENTER);
            pnlPageEnd.add(lblPageEndThrottle,constraintPageEnd);
            constraintPageEnd.gridx = 2;
            constraintPageEnd.gridy = 0;
            lblPageEndSpeed = new JLabel(Integer.toString(currentStep),SwingConstants.CENTER);
            pnlPageEnd.add(lblPageEndDCC,constraintPageEnd);

            add(pnlPageEnd,BorderLayout.PAGE_END);

            JPanel activities = new JPanel(new GridLayout(1,5));
            GridBagConstraints c = new GridBagConstraints();
             //natural height, maximum width
            btnNewBbtnStartStop =
                    new JButton(iconStopIcon);
            btnNewBbtnStartStop.setBorder(BorderFactory.createEtchedBorder());
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.gridx = 0;
            c.gridy = 0;
            activities.add(btnNewBbtnStartStop, c);
            //btnNewBbtnStartStop.setPreferredSize(new Dimension(iconOnOff.getIconWidth(), iconOnOff.getIconHeight()));
            btnNewBbtnStartStop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stopResumeTrain();
                }
            });

            lblReverser = new JLabel(iconForward);
            lblReverser.setHorizontalAlignment(SwingConstants.RIGHT);
            lblReverser.setBorder(BorderFactory.createEtchedBorder());
            c.gridx = 1;
            c.gridy = 0;
            activities.add(lblReverser, c);

            NamedIcon ThrottleZeroIcon =
                    new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleZero.png", "resources/icons/AutoTrainsFrame/ThrottleZero.png");
            lblThrottle = new JLabel(ThrottleZeroIcon);
            lblThrottle.setBorder(BorderFactory.createEtchedBorder());
            c.gridx = 2;
            c.gridy = 0;
            activities.add(lblThrottle, c);
            lblThrottle.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getY() > lblThrottle.getSize().getHeight()/2) {
                        throttleChange(-1);
                    } else {
                        throttleChange(1);
                    }
                    log.info("Clicked");
                }
            });
            
            btnManualAuto =
                    new JButton(iconEngineerAuto);
            btnManualAuto.setBorder(BorderFactory.createEtchedBorder());
            c.gridx = 3;
            c.gridy = 0;
            activities.add(btnManualAuto, c);
            btnManualAuto.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manualAutoTrain();
                }
            });
            add(activities,BorderLayout.CENTER);
            pack();
        }
    }

    private final static Logger log = LoggerFactory.getLogger(AutoTrainsFrame.class);

}


