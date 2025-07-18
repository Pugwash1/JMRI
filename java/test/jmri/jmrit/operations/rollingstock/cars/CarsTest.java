package jmri.jmrit.operations.rollingstock.cars;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsTestCase;
import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.Track;
import jmri.util.JUnitOperationsUtil;

/**
 * Tests for the Operations RollingStock Cars class Last manually cross-checked
 * on 20090131
 * <p>
 * Still to do: Everything
 *
 * @author Bob Coleman Copyright (C) 2008, 2009
 */
public class CarsTest extends OperationsTestCase {

    // test constructors.
    @Test
    public void testCtor() {
        // test the default constructor.
        Car c1 = new Car();
        Assert.assertNotNull("Default Constructor", c1);
    }

    @Test
    public void test2ParmCtor() {
        // test the constructor with roadname and roadnumer as parameters.
        Car c1 = new Car("TESTROAD", "TESTNUMBER1");
        Assert.assertNotNull("Two parameter Constructor", c1);

        Assert.assertEquals("Car Road", "TESTROAD", c1.getRoadName());
        Assert.assertEquals("Car Number", "TESTNUMBER1", c1.getNumber());
        Assert.assertEquals("Car ID", "TESTROAD" + "TESTNUMBER1", c1.getId());
    }

    @Test
    public void testXmlConstructor() {
        // test the constructor loading this car from an XML element.

        // first, we need to build the XML element.
        org.jdom2.Element e = new org.jdom2.Element("cars");
        // set the rolling stock generic attributes.
        e.setAttribute("id", "TESTID");
        e.setAttribute("roadName", "TESTROAD1");
        e.setAttribute("roadNumber", "TESTNUMBER1");
        e.setAttribute(Xml.TYPE, "TESTTYPE");
        e.setAttribute(Xml.LENGTH, "TESTLENGTH");
        e.setAttribute(Xml.COLOR, "TESTCOLOR");
        e.setAttribute("weight", "TESTWEIGHT");
        e.setAttribute("weightTons", "TESTWEIGHTTONS");
        e.setAttribute("built", "TESTBUILT");
        e.setAttribute("locationId", "TESTLOCATION");
        e.setAttribute("routeLocationId", "TESTROUTELOCATION");
        e.setAttribute("secLocationId", "TESTTRACK");
        e.setAttribute("destinationId", "TESTDESTINATION");
        e.setAttribute("routeDestinationId", "TESTROUTEDESTINATION");
        e.setAttribute("secDestionationId", "TESTDESTINATIONTRACK");
        e.setAttribute("lastRouteId", "SAVEDROUTE");
        e.setAttribute("moves", "BAD5");
        e.setAttribute("date", "2015/05/15 15:15:15");
        e.setAttribute("selected", Xml.FALSE);
        e.setAttribute("lastLocationId", "TESTLASTLOCATION");
        e.setAttribute("train", "TESTTRAIN");
        e.setAttribute("owner", "TESTOWNER");
        e.setAttribute("value", "TESTVALUE");
        e.setAttribute("rifd", "12345");
        e.setAttribute("locUnknown", Xml.FALSE);
        e.setAttribute("outOfService", Xml.FALSE);
        e.setAttribute("blocking", "BAD6");
        e.setAttribute("comment", "TESTCOMMENT");
        e.setAttribute(Xml.PASSENGER, Xml.FALSE);
        e.setAttribute(Xml.HAZARDOUS, Xml.FALSE);
        e.setAttribute(Xml.CABOOSE, Xml.TRUE);
        e.setAttribute(Xml.FRED, Xml.FALSE);
        e.setAttribute(Xml.UTILITY, Xml.FALSE);
        e.setAttribute(Xml.KERNEL, "TESTKERNEL");
        e.setAttribute(Xml.LEAD_KERNEL, Xml.FALSE);
        e.setAttribute(Xml.LOAD, "TESTLOAD");
        e.setAttribute(Xml.LOAD_FROM_STAGING, Xml.TRUE);
        e.setAttribute(Xml.WAIT, "BAD7");
        e.setAttribute(Xml.PICKUP_SCHEDULE_ID, "TESTPICKUPSCHEDULE");
        e.setAttribute(Xml.SCHEDULE_ID, "TESTSCHEDULEID");
        e.setAttribute(Xml.NEXT_DEST_ID, "TESTNEXTDESTID");
        e.setAttribute(Xml.NEXT_DEST_TRACK_ID, "TESTNEXTDESTTRACKID");
        e.setAttribute(Xml.PREVIOUS_NEXT_DEST_ID, "TESTPREVOIUSNEXTDESTID");
        e.setAttribute(Xml.PREVIOUS_NEXT_DEST_TRACK_ID, "TESTPREVOUSNEXTDESTTRACKID");
        e.setAttribute(Xml.PREVIOUS_SCHEDULE_ID, "TESTPREVIOUSSCHEDULEID");
        e.setAttribute(Xml.RWE_DEST_ID, "TESTRWEDESTID");
        e.setAttribute(Xml.RWE_LOAD, "TESTRWELOAD");
        e.setAttribute(Xml.RWL_DEST_ID, "TESTRWLDESTID");
        e.setAttribute(Xml.RWL_LOAD, "TESTRWLLOAD");
        e.setAttribute(Xml.ROUTE_PATH, "TESTROUTEPATH");

        Assertions.assertDoesNotThrow( () -> {
            Car c1 = new Car(e);
            Assertions.assertNotNull(c1, "Xml Element Constructor");
            Assert.assertEquals("Car Road", "TESTROAD1", c1.getRoadName());
            Assert.assertEquals("Car Number", "TESTNUMBER1", c1.getNumber());
            Assert.assertEquals("Car ID", "TESTROAD1" + "TESTNUMBER1", c1.getId());
            Assert.assertEquals("Car Type", "TESTTYPE", c1.getTypeName());
            Assert.assertEquals("Car Length", "TESTLENGTH", c1.getLength());
            Assert.assertEquals("Car Color", "TESTCOLOR", c1.getColor());
            Assert.assertFalse("Car Hazardous", c1.isHazardous());
            Assert.assertFalse("Car Fred", c1.hasFred());
            Assert.assertTrue("Car Caboose", c1.isCaboose());
            Assert.assertEquals("Car Weight", "TESTWEIGHT", c1.getWeight());
            Assert.assertEquals("Car Built", "TESTBUILT", c1.getBuilt());
            Assert.assertEquals("Car Owner", "TESTOWNER", c1.getOwnerName());
            Assert.assertEquals("Car Comment", "TESTCOMMENT", c1.getComment());
            Assert.assertEquals("Car Load", "TESTLOAD", c1.getLoadName());
            Assert.assertEquals("Car RWE Load", "TESTRWELOAD", c1.getReturnWhenEmptyLoadName());
            Assert.assertEquals("Car RWL Load", "TESTRWLLOAD", c1.getReturnWhenLoadedLoadName());
            Assert.assertEquals("TESTROUTEPATH", c1.getRoutePath());
        } ,"Exception while executing Xml Element Constructor");

        jmri.util.JUnitAppender
                .assertErrorMessage("Move count (BAD5) for rollingstock (TESTROAD1 TESTNUMBER1) isn't a valid number!");
        jmri.util.JUnitAppender
                .assertErrorMessage("Blocking (BAD6) for rollingstock (TESTROAD1 TESTNUMBER1) isn't a valid number!");
        jmri.util.JUnitAppender.assertErrorMessage("Kernel TESTKERNEL does not exist");
        jmri.util.JUnitAppender
                .assertErrorMessage("Wait count (BAD7) for car (TESTROAD1 TESTNUMBER1) isn't a valid number!");
    }

    // test creation
    @Test
    public void testCreate() {
        Car c1 = new Car("TESTROAD", "TESTNUMBER1");
        c1.setTypeName("TESTTYPE");
        c1.setLength("TESTLENGTH");
        c1.setColor("TESTCOLOR");
        c1.setCarHazardous(true);
        c1.setFred(true);
        c1.setCaboose(true);
        c1.setWeight("TESTWEIGHT");
        c1.setBuilt("TESTBUILT");
        c1.setOwnerName("TESTOWNER");
        c1.setComment("TESTCOMMENT");
        c1.setMoves(5);

        Assert.assertEquals("Car Road", "TESTROAD", c1.getRoadName());
        Assert.assertEquals("Car Number", "TESTNUMBER1", c1.getNumber());
        Assert.assertEquals("Car ID", "TESTROAD" + "TESTNUMBER1", c1.getId());
        Assert.assertEquals("Car Type", "TESTTYPE", c1.getTypeName());
        Assert.assertEquals("Car Length", "TESTLENGTH", c1.getLength());
        Assert.assertEquals("Car Color", "TESTCOLOR", c1.getColor());
        Assert.assertTrue("Car Hazardous", c1.isHazardous());
        Assert.assertTrue("Car Fred", c1.hasFred());
        Assert.assertTrue("Car Caboose", c1.isCaboose());
        Assert.assertEquals("Car Weight", "TESTWEIGHT", c1.getWeight());
        Assert.assertEquals("Car Built", "TESTBUILT", c1.getBuilt());
        Assert.assertEquals("Car Owner", "TESTOWNER", c1.getOwnerName());
        Assert.assertEquals("Car Comment", "TESTCOMMENT", c1.getComment());
        Assert.assertEquals("Car Moves", 5, c1.getMoves());
    }

    // test setting the location
    @Test
    public void testSetLocation() {
        CarManager manager = InstanceManager.getDefault(CarManager.class);

        Car c1 = manager.newRS("CP", "1");
        Car c2 = manager.newRS("ACL", "3");
        Car c3 = manager.newRS("CP", "3");
        Car c4 = manager.newRS("CP", "3-1");
        Car c5 = manager.newRS("PC", "2");
        Car c6 = manager.newRS("AA", "1");

        //setup the cars
        c1.setTypeName("Boxcar");
        c2.setTypeName("Boxcar");
        c3.setTypeName("Boxcar");
        c4.setTypeName("Boxcar");
        c5.setTypeName("Boxcar");
        c6.setTypeName("Boxcar");

        c1.setLength("13");
        c2.setLength("9");
        c3.setLength("12");
        c4.setLength("10");
        c5.setLength("11");
        c6.setLength("14");
        Location l1 = new Location("id1", "B");
        Track l1t1 = l1.addTrack("A", Track.SPUR);
        Track l1t2 = l1.addTrack("B", Track.SPUR);
        Location l2 = new Location("id2", "C");
        Track l2t1 = l2.addTrack("B", Track.SPUR);
        Track l2t2 = l2.addTrack("A", Track.SPUR);
        Location l3 = new Location("id3", "A");
        Track l3t1 = l3.addTrack("B", Track.SPUR);
        Track l3t2 = l3.addTrack("A", Track.SPUR);

        // add track lengths
        l1t1.setLength(100);
        l1t2.setLength(100);
        l2t1.setLength(100);
        l2t2.setLength(100);
        l3t1.setLength(100);
        l3t2.setLength(100);

        l1.addTypeName("Boxcar");
        l2.addTypeName("Boxcar");
        l3.addTypeName("Boxcar");
        l1t1.addTypeName("Boxcar");
        l1t2.addTypeName("Boxcar");
        l2t1.addTypeName("Boxcar");
        l2t2.addTypeName("Boxcar");
        l3t1.addTypeName("Boxcar");
        l3t2.addTypeName("Boxcar");

        CarTypes ct = InstanceManager.getDefault(CarTypes.class);
        ct.addName("Boxcar");

        // place cars on tracks
        Assert.assertEquals("place c1", Track.OKAY, c1.setLocation(l1, l1t1));
        Assert.assertEquals("place c2", Track.OKAY, c2.setLocation(l1, l1t2));
        Assert.assertEquals("place c3", Track.OKAY, c3.setLocation(l2, l2t1));
        Assert.assertEquals("place c4", Track.OKAY, c4.setLocation(l2, l2t2));
        Assert.assertEquals("place c5", Track.OKAY, c5.setLocation(l3, l3t1));
        Assert.assertEquals("place c6", Track.OKAY, c6.setLocation(l3, l3t2));

    }

    // test setting the destination
    @Test
    public void testSetDestination() {
        CarManager manager = InstanceManager.getDefault(CarManager.class);

        Car c1 = manager.newRS("CP", "1");
        Car c2 = manager.newRS("ACL", "3");
        Car c3 = manager.newRS("CP", "3");
        Car c4 = manager.newRS("CP", "3-1");
        Car c5 = manager.newRS("PC", "2");
        Car c6 = manager.newRS("AA", "1");

        //setup the cars
        c1.setTypeName("Boxcar");
        c2.setTypeName("Boxcar");
        c3.setTypeName("Boxcar");
        c4.setTypeName("Boxcar");
        c5.setTypeName("Boxcar");
        c6.setTypeName("Boxcar");

        c1.setLength("13");
        c2.setLength("9");
        c3.setLength("12");
        c4.setLength("10");
        c5.setLength("11");
        c6.setLength("14");
        Location l1 = new Location("id1", "B");
        Track l1t1 = l1.addTrack("A", Track.SPUR);
        Track l1t2 = l1.addTrack("B", Track.SPUR);
        Location l2 = new Location("id2", "C");
        Track l2t1 = l2.addTrack("B", Track.SPUR);
        Track l2t2 = l2.addTrack("A", Track.SPUR);
        Location l3 = new Location("id3", "A");
        Track l3t1 = l3.addTrack("B", Track.SPUR);
        Track l3t2 = l3.addTrack("A", Track.SPUR);

        // add track lengths
        l1t1.setLength(100);
        l1t2.setLength(100);
        l2t1.setLength(100);
        l2t2.setLength(100);
        l3t1.setLength(100);
        l3t2.setLength(100);

        l1.addTypeName("Boxcar");
        l2.addTypeName("Boxcar");
        l3.addTypeName("Boxcar");
        l1t1.addTypeName("Boxcar");
        l1t2.addTypeName("Boxcar");
        l2t1.addTypeName("Boxcar");
        l2t2.addTypeName("Boxcar");
        l3t1.addTypeName("Boxcar");
        l3t2.addTypeName("Boxcar");

        CarTypes ct = InstanceManager.getDefault(CarTypes.class);
        ct.addName("Boxcar");

        // place cars on tracks
        c1.setLocation(l1, l1t1);
        c2.setLocation(l1, l1t2);
        c3.setLocation(l2, l2t1);
        c4.setLocation(l2, l2t2);
        c5.setLocation(l3, l3t1);
        c6.setLocation(l3, l3t2);

        // set car destinations
        Assert.assertEquals("destination c1", Track.OKAY, c1.setDestination(l3, l3t1));
        Assert.assertEquals("destination c2", Track.OKAY, c2.setDestination(l3, l3t2));
        Assert.assertEquals("destination c3", Track.OKAY, c3.setDestination(l2, l2t2));
        Assert.assertEquals("destination c4", Track.OKAY, c4.setDestination(l2, l2t1));
        Assert.assertEquals("destination c5", Track.OKAY, c5.setDestination(l1, l1t1));
        Assert.assertEquals("destination c6", Track.OKAY, c6.setDestination(l1, l1t2));
    }

    @Test
    public void testKernel() {

        CarTypes ct = InstanceManager.getDefault(CarTypes.class);
        ct.addName("Boxcar");
        ct.addName("Flatcar");
        ct.addName("Gon");

        Location westford = JUnitOperationsUtil.createOneNormalLocation("Westford");
        Track spur1 = westford.getTrackByName("Westford Spur 1", null);
        Track yard1 = westford.getTrackByName("Westford Yard 1", null);
        Track yard2 = westford.getTrackByName("Westford Yard 2", null);

        CarLoads carLoads = InstanceManager.getDefault(CarLoads.class);
        carLoads.addName("Boxcar", "Nuts");
        carLoads.addName("Flatcar", "Steel");
        carLoads.addName("Gon", "Scrap");

        Car c1 = JUnitOperationsUtil.createAndPlaceCar("CP", "10", "Boxcar", "40", spur1, 10);
        Car c2 = JUnitOperationsUtil.createAndPlaceCar("CP", "20", "Flatcar", "40", spur1, 11);
        Car c3 = JUnitOperationsUtil.createAndPlaceCar("CP", "30", "Gon", "40", spur1, 12);

        // put all three cars in a kernel
        Kernel k = InstanceManager.getDefault(KernelManager.class).newKernel("K");
        c1.setKernel(k);
        c2.setKernel(k);
        c3.setKernel(k);

        c1.setLoadName("Nuts");
        c1.setDestination(westford, yard1);

        // try updating the non-lead car, does nothing
        c2.updateKernel();
        Assert.assertEquals("Car load", "Nuts", c1.getLoadName());
        Assert.assertEquals("Car load", "E", c2.getLoadName());
        Assert.assertEquals("Car load", "E", c3.getLoadName());

        // the update will add a custom load if available to non-lead cars
        c1.updateKernel();
        Assert.assertEquals("Car load", "Nuts", c1.getLoadName());
        Assert.assertEquals("Car load", "Steel", c2.getLoadName());
        Assert.assertEquals("Car load", "Scrap", c3.getLoadName());

        // disallow steel
        yard1.setLoadOption(Track.EXCLUDE_LOADS);
        yard1.addLoadName("Steel");
        c2.setLoadName("Something");

        c1.updateKernel();
        Assert.assertEquals("Car load", "Nuts", c1.getLoadName());
        Assert.assertEquals("Car load", "Something", c2.getLoadName());
        Assert.assertEquals("Car load", "Scrap", c3.getLoadName());

        // provide a final destination
        c1.setFinalDestinationTrack(yard2);

        c1.updateKernel();
        Assert.assertEquals("Car load", "Nuts", c1.getLoadName());
        Assert.assertEquals("Car load", "Steel", c2.getLoadName());
        Assert.assertEquals("Car load", "Scrap", c3.getLoadName());

        // allow non-lead car to have the same load name as the lead car
        carLoads.addName("Flatcar", "Nuts");

        c1.updateKernel();
        Assert.assertEquals("Car load", "Nuts", c1.getLoadName());
        Assert.assertEquals("Car load", "Nuts", c2.getLoadName());
        Assert.assertEquals("Car load", "Scrap", c3.getLoadName());

    }
}
