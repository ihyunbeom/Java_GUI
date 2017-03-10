package systemprograming0926;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;

class Util {
	static double rand(int max) {
		return Math.random() * max;
	}

	static double rand(double min, double max) {
		return min + (Math.random() * (max - min));
	}

	static boolean prob100(double r) {
		return (Math.random() * 100) <= r;
	}

	static Color randColor() {
		return randColor(0, 255);
	}

	static Color randColor(int min, int max) {
		int r = (int) rand(min, max);
		int g = (int) rand(min, max);
		int b = (int) rand(min, max);
		return new Color(r, g, b);
	}

	static Color randColor(int max) {
		int r = 255;
		int g = (int) rand(0, max);
		int b = (int) rand(0, max);
		return new Color(r, g, b);
	}
}

class Star {
	public static int MAX_STAR_SIZE = 10; // ���� �ִ� ũ��

	private Color color; // ���� ����
	private double x, y; // ���� ��ġ
	private double dy, size; // ���� ���� �̵� �Ÿ�, ũ��

	Star() {
		init();
	}

	// ���� ����, ��ġ �ʱ�ȭ
	void init() {
		color = Util.randColor(0, 127);
		x = Util.rand(SpaceWar.FRAME_W - 1);
		y = Util.rand(SpaceWar.FRAME_H - 1);
		size = Util.rand(1, MAX_STAR_SIZE);
		dy = size / 2;
	}

	// Ÿ�̸ӿ� ���� ���� ������ ó��
	void move() {
		// �Ʒ� �������� �̵�, ȭ�� ������ ������ �ʱ�ȭ
		y += dy;
		if (y > SpaceWar.FRAME_H) {
			init();
			y = -Util.rand(SpaceWar.FRAME_H / 10);
		}
	}

	// �� �׸���
	void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - dy), (int) (y - dy), (int) size, (int) size);
	}
}

class GameObj {
	public static int ST_DEATH = 0;
	public static int ST_ALIVE = 1;
	public static int ST_BLAST = 2;
	public static int ST_TURN = 3;
	// ���� �Լ��� �����ؾ� �Ѵ�.(move,birth,blast...��)

	Image image; // ���� ��ü �̹���
	int state; // ���� ��ü ����
	double x, y; // ���� ��ü ��ġ
	int width, height; // ���� ��ü ũ��
	int blast_count; // ���� ī��Ʈ

	int getState() {
		return state;
	} // ���� Ȯ��

	double getX() {
		return x;
	} // ���� ��ġ Ȯ��

	double getY() {
		return y;
	} // ���� ��ġ Ȯ��

	// �̹��� ���
	void drawImage(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

	// ���� �̹��� ���
	void drawBlast(Graphics g) {
		// blast_count ���� ��ŭ ���� �׸���
		for (int i = 1; i < blast_count; i++) {
			g.setColor(Util.randColor(128, 255));
			double x0 = Util.rand(-30, 30);
			double y0 = Util.rand(-30, 30);
			double r0 = Util.rand(5, 30);
			g.fillOval((int) (x - x0 - r0 / 2), (int) (y - y0 - r0 / 2), (int) r0, (int) r0);
		}
	}

	void rocketDrawBlast(Graphics g) {
		// blast_count ���� ��ŭ ���� �׸���
		for (int i = 1; i < blast_count; i++) {
			g.setColor(Util.randColor(255));
			double x0 = Util.rand(-20, 20);
			double y0 = Util.rand(-20, 20);
			double r0 = Util.rand(5, 30);
			g.fillOval((int) (x - x0 - r0 / 2), (int) (y - y0 - r0 / 2), (int) r0, (int) r0);
		}
	}

	// �ٿ���ڽ� ����
	Rectangle getBBox() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), width, height);
	}

}

class Ufo extends GameObj {
	// private double dx; // ���� �̵� �Ÿ�
	public static int N_SCENARIO = 6;
	public static int MOVE_DIST = 15;

	// �ó�����
	private int sc_x[] = { -100, 700, 700, 100, 100, 900 };
	private int sc_y[] = { 100, 100, 400, 150, 400, 50 };
	private int sc_i; // ���� ��ǥ���� �ε���

	// ������
	Ufo(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// ufo �߻�
	void birth() {// alive����
		state = ST_ALIVE;

		// ���� ��ġ ����
		x = sc_x[0];
		y = sc_y[0];
		// ���� ��ǥ���� �ε��� �ʱ�ȭ
		sc_i = 1;

		/*
		 * // 50% Ȯ���� ���� ��ġ ���� if (Util.prob100(50)) {// 50%Ȯ�� x = -40; dx =
		 * Util.rand(5, 10); } else { x = SpaceWar.FRAME_W + 40; dx =
		 * -Util.rand(5, 10); } y = Util.rand(40, SpaceWar.FRAME_H / 2);
		 */
	}

	// ���� ���� ����
	void blast() {
		state = ST_BLAST;
		blast_count = 15;
	}

	// ���� ���� ����
	void death() {
		state = ST_DEATH;
	}

	// Ÿ�̸ӿ� ���� ufo�� ������ ó��
	void move() {

		/*
		 * // ALIVE ���¿����� �¿�� �̵� if (state == ST_ALIVE || state == ST_TURN) { //
		 * ������ ��ġ���� ���� ��ȯ double dir = Util.rand(SpaceWar.FRAME_W / 3,
		 * (SpaceWar.FRAME_W / 3) * 2);
		 * 
		 * if (x >= dir - Math.abs(dx) * 2 && x <= dir + Math.abs(dx) * 2 &&
		 * state != ST_TURN) { state = ST_TURN; } if (state != ST_TURN) {
		 * 
		 * x += dx; } else if (state == ST_TURN) { x -= dx; } if (x < -40 ||
		 * SpaceWar.FRAME_W + 40 < x) { state = ST_DEATH; } }
		 */

		// ALIVE ���¿����� �̵�
		if (state == ST_ALIVE) {
			// ���� ��ǥ���� �Ÿ� ���
			double dist = Math.sqrt(Math.pow(x - sc_x[sc_i], 2) + Math.pow(y - sc_y[sc_i], 2));
			// ���� ��ǥ���� �Ÿ� Ȯ��
			if (dist >= MOVE_DIST) {
				// ��ǥ���� �������� Ufo �̵�
				x += (sc_x[sc_i] - x) / dist * MOVE_DIST;
				y += (sc_y[sc_i] - y) / dist * MOVE_DIST;
			} else {
				// ������ ��ǥ���� ���� Ȯ��
				if (sc_i < N_SCENARIO - 1)
					sc_i++; // ���� ��ǥ���� ����
				else
					state = ST_DEATH; // ���� ���� ����
			}
		}
		// BLAST ���¿����� count �ð� �� DEATH�� ����
		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// ufo �׸���
	void draw(Graphics g) {
		if (state == ST_ALIVE || state == ST_TURN)
			drawImage(g);
		else if (state == ST_BLAST)
			drawBlast(g);
	}
}

@SuppressWarnings("serial")
class SpaceWarComponent extends JComponent {
	public static int TIME_SLICE = 50;
	public static int MAX_STAR = 1000;
	public static int MAX_UFO = 5;
	public static int MAX_BOMB = 3;
	public static int MAX_ROCKET = 4;
	public static int MAX_MISSILE = MAX_ROCKET * 8 + 1;

	// ���� ������ ����
	public static int ST_TITLE = 0;
	public static int ST_GAME = 1;
	public static int ST_ENDING = 2;

	private Timer t;
	private Star[] star;
	private Ufo[] ufo;
	private MyShip me;
	private Bomb[] bomb;
	private Missile[] missile;
	private Rocket[] rocket;

	Image imgUfo, imgBomb, imgMyShip, imgMissile;
	Image imgTitle;
	Image imgRocket;

	private int state; // ���� ����
	private int score; // ����
	private int life; // ������
	private int ani_count; // �ִϸ��̼� ī����,
	private int[] degree = new int[MAX_MISSILE];

	// 0~19 �ݺ�
	private int sc_count; // �ó����� ī����

	SpaceWarComponent() {
		// Ÿ�̸� ���
		t = new Timer(TIME_SLICE, new TimerHandler());
		t.start();
		// Ű �̺�Ʈ ���
		this.addKeyListener(new KeyHandler());
		this.setFocusable(true);

		// �� ����
		star = new Star[MAX_STAR];
		for (int i = 0; i < MAX_STAR; i++)
			star[i] = new Star();
		// �̹��� �б�
		try {
			imgUfo = ImageIO.read(new File("images/ufo.png"));
			imgBomb = ImageIO.read(new File("images/bomb.png"));
			imgMyShip = ImageIO.read(new File("images/myship.png"));
			imgMissile = ImageIO.read(new File("images/imgmissile.png"));
			imgTitle = ImageIO.read(new File("images/title.png"));
			imgRocket = ImageIO.read(new File("images/rocket.png"));
		} catch (IOException e) {
			System.exit(-1);
		}
		// ufo ����
		ufo = new Ufo[MAX_UFO];
		for (int i = 0; i < MAX_UFO; i++) {
			ufo[i] = new Ufo(imgUfo, 80, 40);
		}
		// ���ּ� ����
		me = new MyShip(imgMyShip, 80, 80);

		// ��ź ����
		// bomb = new Bomb(imgBomb, 30, 30);
		bomb = new Bomb[MAX_BOMB];
		for (int i = 0; i < MAX_BOMB; i++)
			bomb[i] = new Bomb(imgBomb, 30, 30);

		// �̻��� ����
		// missile = new Missile(imgMissile, 20, 50);
		missile = new Missile[MAX_MISSILE];
		for (int i = 0; i < MAX_MISSILE; i++)
			missile[i] = new Missile(imgMissile, 15, 15);

		// ���� ����
		rocket = new Rocket[MAX_ROCKET];
		for (int i = 0; i < MAX_ROCKET; i++)
			rocket[i] = new Rocket(imgRocket, 30, 60);

		// ���� �ʱ�ȭ
		for (int i = 1; i < MAX_MISSILE; i++) {
			degree[i] = (i % 8) * 45;
		}

		// ���� ���� �ʱ�ȭ
		state = ST_TITLE;
		ani_count = 0;
		sc_count = 0;
	}

	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// �� �����̱�
			for (Star s : star)
				s.move();
			// ufo ���� �ó�����
			sc_count++;
			for (int i = 0; i < MAX_UFO; i++) {
				if (sc_count % 200 == (i + 1) * 10)
					ufo[i].birth();
			}
			// ufo �����̱�
			for (Ufo u : ufo) {
				u.move();
				// ��ź �߻�
				if (state == ST_GAME) // GAME ���¿����� ��ź ����
					for (Bomb b : bomb) {
						if (u.getState() == Ufo.ST_ALIVE && Util.prob100(30))
							b.shot(u.getX(), u.getY(), me.getX(), me.getY());
					}
			}

			// ���ּ� ���� ó��
			me.move();

			// ENDING ȭ�� ���ּ� ���� ó��
			ani_count = (ani_count + 1) % 20; // 0 .. 19 �ݺ�
			if (state == ST_ENDING)
				if (ani_count == 0)
					me.blast();

			// ��ź �����̱�
			for (Bomb b : bomb) {
				b.move();
				// ��ź �浹ó��
				if (b.getState() == Bomb.ST_ALIVE) {
					if (me.getState() == MyShip.ST_ALIVE) {
						if (me.getBBox().intersects(b.getBBox())) {
							me.blast();
							b.blast();
							life--; // ������ ����
							if (life == 0)
								state = ST_ENDING; // ���� ����
						}
					}
				}
			}

			/////////////////// �Ź��ⰳ��///////////////////////
			// ���� �����̱�
			for (int i = 0; i < MAX_ROCKET; i++) {
				rocket[i].move();
			}

			// ���� �浹 ó��
			for (int i = 0; i < MAX_ROCKET; i++) {
				if (rocket[i].getState() == Rocket.ST_ALIVE) {
					if (rocket[i].getY() < Util.rand(40, 400)) {
						rocket[i].blast();
						for (int j = i * 8 + 1; j <= (i * 8) + 8; j++) {
							missile[j].shot(rocket[i].getX(), rocket[i].getY());
						}
						// score += 10; // ���� ����
						break;
					}
				}
			}

			for (int i = 1; i < MAX_MISSILE; i++) {
				missile[i].move(degree[i % 8]);
				degree[i % 8]++;
				if (degree[i % 8] >= 360)
					degree[i % 8] = 0;
			}
			for (int i = 1; i < MAX_MISSILE; i++) {
				if (missile[i].getState() == Missile.ST_ALIVE) {
					for (Ufo u : ufo) {
						if (u.getState() == Ufo.ST_ALIVE) {
							if (u.getBBox().intersects(missile[i].getBBox())
									|| missile[i].getBBox().intersects(u.getBBox())) {
								u.blast();
								missile[i].blast();
								score += 10; // ���� ����
								break;
							}
						}
					}
				}
			}

			// ��ü �ٽ� �׸���
			repaint();
		}
	}

	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if (state == ST_TITLE) {
				if (code == KeyEvent.VK_SPACE) { // ���� ����
					state = ST_GAME;
					score = 0;
					life = 3;
					me.startMyShip(); // ���� ����
					for (Ufo u : ufo)
						u.death(); // ufo �ʱ�ȭ
					sc_count = 0;
				}
			} else if (state == ST_GAME) {

				if (code == KeyEvent.VK_LEFT)
					me.moveLeft(); // �������� �̵�
				else if (code == KeyEvent.VK_RIGHT)
					me.moveRight(); // ���������� �̵�
				else if (code == KeyEvent.VK_SPACE) {
					if (me.getState() == MyShip.ST_ALIVE) {
						for (Rocket r : rocket) {
							if (r.getState() == Rocket.ST_DEATH) {
								r.shot(me.getX(), me.getY());
								break;
							}
						}
					}
				}
			} else if (state == ST_ENDING) {
				if (code == KeyEvent.VK_ENTER) {
					state = ST_TITLE; // ���� ȭ������ �̵�
				}
			}
			repaint();
		}
	}

	public void paintComponent(Graphics g) {
		// ������ ���� ��� �׸���
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, SpaceWar.FRAME_W, SpaceWar.FRAME_H);
		// ������� �׸���
		for (Star s : star)
			s.draw(g); // �����̴� �� �׸���

		for (Bomb b : bomb)
			b.draw(g); // ��ź �׸���

		for (Missile m : missile)
			m.draw(g); // �̻��� �׸���

		for (Rocket r : rocket)
			r.draw(g); // ���� �׸���

		for (Ufo u : ufo)
			u.draw(g); // ufo �׸���

		if (state != ST_TITLE) // ���ּ� �׸���
			me.draw(g); // ���ּ� �׸���

		// ���º� ���� ���
		if (state == ST_TITLE) {
			int zoom = Math.abs(ani_count - 10); // 10 .. 0 .. 9 �ݺ�
			g.drawImage(imgTitle, 100 - zoom, 150 - zoom, 600 + zoom * 2, 100 + zoom * 2, null);
			if (ani_count < 10) { // 10 x 50msec = 500msec �ֱ�
				Font font = new Font(Font.SANS_SERIF, Font.BOLD, 36);
				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString("PRESS SPACE KEY", 230, 430);
			}
		} else if (state == ST_GAME) {
			Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString("SCORE: " + score, 20, 30);
			g.drawString("LIFE: " + life, 20, 60);
		} else if (state == ST_ENDING) {
			Font font = new Font(Font.SANS_SERIF, Font.BOLD, 36);
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString("YOUR SCORE IS " + score, 230, 200);
			if (ani_count < 10) // 10 x 50msec = 500msec �ֱ�
				g.drawString("PRESS ENTER KEY", 230, 400);
		}
	}
}

class MyShip extends GameObj {
	// ������
	MyShip(Image img, int w, int h) {
		image = img;
		state = ST_ALIVE;
		x = SpaceWar.FRAME_W / 2;
		y = SpaceWar.FRAME_H - w;
		width = w;
		height = h;
	}

	// �������� �̵�
	void moveLeft() {
		if (x >= 70)
			x -= 20;
	}

	// ���������� �̵�
	void moveRight() {
		if (x < SpaceWar.FRAME_W - 70)
			x += 20;
	}

	// ���� ���·� ����
	void blast() {
		state = ST_BLAST;
		blast_count = 30;
	}

	// ���� ����
	void startMyShip() {
		state = ST_ALIVE;
		x = SpaceWar.FRAME_W / 2;
	}

	// Ÿ�̸ӿ� ���� ���ּ� ���� ó��
	void move() {
		// BLAST ���¿����� count �ð� �� ALIVE�� ����
		if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_ALIVE;
		}
	}

	// ���ּ� �׸���
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST) {
			if (blast_count % 2 == 0)
				drawImage(g);
			drawBlast(g);
		}
	}
}

class Bomb extends GameObj {
	private double dx, dy;// ��ź�� ���� �̵� �Ÿ�

	// ������
	Bomb(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y ��ġ���� mx, my ��ġ�� ��ź �߻�
	void shot(double x, double y, double mx, double my) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
			dx = (mx - x) / 50;
			dy = (my - y) / 50;
		}
	}

	// ���� ���·� ����
	void blast() {
		state = ST_DEATH;
	}

	// Ÿ�̸ӿ� ���� ��ź�� ������ ó��
	void move() {
		if (state == ST_ALIVE) {
			x += dx;
			y += dy;
			if (y < -40 || SpaceWar.FRAME_H + 40 < y)
				state = ST_DEATH;
		}
	}

	// ��ź �׸���
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
	}
}

class Missile extends GameObj {
	private double dx, dy;// ��ź�� ���� �̵� �Ÿ�

	// ������
	Missile(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y ��ġ���� �̻��� �߻�
	void shot(double x, double y) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
			dy = 0;
			dx = 0;
		}
	}

	// ���� ���·� ����
	void blast() {
		state = ST_DEATH;
	}

	// �����̻���
	void move(double tx, double ty) {
		if (state == ST_ALIVE) {

			if (tx <= x) {// Ÿ���� ����
				dy -= 1;
				y += dy;
				dx = (x - tx) / 10;
				x -= dx;
			} else if (x < tx) {// Ÿ���� ������
				dy -= 1;
				y += dy;
				dx = (tx - x) / 10;
				x += dx;
			}
			if (y < -40 || SpaceWar.FRAME_H + 40 < y)// ȭ�� ������ ���� ���
				state = ST_DEATH;
		}
	}

	// �Ź����
	void move(int i) {

		if (state == ST_ALIVE) {
			x -= dy * Math.sin(Math.toRadians(i));
			y -= dy * Math.cos(Math.toRadians(i));
			dy += 0.3;
			if (y < -40 || SpaceWar.FRAME_H + 40 < y || x < -40 || SpaceWar.FRAME_W + 40 < x)
				state = ST_DEATH;
		}
	}

	// �̻��� �׸���
	void draw(Graphics g) {
		if (state == ST_ALIVE) {
			drawImage(g);
		} else if (state == ST_BLAST) {
			drawBlast(g);
			state = ST_DEATH;
		}
	}
}

class Rocket extends GameObj {
	private double dy;// ��ź�� ���� �̵� �Ÿ�

	// ������
	Rocket(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y ��ġ���� �̻��� �߻�
	void shot(double x, double y) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
			dy = 0;
		}
	}

	// ���� ���·� ����
	void blast() {
		state = ST_BLAST;
		blast_count = 30;
	}

	// Ÿ�̸ӿ� ���� �̻����� ������ ó��
	void move() {
		if (state == ST_ALIVE) {
			dy -= 0.3;
			y += dy;
			if (y < -40 || SpaceWar.FRAME_H + 40 < y)// ȭ�� ������ ���� ���
				state = ST_DEATH;
		}
	}

	// �̻��� �׸���
	void draw(Graphics g) {
		if (state == ST_ALIVE) {
			drawImage(g);
		} else if (state == ST_BLAST) {
			rocketDrawBlast(g);
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}
}

public class SpaceWar {
	public static int FRAME_W = 800;
	public static int FRAME_H = 600;

	public static void main(String[] arg) {
		JFrame f = new JFrame("Space War");
		f.setSize(FRAME_W + 8, FRAME_H + 34);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SpaceWarComponent sc = new SpaceWarComponent();
		f.add(sc);
		f.setVisible(true);
	}
}
