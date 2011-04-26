/*
 * Copyright (c) 2011, Todd Cook.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright notice,
 *        this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright notice,
 *        this list of conditions and the following disclaimer in the documentation
 *        and/or other materials provided with the distribution.
 *      * Neither the name of the <ORGANIZATION> nor the names of its contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cookconsulting.gui;

import com.cookconsulting.util.configuration.Configurator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Demo for how to write multi-threaded GUI applications correctly
 *
 * @author : Todd Cook
 * @since : Mar 7, 2011 4:31:42 PM
 */
public class MainApp implements ActionListener {

    private JFrame frame;
    private ComputationsPanel computationsPanel;
    private SimpleComputationPanel simpleComputationPanel;
    private MemoizedComputationsPanel memoizedComputationsPanel;

    // menu labels
    final String SIMPLE_COMPUTATION_PANEL = "Single Computation with Cancel";
    final String COMPUTATIONS_PANEL = "Multiple Computations";
    final String MEMOIZED_COMPUTATIONS_PANEL = "Memoized Computations";
    final String SHUTDOWN_AND_EXIT = "Shutdown gracefully and exit";

    public MainApp () {
    }

    private void initialize () {
        Configurator.instance.initialize();
        frame = new JFrame("Concurrent UI Demo");
        frame.setLayout(new FlowLayout());
        computationsPanel = new ComputationsPanel();
        simpleComputationPanel = new SimpleComputationPanel();
        memoizedComputationsPanel = new MemoizedComputationsPanel();
        computationsPanel.setVisible(false);
        simpleComputationPanel.setVisible(false);
        memoizedComputationsPanel.setVisible(false);
        frame.add(computationsPanel);
        frame.add(simpleComputationPanel);
        frame.add(memoizedComputationsPanel);
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar());
        //    frame.setContentPane (createContentPane ());
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing (WindowEvent ev) {
                Configurator.instance.shutdown();
                System.exit(0);
            }
        });
    }

    public static void main (String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.initialize();
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getActionCommand().equals(COMPUTATIONS_PANEL)) {
            simpleComputationPanel.setVisible(false);
            computationsPanel.setVisible(true);
            memoizedComputationsPanel.setVisible(false);
        }
        if (e.getActionCommand().equals(SIMPLE_COMPUTATION_PANEL)) {
            computationsPanel.setVisible(false);
            simpleComputationPanel.setVisible(true);
            memoizedComputationsPanel.setVisible(false);
        }
        if (e.getActionCommand().equals(MEMOIZED_COMPUTATIONS_PANEL)) {
            computationsPanel.setVisible(false);
            simpleComputationPanel.setVisible(false);
            memoizedComputationsPanel.setVisible(true);
        }
        if (e.getActionCommand().equals(SHUTDOWN_AND_EXIT)) {
            frame.setVisible(false);
            Configurator.instance.shutdown();
        }
    }

    public JMenuBar createMenuBar () {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        //Create the menu bar.
        menuBar = new JMenuBar();
        //Build the first menu.
        menu = new JMenu("Examples");
        menuItem = new JMenuItem(SIMPLE_COMPUTATION_PANEL);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem(COMPUTATIONS_PANEL);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem(MEMOIZED_COMPUTATIONS_PANEL);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem(SHUTDOWN_AND_EXIT);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        return menuBar;
    }

    public Container createContentPane () {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        contentPane.add(simpleComputationPanel);
        contentPane.add(computationsPanel);
        return contentPane;
    }
}
