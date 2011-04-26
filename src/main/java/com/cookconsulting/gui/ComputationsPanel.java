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

import com.cookconsulting.tasks.FibonacciTask;
import com.cookconsulting.util.configuration.CommandRouter;
import com.cookconsulting.util.data.CircularList;
import com.cookconsulting.util.data.Triple;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Examples of how to use Executors, a CompletionService, and
 * the SwingUtilities.invokeLater() method to write a GUI that uses proper
 * multithreaded progamming techniques to keep the UI responsive and the data
 * intact
 */
public final class ComputationsPanel extends JPanel {

    private JPanel panel;
    private Random random = new Random(System.currentTimeMillis());
    private JTextField jtxfRandomValue = new JTextField(3);
    private JButton jbtRandom = new JButton("Next Random");
    private JButton jbtStart = new JButton("Start");
    private JTextArea jtAreaResults = new JTextArea(15, 30);
    private JButton jbtColorChange = new JButton("Change button color");
    private final CircularList<Color> displayColors =
        new CircularList<Color>(Arrays.asList(
            Color.GRAY, Color.BLACK, Color.BLUE, Color.CYAN,
            Color.DARK_GRAY, Color.GREEN, Color.LIGHT_GRAY,
            Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
            Color.WHITE, Color.YELLOW));
    private Executor sequentialExec = Executors.newSingleThreadExecutor();
    private CompletionService<Triple<Integer, Long, Long>> completionService
        = new ExecutorCompletionService<Triple<Integer, Long, Long>>(
        CommandRouter.background.getExecutor());

    public ComputationsPanel () {
        super();
        MigLayout migLayout = new MigLayout("", // Layout Constraints
                                            "[][]2[]", // Column constraints
                                            "[]4[]");
        setLayout(migLayout);
        panel = new JPanel(new MigLayout());
        panel.add(new JLabel("Multiple future tasks"), "cell 0 0");
        panel.add(new JLabel("IFibonacci of random (1-92) : "), "cell 0 1");
        panel.add(jtxfRandomValue, "cell 0 1");
        panel.add(jbtRandom, "cell 0 1");
        panel.add(jbtStart, "cell 0 1");
        panel.add(new JLabel("   Results:"), "cell 0 2");
        panel.add(new JScrollPane(jtAreaResults,
                                  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), "cell 0 3");
        panel.add(jbtColorChange, "cell 0 4");
        jbtColorChange.setToolTipText(
            "Clicking this shows that the UI stays responsive " +
                "even while processing tasks");
        add(panel);
        initialize();
    }

    private void initialize () {

        jbtStart.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                FibonacciTask ft = new FibonacciTask(
                    Integer.parseInt(jtxfRandomValue.getText()));
                completionService.submit(ft);
                jbtRandom.doClick();
                sequentialExec.execute(new Runnable() {
                    public void run () {
                        try {
                            Future<Triple<Integer, Long, Long>> f = completionService.take();
                            final Triple<Integer, Long, Long> result = f.get();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run () {
                                    jtAreaResults.append(
                                        String.format("fibonacci(%d) = %d took %f seconds %n",
                                                      result.get_1()
                                            , result.get_2()
                                            , result.get_3() / 1000D));
                                }
                            });
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                        catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        jbtRandom.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                String nextNumber = Integer.toString(random.nextInt(60) + 1);
                jtxfRandomValue.setText(nextNumber);
            }
        });

        jbtColorChange.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                panel.setBackground(
                    displayColors.next(panel.getBackground()));
            }
        });

        jbtColorChange.setBackground(displayColors.get(0));

        jbtRandom.doClick();
    }
}

 