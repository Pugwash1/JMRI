package jmri.jmrit.dispatcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicToolBarUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jmri.Throttle;
import jmri.implementation.SignalSpeedMap;
import jmri.jmrit.catalog.NamedIcon;
import jmri.jmrit.dispatcher.AutoEngineerJButton;
import jmri.jmrit.roster.RosterEntry;
import jmri.jmrit.roster.RosterIconFactory;

public class AutoEngineerMicro extends JPanel {

    private static int interp = jmri.InstanceManager.getDefault(SignalSpeedMap.class).getInterpretation();
    
    private static NamedIcon iconForward = null;
    private static NamedIcon iconReverse = null;
    private static NamedIcon iconEngineerAuto = null;
    private static NamedIcon iconEngineerManual = null;
    private static NamedIcon iconStopIcon = null;
    private static NamedIcon iconGoIcon = null;
    private static NamedIcon iconRestartIcon = null;
    private static NamedIcon iconDDCBackground = null;
    private static NamedIcon iconSpeedBackground = null;
    private static NamedIcon iconSpeedPCBackground = null;
    private static java.util.List<NamedIcon> iconSpeeds = null;

    static {
        switch (interp) {
            case SignalSpeedMap.SPEED_MPH:
                iconSpeedBackground =
                        new NamedIcon("resources/icons/AutoTrainsFrame/SpeedMPHBackground.png",
                                "resources/icons/AutoTrainsFrame/SpeedMPHBackground.png");
                break;
            case SignalSpeedMap.SPEED_KMPH:
                iconSpeedBackground =
                        new NamedIcon("resources/icons/AutoTrainsFrame/SpeedKPHBackground.png",
                                "resources/icons/AutoTrainsFrame/SpeedKPHBackground.png");
                break;
            default:
                iconSpeedBackground = null;
        }
        iconDDCBackground =
                new NamedIcon("resources/icons/AutoTrainsFrame/DCCBackground.png",
                        "resources/icons/AutoTrainsFrame/DCCBackground.png");
        iconSpeedPCBackground =
                new NamedIcon("resources/icons/AutoTrainsFrame/SpeedPCBackground.png",
                        "resources/icons/AutoTrainsFrame/SpeedPCBackground.png");
        iconForward =
                new NamedIcon("resources/icons/AutoTrainsFrame/Forward.png",
                        "resources/icons/AutoTrainsFrame/Forward.png");
        iconReverse =
                new NamedIcon("resources/icons/AutoTrainsFrame/Reverse.png",
                        "resources/icons/AutoTrainsFrame/Reverse.png");
        iconEngineerAuto =
                new NamedIcon("resources/icons/AutoTrainsFrame/EngineerAuto.png",
                        "resources/icons/AutoTrainsFrame/EngineerAuto.png");
        iconEngineerManual =
                new NamedIcon("resources/icons/AutoTrainsFrame/EngineerManual.png",
                        "resources/icons/AutoTrainsFrame/EngineerManual.png");
        iconStopIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/Stop.png", "resources/icons/AutoTrainsFrame/Stop.png");
        iconGoIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/Go.png", "resources/icons/AutoTrainsFrame/Go.png");
        iconRestartIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/Restart.png",
                        "resources/icons/AutoTrainsFrame/Restart.png");
        iconDDCBackground =
                new NamedIcon("resources/icons/AutoTrainsFrame/DCCBackground.png",
                        "resources/icons/AutoTrainsFrame/DCCBackground.png");
        iconSpeeds =
                Arrays.asList(
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleZero.png",
                                "resources/icons/AutoTrainsFrame/ThrottleZero.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleOne.png",
                                "resources/icons/AutoTrainsFrame/ThrottleOne.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleTwo.png",
                                "resources/icons/AutoTrainsFrame/ThrottleTwo.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleThree.png",
                                "resources/icons/AutoTrainsFrame/ThrottleThree.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleFour.png",
                                "resources/icons/AutoTrainsFrame/ThrottleFour.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleFive.png",
                                "resources/icons/AutoTrainsFrame/ThrottleFive.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleSix.png",
                                "resources/icons/AutoTrainsFrame/ThrottleSix.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleSeven.png",
                                "resources/icons/AutoTrainsFrame/ThrottleSeven.png"),
                        new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleEight.png",
                                "resources/icons/AutoTrainsFrame/ThrottleEight.png"));
    }

    public AutoEngineerMicro(AutoActiveTrain autoActiveTrain) {
        this(null, autoActiveTrain, null);
    }

    public AutoEngineerMicro(jmri.util.JmriJFrame stopAllObject, AutoActiveTrain autoActiveTrain) {
        this(stopAllObject, autoActiveTrain, null);
    }

    public AutoEngineerMicro(jmri.util.JmriJFrame stopAllObject, AutoActiveTrain autoActiveTrain, RosterEntry rosterEntry) {

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

        if (stopAllObject != null) {
            stopAllObjectListener = new java.beans.PropertyChangeListener() {
                @Override
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    handleStopAllListen(e);
                }
            };
            stopAllObject.addPropertyChangeListener(stopAllObjectListener);

        }

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
    }

    private AutoActiveTrain autoActiveTrain = null;
    private java.beans.PropertyChangeListener activeTrainListener = null;
    private java.beans.PropertyChangeListener throttleListener = null;
    private java.beans.PropertyChangeListener stopAllObjectListener = null;
    private jmri.Throttle throttle = null;
    private ActiveTrain activeTrain = null;

    private void handleActiveTrainListen(java.beans.PropertyChangeEvent e) {
        if (e.getPropertyName().equals("mode")) {
            log.info("Modeold[{}]new[{}]", e.getNewValue(), e.getOldValue());
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
        if (throttle == null) {
            log.info("{}:prop[{}]Throttle null", activeTrain.getActiveTrainName(), e.getPropertyName());
        } else {
            log.info("{}:prop[{}]Throttle OK", activeTrain.getActiveTrainName(), e.getPropertyName());
        }
    }

    private RosterEntry rosterEntry = null;
    private int currentStep = 0;
    private float currentThrottleSetting = 0;
    private float currentThrottlePerHour = 0;

    private void handleStopAllListen(java.beans.PropertyChangeEvent e) {
        if ((boolean) e.getNewValue() == true) {
            if (activeTrain.getStatus() != ActiveTrain.STOPPED && activeTrain.getStatus() != ActiveTrain.DONE) {
                autoActiveTrain.getAutoEngineer().setHalt(true);
                autoActiveTrain.saveSpeed();
                autoActiveTrain.setSavedStatus(activeTrain.getStatus());
                activeTrain.setStatus(ActiveTrain.STOPPED);
                btnNewBbtnStartStop.setIcon(iconStopIcon);
            }
        }
    }

    public void forwardReverseTrain() {
        if (activeTrain.getMode() == ActiveTrain.MANUAL) {
            throttle.setIsForward(!throttle.getIsForward());
            btnReverser.setIcon(throttle.getIsForward() ? iconForward : iconReverse);
//            boolean donkey = throttle.getIsForward() ? true : false;
//            log.info("Donkey[{}",donkey);
        } else {
            log.debug("Ignoreing direction change click");
        }
    }

    public void throttleChange(int value) {
        if (activeTrain.getMode() == ActiveTrain.MANUAL) {
            if (currentStep + value > 8 || currentStep + value < 0) {
                return;
            }
            currentStep += value;
            currentThrottleSetting = ((float) currentStep * 1.0f / 8.0f);
            btnThrottle.setIcon(iconSpeeds.get(currentStep));
//            autoActiveTrain.getAutoEngineer().setSpeedImmediate(currentThrottleSetting);
            throttle.setSpeedSetting(currentThrottleSetting);
        } else {
            log.debug("Ignoreing speed change click");
        }
    }

    private void handleThrottleListen(java.beans.PropertyChangeEvent e) {
//        log.info("prop[{}]old[{}]new[{}]", e.getPropertyName(), e.getOldValue(), e.getNewValue());
        if (e.getPropertyName().equals(Throttle.SPEEDSETTING)) {
            /* Convert throttle stop seven speed steps */
            if (throttle.getSpeedSetting() <= 0.0) {
                currentStep = 0;
                currentThrottleSetting = 0;
                currentThrottlePerHour = 0;
                btnThrottle.setIcon(iconSpeeds.get(0));
                updatePgEnd();
                return;
            }
            currentThrottleSetting = throttle.getSpeedSetting();
            currentStep = (int) Math.ceil(currentThrottleSetting * 8);
            log.info("throt[{}]Step[{}]", currentThrottleSetting, currentStep);
            if (rosterEntry != null && rosterEntry.getSpeedProfile() != null) {
                currentThrottlePerHour = rosterEntry.getSpeedProfile()
                        .MMSToScaleSpeed(rosterEntry.getSpeedProfile().getSpeed(currentThrottleSetting, true));
            }
            updatePgEnd();
            btnThrottle.setIcon(iconSpeeds.get(currentStep));
        } else if (e.getPropertyName().equals(Throttle.ISFORWARD)) {
            if (throttle == null) {
                log.error("{}:throttle null Property[{}]", activeTrain.getActiveTrainName(),e.getPropertyName());
            }
            log.debug("{}:Property[{}]", activeTrain.getActiveTrainName(),e.getPropertyName());
            btnReverser.setIcon(throttle.getIsForward() ? iconForward : iconReverse);
        } else {
            return;
        }
        // log.info("Throttle Speed [{}] index [{}]", throttle.getSpeedSetting(), currentStep);
    }

    private void updatePgEnd() {
        lblPageEndSpeed.setText(String.format("%3.0f", currentThrottlePerHour));
        lblPageEndThrottle.setText(String.format("%3.0f%%", currentThrottleSetting * 100));
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
            //autoActiveTrain.setForward(!autoActiveTrain.getRunInReverse());
            if ((activeTrain.getStatus() == ActiveTrain.RUNNING) ||
                    (activeTrain.getStatus() == ActiveTrain.WAITING)) {
                autoActiveTrain.setSpeedBySignal();
            }
        }
    }

    JLabel lblPageStart = null;
    JLabel lblPageEndDCC = null;
    JLabel lblPageEndThrottle = null;
    JLabel lblPageEndSpeed = null;
    AutoEngineerJButton btnNewBbtnStartStop = null;
    AutoEngineerJButton btnReverser = null;
    AutoEngineerJButton btnManualAuto = null;
    AutoEngineerJButton btnThrottle = null;

    private void drawComponent() {

        
       // JPanel componentJPanel = new JPanel();
        JPanel componentBase = new JPanel();
        componentBase.setLayout(new BorderLayout());
        componentBase.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JToolBar componentJPanel = new JToolBar();
        
        componentJPanel.setLayout(new BorderLayout());
        componentJPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        
        componentJPanel.setFloatable(true);
        
        ImageIcon iconRosterEntry = null;
        if (rosterEntry != null) {
            iconRosterEntry = jmri.InstanceManager.getDefault(RosterIconFactory.class).getIcon(rosterEntry);
        }
        if (iconRosterEntry != null) {
            lblPageStart = new JLabel(iconRosterEntry);
        } else {
            lblPageStart = new JLabel(activeTrain.getActiveTrainName(), SwingConstants.CENTER);
        }
        componentJPanel.add(lblPageStart, BorderLayout.PAGE_START);

        JPanel pnlPageEnd = new JPanel(new GridLayout(1, 3));
        GridBagConstraints constraintPageEnd = new GridBagConstraints();
        constraintPageEnd.weightx = 1.0;
        constraintPageEnd.weighty = 1.0;

        constraintPageEnd.gridx = 0;
        constraintPageEnd.gridy = 0;
        lblPageEndDCC = new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(iconDDCBackground.getOriginalImage(), 0, 0, null);
                super.paintComponent(g);
            }
        };
        lblPageEndDCC.setHorizontalTextPosition(JLabel.CENTER);
        lblPageEndDCC.setVerticalTextPosition(JLabel.CENTER);
        lblPageEndDCC.setBorder(BorderFactory.createEtchedBorder());
        lblPageEndDCC.setText(activeTrain.getDccAddress());
        pnlPageEnd.add(lblPageEndDCC, constraintPageEnd);
        lblPageEndThrottle = new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(iconSpeedPCBackground.getOriginalImage(), 0, 0, null);
                super.paintComponent(g);
            }
        };
        ;
        constraintPageEnd.gridx = 1;
        constraintPageEnd.gridy = 0;
        lblPageEndThrottle.setText(Integer.toString(currentStep));
        lblPageEndThrottle.setBorder(BorderFactory.createEtchedBorder());
        lblPageEndThrottle.setOpaque(false);
        pnlPageEnd.add(lblPageEndThrottle, constraintPageEnd);

        constraintPageEnd.gridx = 2;
        constraintPageEnd.gridy = 0;
        lblPageEndSpeed = new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(iconSpeedBackground.getOriginalImage(), 0, 0, null);
                super.paintComponent(g);
            }
        };
        ;
        lblPageEndSpeed.setBorder(BorderFactory.createEtchedBorder());
        lblPageEndSpeed.setOpaque(false);
        pnlPageEnd.add(lblPageEndSpeed, constraintPageEnd);

        componentJPanel.add(pnlPageEnd, BorderLayout.PAGE_END);

        JPanel activities = new JPanel(new GridLayout(1, 5));
        //JToolBar activities = new JToolBar();
        Dimension buttonSize = new Dimension(64,64);
        // natural height, maximum width
        btnNewBbtnStartStop =
                new AutoEngineerJButton(iconStopIcon);
        btnNewBbtnStartStop.reSizeIcon(buttonSize);
        btnNewBbtnStartStop.setBorder(BorderFactory.createEtchedBorder());
        activities.add(btnNewBbtnStartStop);
        btnNewBbtnStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopResumeTrain();
            }
        });

        btnReverser = new AutoEngineerJButton(iconForward);
        btnReverser.reSizeIcon(buttonSize);
        btnReverser.setHorizontalAlignment(SwingConstants.RIGHT);
        btnReverser.setBorder(BorderFactory.createEtchedBorder());
        activities.add(btnReverser);
        btnReverser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                forwardReverseTrain();
            }
        });

        NamedIcon ThrottleZeroIcon =
                new NamedIcon("resources/icons/AutoTrainsFrame/ThrottleZero.png",
                        "resources/icons/AutoTrainsFrame/ThrottleZero.png");
        btnThrottle = new AutoEngineerJButton(ThrottleZeroIcon);
        btnThrottle.reSizeIcon(buttonSize);
        btnThrottle.setBorder(BorderFactory.createEtchedBorder());
        activities.add(btnThrottle);
        btnThrottle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getY() > btnThrottle.getSize().getHeight() / 2) {
                    throttleChange(-1);
                } else {
                    throttleChange(1);
                }
                log.info("Clicked");
            }
        });

        btnManualAuto =
                new AutoEngineerJButton(iconEngineerAuto);
        btnManualAuto.reSizeIcon(buttonSize);
        btnManualAuto.setBorder(BorderFactory.createEtchedBorder());
        activities.add(btnManualAuto);
        btnManualAuto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manualAutoTrain();
            }
        });
        componentJPanel.add(activities, BorderLayout.CENTER);
        //componentJPanel.setPreferredSize(componentJPanel.getMinimumSize());
        log.info("dim[{}][{}]",componentJPanel.getHeight(),componentJPanel.getWidth());
        componentJPanel.revalidate();
        componentBase.add(componentJPanel, BorderLayout.CENTER);
        BasicToolBarUI ui = new BasicToolBarUI();
        componentJPanel.setUI(ui);
        add(componentBase);
    }

    private final static Logger log = LoggerFactory.getLogger(AutoTrainsFrame.class);

}
