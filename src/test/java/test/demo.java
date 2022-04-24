package test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class demo extends JFrame {
	private GhostGlassPane glassPane;
	private GhostComponentAdapter componentAdapter;
	private JButton ceshi1;
	private JButton ceshi2;
	private JButton ceshi3;
	private JButton ceshi4;
	private JLabel lceshi;
	private JPanel jpanel;
	private JButton button;

	public demo() {
		super("Drag n' Ghost Demo");
		setLayout(new BorderLayout());

		glassPane = new GhostGlassPane();
		componentAdapter = new GhostComponentAdapter(null, null);
		setGlassPane(glassPane);

		ceshi1 = new JButton("按住我移动鼠标");
		ceshi1.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级Button"));
		ceshi1.addMouseMotionListener(new GhostMotionAdapter(glassPane));

		ceshi2 = new JButton("按住我移动鼠标");
		ceshi2.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级Button"));
		ceshi2.addMouseMotionListener(new GhostMotionAdapter(glassPane));

		ceshi3 = new JButton("按住我移动鼠标");
		ceshi3.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级Button"));
		ceshi3.addMouseMotionListener(new GhostMotionAdapter(glassPane));

		ceshi4 = new JButton("按住我移动鼠标");
		ceshi4.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级Button"));
		ceshi4.addMouseMotionListener(new GhostMotionAdapter(glassPane));

		lceshi = new JLabel("按住我拖拽(JLabel)");
		lceshi.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级JLabel"));
		lceshi.addMouseMotionListener(new GhostMotionAdapter(glassPane));

		button = new JButton("按住我移动鼠标");
		button.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级Button"));
		button.addMouseMotionListener(new GhostMotionAdapter(glassPane));

		jpanel = new JPanel();
		jpanel.setBackground(Color.gray);
		jpanel.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "我是超级JPanel"));
		jpanel.addMouseMotionListener(new GhostMotionAdapter(glassPane));
		jpanel.add(lceshi);
		jpanel.add(button);

		add(ceshi1, BorderLayout.SOUTH);
		add(ceshi2, BorderLayout.WEST);
		add(ceshi3, BorderLayout.EAST);
		add(ceshi4, BorderLayout.NORTH);
		add(jpanel, BorderLayout.CENTER);
		setSize(400, 300);
		setTitle("半透明拖拽组件");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		demo d = new demo();
		d.setVisible(true);
	}
}