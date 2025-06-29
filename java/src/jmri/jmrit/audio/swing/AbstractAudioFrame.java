package jmri.jmrit.audio.swing;

import java.awt.FlowLayout;
import java.util.Hashtable;

import javax.swing.*;
import javax.vecmath.Vector3f;

import jmri.Audio;
import jmri.implementation.AbstractAudio;
import jmri.jmrit.beantable.AudioTableAction.AudioTableDataModel;
import jmri.util.JmriJFrame;
import jmri.util.swing.JmriJOptionPane;

/**
 * Abstract GUI to edit Audio objects
 *
 * <hr>
 * This file is part of JMRI.
 * <p>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <p>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * @author Matthew Harris copyright (c) 2009
 */
public abstract class AbstractAudioFrame extends JmriJFrame {

    AbstractAudioFrame frame = this;

    JPanel main = new JPanel();
    private JScrollPane scroll
            = new JScrollPane(main,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    final AudioTableDataModel model;

    private static final int INT_PRECISION = (int) Math.pow(10, Audio.DECIMAL_PLACES);
    static final float FLT_PRECISION = 1 / (float) INT_PRECISION;

    // Common UI components for Add/Edit Audio
    private static final JLabel SYS_NAME_LABEL = new JLabel(Bundle.getMessage("LabelSystemName"));
    JTextField sysName = new JTextField(5);
    private static final JLabel USER_NAME_LABEL = new JLabel(Bundle.getMessage("LabelUserName"));
    JTextField userName = new JTextField(15);

    /**
     * Standard constructor.
     *
     * @param title Title of this AudioFrame
     * @param model AudioTableDataModel holding Audio data
     */
    public AbstractAudioFrame(String title, AudioTableDataModel model) {
        super(title);
        this.model = model;
    }

    /**
     * Layout the frame.
     * <p>
     * This contains common items.
     * <p>
     * Sub-classes will override this method and provide additional GUI items.
     */
    public void layoutFrame() {
        frame.addHelpMenu("package.jmri.jmrit.beantable.AudioAddEdit", true);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel p;

        p = new JPanel();
        p.setLayout(new FlowLayout());
        p.add(SYS_NAME_LABEL);
        p.add(sysName);
        frame.getContentPane().add(p);

        p = new JPanel();
        p.setLayout(new FlowLayout());
        p.add(USER_NAME_LABEL);
        p.add(userName);
        frame.getContentPane().add(p);

        frame.add(scroll);
    }

    /**
     * Populate the Audio frame with default values.
     */
    public abstract void resetFrame();

    /**
     * Populate the Audio frame with current values.
     *
     * @param a Audio object to use
     */
    public void populateFrame(Audio a) {
        sysName.setText(a.getSystemName());
        userName.setText(a.getUserName());
    }

    /**
     * Check System Name user input.
     *
     * @param entry string retrieved from text field
     * @param counter index of all similar (Source/Buffer) items
     * @param prefix (AudioListener/Source/Buffer) system name prefix string to compare entry against
     * @return true if prefix doesn't match
     */
    protected boolean entryError(String entry, String prefix, String counter) {
        if (!entry.startsWith(prefix)) {
            JmriJOptionPane.showMessageDialog(this, Bundle.getMessage("AudioCreateError", prefix),
                    Bundle.getMessage("AudioCreateErrorTitle"), JmriJOptionPane.ERROR_MESSAGE);
            sysName.setText(prefix + counter);
            return true;
        }
        return false;
    }

    // private static final Logger log = LoggerFactory.getLogger(AbstractAudioFrame.class);

    /**
     * Convenience class to create a JPanel to edit a Vector3f object using 3
     * separate JSpinner Swing objects.
     */
    protected static class JPanelVector3f extends JPanel {

        JLabel xLabel = new JLabel(Bundle.getMessage("LabelX"));
        JSpinner xValue = new JSpinner();
        JLabel yLabel = new JLabel(Bundle.getMessage("LabelY"));
        JSpinner yValue = new JSpinner();
        JLabel zLabel = new JLabel(Bundle.getMessage("LabelZ"));
        JSpinner zValue = new JSpinner();
        JLabel unitsLabel = new JLabel();

        JPanelVector3f() {
            super();
            layoutPanel("", "");
        }

        JPanelVector3f(String title) {
            super();
            layoutPanel(title, "");
        }

        JPanelVector3f(String title, String units) {
            super();
            layoutPanel(title, units);
        }

        private void layoutPanel(String title, String units) {
            this.setLayout(new FlowLayout());
            if (title.length() != 0) {
                this.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
            this.add(xLabel);
            xValue.setPreferredSize(new JTextField(8).getPreferredSize());
            xValue.setModel(
                new SpinnerNumberModel(Float.valueOf(0f), Float.valueOf(-Audio.MAX_DISTANCE),
                    Float.valueOf(Audio.MAX_DISTANCE), Float.valueOf(FLT_PRECISION)));
            // TODO - I18N of format
            xValue.setEditor(new JSpinner.NumberEditor(xValue, "0.00"));
            this.add(xValue);

            this.add(yLabel);
            yValue.setPreferredSize(new JTextField(8).getPreferredSize());
            yValue.setModel(
                new SpinnerNumberModel(Float.valueOf(0f), Float.valueOf(-Audio.MAX_DISTANCE),
                    Float.valueOf(Audio.MAX_DISTANCE), Float.valueOf(FLT_PRECISION)));
            // TODO - I18N of format
            yValue.setEditor(new JSpinner.NumberEditor(yValue, "0.00"));
            this.add(yValue);

            this.add(zLabel);
            zValue.setPreferredSize(new JTextField(8).getPreferredSize());
            zValue.setModel(
                new SpinnerNumberModel(Float.valueOf(0f), Float.valueOf(-Audio.MAX_DISTANCE),
                    Float.valueOf(Audio.MAX_DISTANCE), Float.valueOf(FLT_PRECISION)));
            // TODO - I18N of format
            zValue.setEditor(new JSpinner.NumberEditor(zValue, "0.00"));
            this.add(zValue);

            if (units.length() != 0) {
                unitsLabel.setText(units);
                this.add(unitsLabel);
            }
        }

        /**
         * Set the value of this object.
         *
         * @param value value to set
         */
        public void setValue(Vector3f value) {
            xValue.setValue(value.x);
            yValue.setValue(value.y);
            zValue.setValue(value.z);
        }

        /**
         * Retrieve the current value of this object
         *
         * @return current value
         */
        public Vector3f getValue() {
            return new Vector3f(
                    AbstractAudio.roundDecimal((Float) xValue.getValue()),
                    AbstractAudio.roundDecimal((Float) yValue.getValue()),
                    AbstractAudio.roundDecimal((Float) zValue.getValue()));
        }
    }

    /**
     * A convenience class to create a JPanel for editing a float value using
     * combined JSlider and JSPinner Swing objects.
     */
    static class JPanelSliderf extends JPanel {

        private JSlider slider = new JSlider();

        private JSpinner spinner = new JSpinner();

        JPanelSliderf(String title, Float min, Float max, int majorTicks, int minorTicks) {
            super();
            int iMin = Math.round(min * INT_PRECISION);
            int iMax = Math.round(max * INT_PRECISION);
            int iInterval = (iMax - iMin) / majorTicks;

            this.setLayout(new FlowLayout());
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(title),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            slider.setMinimum(Math.round(min * INT_PRECISION));
            slider.setMaximum(Math.round(max * INT_PRECISION));
            slider.setMajorTickSpacing(iInterval);
            slider.setMinorTickSpacing(iInterval / minorTicks);
//            @SuppressWarnings("UseOfObsoleteCollectionType")
            // Need to use Hashtable for JSlider labels
            Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
            for (int i = iMin; i <= iMax; i += iInterval) {
                float f = i;
                f /= INT_PRECISION;
                // TODO - I18N of format
                labelTable.put(i, new JLabel(Float.toString(f)));
            }
            slider.setLabelTable(labelTable);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.addChangeListener( e -> {
                float f = slider.getValue();
                f /= INT_PRECISION;
                spinner.setValue(f);
            });
            spinner.setPreferredSize(new JTextField(5).getPreferredSize());
            spinner.setModel(
                    new SpinnerNumberModel(min, min, max, Float.valueOf(FLT_PRECISION)));
            // TODO - I18N of format
            spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.00"));
            spinner.addChangeListener( e ->
                slider.setValue(
                        Math.round((Float) spinner.getValue() * INT_PRECISION)));
            this.add(slider);
            this.add(spinner);
        }

        /**
         * Set the value of this object.
         *
         * @param value value to set
         */
        public void setValue(float value) {
            spinner.setValue(value);
        }

        /**
         * Retrieve the current value of this object.
         *
         * @return current value
         */
        public float getValue() {
            return AbstractAudio.roundDecimal((Float) spinner.getValue());
        }
    }

}
