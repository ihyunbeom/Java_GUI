package systemprograming02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoxEditer extends JPanel {
	int x1, x2, y1, y2;
	// int x,y,w,h;
	int MAX = 100; // ȭ�鿡 �׷����� �ִ� �׸�����
	int[] x = new int[MAX];
	int[] y = new int[MAX];
	int[] w = new int[MAX];
	int[] h = new int[MAX];
	int count = 0; // ��� �迭 ũ��
	int boxCount = 0; // ȭ�鿡 �׷��� �׸�����
	static boolean click = false; // ��ư����(false->add,true->delete)

	public static void main(String[] args) {
		JFrame f = new JFrame("�������� �ڽ�������");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new BoxEditer());

		// ��ư(Add,Delete)
		Container con = f.getContentPane();
		con.setLayout(null);
		Font font = new Font("���", 0, 15);

		JButton btn = new JButton("Add");
		btn.setSize(80, 30);
		btn.setLocation(280, 10);
		btn.setFont(font);
		btn.addActionListener(new ActionListener() {// �͸� Ŭ���� ���
			public void actionPerformed(ActionEvent e) {
				JButton btn = (JButton) e.getSource();

				if (btn.getText().equals("Add")) {
					btn.setText("Delete");
					click = true;
				} else if (btn.getText().equals("Delete")) {
					btn.setText("Add");
					click = false;
				}
			}
		});
		con.add(btn);
		f.setSize(640, 480);
		f.setVisible(true);
	}

	BoxEditer() {
		x1 = x2 = y1 = y2 = 0;
		// x = y = w = h = 0;
		MyMouseListener listener = new MyMouseListener();
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	public void setStartPoint(int x, int y) {
		x1 = x;
		y1 = y;
	}

	public void setEndPoint(int x, int y) {
		x2 = x;
		y2 = y;
	}

	class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (!click && boxCount <= 100) {
				setStartPoint(e.getX(), e.getY());
				count++;
				MAX++;
				boxCount++;
			} else if (click) {
				for (int i = count; i >= 0; i--) {
					if (x[i] < e.getX() && (x[i] + w[i]) > e.getX() && y[i] < e.getY() && (y[i] + h[i]) > e.getY()) {
						x[i] = 0;
						y[i] = 0;
						w[i] = 0;
						h[i] = 0;
						boxCount--;
						break;
					}
				}
			}
		}

		public void mouseDragged(MouseEvent e) {
			setEndPoint(e.getX(), e.getY());
			repaint();
		}

		public void mouseReleased(MouseEvent e) {
			setEndPoint(e.getX(), e.getY());

			// Text
			//System.out.println("Count = " + count);
			System.out.println("���� �ڽ� ���� : " + boxCount);
			System.out.println(click ? "Delete ����" : "Add ����");
			//System.out.println("MAX = " + MAX);

			repaint();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i <= count; i++) {
			g.setColor(Color.WHITE);
			g.fillRect(x[i], y[i], w[i], h[i]);
			g.setColor(Color.RED);
			g.drawRect(x[i], y[i], w[i], h[i]);
		}
		if (!click) {
			g.setColor(Color.WHITE);
			x[count] = Math.min(x1, x2);
			y[count] = Math.min(y1, y2);
			w[count] = Math.abs(x1 - x2);
			h[count] = Math.abs(y1 - y2);
			g.fillRect(x[count], y[count], w[count], h[count]);
			g.setColor(Color.RED);
			g.drawRect(x[count], y[count], w[count], h[count]);
		}

	}
}