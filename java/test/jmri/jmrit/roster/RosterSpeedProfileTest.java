package jmri.jmrit.roster;

import java.util.HashMap;

import jmri.util.JUnitUtil;
import jmri.DccThrottle;
import jmri.SpeedStepMode;
import jmri.InstanceManager;
import jmri.LocoAddress;
import jmri.ThrottleListener;
import jmri.ThrottleManager;
import jmri.implementation.SignalSpeedMap;
import jmri.jmrit.roster.RosterSpeedProfile.SpeedSetting;
import jmri.util.JUnitAppender;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class RosterSpeedProfileTest {

    private boolean throttleResult;
    private DccThrottle throttle;

    private class ThrottleListen implements ThrottleListener {

        @Override
        public void notifyThrottleFound(DccThrottle t){
            throttleResult = true;
            throttle = t;
        }

        @Override
        public void notifyFailedThrottleRequest(LocoAddress address, String reason){
            throttleResult = false;
        }

        @Override
        public void notifyDecisionRequired(LocoAddress address, DecisionType question) {
            if ( question == DecisionType.STEAL ){
                throttleResult = false;
            }
        }
    }

    @Test
    public void testCTor() {
        RosterSpeedProfile t = new RosterSpeedProfile(new RosterEntry());
        Assert.assertNotNull("exists",t);
    }

    private float globalTotalDistanceTolerance = 0.1f;
    private float testScene(org.jdom2.Element profile, float currentSpeed,
            float newSpeed, float testDistance,
            float minSpeed, float maxSpeed, SpeedStepMode speedStepMode) {
        // statics for test objects
        org.jdom2.Element f1 = profile;
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        ThrottleListener throtListen = new ThrottleListen();
        ThrottleManager tm = InstanceManager.getDefault(ThrottleManager.class);
        boolean OK = tm.requestThrottle(rF1, throtListen, throttleResult);
        Assert.assertTrue("Throttle request denied", OK);
        JUnitUtil.waitFor(() -> (throttleResult), "Got No throttle");
        throttle.setSpeedStepMode(speedStepMode);
        throttle.setIsForward(true);
        throttle.setSpeedSetting(currentSpeed);
        RosterSpeedProfile sp = rF1.getSpeedProfile();
        float mmFactor = sp.getForwardSpeed(1.0f);
        sp.setTestMode(true);
        sp.setExtraInitialDelay(0f);
        sp.setMinMaxLimits(minSpeed, maxSpeed);
        sp.changeLocoSpeed(throttle, testDistance, newSpeed);
        // Allow speed step table to be constructed
        //JUnitUtil.waitFor(2000);
        float totalDistance = 0.0f;
        for (SpeedSetting ss : sp.getSpeedStepTrace()) {
            totalDistance += (ss.getDuration() / 1000.0f) * (ss.getSpeedStep() * mmFactor);
        }

        sp.cancelSpeedChange();

        return totalDistance;
    }

    @Test
    public void testScenefiftyFromZeroPercent_128() {
        float testDistance = 50.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                0.0f, //current speed
                0.5f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        Assert.assertEquals("Distance not close 128 0-0.50", testDistance, actualDistance, globalTotalDistanceTolerance);
    }


    @Test
    public void testSceneStopFromFiftyPercent_128() {
        float testDistance = 500.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                0.6f, //current speed
                0.0f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        Assert.assertEquals("Distance not close 128 0.50-0", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneStopFromFiftyPercent_28() {
        float testDistance = 50.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                0.5f, //current speed
                0.0f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_28 // stepmode
                );
        Assert.assertEquals("Distance not close 28 0.50-0", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneStopFromFiftyPercent_14() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                0.5f, //current speed
                0.0f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_14 // stepmode
                );
        Assert.assertEquals("Distance not close 14 0.50-0", testDistance, actualDistance, globalTotalDistanceTolerance);
    }
    @Test
    public void testSceneStopFromStepToStepplessATad() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                1.0f/128.0f, //current speed
                1.0f/128.0f-0.0001f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        Assert.assertEquals("Distance not close 128 step1 to step1 less a tad.", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneStopFromFiftyPercentWIthMinimum() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                0.5f, //current speed 
                0.0f, // new speed
                testDistance, // distance
                0.1f, // minSpeed
                100.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        Assert.assertEquals("Distance not close 128 0.50-0 min 0.1", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneSlowFromFiftyPercentToTwenty() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement200(),  //profile
                0.6f, //current speed
                0.2f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // max speed
                );
        Assert.assertEquals("Distance not close 128 0.50-0.20", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneSlowFrom301WithMin300() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement200(),  //profile
                0.301f, //current speed
                0.2f, // new speed
                testDistance, // distance
                0.3f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        Assert.assertEquals("Distance not close 128 0.3-.301 min .3", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneFrom50to501_128() {
        float testDistance = 152.0f;
        float actualDistance = testScene(getLocoElement200(),  //profile
                0.5f, //current speed 
                0.501f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        // less than a speed step no speed change
        Assert.assertEquals("Distance not close 128 0.5-0.501", 0.0f, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneFrom50to501_28() {
        float testDistance = 152.0f;
        float actualDistance = testScene(getLocoElement200(),  //profile
                0.5f, //current speed 
                0.501f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_28 // stepmode
                );
        // distance 0.0f less than a speed step
        Assert.assertEquals("Distance not close 28 0.5-0.501", 0.0f, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneFrom50to501_14() {
        float testDistance = 152.0f;
        float actualDistance = testScene(getLocoElement200(),  //profile
                0.5f, //current speed 
                0.501f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_14 // stepmode
                );
        // less than a speed step. distance 0
        Assert.assertEquals("Distance not close 14 0.5-0.501", 0.0f, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneZeroto50_14() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement140(),  //profile
                0.0f, //current speed 
                0.5f, // new speed
                testDistance, // distance
                0.0f, // minSpeed
                1.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        // less than a speed step. distance 0
        Assert.assertEquals("Distance not close 14 0.0-0.5", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneStopFromSixtyPercentWIthMinimum_128() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                8.0f/14.0f, // 57% throttle current speed 
                0.0f, // new speed
                testDistance, // distance
                0.1f, // minSpeed
                100.0f, // max speed
                SpeedStepMode.NMRA_DCC_128 // stepmode
                );
        Assert.assertEquals("Distance not close 128 0.6-0 min 0.1", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    @Test
    public void testSceneStopFromSixtyPercentWIthMinimum_28() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                8.0f/14.0f, // 57% throttle current speed 
                0.0f, // new speed
                testDistance, // distance
                0.1f, // minSpeed
                100.0f, // max speed
                SpeedStepMode.NMRA_DCC_28 // stepmode
                );
        Assert.assertEquals("Distance not close 28 0.6-0 min 0.1", testDistance, actualDistance, globalTotalDistanceTolerance);
    }
    @Test
    public void testSceneStopFromSixtyPercentWIthMinimum_14() {
        float testDistance = 150.0f;
        float actualDistance = testScene(getLocoElement100(),  //profile
                8.0f/14.0f, // 57% throttle current speed 
                0.0f, // new speed
                testDistance, // distance
                0.1f, // minSpeed
                100.0f, // max speed
                SpeedStepMode.NMRA_DCC_14 // stepmode
                );
        Assert.assertEquals("Distance not close 14 0.6-0 min 0.1", testDistance, actualDistance, globalTotalDistanceTolerance);
    }

    private static org.jdom2.Element getLocoElement100() {
        return new org.jdom2.Element("locomotive")
            .setAttribute("id", "id info")
            .setAttribute("fileName", "file here")
            .setAttribute("roadNumber", "431")
            .setAttribute("roadName", "SP")
            .setAttribute("mfg", "Athearn")
            .setAttribute("dccAddress", "1234")
            .addContent(new org.jdom2.Element("decoder")
                .setAttribute("family", "91")
                .setAttribute("model", "33")
            )
            .addContent(new org.jdom2.Element("locoaddress")
                .addContent(new org.jdom2.Element("number").addContent("1234"))
                //As there is no throttle manager available all protocols default to dcc short
                .addContent(new org.jdom2.Element("protocol").addContent("dcc_short"))
            )
            .addContent(new org.jdom2.Element("speedprofile")
                .addContent(new org.jdom2.Element("overRunTimeForward").addContent("0.0"))
                .addContent(new org.jdom2.Element("overRunTimeReverse").addContent("0.0"))
                .addContent(new org.jdom2.Element("speeds")
                    .addContent(new org.jdom2.Element("speed")
                        .addContent(new org.jdom2.Element("step").addContent("1000"))
                        .addContent(new org.jdom2.Element("forward").addContent("100.00"))
                        .addContent(new org.jdom2.Element("reverse").addContent("100.00"))
                    )
                )
            );
    }

    private static org.jdom2.Element getLocoElement140() {
        return new org.jdom2.Element("locomotive")
            .setAttribute("id", "id info")
            .setAttribute("fileName", "file here")
            .setAttribute("roadNumber", "431")
            .setAttribute("roadName", "SP")
            .setAttribute("mfg", "Athearn")
            .setAttribute("dccAddress", "1234")
            .addContent(new org.jdom2.Element("decoder")
                .setAttribute("family", "91")
                .setAttribute("model", "33")
            )
            .addContent(new org.jdom2.Element("locoaddress")
                .addContent(new org.jdom2.Element("number").addContent("1234"))
                //As there is no throttle manager available all protocols default to dcc short
                .addContent(new org.jdom2.Element("protocol").addContent("dcc_short"))
            )
            .addContent(new org.jdom2.Element("speedprofile")
                .addContent(new org.jdom2.Element("overRunTimeForward").addContent("0.0"))
                .addContent(new org.jdom2.Element("overRunTimeReverse").addContent("0.0"))
                .addContent(new org.jdom2.Element("speeds")
                    .addContent(new org.jdom2.Element("speed")
                        .addContent(new org.jdom2.Element("step").addContent("1000"))
                        .addContent(new org.jdom2.Element("forward").addContent("140.00"))
                        .addContent(new org.jdom2.Element("reverse").addContent("140.00"))
                    )
                )
            );
    }

    @Test
    public void testSpeedProfileStopFromFiftyPercent() {
        // statics for test objects
        org.jdom2.Element f1 = getLocoElement100();
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        ThrottleListener throtListen = new ThrottleListen();
        ThrottleManager tm = InstanceManager.getDefault(ThrottleManager.class);
        boolean OK = tm.requestThrottle(rF1, throtListen, throttleResult);
        Assert.assertTrue("Throttle request denied",OK);
        JUnitUtil.waitFor(()-> (throttleResult), "Got No throttle");
        throttle.setIsForward(true);
        throttle.setSpeedSetting(0.5f);
        RosterSpeedProfile sp = rF1.getSpeedProfile();
        sp.setTestMode(true);
        float testDistance = 50.0f;
        sp.changeLocoSpeed(throttle, testDistance, 0.0f);
        // Allow speed step table to be constructed
        //JUnitUtil.waitFor(5000);
        // Note it must be a perfect 0.0
        Assert.assertEquals("Speed didnt get to a perfect zero", 0.0f, throttle.getSpeedSetting(), 0.0f);
        JUnitUtil.waitFor(()->(throttle.getSpeedSetting() == 0.00f),"Failed to reach requested speed");
        float maxDelta = 1.0f/126.0f/2.0f;  //half step
        Assert.assertEquals("SpeedStep Table has incorrect number of entries.", 7, sp.getSpeedStepTrace().size() ) ;

        int[] correctDuration = {750, 750, 750, 750, 750, 519, 0} ;
        int[] durations = new int[sp.getSpeedStepTrace().size()];
        int ix = 0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            durations[ix]= ss.getDuration();
            ix++;
        }
        Assert.assertArrayEquals("Durations are wrong",correctDuration, durations);

        float[] correctSpeed = {0.30798f, 0.17571f, 0.09067f, 0.04558f, 0.02260f, 0.01466f, 0.0f} ;
        float[] speed = new float[sp.getSpeedStepTrace().size()];
        ix=0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            speed[ix]= ss.getSpeedStep();
            ix++;
        }
        Assert.assertArrayEquals("Speeds are wrong", correctSpeed, speed, maxDelta);
        sp.cancelSpeedChange();

        Assertions.assertEquals(10.0f, sp.mmsToScaleSpeed(10, false), 0.0001);
    }
    @Test
    public void testSpeedProfileStopFrom30WIthMin30() {
        // statics for test objects
        org.jdom2.Element f1 = getLocoElement200();
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        ThrottleListener throtListen = new ThrottleListen();
        ThrottleManager tm = InstanceManager.getDefault(ThrottleManager.class);
        boolean OK = tm.requestThrottle(rF1, throtListen, throttleResult);
        Assert.assertTrue("Throttle request denied",OK);
        JUnitUtil.waitFor(()-> (throttleResult), "Got No throttle");
        throttle.setIsForward(true);
        throttle.setSpeedSetting(0.301f);
        RosterSpeedProfile sp = rF1.getSpeedProfile();
        sp.setTestMode(true);
        sp.setExtraInitialDelay(0f);
        sp.setMinMaxLimits(0.30f, 0.800f);
        float testDistance = 100.0f;
        sp.changeLocoSpeed(throttle, testDistance, 0.0f);
        // Allow speed step table to be constructed
        JUnitUtil.waitFor(2000);
        // Note it must be a perfect 30.0
        //Assert.assertEquals("Speed didnt get to a perfect zero", 0.0f, throttle.getSpeedSetting(), 0.0f);
        //JUnitUtil.waitFor(()->(throttle.getSpeedSetting() == 0.00f),"Failed to reach requested speed");
        float maxDelta = 1.0f/126.0f/2.0f;  //half step
        Assert.assertEquals("SpeedStep Table has incorrect number of entries.", 2, sp.getSpeedStepTrace().size() ) ;
        int[] correctDuration = {1663, 0} ;
        int[] durations = new int[sp.getSpeedStepTrace().size()];
        int ix = 0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            durations[ix]= ss.getDuration();
            ix++;
        }
        Assert.assertArrayEquals("Durations are wrong",correctDuration, durations);

        float[] correctSpeed = {0.3f, 0.0f} ;
        float[] speed = new float[sp.getSpeedStepTrace().size()];
        ix=0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            speed[ix]= ss.getSpeedStep();
            ix++;
        }
        Assert.assertArrayEquals("Speeds are wrong", correctSpeed, speed, maxDelta);
         sp.cancelSpeedChange();
 
         Assertions.assertEquals(10.0f, sp.mmsToScaleSpeed(10, false), 0.0001);
    }

    @Test
    public void testSpeedProfileStopFromFiftyPercentWithMinimumSpeed() {
        // statics for test objects
        org.jdom2.Element f1 = getLocoElement100();
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        ThrottleListener throtListen = new ThrottleListen();
        ThrottleManager tm = InstanceManager.getDefault(ThrottleManager.class);
        boolean OK = tm.requestThrottle(rF1, throtListen, throttleResult);
        Assert.assertTrue("Throttle request denied",OK);
        JUnitUtil.waitFor(()-> (throttleResult), "Got No throttle");
        throttle.setIsForward(true);
        throttle.setSpeedSetting(0.5f);
        RosterSpeedProfile sp = rF1.getSpeedProfile();
        sp.setTestMode(true);
        sp.setMinMaxLimits(0.1f, 1.0f);
        float testDistance = 50.0f;
        sp.changeLocoSpeed(throttle,testDistance, 0.0f);
        // Allow speed step table to be constructed
        //JUnitUtil.waitFor(5000);
        // Note it must be a perfect 0.0
        //Assert.assertEquals("Speed didnt get to a perfect zero", 0.0f, throttle.getSpeedSetting(), 0.0f);
        JUnitUtil.waitFor(()->(throttle.getSpeedSetting() == 0.00f),"Failed to reach requested speed");
        float maxDelta = 1.0f/126.0f/2.0f;  //half step
        //Assert.assertEquals("SpeedStep Table has incorrect number of entries.", 5, sp.getSpeedStepTrace().size() ) ;
        int[] correctDuration = {750, 750, 750, 361, 0} ;
        int[] durations = new int[sp.getSpeedStepTrace().size()];
        int ix = 0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            durations[ix]= ss.getDuration();
            ix++;
        }
        Assert.assertArrayEquals("Durations are wrong",correctDuration, durations);

        float[] correctSpeed = {0.31962f, 0.18434f, 0.10993f, 0.1f, 0.0f} ;
        float[] speed = new float[sp.getSpeedStepTrace().size()];
        ix=0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            speed[ix]= ss.getSpeedStep();
            ix++;
        }
        Assert.assertArrayEquals("Speeds are wrong", correctSpeed, speed, maxDelta);
        sp.cancelSpeedChange();

        Assertions.assertEquals(10.0f, sp.mmsToScaleSpeed(10, false), 0.0001);
    }
    @Test
    public void testSpeedProfileFromFiftyPercentToTwenty() {
        // statics for test objects
        org.jdom2.Element f1 = getLocoElement200();
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        ThrottleListener throtListen = new ThrottleListen();
        ThrottleManager tm = InstanceManager.getDefault(ThrottleManager.class);
        boolean OK = tm.requestThrottle(rF1, throtListen, throttleResult);
        Assert.assertTrue("Throttle request denied",OK);

        JUnitUtil.waitFor(()-> (throttleResult), "Got No throttle");
        throttle.setIsForward(true);
        throttle.setSpeedSetting(0.6f);
        RosterSpeedProfile sp = rF1.getSpeedProfile();
        sp.setTestMode(true);
        sp.changeLocoSpeed(throttle, 150.0f, 0.20f);
        // Allow speed step table to be constructed
        //JUnitUtil.waitFor(5000);
        // Note it must be a perfect 0.20
        JUnitUtil.waitFor(()->(throttle.getSpeedSetting() == 0.20f),"Failed to reach requested speed");
        //Assert.assertEquals("Speed didnt get to a perfect 20", 0.20f, throttle.getSpeedSetting(), 0.00f);
        float maxDelta = 1.0f/126.0f/2.0f;  //half step
        //Assert.assertEquals("SpeedStep Table has lincorrect number of entries.", 4, sp.getSpeedStepTrace().size() ) ;
        int[] correctDuration = {750, 750, 750, 514} ;
        int[] durations = new int[sp.getSpeedStepTrace().size()];
        int ix = 0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            durations[ix]= ss.getDuration();
            ix++;
        }
        Assert.assertArrayEquals("Durations are wrong",correctDuration, durations);

        float[] correctSpeed = {0.43912f, 0.30069f, 0.20311f, 0.2f} ;
        float[] speed = new float[sp.getSpeedStepTrace().size()];
        ix=0;
        for (SpeedSetting ss: sp.getSpeedStepTrace()) {
            speed[ix]= ss.getSpeedStep();
            ix++;
        }
        Assert.assertArrayEquals("Speeds are wrong", correctSpeed, speed, maxDelta);
        sp.cancelSpeedChange();
    }

    private static org.jdom2.Element getLocoElement200() {
        return new org.jdom2.Element("locomotive")
            .setAttribute("id", "id info")
            .setAttribute("fileName", "file here")
            .setAttribute("roadNumber", "431")
            .setAttribute("roadName", "SP")
            .setAttribute("mfg", "Athearn")
            .setAttribute("dccAddress", "1234")
            .addContent(new org.jdom2.Element("decoder")
                .setAttribute("family", "91")
                .setAttribute("model", "33")
            )
            .addContent(new org.jdom2.Element("locoaddress")
                .addContent(new org.jdom2.Element("number").addContent("1234"))
                //As there is no throttle manager available all protocols default to dcc short
                .addContent(new org.jdom2.Element("protocol").addContent("dcc_short"))
            )
            .addContent(new org.jdom2.Element("speedprofile")
                .addContent(new org.jdom2.Element("overRunTimeForward").addContent("0.0"))
                .addContent(new org.jdom2.Element("overRunTimeReverse").addContent("0.0"))
                .addContent(new org.jdom2.Element("speeds")
                    .addContent(new org.jdom2.Element("speed")
                        .addContent(new org.jdom2.Element("step").addContent("1000"))
                        .addContent(new org.jdom2.Element("forward").addContent("200.00"))
                        .addContent(new org.jdom2.Element("reverse").addContent("200.00"))
                    )
                )
            );
    }

    @Test
    public void testSpeedProfileFromFiftyPercentToTwentyShortBlock() {
        // statics for test objects
        org.jdom2.Element f1 = getLocoElement400();
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        ThrottleListener throtListen = new ThrottleListen();
        ThrottleManager tm = InstanceManager.getDefault(ThrottleManager.class);
        boolean OK = tm.requestThrottle(rF1, throtListen, throttleResult);
        Assert.assertTrue("Throttle request denied",OK);
        JUnitUtil.waitFor(()-> (throttleResult), "Got No throttle");
        throttle.setIsForward(true);
        throttle.setSpeedSetting(0.6f);
        RosterSpeedProfile sp = rF1.getSpeedProfile();
        sp.setTestMode(true);
        sp.setExtraInitialDelay(1500f);
        sp.changeLocoSpeed(throttle, 152.0f, 0.20f);
        // Allow speed step table to be constructed
        //JUnitUtil.waitFor(3000);
        // Note it must be a perfect 0.20
        JUnitUtil.waitFor(()->(throttle.getSpeedSetting() == 0.20f),"Failed to reach requested speed");

        JUnitAppender.assertWarnMessageStartsWith("distance remaining is now 0, but we have not reached desired speed setting 0.2 v 0.3");

        // as the calc goes wrong we immediatly set speed to final speed. The entries are rubbish so dont bother checking
        Assert.assertEquals("SpeedStep Table has lincorrect number of entries.", 1, sp.getSpeedStepTrace().size() ) ;
        sp.cancelSpeedChange();
    }

    private static org.jdom2.Element getLocoElement400 () {
        return new org.jdom2.Element("locomotive")
            .setAttribute("id", "id info")
            .setAttribute("fileName", "file here")
            .setAttribute("roadNumber", "431")
            .setAttribute("roadName", "SP")
            .setAttribute("mfg", "Athearn")
            .setAttribute("dccAddress", "1234")
            .addContent(new org.jdom2.Element("decoder")
                .setAttribute("family", "91")
                .setAttribute("model", "33")
            )
            .addContent(new org.jdom2.Element("locoaddress")
                .addContent(new org.jdom2.Element("number").addContent("1234"))
                //As there is no throttle manager available all protocols default to dcc short
                .addContent(new org.jdom2.Element("protocol").addContent("dcc_short"))
            )
            .addContent(new org.jdom2.Element("speedprofile")
                .addContent(new org.jdom2.Element("overRunTimeForward").addContent("0.0"))
                .addContent(new org.jdom2.Element("overRunTimeReverse").addContent("0.0"))
                .addContent(new org.jdom2.Element("speeds")
                    .addContent(new org.jdom2.Element("speed")
                        .addContent(new org.jdom2.Element("step").addContent("200"))
                        .addContent(new org.jdom2.Element("forward").addContent("40.00"))
                        .addContent(new org.jdom2.Element("reverse").addContent("40.00"))
                    )
                    .addContent(new org.jdom2.Element("speed")
                        .addContent(new org.jdom2.Element("step").addContent("1000"))
                        .addContent(new org.jdom2.Element("forward").addContent("400.00"))
                        .addContent(new org.jdom2.Element("reverse").addContent("400.00"))
                    )
                )
            );
    }

    @Test
    public void testconvertThrottleSettingToScaleSpeedWithUnits(){

        SignalSpeedMap ssm = InstanceManager.getDefault(SignalSpeedMap.class);
        setSpeedInterpretation(ssm, SignalSpeedMap.PERCENT_NORMAL);
        Assertions.assertEquals("0.50 millimeters/sec",RosterSpeedProfile.convertMMSToScaleSpeedWithUnits(0.5f));

        setSpeedInterpretation(ssm, SignalSpeedMap.PERCENT_THROTTLE);
        Assertions.assertEquals("0.50 millimeters/sec",RosterSpeedProfile.convertMMSToScaleSpeedWithUnits(0.5f));

        setSpeedInterpretation(ssm, SignalSpeedMap.SPEED_KMPH);
        Assertions.assertEquals("0.16 Kilometers/Hour",RosterSpeedProfile.convertMMSToScaleSpeedWithUnits(0.5f));

        setSpeedInterpretation(ssm, SignalSpeedMap.SPEED_MPH);
        Assertions.assertEquals("0.10 Miles/Hour",RosterSpeedProfile.convertMMSToScaleSpeedWithUnits(0.5f));

    }

    @Test
    public void testMmsToScaleSpeed(){

        org.jdom2.Element f1 = getLocoElement100();
        RosterEntry rF1 = new RosterEntry(f1) {
            @Override
            protected void warnShortLong(String s) {
            }
        };
        RosterSpeedProfile rsp = rF1.getSpeedProfile();

        SignalSpeedMap ssm = InstanceManager.getDefault(SignalSpeedMap.class);
        var timeBase = InstanceManager.getDefault(jmri.Timebase.class);
        timeBase.setRun(false);
        Assertions.assertDoesNotThrow(() -> { timeBase.userSetRate(1.0d); } );

        setSpeedInterpretation(ssm, SignalSpeedMap.PERCENT_THROTTLE);
        Assertions.assertEquals(10.0f, rsp.mmsToScaleSpeed(10, false), 0.0001);
        Assertions.assertEquals(10.0f, rsp.mmsToScaleSpeed(10, true), 0.0001);

        setSpeedInterpretation(ssm, SignalSpeedMap.SPEED_KMPH);
        Assertions.assertEquals(3.13559f, rsp.mmsToScaleSpeed(10, false), 0.001);
        Assertions.assertEquals(3.13559f, rsp.mmsToScaleSpeed(10, true), 0.001);

        setSpeedInterpretation(ssm, SignalSpeedMap.SPEED_MPH);
        Assertions.assertEquals(1.94837f, rsp.mmsToScaleSpeed(10, false), 0.0001);
        Assertions.assertEquals(1.94837f, rsp.mmsToScaleSpeed(10, true), 0.0001);

        Assertions.assertDoesNotThrow(() -> { timeBase.userSetRate(2.0d); } );
        Assertions.assertEquals(1.94837f, rsp.mmsToScaleSpeed(10, false), 0.0001);
        Assertions.assertEquals(3.89675f, rsp.mmsToScaleSpeed(10, true), 0.0001);

        setSpeedInterpretation(ssm, SignalSpeedMap.SPEED_KMPH);
        Assertions.assertEquals(3.13559f, rsp.mmsToScaleSpeed(10, false), 0.001);
        Assertions.assertEquals(6.27119f, rsp.mmsToScaleSpeed(10, true), 0.001);
    }

    private void setSpeedInterpretation(SignalSpeedMap map, int interpretation) {
        var speedNames = map.getValidSpeedNames();
        HashMap<String, Float> newMap = new HashMap<>(speedNames.size());
        for ( var speedName : speedNames ) {
            newMap.put(speedName, map.getSpeed(speedName));
            // System.out.println("key " + speedName + " value: " + map.getSpeed(speedName));
        }
        map.setAspects(newMap, interpretation);
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.initDebugThrottleManager();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    class testScene {
        private org.jdom2.Element profile;
        private float currentSpeed, newSpeed, distance,  minSpeed, maxSpeed;
        
        testScene (org.jdom2.Element profile, float currentSpeed, 
                float newSpeed, float distance, 
                float minSpeed, float maxSpeed) {
            this.profile = profile;
            this.currentSpeed = currentSpeed;
            this.newSpeed = newSpeed;
            this.distance = distance;
            this.minSpeed = minSpeed;
            this.maxSpeed = maxSpeed;
        }
    }
}
