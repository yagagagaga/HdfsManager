package images.mvc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//视图
//BeatObserver，BPMObserver分别是视图实现的观察者接口
public class DJView implements ActionListener,  BeatObserver, BPMObserver
{
    BeatModelInterface model;//视图作为模型的观察者
    ControllerInterface controller;//视图使用控制器作为处理用户输入的策略
    JFrame viewFrame;
    JPanel viewPanel;
    BeatBar beatBar;
    JLabel bpmOutputLabel;
    JFrame controlFrame;
    JPanel controlPanel;
    JLabel bpmLabel;
    JTextField bpmTextField;
    JButton setBPMButton;
    JButton increaseBPMButton;
    JButton decreaseBPMButton;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem startMenuItem;
    JMenuItem stopMenuItem;

    //视图需要使用控制器作为策略，同时视图成为模型的观察者
    public DJView(ControllerInterface controller, BeatModelInterface model)
    {
        this.controller = controller;
        this.model = model;
        model.registerObserver((BeatObserver)this);
        model.registerObserver((BPMObserver)this);
    }

    //显示节拍视图
    public void createView()
    {
        // Create all Swing components here
        //JPanel
        bpmOutputLabel = new JLabel("offline", SwingConstants.CENTER);
        beatBar = new BeatBar();
        beatBar.setValue(0);
        JPanel bpmPanel = new JPanel(new GridLayout(2, 1));
        bpmPanel.add(beatBar);
        bpmPanel.add(bpmOutputLabel);

        //JPanel
        viewPanel = new JPanel(new GridLayout(1, 2));
        viewPanel.add(bpmPanel);

        //JFrame
        viewFrame = new JFrame("View");
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.setSize(new Dimension(100, 80));
        viewFrame.getContentPane().add(viewPanel, BorderLayout.CENTER);
        viewFrame.pack();
        viewFrame.setVisible(true);
    }

    //控制节拍视图
    public void createControls()
    {
        /////////////////////////////JFrame//////////////////////////////////////
        JFrame.setDefaultLookAndFeelDecorated(true);
        controlFrame = new JFrame("Control");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setSize(new Dimension(100, 80));
        controlPanel = new JPanel(new GridLayout(1, 2));


        /////////////////////////////Menubar//////////////////////////////////////
        menuBar = new JMenuBar();
        controlFrame.setJMenuBar(menuBar);


        /////////////////////////////Menu//////////////////////////////////////
        menu = new JMenu("DJ Control");
        menuBar.add(menu);

        //startMenuItem
        startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                controller.start();
            }
        });
        menu.add(startMenuItem);

        //stopMenuItem
        stopMenuItem = new JMenuItem("Stop");
        stopMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                controller.stop();
            }
        });
        menu.add(stopMenuItem);

        //ExitMenuItem
        JMenuItem exit = new JMenuItem("Quit");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        menu.add(exit);

        ///////////////////////////主体的上面部分//////////////////////////////////////
        JPanel insideControlPanel = new JPanel(new GridLayout(3, 1));

        //上面部分
        JPanel enterPanel = new JPanel(new GridLayout(1, 2));
        bpmLabel = new JLabel("Enter BPM:", SwingConstants.RIGHT);
        bpmLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bpmOutputLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bpmTextField = new JTextField(2);
        enterPanel.add(bpmLabel);
        enterPanel.add(bpmTextField);


        ///////////////////////////主体的中间部分//////////////////////////////////////
        //setBPMButton
        setBPMButton = new JButton("Set");
        setBPMButton.setSize(new Dimension(10,40));
        setBPMButton.addActionListener(this);


        ///////////////////////////主体的下面部分//////////////////////////////////////
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        //increaseBPMButton
        increaseBPMButton = new JButton(">>");
        increaseBPMButton.addActionListener(this);
        buttonPanel.add(increaseBPMButton);

        //decreaseBPMButton
        decreaseBPMButton = new JButton("<<");
        decreaseBPMButton.addActionListener(this);
        buttonPanel.add(decreaseBPMButton);

        insideControlPanel.add(enterPanel);
        insideControlPanel.add(setBPMButton);
        insideControlPanel.add(buttonPanel);

        //添加顶层Panel
        controlPanel.add(insideControlPanel);
        controlFrame.getRootPane().setDefaultButton(setBPMButton);

        //在JFrame中添加JPanel
        controlFrame.getContentPane().add(controlPanel, BorderLayout.CENTER);
        controlFrame.pack();
        controlFrame.setVisible(true);
    }

    public void enableStopMenuItem() {
        stopMenuItem.setEnabled(true);
    }

    public void disableStopMenuItem() {
        stopMenuItem.setEnabled(false);
    }

    public void enableStartMenuItem() {
        startMenuItem.setEnabled(true);
    }

    public void disableStartMenuItem() {
        startMenuItem.setEnabled(false);
    }

    //按钮事件
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == setBPMButton)
        {

            int bpm = Integer.parseInt(bpmTextField.getText());
            controller.setBPM(bpm);
        }
        else
        if (event.getSource() == increaseBPMButton)
        {
            //将用户的输入交给控制器，这里为增加节拍
            controller.increaseBPM();
        }
        else
        if (event.getSource() == decreaseBPMButton)
        {
            controller.decreaseBPM();
        }
    }

    //更新状态，这里作为观察者
    public void updateBPM()
    {
        if (model != null)
        {
            int bpm = model.getBPM();
            if (bpm == 0)
            {
                if (bpmOutputLabel != null)
                {
                    bpmOutputLabel.setText("offline");
                }
            }
            else
            {
                if (bpmOutputLabel != null)
                {
                    bpmOutputLabel.setText("Current BPM: " + model.getBPM());
                }
            }
        }
    }

    //更新状态，这里作为观察者
    public void updateBeat()
    {
        if (beatBar != null) {
            beatBar.setValue(100);
        }
    }
}