package systemprograming03;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class earthDefensor extends JPanel {
	int score = 0;// 폭탄 제거 수
	int count = 10;// 생성되는 폭탄 수

	Bomb[] b = new Bomb[count];// 폭탄 객체를 배열로 선언
	Timer t = new Timer(20, new TimerHandler());
	JLabel la = new JLabel("Score = " + Integer.toString(score));

	earthDefensor() {
		Font font = new Font("고딕", 0, 50);
		setLayout(null);
		la.setLocation(10, 10);
		la.setSize(350, 80);
		la.setFont(font);
		add(la);

		for (int i = 0; i < count; i++) {// 폭탄 생성
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
				b[i].y += b[i].s;// 위치에 각 객체마다 정해진 속도를 더하여 폭탄을 이동
				if (b[i].y > 700) {// 화면 밖으로 넘어갈 시 위치 재조정
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
			g.fillOval(b[i].x, b[i].y, b[i].r*2, b[i].r*2);//좌표값이 왼쪽 상단이기 때문에 지름값을 넣어야한다.
		}
	}

	class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			for (int i = count - 1; i >= 0; i--) {
				// 폭탄과 마우스 좌표 비교
				if (Math.sqrt(Math.pow((e.getX() - (b[i].x + b[i].r)), 2) + Math.pow((e.getY() - (b[i].y + b[i].r)), 2)) < b[i].r) {
					b[i].newBomb();// 폭탄의 위치를 재조정
					score++;
					// System.out.println("b["+i+"]" + "번째 폭탄 제거됨..." );
					la.setText("Score = " + Integer.toString(score));
					if (score == 10) {
						t.stop();// 프로그램 종료와 타이머 종료
						System.exit(0);
					}
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame f = new JFrame("현범이의 지구방위대");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new earthDefensor());
		f.setSize(800, 600);
		f.setVisible(true);
	}

}

class Bomb {// 폭탄 클래스
	int x, y;// 중심 좌표
	int r;// 반지름
	int s;// 속도
	Color c;

	void newBomb() {// 폭탄의 정보 초기화 함수
		x = ((int) (Math.random() * 700));
		y = ((int) (Math.random() * (-300)) - 100);
		r = 60;
		s = ((int) (Math.random() * 6) + 3);
		c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 255);
	}

}
