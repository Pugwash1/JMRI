package jmri.jmrit.dispatcher;


import javax.swing.JPanel;
import java.awt.Window;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;
import jmri.Block;
import jmri.Throttle;
import jmri.jmrit.roster.RosterEntry;
import jmri.util.swing.JmriJOptionPane;

abstract  class AbstractAutoTrainControl extends JPanel {

    public AbstractAutoTrainControl(AutoActiveTrain autoActiveTrain) {

        this.autoActiveTrain = autoActiveTrain;
        activeTrain = autoActiveTrain.getActiveTrain();
        activeTrain.addPropertyChangeListener(activeTrainListener = new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent e) {
                handleActiveTrainListen(e);
            }
        });
        rosterEntry = autoActiveTrain.getRosterEntry();
        drawComponent();
    }

    protected void stopAll() {
        if (activeTrain.getStatus() != ActiveTrain.STOPPED &&
                activeTrain.getStatus() != ActiveTrain.DONE) {
            autoActiveTrain.getAutoEngineer().setHalt(true);
            autoActiveTrain.saveSpeedAndDirection();
            autoActiveTrain.setSavedStatus(activeTrain.getStatus());
            activeTrain.setStatus(ActiveTrain.STOPPED);
        }
    }

    protected AutoActiveTrain autoActiveTrain = null;
    private java.beans.PropertyChangeListener activeTrainListener = null;
    private java.beans.PropertyChangeListener throttleListener = null;
    protected jmri.Throttle throttle = null;
    protected ActiveTrain activeTrain = null;
    protected RosterEntry rosterEntry = null;
    protected boolean useOnTopOnSpeedChange;

    protected void handleThrottleListen(java.beans.PropertyChangeEvent e) {
        if (!e.getPropertyName().equals(Throttle.SPEEDSETTING) && !e.getPropertyName().equals(Throttle.ISFORWARD)) {
            return; // ignore if not speed or direction
        }
        if (useOnTopOnSpeedChange) {
            Window x  =  SwingUtilities.getWindowAncestor(this);
            log.trace("OnTop");
            x.setAlwaysOnTop(true);
            x.setAlwaysOnTop(false);
        }
        if (e.getPropertyName().equals(Throttle.SPEEDSETTING)) {
                updateSpeedChange(e);
        } else if (e.getPropertyName().equals(Throttle.ISFORWARD)) {
                directionChange(e);
        }
    }

    protected void setOnTopOnSpeedChange(boolean value) {
        useOnTopOnSpeedChange = value;
    }
    protected abstract void updateSpeedChange(java.beans.PropertyChangeEvent e);
    protected abstract void directionChange(java.beans.PropertyChangeEvent e);

    private void handleActiveTrainListen(java.beans.PropertyChangeEvent e) {
        if (e.getNewValue() != null) {
        log.trace("Property[{}] newValue[{}]",e.getPropertyName(),e.getNewValue());
        } else {
            log.trace("Property[{}] newValue[{}]",e.getPropertyName(),"NULL");
        }
        if (e.getPropertyName().equals("mode")) {
            int newValue = ((Integer) e.getNewValue()).intValue();
            if (newValue == ActiveTrain.DISPATCHED) {
                jmri.InstanceManager.throttleManagerInstance().removeListener(throttle.getLocoAddress(),
                        throttleListener);
                activeTrainNewModeDispatched();
            } else if (newValue == ActiveTrain.AUTOMATIC) {
                if (throttle == null) {
                    if (autoActiveTrain.getThrottle() != null) {
                    log.debug("[{}]:Set new throttle", autoActiveTrain.getActiveTrain().getActiveTrainName());
                        throttle = autoActiveTrain.getThrottle();
                        throttleListener = new java.beans.PropertyChangeListener() {
                            @Override
                            public void propertyChange(java.beans.PropertyChangeEvent e) {
                                handleThrottleListen(e);
                            }
                        };
                        jmri.InstanceManager.throttleManagerInstance().attachListener(throttle.getLocoAddress(), throttleListener);
                        rosterEntry = autoActiveTrain.getRosterEntry();
                    } else {
                        log.error("No throttle going automatic");
                    }
                }
                activeTrainNewModeAutomatic();
            } else if ((int) e.getNewValue() == ActiveTrain.TERMINATED) {
                if (throttle != null && throttleListener != null) {
                    throttle.removePropertyChangeListener(throttleListener);
                    throttle = null;
                }
                activeTrain.removePropertyChangeListener(activeTrainListener);
                // please someone stop me before I do something silly
                firePropertyChange("terminated", null, null);
            }
        } else if (e.getPropertyName().equals("status")) {
            log.debug("NewStatus[{}]", e.getNewValue());
            if ((int) e.getNewValue() == ActiveTrain.STOPPED) {
                activeTrainNewStatusStopped();
            } else if ((int) e.getNewValue() == ActiveTrain.RUNNING) {
                activeTrainNewStatusRunning();
            } else if ((int)e.getNewValue() == ActiveTrain.WAITING) {
                activeTrainNewStatusWaiting();
            } else if ((int) e.getNewValue() == ActiveTrain.DONE) {
                activeTrainNewStatusDone();
            } else {
                log.debug("Ignored newstatus[{}]", e.getNewValue());
            }
        }
        //super.pack();
    }

    protected abstract void activeTrainNewModeDispatched();
    protected abstract void activeTrainNewModeAutomatic();
    protected abstract void activeTrainNewStatusStopped();
    protected abstract void activeTrainNewStatusRunning ();
    protected abstract void activeTrainNewStatusWaiting ();
    protected abstract void activeTrainNewStatusDone ();

    @OverrideMustInvoke
    protected void autoToManual() {
        if (autoActiveTrain.getAutoEngineer() != null) {
            autoActiveTrain.saveSpeedAndDirection();
            autoActiveTrain.getAutoEngineer().setHalt(true);
            autoActiveTrain.setTargetSpeed(0.0f);
            autoActiveTrain.waitUntilStopped();
            autoActiveTrain.getAutoEngineer().setHalt(false);
        }
    }
    @OverrideMustInvoke
    protected void manualToAuto() {
        autoActiveTrain.restoreSavedSpeedAndDirection();
        // autoActiveTrain.setForward(!autoActiveTrain.getRunInReverse());
        if ((activeTrain.getStatus() == ActiveTrain.RUNNING) ||
                (activeTrain.getStatus() == ActiveTrain.WAITING)) {
            autoActiveTrain.setSpeedBySignal();
        }
    }

    JToolBar componentJPanel;

    protected abstract void drawComponent();

    void stopToResume() {
        if (autoActiveTrain.getAutoEngineer() != null) {
            ActiveTrain at = autoActiveTrain.getActiveTrain();
            if (at.getStatus() == ActiveTrain.STOPPED) {
                log.trace("Train Is Stopped - Resume");
                if (autoActiveTrain.getCurrentBlock().getState() != Block.OCCUPIED) {
                    JmriJOptionPane.showMessageDialog(
                            this,
                            Bundle.getMessage("AutoTrainsFramePleaseMoveTrain",autoActiveTrain.getCurrentBlock().getDisplayName()),
                            Bundle.getMessage("ResumeAutoButton"),
                            JmriJOptionPane.INFORMATION_MESSAGE
                        );
                    return;
                }
                Block b = autoActiveTrain.isBlockAhead();
                if (b != null) {
                    JmriJOptionPane.showMessageDialog(
                            this,
                            Bundle.getMessage("AutoTrainsFrameUnExplainedOccupancy",
                                    b.getDisplayName(),
                                    autoActiveTrain.getNextBlock()),
                            Bundle.getMessage("ResumeAutoButton"),
                            JmriJOptionPane.INFORMATION_MESSAGE
                        );
                    return;
                }

                boolean tmpIsForward = true;
                if ( autoActiveTrain.getRunInReverse() !=  at.isTransitReversed()) {
                    tmpIsForward = false;
                }
                Object[] options = {Bundle.getMessage("AutoTrainsFrameUseImpliedDirection",getDirString(tmpIsForward)),
                        Bundle.getMessage("AutoTrainsFrameRestoreSaved",getDirString(autoActiveTrain.getSavedDirection())),
                        Bundle.getMessage("AutoTrainsFrameUseCurrent",getDirString(autoActiveTrain.getForward())) };
                if ( tmpIsForward != autoActiveTrain.getSavedDirection() ||
                        tmpIsForward != autoActiveTrain.getForward()) {
                    int retval = JmriJOptionPane.showOptionDialog(this,
                            Bundle.getMessage("AutoTrainsFrameWhichDirectionToUse"),
                            Bundle.getMessage("AutoTrainsFrameDirectionConflict"),
                            JmriJOptionPane.YES_NO_OPTION,
                            JmriJOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                    switch (retval) {
                        case 0:
                            autoActiveTrain.setEngineDirection(tmpIsForward);
                            break;
                        case 1:
                            autoActiveTrain.setEngineDirection(autoActiveTrain.getSavedDirection());
                            break;
                        case 2:
                            autoActiveTrain.setEngineDirection(autoActiveTrain.getForward());
                            break;
                        default:
                            // do nothing.
                            log.info("rturneded[{}]",retval);
                    }
                }
                autoActiveTrain.getAutoEngineer().setHalt(false);
                at.setStatus(autoActiveTrain.getSavedStatus());
                if (at.getStatus() == ActiveTrain.STOPPED) {
                    at.setStatus(ActiveTrain.WAITING);
                }
                if ((at.getStatus() == ActiveTrain.RUNNING) || (at.getStatus() == ActiveTrain.WAITING)) {
                    autoActiveTrain.setSpeedBySignal();
                }
            } else if (at.getStatus() == ActiveTrain.READY) {
                handleActiveTrainListen(new java.beans.PropertyChangeEvent (this,"status", Integer.valueOf(0), Integer.valueOf(ActiveTrain.READY)));
            } else if (at.getStatus() == ActiveTrain.DONE) {
                log.trace("Train Is Done - Restart");
                // restart
                at.allocateAFresh();
                at.restart();
            } else {
                log.trace("Process As Stop");
                // stop
                autoActiveTrain.saveSpeedAndDirection();
                autoActiveTrain.getAutoEngineer().setHalt(true);
                autoActiveTrain.setSavedStatus(at.getStatus());
                at.setStatus(ActiveTrain.STOPPED);
//                speedSlider.setValue(0);
            }
        } else {
            log.error("unexpected null autoEngineer");
        }
    }

    private String getDirString(boolean isFwd) {
        return isFwd ? Bundle.getMessage("Fwd") : Bundle.getMessage("Rev");
    }

    private final static Logger log = LoggerFactory.getLogger(AbstractAutoTrainControl.class);

}
