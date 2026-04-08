package jmri.jmrix.openlcb.swing;

import java.util.TimerTask;
import javax.swing.JLabel;

import jmri.jmrix.can.CanListener;
import jmri.jmrix.can.CanMessage;
import jmri.jmrix.can.CanReply;
import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.util.TimerUtil;

/**
 *
 * @author Bob Jacobsen
 */
public class TrafficStatusLabel extends JLabel implements CanListener {
    
    private static final int INTERVAL = 200;
    CanSystemConnectionMemo memo;
    TimerTask timertask;
    boolean active;
    
    public TrafficStatusLabel(CanSystemConnectionMemo memo) {
        super("Active");
        this.setEnabled(false);
        this.memo = memo;
        this.active = false;
        
        // set up traffic listener
        memo.getTrafficController().addCanConsoleListener(this);
        
        // start the process
        displayActive();
    }
    
    void traffic() {
        if (timertask != null) { // only null at startup
            timertask.cancel();
        }
        active = true;
        displayActive();
    }
    
    void displayActive() {
        if (active != isEnabled()) setEnabled(active); // reduce redisplays 
        timertask = new TimerTask() {
            @Override
            public void run() {
                active = false; displayActive(); 
            }
        };
        TimerUtil.scheduleOnGUIThread(timertask, INTERVAL);
        
    }

    @Override
    public synchronized void message(CanMessage l) {
        traffic();
    }

    @Override
    public synchronized void reply(CanReply l) { 
        traffic();
    }
    
    // private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TrafficStatusLabel.class);
}
