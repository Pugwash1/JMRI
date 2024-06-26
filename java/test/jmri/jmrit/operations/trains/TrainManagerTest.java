
package jmri.jmrit.operations.trains;

import java.awt.GraphicsEnvironment;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.Test;

import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsTestCase;
import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.LocationManager;
import jmri.jmrit.operations.rollingstock.cars.CarLoad;
import jmri.util.JUnitOperationsUtil;
import jmri.util.JUnitUtil;
import jmri.util.JmriJFrame;
import jmri.util.swing.JemmyUtil;

/**
 * Tests for the TrainManager class Last manually cross-checked on 20090131
 *
 * @author Bob Coleman Copyright (C) 2008, 2009
 */
public class TrainManagerTest extends OperationsTestCase {

    // test train manager
    @Test
    public void testTrainManager() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);

        // test defaults
        Assert.assertTrue("Build Messages", tmanager.isBuildMessagesEnabled());
        Assert.assertFalse("Build Reports", tmanager.isBuildReportEnabled());
        Assert.assertFalse("Print Preview", tmanager.isPrintPreviewEnabled());
        
        Assert.assertFalse("Run File", tmanager.isRunFileEnabled());
        Assert.assertFalse("Open File", tmanager.isOpenFileEnabled());
        Assert.assertFalse("Hyphen Name", tmanager.isShowLocationHyphenNameEnabled());

        // Swap them
        tmanager.setBuildMessagesEnabled(false);
        tmanager.setBuildReportEnabled(true);
        tmanager.setPrintPreviewEnabled(true);
        
        tmanager.setRunFileEnabled(true);
        tmanager.setOpenFileEnabled(true);
        tmanager.setShowLocationHyphenNameEnabled(true);

        Assert.assertFalse("Build Messages", tmanager.isBuildMessagesEnabled());
        Assert.assertTrue("Build Reports", tmanager.isBuildReportEnabled());
        Assert.assertTrue("Print Preview", tmanager.isPrintPreviewEnabled());
        
        Assert.assertTrue("Run File", tmanager.isRunFileEnabled());
        Assert.assertTrue("Open File", tmanager.isOpenFileEnabled());
        Assert.assertTrue("Hyphen Name", tmanager.isShowLocationHyphenNameEnabled());
    }
    
    @Test
    public void testShutDownScripts() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        
        tmanager.addShutDownScript("path to shutdown script");
        Assert.assertEquals("Confirm size", 1, tmanager.getShutDownScripts().size());
        Assert.assertEquals("Confirm script", "path to shutdown script", tmanager.getShutDownScripts().get(0));
        
        tmanager.deleteShutDownScript("path to shutdown script");
        Assert.assertEquals("Confirm size", 0, tmanager.getShutDownScripts().size());
    }
    
    @Test
    public void testStartUpScripts() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        
        tmanager.addStartUpScript("path to startup script");
        Assert.assertEquals("Confirm size", 1, tmanager.getStartUpScripts().size());
        Assert.assertEquals("Confirm script", "path to startup script", tmanager.getStartUpScripts().get(0));
        
        tmanager.deleteStartUpScript("path to startup script");
        Assert.assertEquals("Confirm size", 0, tmanager.getStartUpScripts().size());
    }

    /**
     * Make sure we can retrieve a train from the manager by name.
     */
    @Test
    public void testGetTrainByName() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Assert.assertNotNull("Retrieve Train", tmanager.getTrainByName("STF"));
    }

    /**
     * Make sure we can retrieve a train from the manager by name.
     */
    @Test
    public void testGetTrainById() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Assert.assertNotNull("Retrieve Train", tmanager.getTrainById("1"));
    }

    @Test
    public void testTrainCopy() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Train train = tmanager.getTrainById("1");
        Train copiedTrain = tmanager.copyTrain(train, "Copied train");

        Assert.assertEquals("Copied train", copiedTrain.getName());
        Assert.assertEquals(train.getRoute(), copiedTrain.getRoute());
    }

    @Test
    public void testReplaceLoad() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Train train = tmanager.getTrainById("1");
        train.setLoadOption(Train.INCLUDE_LOADS);
        train.addLoadName("Nuts");
        train.addLoadName("Boxcar" + CarLoad.SPLIT_CHAR + "Nuts");
        train.addLoadName("Bolts");
        train.addLoadName("Boxcar" + CarLoad.SPLIT_CHAR + "Bolts");

        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Nuts"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Nuts", "Boxcar"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts", "Boxcar"));
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("NUTS"));
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("BOLTS"));

        tmanager.replaceLoad("Boxcar", "Nuts", "NUTS");

        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("NUTS"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("NUTS", "Boxcar"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts", "Boxcar"));
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("Nuts"));
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("BOLTS"));
        
        // change bolts for all cars except for boxcars and bolts
        tmanager.replaceLoad("Flat", "Bolts", "BOLTS");
        
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("NUTS"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("NUTS", "Boxcar"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("BOLTS"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts", "Boxcar")); // not changed
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("Nuts"));
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("bolts"));    
    }
    
    @Test
    public void testDeleteLoad() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Train train = tmanager.getTrainById("1");
        train.setLoadOption(Train.INCLUDE_LOADS);
        train.addLoadName("Nuts");
        train.addLoadName("Boxcar" + CarLoad.SPLIT_CHAR + "Nuts");
        train.addLoadName("Bolts");
        train.addLoadName("Boxcar" + CarLoad.SPLIT_CHAR + "Bolts");

        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Nuts"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Nuts", "Boxcar"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts", "Boxcar"));

        tmanager.replaceLoad("Boxcar", "Nuts", null);
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("Nuts"));
        Assert.assertFalse("confirm load name", train.isLoadNameAccepted("Nuts", "Boxcar"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts"));
        Assert.assertTrue("confirm load name", train.isLoadNameAccepted("Bolts", "Boxcar"));
    }
    
    @Test
    public void testIsAnyTrainBuilt() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Train train = tmanager.getTrainById("1");
        
        Assert.assertFalse("no built trains", tmanager.isAnyTrainBuilt());
        Assert.assertTrue("train built",train.build());
        Assert.assertTrue("One built train", tmanager.isAnyTrainBuilt());
        
        JUnitOperationsUtil.checkOperationsShutDownTask();
    }
    
    @Test
    public void testGetTrainsArrivingThisLocationList() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Train train1 = tmanager.getTrainById("1");
        Train train2 = tmanager.getTrainById("2");
        
        Location location = InstanceManager.getDefault(LocationManager.class).getLocationById("3");
        List<Train> trains = tmanager.getTrainsArrivingThisLocationList(location);
        
        // no trains have been built so no trains arriving South End
        Assert.assertEquals("list size", 0 , trains.size());
        
        Assert.assertTrue(train1.build());
        Assert.assertTrue(train2.build());
        
        trains = tmanager.getTrainsArrivingThisLocationList(location);
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals(train1, trains.get(0));
        Assert.assertEquals(train2, trains.get(1));
        
        // change arrival time
        train2.move();
        
        trains = tmanager.getTrainsArrivingThisLocationList(location);
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals(train2, trains.get(0));
        Assert.assertEquals(train1, trains.get(1));
        
        JUnitOperationsUtil.checkOperationsShutDownTask();
    }
    
    @Test
    public void testGetTrainsByDepartureList() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        List<Train> trains = tmanager.getTrainsByDepartureList();
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals("STF", trains.get(0).getName());
        Assert.assertEquals("SFF", trains.get(1).getName());
    }
    
    @Test
    public void testGetTrainsByDescriptionList() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        Train train1 = tmanager.getTrainById("1");
        Train train2 = tmanager.getTrainById("2");
        train1.setDescription("Bad Train");
        train2.setDescription("A Good Train");
         
        List<Train> trains = tmanager.getTrainsByDescriptionList();
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals("SFF", trains.get(0).getName());
        Assert.assertEquals("STF", trains.get(1).getName());
    }
    
    @Test
    public void testGetTrainsByRouteList() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        List<Train> trains = tmanager.getTrainsByRouteList();
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals("STF", trains.get(0).getName());
        Assert.assertEquals("SFF", trains.get(1).getName());
    }
    
    @Test
    public void testGetTrainsByStatusList() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        List<Train> trains = tmanager.getTrainsByStatusList();
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals("STF", trains.get(0).getName());
        Assert.assertEquals("SFF", trains.get(1).getName());
        
        // change status
        Train train1 = tmanager.getTrainById("1");
        train1.reset();
        
        trains = tmanager.getTrainsByStatusList();
        Assert.assertEquals("list size", 2 , trains.size());
        Assert.assertEquals("SFF", trains.get(0).getName());
        Assert.assertEquals("STF", trains.get(1).getName());     
    }
    
    @Test
    public void testSelectedTrainsGUI() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        JUnitOperationsUtil.initOperationsData();
        // don't build train 1
        Train train1 = tmanager.getTrainById("1");
        train1.setBuildEnabled(false);
        
        Train train2 = tmanager.getTrainById("2");
        
        tmanager.buildSelectedTrains(tmanager.getTrainsByIdList());
        
        Thread t = jmri.util.JUnitUtil.getThreadByName("Build Trains");
        if (t != null) {
            jmri.util.JUnitUtil.waitFor(() -> {
                return t.getState().equals(Thread.State.TERMINATED);
            }, "wait for builds to complete");
        }
        
        Assert.assertFalse(train1.isBuilt());
        Assert.assertTrue(train2.isBuilt());
        
        // now preview all built trains
        tmanager.setPrintPreviewEnabled(true);
        tmanager.printSelectedTrains(tmanager.getTrainsByIdList());
        
        Assert.assertFalse(train1.isPrinted());
        Assert.assertFalse("Print preview", train2.isPrinted());
        
        // confirm print preview window is showing
        ResourceBundle rb = ResourceBundle
                .getBundle("jmri.util.UtilBundle");
        JmriJFrame printPreviewFrame = JmriJFrame.getFrame(rb.getString("PrintPreviewTitle") + " " + train2.getDescription());
        Assert.assertNotNull("exists", printPreviewFrame);

        JUnitUtil.dispose(printPreviewFrame);
        
        // should cause dialog window to confirm termination without printing
        Thread terminate = new Thread(new Runnable() {
            @Override
            public void run() {
                tmanager.terminateSelectedTrains(tmanager.getTrainsByIdList());
            }
        });
        terminate.setName("Terminate Trains Test"); // NOI18N
        terminate.start();

        jmri.util.JUnitUtil.waitFor(() -> {
            return terminate.getState().equals(Thread.State.WAITING);
        }, "wait for prompt");

        
        JemmyUtil.pressDialogButton(MessageFormat.format(Bundle.getMessage("TerminateTrain"),
                            new Object[]{train2.getName(), train2.getDescription()}), Bundle.getMessage("ButtonYes"));
        
        jmri.util.JUnitUtil.waitFor(() -> {
            return terminate.getState().equals(Thread.State.TERMINATED);
        }, "wait terminate");
       
        Assert.assertFalse(train2.isBuilt());
        
        JUnitOperationsUtil.checkOperationsShutDownTask();
    }
}
