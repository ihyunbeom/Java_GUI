package systemprograming03;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class earthDefensor extends JPanel {
	int score = 0;// ��ź ���� ��
	int count = 10;// �����Ǵ� ��ź ��

	Bomb[] b = new Bomb[count];// ��ź ��ü�� �迭�� ����
	Timer t = new Timer(20, new TimerHandler());
	JLabel la = new JLabel("Score = " + Integer.toString(score));

	earthDefensor() {
		Font font = new Font("���", 0, 50);
		setLayout(null);
		la.setLocation(10, 10);
		la.setSize(350, 80);
		la.setFont(font);
		add(la);

		for (int i = 0; i < count; i++) {// ��ź ����
			b[i] = new Bomb();
			b[i].newBomb();
		}
		t.start();
		MyMouseListener listener = new MyMouseListener();
		addMouseListener(listener);
	}

	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < count; i++) {
				b[i].y += b[i].s;// ��ġ�� �� ��ü���� ������ �ӵ��� ���Ͽ� ��ź�� �̵�
				if (b[i].y > 700) {// ȭ�� ������ �Ѿ �� ��ġ ������
					b[i].newBomb();
				}
			}
			repaint();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < count; i++) {
			g.setColor(b[i].c);
			g.fillOval(b[i].x, b[i].y, b[i].r*2, b[i].r*2);//��ǥ���� ���� ����̱� ������ �������� �־���Ѵ�.
		}
	}

	class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			for (int i = count - 1; i >= 0; i--) {
				// ��ź�� ���콺 ��ǥ ��
				if (Math.sqrt(Math.pow((e.getX() - (b[i].x + b[i].r)), 2) + Math.pow((e.getY() - (b[i].y + b[i].r)), 2)) < b[i].r) {
					b[i].newBomb();// ��ź�� ��ġ�� ������
					score++;
					// System.out.println("b["+i+"]" + "��° ��ź ���ŵ�..." );
					la.setText("Score = " + Integer.toString(score));
					if (score == 10) {
						t.stop();// ���α׷� ����� Ÿ�̸� ����
						System.exit(0);
					}
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame f = new JFrame("�������� ����������");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new earthDefensor());
		f.setSize(800, 600);
		f.setVisible(true);
	}

}

class Bomb {// ��ź Ŭ����
	int x, y;// �߽� ��ǥ
	int r;// ������
	int s;// �ӵ�
	Color c;

	void newBomb() {// ��ź�� ���� �ʱ�ȭ �Լ�
		x = ((int) (Math.random() * 700));
		y = ((int) (Math.random() * (-300)) - 100);
		r = 60;
		s = ((int) (Math.random() * 6) + 3);
		c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 255);
	}

}
