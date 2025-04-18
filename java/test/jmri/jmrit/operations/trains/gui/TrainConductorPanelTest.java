package jmri.jmrit.operations.trains.gui;

import jmri.util.JUnitUtil;
import org.junit.jupiter.api.*;

/**
 * Tests for the TrainConductorPanel class
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class TrainConductorPanelTest extends jmri.jmrit.operations.CommonConductorYardmasterPanelTest {

    @BeforeEach
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        p = new TrainConductorPanel();
    }

}
