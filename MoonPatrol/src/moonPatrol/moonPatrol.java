package moonPatrol;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

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

public class moonPatrol {
	public static int FRAME_W = 1000;
	public static int FRAME_H = 600;

	public static void main(String[] arg) {
		JFrame f = new JFrame("Moon Patrol");
		f.setSize(FRAME_W + 8, FRAME_H + 34);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		moonPatrolComponent sc = new moonPatrolComponent();
		f.add(sc);
		f.setVisible(true);
	}

}

@SuppressWarnings("serial")
class moonPatrolComponent extends JComponent {
	public static int TIME_SLICE = 10;
	public static int MAX_GROUND = 6;
	public static int MAX_MOUNT = 3;
	public static int MAX_STAR = 1000;
	public static int MAX_UFO = 5;
	public static int MAX_BOMB = 3;
	public static int MAX_SHADOW = 1;

	private MyTank tank;
	private Ground[] ground;
	private Star[] star;
	private Mount[] mount;
	private Mount2[] mount2;
	private Wheel[] wheel;
	private Ufo[] ufo;
	private Bomb[] bomb;
	private Missile missile;
	private Missile2 missile2;
	private Shadow shadow;

	private Timer t;
	private Timer t2;

	private double jumpSpeed = 0.2;// 속도
	private double moveSpeed = 0.1;
	private double moveWidth = 0;
	private double jumpHeight = 15;// 높이
	private double original_jumpHeight = 0;
	private double original_Y = 0;
	private double original_X = 0;
	private boolean nowJumping = false;
	private boolean leftMove = false;
	private boolean rightMove = false;
	private int pixel1[] = new int[400];
	private int pixel2[] = new int[400];
	private int pixel3[] = new int[400];
	private int c1 = 0;
	private int c2 = 0;
	private int c3 = 0;
	private int ck = 0;
	private int ck2 = 0;
	private int bi1;
	private int bi2;
	private int bi3;
	private int ufo_sc_count;
	private int shadow_sc_count;

	private boolean on_ufo = false;
	private boolean on_trap = false;
	private boolean on_shadow = false;

	Image imgTank, imgUfo, imgBomb, imgMissile, imgMissile2;
	Image imgMount, imgMount2;
	Image imgWheel;
	Image imgGr1, imgGr2, imgGr3, imgTrap, imgFire;
	Image[] imgShadow;

	moonPatrolComponent() {
		// 타이머 등록
		t = new Timer(TIME_SLICE, new TimerHandler());
		t2 = new Timer(50, new TimerHandler2());
		t.start();
		t2.start();
		// 키 이벤트 등록
		this.addKeyListener(new KeyHandler());
		this.setFocusable(true);
		// 별 그리기
		star = new Star[MAX_STAR];
		for (int i = 0; i < MAX_STAR; i++)
			star[i] = new Star();

		// 이미지 읽기
		try {
			imgTank = ImageIO.read(new File("images/MyTank.png"));
			imgWheel = ImageIO.read(new File("images/Wheel.png"));
			imgGr1 = ImageIO.read(new File("images/gr11.png"));
			imgGr2 = ImageIO.read(new File("images/gr55.png"));
			imgGr3 = ImageIO.read(new File("images/gr66.png"));
			imgTrap = ImageIO.read(new File("images/trap44.png"));
			imgFire = ImageIO.read(new File("images/fire22.png"));
			imgMount = ImageIO.read(new File("images/mount.png"));
			imgMount2 = ImageIO.read(new File("images/mount2.png"));
			imgUfo = ImageIO.read(new File("images/ufo.png"));
			imgBomb = ImageIO.read(new File("images/bomb.png"));
			imgMissile = ImageIO.read(new File("images/missile.png"));
			imgMissile2 = ImageIO.read(new File("images/rocket.png"));
			imgShadow = new Image[2];
	         for(int i=0; i<imgShadow.length; i++)
	            imgShadow[i] = ImageIO.read(new File("images/Shadow_" + i + ".png"));
		} catch (IOException e) {
			System.exit(-1);
			System.out.println("errrrrrrrrrrrrrrrorrrrrrr");
		}
		// 탱크 생성

		tank = new MyTank(imgTank, 86, 44);
		wheel = new Wheel[3];
		wheel[0] = new Wheel(imgWheel, 400, 500, 25, 25);
		wheel[1] = new Wheel(imgWheel, 370, 500, 25, 25);
		wheel[2] = new Wheel(imgWheel, 340, 500, 25, 25);

		ground = new Ground[MAX_GROUND];

		ground[0] = new Ground(imgGr1, 200, 23, 0);
		ground[1] = new Ground(imgGr2, 200, 23, 1);
		ground[2] = new Ground(imgGr3, 200, 23, 2);

		ground[3] = new Ground(imgGr1, 200, 23, 3);
		ground[4] = new Ground(imgGr2, 200, 23, 4);
		ground[5] = new Ground(imgTrap, 200, 23, 5);

		mount = new Mount[MAX_MOUNT];
		for (int i = 0; i < MAX_MOUNT; i++) {
			mount[i] = new Mount(imgMount, 600, 250, i);
		}
		mount2 = new Mount2[MAX_MOUNT];
		for (int i = 0; i < MAX_MOUNT; i++) {
			mount2[i] = new Mount2(imgMount2, 600, 250, i);
		}
		// ufo 생성
		ufo = new Ufo[MAX_UFO];
		for (int i = 0; i < MAX_UFO; i++) {
			ufo[i] = new Ufo(imgUfo, 50, 20);
		}
		// 폭탄 생성
		bomb = new Bomb[MAX_BOMB];
		for (int i = 0; i < MAX_BOMB; i++)
			bomb[i] = new Bomb(imgBomb, 15, 15);
		// 미사일
		missile = new Missile(imgMissile, 40, 15);
		missile2 = new Missile2(imgMissile2, 15, 30);
		
		// shadow 생성
	      shadow = new Shadow(imgShadow[0], 50, 30);

	}

	public void handlepixels(Image img, int x, int y, int w, int h, int p) {
		int[] pixels = new int[w * h];
		int pixel[] = new int[w];
		int[][] pic = new int[w][h];

		PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels! ");
			return;
		}
		if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
			System.err.println("image fetch aborted or errored");
			return;
		}

		for (int i = 0; i < w; i++) {
			pixel[i] = 0;
		}
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == 0)
				pic[i % w][i / w] = 0;
			else
				pic[i % w][i / w] = 1;
		}
		for (int i = 0; i < w; i++) {
			// System.out.print(i);
			for (int j = 0; j < h; j++) {
				if (pic[i][j] == 1)
					pixel[i]++;
				// System.out.print(pic[i][j]);
			}
			// System.out.println();

		}

		if (p == 0)
			for (int i = 0; i < w; i++)
				pixel1[i] = pixel[i];
		else if (p == 1)
			for (int i = 0; i < w; i++)
				pixel2[i] = pixel[i];
		else if (p == 2)
			for (int i = 0; i < w; i++)
				pixel3[i] = pixel[i];

	}

	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if (code == KeyEvent.VK_UP && nowJumping == false) {
				nowJumping = true;
				original_jumpHeight = jumpHeight;
				original_Y = tank.y;
			}
			if (code == KeyEvent.VK_LEFT && leftMove == false && rightMove == false) {
				tank.moveLeft(); // 왼쪽으로 이동
				leftMove = true;
				original_X = tank.x;
			} else if (code == KeyEvent.VK_RIGHT && rightMove == false && leftMove == false) {
				tank.moveRight(); // 오른쪽으로 이동
				rightMove = true;
				original_X = tank.x;
			} else if (code == KeyEvent.VK_SPACE) {
				if (tank.getState() == MyTank.ST_ALIVE) {
					if (missile.getState() == Missile.ST_DEATH) {
						missile.shot(tank.getX() + 40, tank.getY());
					}
					if (missile2.getState() == Missile2.ST_DEATH) {
						missile2.shot(tank.getX() - 22, tank.getY() - 15);
					}

				}
			}

		}
	}

	class TimerHandler2 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ck2++;
			if (ck2 >= 10) {
				// ufo 생성 시나리오
				if (on_ufo == true) {
					ufo_sc_count++;
					for (int i = 0; i < MAX_UFO; i++) {
						if (ufo_sc_count % 250 == (i + 1) * 10)
							ufo[i].birth();
					}
					// ufo 움직이기
					for (Ufo u : ufo) {
						u.move();
						// 폭탄 발사
						if (u.getState() == Ufo.ST_ALIVE && Util.prob100(100)) {
							for (Bomb b : bomb) {
								b.shot(u.getX(), u.getY(), tank.getX() + Util.rand(10, 100), tank.getY());
								break;
							}
						}
					}
				}
				if(on_shadow == true){
				 // shadow 생성 시나리오
		         shadow_sc_count++;
		         for (int i = 0; i < MAX_SHADOW; i++) {
		            if (shadow_sc_count % 250 == (i + 1) * 10)
		               shadow.birth();
		         }
		      // Shadow 움직이기
		         shadow.move();
		         if(shadow.getState() == Shadow.ST_ALIVE && shadow.stop_count == 0)
		            shadow.image = imgShadow[1];
		         else
		            shadow.image = imgShadow[0];
				}

				if (missile2.getState() == Missile2.ST_ALIVE) {
					for (Ufo u : ufo) {
						if (u.getState() == Ufo.ST_ALIVE) {
							if (u.getBBox().intersects(missile2.getBBox())
									|| missile2.getBBox().intersects(u.getBBox())) {
								u.blast();
								missile2.blast();
								break;
							}
						}
					}
				}


				repaint();
			}
			System.out.println(ck2);
			//if(ck2 > 100 && on_ufo==false)
			//	on_ufo=true;
			//if(ck2>350 && on_ufo == true)
			//	on_ufo=false;
			//if(ck2 > 350 && on_shadow == false)
			//	on_shadow = true;
			//if(ck2> 400)
			//	ck2 = 10;

		}
	}

	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ck++;
			if (ck >= 10 && tank.getState() == MyTank.ST_ALIVE) {
				for (Ground gr : ground)
					gr.move();
				for (Mount m : mount)
					m.move();
				for (Mount2 m2 : mount2)
					m2.move();
				for (Star s : star)
					s.move();
				
				// 폭탄 움직이기
				for (Bomb b : bomb)
					b.move();
				// 폭탄 충돌처리
				for (Bomb b : bomb) {
					if (b.getState() == Bomb.ST_ALIVE) {
						if (tank.getState() == MyTank.ST_ALIVE) {
							if (tank.getBBox().intersects(b.getBBox())) {
								b.blast();
							}
						}
					}
				}

				if (nowJumping == true) {
					jumpHeight -= jumpSpeed;
					tank.y -= (((jumpHeight * (jumpHeight + 1)) / 2)
							- (((jumpHeight - jumpSpeed) * (jumpHeight - jumpSpeed + 1)) / 2));
					jumpHeight -= jumpSpeed;
					wheel[0].y = tank.y + 7;
					wheel[1].y = tank.y + 7;
					wheel[2].y = tank.y + 7;

					if (jumpHeight <= -(original_jumpHeight)) {
						tank.y = original_Y;

						jumpHeight = original_jumpHeight;
						nowJumping = false;
					}
				}
				if (nowJumping == false) {
					for (int i = 0; i < 6; i++) {
						if (ground[i].getBBox().intersects(wheel[0].getBBox())) {
							handlepixels(ground[i].image, 0, 0, ground[i].width, ground[i].height, 0);
							if (bi1 != i)
								c1 = 0;
							bi1 = i;
							c1 += 2;

						}
						if (ground[i].getBBox().intersects(wheel[1].getBBox())) {
							handlepixels(ground[i].image, 0, 0, ground[i].width, ground[i].height, 1);
							if (bi2 != i)
								c2 = 0;
							bi2 = i;
							c2 += 2;

						}
						if (ground[i].getBBox().intersects(wheel[2].getBBox())) {
							handlepixels(ground[i].image, 0, 0, ground[i].width, ground[i].height, 2);
							if (bi3 != i)
								c3 = 0;
							bi3 = i;
							c3 += 2;

						}
					}

					if (rightMove == true) {
						moveWidth += moveSpeed;
						tank.x += moveWidth;
						wheel[0].x += moveWidth;
						wheel[1].x += moveWidth;
						wheel[2].x += moveWidth;
						// moveWidth += moveSpeed;

						c1 += 1;
						c2 += 1;
						c3 += 1;
						if (tank.x > original_X + 50) {
							rightMove = false;
							moveSpeed = 0.1;
							moveWidth = 0;
						}

					}
					if (leftMove == true) {
						moveWidth += moveSpeed;
						tank.x -= moveWidth;
						wheel[0].x -= moveWidth;
						wheel[1].x -= moveWidth;
						wheel[2].x -= moveWidth;
						// moveWidth += moveSpeed;
						c1 -= 1;
						c2 -= 1;
						c3 -= 1;

						if (tank.x < original_X - 50) {
							leftMove = false;
							moveSpeed = 0.1;
							moveWidth = 0;
						}

					}

					wheel[0].y = 505 - pixel1[c1];
					wheel[1].y = 505 - pixel2[c2];
					wheel[2].y = 505 - pixel3[c3];
					tank.y = 485 - pixel3[c2] * 0.7;

				}
				missile.move();
				missile2.move();

				repaint();

			}
		}

	}

	public void paintComponent(Graphics g) {
		// 검정색 우주 배경 그리기
		Color c = new Color(255, 214, 102);
		Color c2 = new Color(0, 184, 70);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
		g.setColor(c);
		g.fillRect(0, 500, moonPatrol.FRAME_W, moonPatrol.FRAME_H);

		g.setColor(c2);
		g.fillRect((int) ground[5].x - 60, (int) ground[5].y, (int) ground[5].width - 80, (int) ground[5].height + 80);
		g.drawImage(imgFire, (int) ground[5].x - 60, 520, (int) ground[5].width - 80, 80, null);
		for (int i = 1; i < 5; i++) {
			g.setColor(Util.randColor(255));
			double x0 = Util.rand(-50, 50);
			double y0 = Util.rand(-50, 50);
			double r0 = Util.rand(5, 10);
			g.fillOval((int) ((int) ground[5].x - x0 - r0 / 2), (int) (520 - y0 - r0 / 2), (int) r0, (int) r0);
		}

		for (Star s : star)
			s.draw(g); // 움직이는 별 그리기
		for (Mount2 m2 : mount2)
			m2.draw(g);
		for (Mount m : mount)
			m.draw(g);
		for (Ground gr : ground)
			gr.draw(g);
		for (Bomb b : bomb)
			b.draw(g); // 폭탄 그리기
		 shadow.draw(g);
		missile.draw(g);
		missile2.draw(g);
		tank.draw(g);
		for (Wheel w : wheel)
			w.draw(g);
		for (Ufo u : ufo)
			u.draw(g); // ufo 그리기

	}
}

class GameObj {
	public static int ST_DEATH = 0;
	public static int ST_ALIVE = 1;
	public static int ST_BLAST = 2;
	public static int ST_STOP = 3;

	Image image; // 게임 객체 이미지
	int state; // 게임 객체 상태
	double x, y; // 게임 객체 위치
	int width, height; // 게임 객체 크기
	int blast_count; // 폭발 카운트
	int stop_count = 50;

	int getState() {
		return state;
	} // 상태 확인

	double getX() {
		return x;
	} // 가로 위치 확인

	double getY() {
		return y;
	} // 세로 위치 확인

	// 이미지 출력
	void drawImage(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
		// g.drawRect((int) (x - width / 2), (int) (y - height / 2), width + 20,
		// height + 20);
	}

	// 바운딩박스 생성
	Rectangle getBBox() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), width, height);
	}

	void drawBlast(Graphics g) {
		// blast_count 개수 만큼 연기 그리기
		for (int i = 1; i < blast_count; i++) {
			g.setColor(Util.randColor(128, 255));
			double x0 = Util.rand(-30, 30);
			double y0 = Util.rand(-30, 30);
			double r0 = Util.rand(5, 30);
			g.fillOval((int) (x - x0 - r0 / 2), (int) (y - y0 - r0 / 2), (int) r0, (int) r0);
		}
	}

	void drawFire(Graphics g) {
		// blast_count 개수 만큼 연기 그리기
		for (int i = 1; i < blast_count; i++) {
			g.setColor(Util.randColor(255));
			double x0 = Util.rand(-20, 20);
			double y0 = Util.rand(-20, 20);
			double r0 = Util.rand(5, 30);
			g.fillOval((int) (x - x0 - r0 / 2), (int) (y - y0 - r0 / 2), (int) r0, (int) r0);
		}
	}

}

class Star {
	public static int MAX_STAR_SIZE = 5; // 별의 최대 크기

	private Color color; // 별의 색상
	private double x, y; // 별의 위치
	private double dx, size; // 별의 단위 이동 거리, 크기

	Star() {
		init();
	}

	// 별의 색상, 위치 초기화
	void init() {
		color = Util.randColor(0, 127);
		x = Util.rand(moonPatrol.FRAME_W + 100);
		y = Util.rand(moonPatrol.FRAME_H - 100);
		size = Util.rand(1, MAX_STAR_SIZE);
		dx = 0.2;
	}

	// 타이머에 의한 별의 움직임 처리
	void move() {
		// 아래 방향으로 이동, 화면 밖으로 나가면 초기화
		x -= dx;
		if (x < 0) {
			init();
			x = Util.rand(moonPatrol.FRAME_W, moonPatrol.FRAME_W + 100);
		}
	}

	// 별 그리기
	void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - dx), (int) (y - dx), (int) size, (int) size);
	}
}

class Ground extends GameObj {
	private int dx = 4;

	Ground(Image img, int w, int h, int s) {
		image = img;

		state = ST_ALIVE;

		if (s == 0) {
			x = 100;
		} else
			x = s * 200 + 100;
		y = 495;
		width = w;
		height = h;

	}

	void init() {
		x = 1090;
	}

	void move() {

		x -= dx;
		if (x < -100) {
			init();
		}

	}

	void draw(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

}

class Mount extends GameObj {
	private int dx = 1;

	Mount(Image img, int w, int h, int s) {
		image = img;
		state = ST_ALIVE;
		if (s == 0) {
			x = 300;
		} else if (s == 1) {
			x = 900;
		} else if (s == 2) {
			x = 1500;
		}
		y = (moonPatrol.FRAME_H / 10) * 7 - 40;
		width = w;
		height = h;

	}

	void init() {
		x = 1450;
	}

	void move() {

		x -= dx;
		if (x < -310) {
			init();
		}

	}

	void draw(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

}

class Mount2 extends GameObj {
	private double dx = 0.4;

	Mount2(Image img, int w, int h, int s) {
		image = img;
		state = ST_ALIVE;
		if (s == 0) {
			x = 300;
		} else if (s == 1) {
			x = 900;
		} else if (s == 2) {
			x = 1500;
		}
		y = (moonPatrol.FRAME_H / 10) * 6 - 40;
		width = w;
		height = h;

	}

	void init() {
		x = 1450;
	}

	void move() {

		x -= dx;
		if (x < -310) {
			init();
		}

	}

	void draw(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

}

class MyTank extends GameObj {
	private int dx;

	// 생성자
	MyTank(Image img, int w, int h) {
		image = img;// 몸체
		state = ST_ALIVE;
		x = 370;
		y = 475;
		width = w;
		height = h;
		dx = 2;
	}

	void move(int p) {

		// y = dy - p;

	}

	// 뒤쪽으로 이동
	void moveLeft() {
		if (x >= 70) {
			x -= dx;
			// dx++;
		}
	}

	// 앞쪽으로 이동
	void moveRight() {
		if (x < moonPatrol.FRAME_W - 70) {
			x += dx;
			// dx++;
		}
	}

	void blast() {
		state = ST_BLAST;
		blast_count = 15;
	}

	// 데드 상태 설정
	void death() {
		state = ST_DEATH;
	}

	void drawImage(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

	// 탱크 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST)
			drawBlast(g);
	}

}

class Wheel extends GameObj {
	private int dx;

	// 생성자
	Wheel(Image img, int x, int y, int w, int h) {
		image = img;// 몸체
		state = ST_ALIVE;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		dx = 2;
	}

	void drawImage(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

	void move() {

	}

	// 뒤쪽으로 이동
	void moveLeft() {
		if (x >= 70) {
			x -= dx;
			// dx++;
		}
	}

	// 앞쪽으로 이동
	void moveRight() {
		if (x < moonPatrol.FRAME_W - 70) {
			x += dx;
			// dx++;
		}
	}

	void blast(int c) {
		if (c == 0) {
			x++;
			y--;
		} else if (c == 1) {
			y--;
		} else if (c == 2) {
			x--;
			y--;
		}
		state = ST_DEATH;

	}

	// 탱크 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
	}

}

class Ufo extends GameObj {
	public static int SC_INDEX = 6;
	public static int MOVE_DIST = 15;
	public static int SCENARIO = 1;
	public static int UFO_COUNT = 5;

	// 시나리오
	private int sc_x1[] = { -100, 900, 900, 100, 100, 1100 };
	private int sc_y1[] = { 50, 50, 150, 150, 100, 100 };
	private int sc_x2[] = { -100, 300, 300, 600, 600, 1100 };
	private int sc_y2[] = { 50, 50, 100, 100, 150, 150 };
	private int sc_x3[] = { -100, 400, 200, 800, 600, 1100 };
	private int sc_y3[] = { 50, 50, 100, 100, 50, 50 };
	private int sc_i; // 다음 목표지점 인덱스

	// 생성자
	Ufo(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// ufo 발생
	void birth() {
		state = ST_ALIVE;

		// 시작 위치 지정
		switch (SCENARIO) {
		case 1:
			x = sc_x1[0];
			y = sc_y1[0];
			break;
		case 2:
			x = sc_x2[0];
			y = sc_y2[0];
			break;
		case 3:
			x = sc_x3[0];
			y = sc_y3[0];
			break;
		}

		// 다음 목표지점 인덱스 초기화
		sc_i = 1;
	}

	// 폭발 상태 설정
	void blast() {
		state = ST_BLAST;
		blast_count = 15;
	}

	// 데드 상태 설정
	void death() {
		state = ST_DEATH;
	}

	// 타이머에 의한 ufo의 움직임 처리
	void move() {
		// ALIVE 상태에서는 좌우로 이동
		if (state == ST_ALIVE) {
			// 다음 목표지점 거리 계산
			if (SCENARIO == 1) {
				double dist = Math.sqrt(Math.pow(x - sc_x1[sc_i], 2) + Math.pow(y - sc_y1[sc_i], 2));
				// 다음 목표지점 거리 확인
				if (dist >= MOVE_DIST) {
					// 목표지점 방향으로 Ufo 이동
					x += (sc_x1[sc_i] - x) / dist * MOVE_DIST;
					y += (sc_y1[sc_i] - y) / dist * MOVE_DIST;
				} else {
					// 마지막 목표지점 도착 확인
					if (sc_i < SC_INDEX - 1)
						sc_i++;
					else {
						state = ST_DEATH; // 다음 목표지점 설정
						UFO_COUNT--;
						if (UFO_COUNT == 0) {
							SCENARIO++;
							UFO_COUNT = 5;
						}
					}
				}
			} else if (SCENARIO == 2) {
				double dist = Math.sqrt(Math.pow(x - sc_x2[sc_i], 2) + Math.pow(y - sc_y2[sc_i], 2));
				// 다음 목표지점 거리 확인
				if (dist >= MOVE_DIST) {
					// 목표지점 방향으로 Ufo 이동
					x += (sc_x2[sc_i] - x) / dist * MOVE_DIST;
					y += (sc_y2[sc_i] - y) / dist * MOVE_DIST;
				} else {
					// 마지막 목표지점 도착 확인
					if (sc_i < SC_INDEX - 1)
						sc_i++;
					else {
						state = ST_DEATH; // 다음 목표지점 설정
						UFO_COUNT--;
						if (UFO_COUNT == 0) {
							SCENARIO++;
							UFO_COUNT = 5;
						}
					}
				}
			} else if (SCENARIO == 3) {
				double dist = Math.sqrt(Math.pow(x - sc_x3[sc_i], 2) + Math.pow(y - sc_y3[sc_i], 2));
				// 다음 목표지점 거리 확인
				if (dist >= MOVE_DIST) {
					// 목표지점 방향으로 Ufo 이동
					x += (sc_x3[sc_i] - x) / dist * MOVE_DIST;
					y += (sc_y3[sc_i] - y) / dist * MOVE_DIST;
				} else {
					// 마지막 목표지점 도착 확인
					if (sc_i < SC_INDEX - 1)
						sc_i++;
					else {
						state = ST_DEATH; // 다음 목표지점 설정
						UFO_COUNT--;
						if (UFO_COUNT == 0) {
							SCENARIO = 1;
							UFO_COUNT = 5;
						}
					}
				}
			}
		}

		// BLAST 상태에서는 count 시간 후 DEATH로 설정
		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// ufo 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST)
			drawBlast(g);
	}
}

class Bomb extends GameObj {
	private double dx, dy; // 폭탄의 단위 이동 거리
	// 생성자

	Bomb(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y 위치에서 mx, my 위치로 폭탄 발사
	void shot(double x, double y, double mx, double my) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
			dx = (mx - x) / 250;
			dy = (my - y) / 250;
		}
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_BLAST;
		blast_count = 30; // 폭탄 폭발을 위해 상태를 BLAST로 변경함.
	}

	// 타이머에 의한 폭탄의 움직임 처리
	void move() {
		if (state == ST_ALIVE) {
			x += dx;
			y += dy;
			if (y < -40 || moonPatrol.FRAME_H + 40 < y) // 화면 밖으로 넘어갔을 때
				state = ST_DEATH;
		}
	}

	// 폭탄 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST) {
			if (blast_count % 2 == 0)
				drawImage(g);
			drawBlast(g);
			state = ST_DEATH; // 폭탄 폭발 후 DEATH 상태로 만들어 blast 이미지가 사라지게 함/.
		}
	}
}

class Missile extends GameObj {
	private double dx;// 폭탄의 단위 이동 거리

	// 생성자
	Missile(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y 위치에서 미사일 발사
	void shot(double x, double y) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
			dx = 0;
		}
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_DEATH;
	}

	void move() {
		if (state == ST_ALIVE) {

			dx += 0.1;
			x += dx;

			if (moonPatrol.FRAME_W + 30 < x)// 화면 밖으로 나갈 경우
				state = ST_DEATH;
		}
	}

	// 미사일 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE) {
			drawImage(g);
		} else if (state == ST_BLAST) {
			drawBlast(g);
			state = ST_DEATH;
		}
	}
}

class Missile2 extends GameObj {
	private double dy;// 폭탄의 단위 이동 거리

	// 생성자
	Missile2(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y 위치에서 미사일 발사
	void shot(double x, double y) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
			dy = 0;
		}
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_DEATH;
	}

	void move() {
		if (state == ST_ALIVE) {

			dy += 0.1;
			y -= dy;

			if (0 > y)// 화면 밖으로 나갈 경우
				state = ST_DEATH;
		}
	}

	// 미사일 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE) {
			drawImage(g);
		} else if (state == ST_BLAST) {
			drawBlast(g);
			state = ST_DEATH;
		}
	}
}
class Shadow extends GameObj {
	   public static int SC_INDEX = 3;
	   public static int MOVE_DIST = 5;
	   // 시나리오
	   private int sc_x[] = { -100, 100, 1100};
	   private int sc_y[] = { 475, 475, 475};
	   private int sc_i; // 다음 목표지점 인덱스
	   
	   // 생성자
	   
	   Shadow(Image img, int w, int h) {
	      image = img;
	      state = ST_DEATH;
	      width = w;
	      height = h;
	   }

	   // Shadow 발생
	   void birth() {
	      state = ST_ALIVE;

	      // 시작 위치 지정
	      x = sc_x[0];
	      y = sc_y[0];
	      // 다음 목표지점 인덱스 초기화
	      sc_i = 1;
	      MOVE_DIST = 5;
	   }

	   // 폭발 상태 설정
	   void blast() {
	      state = ST_BLAST;
	      blast_count = 15;
	   }
	   
	   // 데드 상태 설정 
	   void death() {
	      state = ST_DEATH;
	   }
	   
	   void stop() {
	      state = ST_STOP;
	      stop_count = 50;
	      MOVE_DIST = 20;
	   }


	   // 타이머에 의한 Shadow의 움직임 처리
	   void move() {
	      // ALIVE 상태에서는 좌우로 이동
	      if (state == ST_ALIVE) {
	         // 다음 목표지점 거리 계산
	         double dist = Math.sqrt(Math.pow(x - sc_x[sc_i], 2) + Math.pow(y - sc_y[sc_i], 2));
	         // 다음 목표지점 거리 확인
	         if (dist >= MOVE_DIST) {
	            // 목표지점 방향으로 Ufo 이동
	            x += (sc_x[sc_i] - x) / dist * MOVE_DIST;
	            y += (sc_y[sc_i] - y) / dist * MOVE_DIST;
	         } else {
	            // 마지막 목표지점 도착 확인
	            if(sc_i < SC_INDEX - 1)
	            {
	               sc_i++;
	               System.out.println(sc_i);
	               if(sc_i == 2)
	                  stop();
	            }
	            else
	            {
	               state = ST_DEATH; // 다음 목표지점 설정
	               stop_count = 50;
	            }
	         }
	      }

	      // BLAST 상태에서는 count 시간 후 DEATH로 설정
	      else if (state == ST_BLAST) {
	         blast_count--;
	         if (blast_count == 0)
	            state = ST_DEATH;
	      }
	      
	      else if (state == ST_STOP) {
	         stop_count--;
	         if(stop_count == 0)
	            state = ST_ALIVE;
	      }
	   }

	   // Shadow 그리기
	   void draw(Graphics g) {
	      if (state == ST_ALIVE || state == ST_STOP)
	         drawImage(g);
	      else if (state == ST_BLAST)
	         drawBlast(g);
	   }
	}