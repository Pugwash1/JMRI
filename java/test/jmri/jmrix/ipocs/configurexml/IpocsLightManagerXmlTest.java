package jmri.jmrix.ipocs.configurexml;

import static org.mockito.Mockito.mock;

import jmri.util.JUnitUtil;

import org.jdom2.Element;

import org.junit.Assert;
import org.junit.jupiter.api.*;

public class IpocsLightManagerXmlTest {

    @Test
    public void testConstructor() {
        Assert.assertNotNull("IpocsLightManagerXml constructor", new IpocsLightManagerXml());
    }
  
    @Test
    public void testLoad() {
        Element element = mock(Element.class);
        new IpocsLightManagerXml().load(element, null);
    }

    @Test
    public void testSetStoreElementClass() {
        Element element = mock(Element.class);
        new IpocsLightManagerXml().setStoreElementClass(element);
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}
