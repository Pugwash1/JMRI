package jmri.jmrit.operations.trains.gui;

import java.text.MessageFormat;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsTestCase;
import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.LocationManager;
import jmri.jmrit.operations.routes.*;
import jmri.jmrit.operations.trains.Train;
import jmri.jmrit.operations.trains.TrainManager;
import jmri.util.*;
import jmri.util.swing.JemmyUtil;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
@jmri.util.junit.annotations.DisabledIfHeadless
public class TrainEditFrameTest extends OperationsTestCase {

    @Test
    public void testCTor() {
        Train train1 = new Train("TESTTRAINID", "TESTTRAINNAME");
        TrainEditFrame f = new TrainEditFrame(train1);
        Assert.assertNotNull("exists", f);
        JUnitUtil.dispose(f);
    }

    @Test
    public void testTrainEditFrame() {
        TrainEditFrame trainEditFrame = new TrainEditFrame(null);
        trainEditFrame.setTitle("Test Edit Train Frame");
        ThreadingUtil.runOnGUI(() -> {
            // fill in name and description fields
            trainEditFrame.trainNameTextField.setText("Test Train Name");
            trainEditFrame.trainDescriptionTextField.setText("Test Train Description");
            trainEditFrame.commentTextArea.setText("Test Train Comment");
            trainEditFrame.hourBox.setSelectedItem("15");
            trainEditFrame.minuteBox.setSelectedItem("45");
        });
        JemmyUtil.enterClickAndLeave(trainEditFrame.addTrainButton);

        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.getTrainByName("Test Train Name");
        Assert.assertNotNull("train exists", train);

        // test defaults
        Assert.assertEquals("train name", "Test Train Name", train.getName());
        Assert.assertEquals("train description", "Test Train Description", train.getDescription());
        Assert.assertEquals("train comment", "Test Train Comment", train.getCommentWithColor());
        Assert.assertEquals("train depart time", "15:45", train.getDepartureTime());
        Assert.assertEquals("train route", null, train.getRoute());
        Assert.assertTrue("train accepts car type Boxcar", train.isTypeNameAccepted("Boxcar"));
        Assert.assertEquals("train roads", Train.ALL_ROADS, train.getCarRoadOption());
        Assert.assertEquals("train requirements", Train.NO_CABOOSE_OR_FRED, train.getRequirements());

        // no route, should cause pop up dialog
        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.saveTrainButton);

        // clear no route dialog box
        JemmyUtil.pressDialogButton(trainEditFrame, Bundle.getMessage("TrainNoRoute"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);

        // test route field, 5 routes and a blank
        Assert.assertEquals("Route Combobox item count", 6, trainEditFrame.routeBox.getItemCount());
        ThreadingUtil.runOnGUI(() -> {
            trainEditFrame.routeBox.setSelectedIndex(3); // the 3rd item should be "Test Route C"
        });

        // save should work now
        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train depart time", "15:45", train.getDepartureTime());

        Assert.assertEquals("train route", "Test Route C", train.getRoute().getName());
        // test route edit button
        JemmyUtil.enterClickAndLeave(trainEditFrame.editButton);

        // confirm panel creation
        JmriJFrame ref = JmriJFrame.getFrame(Bundle.getMessage("TitleRouteEdit"));
        Assert.assertNotNull("route add frame", ref);

        // increase screen size so clear and set buttons are shown
        ThreadingUtil.runOnGUI(() -> {
            trainEditFrame.setLocation(10, 0);
            trainEditFrame.setSize(trainEditFrame.getWidth(), trainEditFrame.getHeight() + 200);
        });

        // test car types using the clear and set buttons
        JemmyUtil.enterClickAndLeave(trainEditFrame.clearButton);
        Assert.assertFalse("train accepts car type Boxcar", train.isTypeNameAccepted("Boxcar"));

        JemmyUtil.enterClickAndLeave(trainEditFrame.setButton);
        Assert.assertTrue("train accepts car type Boxcar", train.isTypeNameAccepted("Boxcar"));

        // test engine fields
        Assert.assertEquals("number of engines", "0", train.getNumberEngines());
        Assert.assertEquals("engine model", "", train.getEngineModel());
        Assert.assertEquals("engine road", "", train.getEngineRoad());
        // now change them
        ThreadingUtil.runOnGUI(() -> {
            trainEditFrame.numEnginesBox.setSelectedItem("3");
            trainEditFrame.modelEngineBox.setSelectedItem("FT");
            trainEditFrame.roadEngineBox.setSelectedItem("UP");
        });
        // shouldn't change until Save
        Assert.assertEquals("number of engines 1", "0", train.getNumberEngines());
        Assert.assertEquals("engine model 1", "", train.getEngineModel());
        Assert.assertEquals("engine road 1", "", train.getEngineRoad());
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        // confirm changes
        Assert.assertEquals("number of engines 2", "3", train.getNumberEngines());
        Assert.assertEquals("engine model 2", "FT", train.getEngineModel());
        Assert.assertEquals("engine road 2", "UP", train.getEngineRoad());

        // test caboose and FRED buttons and fields
        // require a car with FRED
        JemmyUtil.enterClickAndLeave(trainEditFrame.fredRadioButton);

        // shouldn't change until Save
        Assert.assertEquals("train requirements 1", Train.NO_CABOOSE_OR_FRED, train.getRequirements());
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train requirements FRED", Train.FRED, train.getRequirements());

        JemmyUtil.enterClickAndLeave(trainEditFrame.cabooseRadioButton);
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train depart time", "15:45", train.getDepartureTime());
        Assert.assertEquals("train requirements Caboose", Train.CABOOSE, train.getRequirements());

        Assert.assertEquals("caboose road 1", "", train.getCabooseRoad());
        // shouldn't change until Save
        String roadNames[] = Bundle.getMessage("carRoadNames").split(",");
        ThreadingUtil.runOnGUI(() -> {
            trainEditFrame.roadCabooseBox.setSelectedItem(roadNames[2]);
        });
        Assert.assertEquals("caboose road", "", train.getCabooseRoad());
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("caboose road new", roadNames[2], train.getCabooseRoad());

        // remove Caboose or FRED requirement
        JemmyUtil.enterClickAndLeave(trainEditFrame.noneRadioButton);
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train requirements 4", Train.NO_CABOOSE_OR_FRED, train.getRequirements());

        // test frame size and location
        ThreadingUtil.runOnGUI(() -> {
            trainEditFrame.setSize(650, 600);
            trainEditFrame.setLocation(25, 30);
        });
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);

        // test delete button
        // the delete opens a dialog window to confirm the delete
        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.deleteTrainButton);
        // don't delete, we need this train for the next two tests
        // testTrainBuildOptionFrame() and testTrainEditFrameRead()
        JemmyUtil.pressDialogButton(trainEditFrame, Bundle.getMessage("deleteTrain"), Bundle.getMessage("ButtonNo"));
        JemmyUtil.waitFor(trainEditFrame);

        ThreadingUtil.runOnGUI(() -> {
            JUnitUtil.dispose(ref);
            JUnitUtil.dispose(trainEditFrame);
        });

        // now reload the window
        Train t2 = tmanager.getTrainByName("Test Train Name");
        Assert.assertNotNull(t2);

        // change the train so it doesn't match the add test
        t2.setRequirements(Train.CABOOSE);
        t2.setCabooseRoad("CP");

        TrainEditFrame f = new TrainEditFrame(t2);
        f.setTitle("Test Edit Train Frame");

        Assert.assertEquals("train name", "Test Train Name", f.trainNameTextField.getText());
        Assert.assertEquals("train description", "Test Train Description", f.trainDescriptionTextField.getText());
        Assert.assertEquals("train comment", "Test Train Comment", f.commentTextArea.getText());
        Assert.assertEquals("train depart hour", "15", f.hourBox.getSelectedItem());
        Assert.assertEquals("train depart minute", "45", f.minuteBox.getSelectedItem());
        Assert.assertEquals("train route", t2.getRoute(), f.routeBox.getSelectedItem());
        Assert.assertEquals("number of engines", "3", f.numEnginesBox.getSelectedItem());
        Assert.assertEquals("engine model", "FT", f.modelEngineBox.getSelectedItem());
        Assert.assertEquals("engine road", "UP", f.roadEngineBox.getSelectedItem());
        Assert.assertEquals("caboose road", "CP", f.roadCabooseBox.getSelectedItem());
        // check radio buttons
        Assert.assertTrue("caboose selected", f.cabooseRadioButton.isSelected());
        Assert.assertFalse("none selected", f.noneRadioButton.isSelected());
        Assert.assertFalse("FRED selected", f.fredRadioButton.isSelected());

        ThreadingUtil.runOnGUI(() -> {
            JUnitUtil.dispose(f);
        });
    }

    @Test
    public void testTrainEditFrameAddButton() {
        TrainEditFrame trainEditFrame = new TrainEditFrame(null);
        trainEditFrame.setTitle("Test Add Button Train Frame");
        // fill in name and description fields
        trainEditFrame.trainNameTextField.setText("Test Add Train Name");
        trainEditFrame.trainDescriptionTextField.setText("Test Train Description");
        trainEditFrame.commentTextArea.setText("Test Train Comment");

        JemmyUtil.enterClickAndLeave(trainEditFrame.addTrainButton);

        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.getTrainByName("Test Add Train Name");
        Assert.assertNotNull("train exists", train);

        // test defaults
        Assert.assertEquals("train name", "Test Add Train Name", train.getName());
        Assert.assertEquals("train description", "Test Train Description", train.getDescription());
        Assert.assertEquals("train comment", "Test Train Comment", train.getCommentWithColor());
        Assert.assertEquals("train depart time", "00:00", train.getDepartureTime());
        Assert.assertEquals("train route", null, train.getRoute());
        Assert.assertTrue("train accepts car type Boxcar", train.isTypeNameAccepted("Boxcar"));
        Assert.assertEquals("train roads", Train.ALL_ROADS, train.getCarRoadOption());
        Assert.assertEquals("train requirements", Train.NO_CABOOSE_OR_FRED, train.getRequirements());

        JUnitUtil.dispose(trainEditFrame);

        // test that you can't add a train with the same name
        trainEditFrame = new TrainEditFrame(null);
        trainEditFrame.setTitle("Test Edit Train Frame");
        // fill in name and description fields
        trainEditFrame.trainNameTextField.setText("Test Add Train Name");

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.addTrainButton);
        // clear can not add train dialog box
        JemmyUtil.pressDialogButton(trainEditFrame,
                MessageFormat.format(Bundle.getMessage("CanNot"), new Object[] { Bundle.getMessage("add") }),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);
        JUnitUtil.dispose(trainEditFrame);
    }

    @Test
    public void testTrainEditFrameAddButtonTrainNameTooLong() {
        TrainEditFrame trainEditFrame = new TrainEditFrame(null);
        trainEditFrame.setTitle("Test Add Button Train Frame");
        // fill in name and description fields
        trainEditFrame.trainNameTextField.setText("Test Add Train Name Too Long More Than 25 Characters");

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.addTrainButton);

        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.getTrainByName("Test Add Train Name Too Long More Than 25 Characters");
        Assert.assertNull("train does not exist", train);

        // clear can not add train dialog box
        JemmyUtil.pressDialogButton(trainEditFrame,
                MessageFormat.format(Bundle.getMessage("CanNot"), new Object[] { Bundle.getMessage("add") }),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);
        JUnitUtil.dispose(trainEditFrame);
    }

    @Test
    public void testTrainEditFrameAddButtonTrainNameSpecialCharacter() {
        TrainEditFrame trainEditFrame = new TrainEditFrame(null);
        trainEditFrame.setTitle("Test Add Button Train Frame");
        // fill in name and description fields
        trainEditFrame.trainNameTextField.setText("Not this character :");

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.addTrainButton);

        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.getTrainByName("Not this character :");
        Assert.assertNull("train does not exist", train);

        // clear can not add train dialog box
        JemmyUtil.pressDialogButton(trainEditFrame,
                MessageFormat.format(Bundle.getMessage("CanNot"), new Object[] { Bundle.getMessage("add") }),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);
        jmri.util.JUnitAppender.assertErrorMessage("Train name must not contain reserved characters");

        JUnitUtil.dispose(trainEditFrame);
    }


    /**
     * Test that you can't save using an existing train's name
     */
    @Test
    public void testTrainEditFrameSaveExistingTrain() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.getTrainByName("Test_Train 1");
        Assert.assertNotNull(train);

        Train train2 = tmanager.getTrainByName("Test_Train 2");
        Assert.assertNotNull(train2);

        TrainEditFrame trainEditFrame = new TrainEditFrame(train);
        trainEditFrame.setTitle("Test Save Train Frame");
        trainEditFrame.trainNameTextField.setText("Test_Train 2");

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.saveTrainButton);

        // And now press the confirmation button
        JemmyUtil.pressDialogButton(trainEditFrame,
                MessageFormat.format(Bundle.getMessage("CanNot"), new Object[] { Bundle.getMessage("save") }),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);
        JUnitUtil.dispose(trainEditFrame);
    }

    @Test
    public void testTrainEditFrameSaveButton() {
        // create a train
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.newTrain("Test Train Save Button");
        Assert.assertNotNull(train);

        // give the train a route with one location
        RouteManager rmanager = InstanceManager.getDefault(RouteManager.class);
        Route routeB = rmanager.getRouteByName("Test Route B");

        Location loc = InstanceManager.getDefault(LocationManager.class).getLocationByName("Test_Location 1");
        Assert.assertNotNull("Confirm location exsists", loc);

        RouteLocation rB = routeB.addLocation(loc);
        rB.setDepartureTime("10", "12");

        train.setRoute(routeB);

        train.setDescription("Test Train Save Button Description");
        train.setComment("Test Train Save Button Comment");
        train.setRequirements(Train.CABOOSE);
        train.setNumberEngines("2");
        train.setEngineModel("GP40");
        train.setEngineRoad("PU");
        train.setCabooseRoad("CP");

        TrainEditFrame trainEditFrame = new TrainEditFrame(train);
        trainEditFrame.setTitle("Test Edit Train Frame");

        Assert.assertEquals("train name", "Test Train Save Button", trainEditFrame.trainNameTextField.getText());
        Assert.assertEquals("train description", "Test Train Save Button Description",
                trainEditFrame.trainDescriptionTextField.getText());
        Assert.assertEquals("train comment", "Test Train Save Button Comment",
                trainEditFrame.commentTextArea.getText());
        Assert.assertEquals("train depart hour", "10", trainEditFrame.hourBox.getSelectedItem());
        Assert.assertEquals("train depart minute", "12", trainEditFrame.minuteBox.getSelectedItem());
        Assert.assertEquals("train route", train.getRoute(), trainEditFrame.routeBox.getSelectedItem());
        Assert.assertEquals("number of engines", "2", trainEditFrame.numEnginesBox.getSelectedItem());
        Assert.assertEquals("engine model", "GP40", trainEditFrame.modelEngineBox.getSelectedItem());
        Assert.assertEquals("engine road", "PU", trainEditFrame.roadEngineBox.getSelectedItem());
        Assert.assertEquals("caboose road", "CP", trainEditFrame.roadCabooseBox.getSelectedItem());
        // check radio buttons
        Assert.assertTrue("caboose selected", trainEditFrame.cabooseRadioButton.isSelected());
        Assert.assertFalse("none selected", trainEditFrame.noneRadioButton.isSelected());
        Assert.assertFalse("FRED selected", trainEditFrame.fredRadioButton.isSelected());

        // test departure time fields
        Assert.assertFalse("Confirm hour is disabled", trainEditFrame.hourBox.isEnabled());
        Assert.assertFalse("Confirm minute is disabled", trainEditFrame.minuteBox.isEnabled());
        Assert.assertEquals("train departure time", "10:12", train.getDepartureTime());

        // test route field, 5 routes and a blank
        Assert.assertEquals("Route Combobox item count", 6, trainEditFrame.routeBox.getItemCount());

        trainEditFrame.routeBox.setSelectedIndex(3); // the 3rd item should be "Test Route C"
        Assert.assertTrue("Confirm hour is enabled", trainEditFrame.hourBox.isEnabled());
        Assert.assertTrue("Confirm minute is enabled", trainEditFrame.minuteBox.isEnabled());
        Assert.assertEquals("train depart hour", "00", trainEditFrame.hourBox.getSelectedItem());
        Assert.assertEquals("train depart minute", "00", trainEditFrame.minuteBox.getSelectedItem());

        Assert.assertEquals("train route", "Test Route C", train.getRoute().getName());
        // test route edit button
        JemmyUtil.enterClickAndLeave(trainEditFrame.editButton);

        // confirm panel creation
        JmriJFrame ref = JmriJFrame.getFrame(Bundle.getMessage("TitleRouteEdit"));
        Assert.assertNotNull("route add frame", ref);

        // increase screen size so clear and set buttons are shown
        trainEditFrame.setLocation(10, 0);
        trainEditFrame.setSize(trainEditFrame.getWidth(), trainEditFrame.getHeight() + 200);

        // test car types using the clear and set buttons
        JemmyUtil.enterClickAndLeave(trainEditFrame.clearButton);
        Assert.assertFalse("train accepts car type Boxcar", train.isTypeNameAccepted("Boxcar"));

        JemmyUtil.enterClickAndLeave(trainEditFrame.setButton);
        Assert.assertTrue("train accepts car type Boxcar", train.isTypeNameAccepted("Boxcar"));

        // now change them
        // ThreadingUtil.runOnGUI(() -> {
        trainEditFrame.numEnginesBox.setSelectedItem("3");
        trainEditFrame.modelEngineBox.setSelectedItem("FT");
        trainEditFrame.roadEngineBox.setSelectedItem("UP");
        // });
        // shouldn't change until Save
        Assert.assertEquals("number of engines 1", "2", train.getNumberEngines());
        Assert.assertEquals("engine model 1", "GP40", train.getEngineModel());
        Assert.assertEquals("engine road 1", "PU", train.getEngineRoad());
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        // confirm changes
        Assert.assertEquals("number of engines 2", "3", train.getNumberEngines());
        Assert.assertEquals("engine model 2", "FT", train.getEngineModel());
        Assert.assertEquals("engine road 2", "UP", train.getEngineRoad());

        // test caboose and FRED buttons and fields
        // require a car with FRED
        JemmyUtil.enterClickAndLeave(trainEditFrame.fredRadioButton);

        // shouldn't change until Save
        Assert.assertEquals("train requirements", Train.CABOOSE, train.getRequirements());
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train requirements FRED", Train.FRED, train.getRequirements());

        JemmyUtil.enterClickAndLeave(trainEditFrame.cabooseRadioButton);
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train requirements Caboose", Train.CABOOSE, train.getRequirements());

        Assert.assertEquals("caboose road 1", "", train.getCabooseRoad());
        // shouldn't change until Save
        String roadNames[] = Bundle.getMessage("carRoadNames").split(",");
        // ThreadingUtil.runOnGUI(() -> {
        trainEditFrame.roadCabooseBox.setSelectedItem(roadNames[2]);
        // });
        Assert.assertEquals("caboose road", "", train.getCabooseRoad());
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("caboose road new", roadNames[2], train.getCabooseRoad());

        // remove Caboose or FRED requirement
        JemmyUtil.enterClickAndLeave(trainEditFrame.noneRadioButton);
        JemmyUtil.enterClickAndLeave(trainEditFrame.saveTrainButton);
        Assert.assertEquals("train requirements 4", Train.NO_CABOOSE_OR_FRED, train.getRequirements());

        // test delete button
        // the delete opens a dialog window to confirm the delete
        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.deleteTrainButton);
        JemmyUtil.pressDialogButton(trainEditFrame, Bundle.getMessage("deleteTrain"), Bundle.getMessage("ButtonNo"));
        JemmyUtil.waitFor(trainEditFrame);

        // confirm that train wasn't deleted
        Train t2 = tmanager.getTrainByName("Test Train Save Button");
        Assert.assertNotNull(t2);

        JUnitUtil.dispose(ref);
        JUnitUtil.dispose(trainEditFrame);
    }

    @Test
    public void testTrainEditFrameNoRoute() {
        // create a train
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.newTrain("Test Train No Route");
        Assert.assertNotNull(train);

        TrainEditFrame trainEditFrame = new TrainEditFrame(train);
        JemmyUtil.waitFor(trainEditFrame);

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.saveTrainButton);
        // clear no route dialog box
        JemmyUtil.pressDialogButton(trainEditFrame, Bundle.getMessage("TrainNoRoute"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);
        JUnitUtil.dispose(trainEditFrame);
    }

    @Test
    public void testTrainEditFrameNoName() {
        // create a train
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.newTrain("Test Train No Name");
        Assert.assertNotNull(train);

        // give the train a route
        RouteManager rmanager = InstanceManager.getDefault(RouteManager.class);
        Route routeB = rmanager.getRouteByName("Test Route B");
        train.setRoute(routeB);

        TrainEditFrame trainEditFrame = new TrainEditFrame(train);
        trainEditFrame.setTitle("Test Edit Train Frame");

        trainEditFrame.trainNameTextField.setText("");

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.saveTrainButton);
        // clear can not save train
        JemmyUtil.pressDialogButton(trainEditFrame,
                MessageFormat.format(Bundle.getMessage("CanNot"), new Object[] { Bundle.getMessage("save") }),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);
        JUnitUtil.dispose(trainEditFrame);
    }

    /**
     * Test that delete train works
     */
    @Test
    public void testTrainEditFrameDelete() {
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train = tmanager.getTrainByName("Test_Train 1");
        Assert.assertNotNull(train);

        TrainEditFrame trainEditFrame = new TrainEditFrame(train);
        trainEditFrame.setTitle("Test Delete Train Frame");

        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.deleteTrainButton);
        // And now press the confirmation button
        JemmyUtil.pressDialogButton(trainEditFrame, Bundle.getMessage("deleteTrain"), Bundle.getMessage("ButtonYes"));
        JemmyUtil.waitFor(trainEditFrame);

        train = tmanager.getTrainByName("Test_Train 1");
        Assert.assertNull("train deleted", train);

        // Now add it back
        JemmyUtil.enterClickAndLeave(trainEditFrame.addTrainButton);

        train = tmanager.getTrainByName("Test_Train 1");
        Assert.assertNotNull("train added", train);

        JUnitUtil.dispose(trainEditFrame);
    }

    /**
     * Test the reset train button
     */
    @Test
    public void testTrainEditFrameReset() {

        JUnitOperationsUtil.initOperationsData();
        TrainManager tmanager = InstanceManager.getDefault(TrainManager.class);
        Train train1 = tmanager.getTrainById("1");
        Assert.assertNotNull(train1);

        train1.build();
        Assert.assertTrue("Train status", train1.isBuilt());

        TrainEditFrame trainEditFrame = new TrainEditFrame(train1);
        trainEditFrame.setTitle("Test Reset Train Frame");

        JemmyUtil.enterClickAndLeave(trainEditFrame.resetButton);
        Assert.assertFalse("Train status", train1.isBuilt());

        // now test trying to reset a train that is en-route
        train1.build();
        Assert.assertTrue("Train status", train1.isBuilt());
        train1.move();

        // should fail
        JemmyUtil.enterClickAndLeaveThreadSafe(trainEditFrame.resetButton);
        // clear the error dialog
        JemmyUtil.pressDialogButton(trainEditFrame, Bundle.getMessage("CanNotResetTrain"),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(trainEditFrame);

        Assert.assertTrue("Train status", train1.isBuilt());

        JUnitUtil.dispose(trainEditFrame);
        JUnitOperationsUtil.checkOperationsShutDownTask();
    }

    @Test
    public void testCloseWindowOnSave() {
        Train train = new Train("TESTTRAINID", "TESTTRAINNAME");
        Route route = new Route("ROUTEID","ROUTENAME");
        train.setRoute(route);
        TrainEditFrame f = new TrainEditFrame(train);
        JUnitOperationsUtil.testCloseWindowOnSave(f.getTitle());
    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        JUnitOperationsUtil.loadTrains();
    }

    // private final static Logger log =
    // LoggerFactory.getLogger(TrainEditFrameTest.class);
}
