package hdfsmanager.support.component;

import java.awt.*;
import java.awt.event.MouseAdapter;

import javax.swing.*;

import hdfsmanager.util.ResourcesDepository;

public class LoadingPanel extends JPanel {

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graphics2d = (Graphics2D) g.create();
		graphics2d.setComposite(AlphaComposite.SrcOver.derive(.1f));
		graphics2d.fill(getBounds());
		graphics2d.dispose();
	}

	public LoadingPanel() {
		setLayout(new BorderLayout());
		JLabel imgLabel = new JLabel(ResourcesDepository.getIcon("/images/loading.gif"));
		add(imgLabel, BorderLayout.CENTER);

		setOpaque(false); // 设置为透明
		addMouseListener(new MouseAdapter() {
			/* 阻止所有鼠标事件 */
		});
	}

	public void loading() {
		setVisible(true);
	}

	public void loaded() {
		setVisible(false);
	}
}