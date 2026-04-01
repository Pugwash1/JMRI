package jmri.jmrix.pi;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.ShutdownException;

import javax.annotation.CheckForNull;

import jmri.jmrix.pi.simulator.GpioSimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an Adapter to allow the system connection memo and multiple
 * RaspberryPi managers to be handled.
 * <p>
 * Uses Pi4J 3.x on real Raspberry Pi hardware, or the JMRI-internal
 * {@link GpioSimulator} in simulator mode.
 * Pin addresses are BCM (Broadcom) numbers; e.g. "PS4" → BCM GPIO 4.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2002
 * @author Paul Bender Copyright (C) 2015
 */
public class RaspberryPiAdapter extends jmri.jmrix.AbstractPortController {

    private static boolean _isSimulator = false;

    /**
     * Shared Pi4J context used by this adapter, {@link RaspberryPiSensor}, and
     * {@link RaspberryPiTurnout}. Null in simulator mode or before a real
     * adapter has been opened.
     */
    private static Context sharedPi4JContext = null;

    /**
     * No-arg constructor. Inherits the current value of the static simulator
     * flag so that tests which call {@link #setIsSimulator(boolean)} before
     * construction will automatically get a simulator-mode adapter.
     */
    public RaspberryPiAdapter() {
        this(_isSimulator);
    }

    public RaspberryPiAdapter(boolean isSimulator) {
        super(new RaspberryPiSystemConnectionMemo());
        log.debug("RaspberryPi GPIO Adapter Constructor called");
        setIsSimulator(isSimulator);
        this.manufacturerName = RaspberryPiConnectionTypeList.PI;
        if (!isSimulator) {
            try {
                sharedPi4JContext = Pi4J.newAutoContext();
                opened = true;
            } catch (UnsatisfiedLinkError er) {
                log.error("Expected to run on Raspberry PI, but does not appear to be.");
            }
        } else {
            opened = true;
        }
    }

    public static boolean isSimulator() {
        return _isSimulator;
    }

    /**
     * Package-private setter so tests in {@code jmri.jmrix.pi} can enable
     * simulator mode before constructing sensors or turnouts.
     */
    static void setIsSimulator(boolean isSimulator) {
        _isSimulator = isSimulator;
    }

    /**
     * Return the shared Pi4J context, creating it lazily if needed.
     * Returns {@code null} in simulator mode.
     *
     * @return the Pi4J context, or {@code null}
     */
    @CheckForNull
    static synchronized Context getSharedContext() {
        if (sharedPi4JContext == null && !_isSimulator) {
            try {
                sharedPi4JContext = Pi4J.newAutoContext();
            } catch (UnsatisfiedLinkError er) {
                log.error("Expected to run on Raspberry PI, but does not appear to be.");
            }
        }
        return sharedPi4JContext;
    }

    @Override
    public String getCurrentPortName() {
        return "GPIO";
    }

    @Override
    public void dispose() {
        super.dispose();
        if (!_isSimulator && sharedPi4JContext != null) {
            try {
                sharedPi4JContext.shutdown();
            } catch (ShutdownException ex) {
                log.error("Error shutting down Pi4J context", ex);
            }
            sharedPi4JContext = null;
        } else if (_isSimulator) {
            GpioSimulator.getInstance().shutdown();
        }
    }

    @Override
    public void connect() {
    }

    @Override
    public void configure() {
        this.getSystemConnectionMemo().configureManagers();
    }

    @Override
    public java.io.DataInputStream getInputStream() {
        return null;
    }

    @Override
    public java.io.DataOutputStream getOutputStream() {
        return null;
    }

    @Override
    public RaspberryPiSystemConnectionMemo getSystemConnectionMemo() {
        return (RaspberryPiSystemConnectionMemo) super.getSystemConnectionMemo();
    }

    @Override
    public void recover() {
    }

    /**
     * Get the Pi4J context associated with this adapter.
     * Returns {@code null} in simulator mode or when not running on a Pi.
     *
     * @return the Pi4J {@link Context}, or {@code null}
     */
    @CheckForNull
    public Context getPi4JContext() {
        return sharedPi4JContext;
    }

    private final static Logger log = LoggerFactory.getLogger(RaspberryPiAdapter.class);

}
