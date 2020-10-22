package hdfsmanager.frame.main_frame.glass_view;

import java.awt.*;
import java.awt.event.MouseAdapter;

import javax.swing.*;

import hdfsmanager.util.ResourcesDepository;

public class MainGlassView extends JPanel {

	@Override
	public void paint(Graphics g){
    	super.paint(g);
        Graphics2D graphics2d = (Graphics2D) g.create();
        graphics2d.setComposite(AlphaComposite.SrcOver.derive(.1f));
        graphics2d.fill(getBounds());
        graphics2d.dispose();
    }
	
	MainGlassView() {
		initUI();
		initLookAndFeel();
		
		addEventListener();
	}
	
	private void initUI() {
		setLayout(new BorderLayout());

		JLabel imgLabel = new JLabel(ResourcesDepository.getIcon("/images/loading.gif"));
		
		add(imgLabel, BorderLayout.CENTER);
	}
	
	private void initLookAndFeel() {
		setOpaque(false);	// 设置为透明
	}
	
	private void addEventListener() {
		addMouseListener(new MouseAdapter() {});	// 阻止所有鼠标事件
	}
}
