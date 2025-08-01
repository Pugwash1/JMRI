package jmri.jmrix.loconet;

import jmri.CommandStation;
import jmri.util.JUnitUtil;
import jmri.SpeedStepMode;

import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class Pr2ThrottleTest extends jmri.jmrix.AbstractThrottleTest {

    private LocoNetSystemConnectionMemo memo;
 
    @Test
    public void testCTor() {
        Assert.assertNotNull("exists",instance);
    }

    /**
     * {@inheritDoc}
     *
     * The default mode is 28 speed steps
     */
    @Test
    @Override
    public void testGetSpeedStepMode() {
        SpeedStepMode expResult = SpeedStepMode.NMRA_DCC_28;
        SpeedStepMode result = instance.getSpeedStepMode();
        Assert.assertEquals(expResult, result);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testGetSpeedIncrement() {
        float expResult = SpeedStepMode.NMRA_DCC_28.increment;
        float result = instance.getSpeedIncrement();
        Assert.assertEquals(expResult, result, 0.0);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testGetSpeed_float() {
        // set speed step mode to 28 (PR2Throttle does not support 128?)
        instance.setSpeedStepMode(jmri.SpeedStepMode.NMRA_DCC_28);
        Assert.assertEquals("Idle", 0, ((Pr2Throttle)instance).intSpeed(0.0F));
        Assert.assertEquals("Emergency", 1, ((Pr2Throttle)instance).intSpeed(-1.0F));
        Assert.assertEquals("Emergency", 1, ((Pr2Throttle)instance).intSpeed(-0.001F));
        Assert.assertEquals("Full Speed", 124, ((Pr2Throttle)instance).intSpeed(1.0F)); // 124 from class source

        float incre = 1.F/111F;
        float speed = incre;
        for ( int i=1; i < 112; i++ ) {
            int result = ((Pr2Throttle)instance).intSpeed(speed);
            // System.out.println("speed="+speed+" step="+result+" i="+i);
            Assert.assertNotSame(speed + "(28 steps) should not idle", 0, result);
            Assert.assertNotSame(speed + "(28 steps) should not eStop", 1, result);
            Assert.assertTrue(speed + " should be 16 or larger ", result >= 16);
            if(i>4)
            {
                Assert.assertEquals("loconet speed from " + speed, i+12, result);
            }
            else
            {
                Assert.assertEquals("loconet speed from " + speed, 16, result);
            }
            speed += incre;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF0() {
        boolean f0 = false;
        instance.setF0(f0);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF1() {
        boolean f1 = false;
        instance.setF1(f1);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF2() {
        boolean f2 = false;
        instance.setF2(f2);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF3() {
        boolean f3 = false;
        instance.setF3(f3);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF4() {
        boolean f4 = false;
        instance.setF4(f4);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF5() {
        boolean f5 = false;
        instance.setF5(f5);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF6() {
        boolean f6 = false;
        instance.setF6(f6);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF7() {
        boolean f7 = false;
        instance.setF7(f7);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF8() {
        boolean f8 = false;
        instance.setF8(f8);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF9() {
        boolean f9 = false;
        instance.setF9(f9);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF10() {
        boolean f10 = false;
        instance.setF10(f10);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF11() {
        boolean f11 = false;
        instance.setF11(f11);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF12() {
        boolean f12 = false;
        instance.setF12(f12);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF13() {
        boolean f13 = false;
        instance.setF13(f13);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF14() {
        boolean f14 = false;
        instance.setF14(f14);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF15() {
        boolean f15 = false;
        instance.setF15(f15);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF16() {
        boolean f16 = false;
        instance.setF16(f16);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF17() {
        boolean f17 = false;
        instance.setF17(f17);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF18() {
        boolean f18 = false;
        instance.setF18(f18);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF19() {
        boolean f19 = false;
        instance.setF19(f19);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF20() {
        boolean f20 = false;
        instance.setF20(f20);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF21() {
        boolean f21 = false;
        instance.setF21(f21);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF22() {
        boolean f22 = false;
        instance.setF22(f22);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF23() {
        boolean f23 = false;
        instance.setF23(f23);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF24() {
        boolean f24 = false;
        instance.setF24(f24);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF25() {
        boolean f25 = false;
        instance.setF25(f25);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF26() {
        boolean f26 = false;
        instance.setF26(f26);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF27() {
        boolean f27 = false;
        instance.setF27(f27);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    public void testSetF28() {
        boolean f28 = false;
        instance.setF28(f28);
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    @Disabled("Test requires further development")
    public void testSendFunctionGroup1() {
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    @Disabled("Test requires further development")
    public void testSendFunctionGroup2() {
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    @Disabled("Test requires further development")
    public void testSendFunctionGroup3() {
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    @Disabled("Test requires further development")
    public void testSendFunctionGroup4() {
    }

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    @Disabled("Test requires further development")
    public void testSendFunctionGroup5() {
    }

    @BeforeEach
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        LnTrafficController lnis = new LocoNetInterfaceScaffold();
        SlotManager slotmanager = new SlotManager(lnis);
        memo = new LocoNetSystemConnectionMemo(lnis,slotmanager);
        memo.store(slotmanager, CommandStation.class);
        var tm = new LnPr2ThrottleManager(memo);
        jmri.InstanceManager.setDefault(jmri.ThrottleManager.class, tm);
        memo.store(tm, jmri.ThrottleManager.class);
        instance = new Pr2Throttle(memo,new jmri.DccLocoAddress(5,false));
    }

    @AfterEach
    @Override
    public void tearDown() {
        memo.dispose();
        memo = null;
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(Pr2ThrottleTest.class);

}
