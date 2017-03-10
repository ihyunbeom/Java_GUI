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
	public static int MAX_STAR_SIZE = 10; // 별의 최대 크기

	private Color color; // 별의 색상
	private double x, y; // 별의 위치
	private double dy, size; // 별의 단위 이동 거리, 크기

	Star() {
		init();
	}

	// 별의 색상, 위치 초기화
	void init() {
		color = Util.randColor(0, 127);
		x = Util.rand(SpaceWar.FRAME_W - 1);
		y = Util.rand(SpaceWar.FRAME_H - 1);
		size = Util.rand(1, MAX_STAR_SIZE);
		dy = size / 2;
	}

	// 타이머에 의한 별의 움직임 처리
	void move() {
		// 아래 방향으로 이동, 화면 밖으로 나가면 초기화
		y += dy;
		if (y > SpaceWar.FRAME_H) {
			init();
			y = -Util.rand(SpaceWar.FRAME_H / 10);
		}
	}

	// 별 그리기
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
	// 가상 함수를 생성해야 한다.(move,birth,blast...등)

	Image image; // 게임 객체 이미지
	int state; // 게임 객체 상태
	double x, y; // 게임 객체 위치
	int width, height; // 게임 객체 크기
	int blast_count; // 폭발 카운트

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
	}

	// 폭발 이미지 출력
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

	void rocketDrawBlast(Graphics g) {
		// blast_count 개수 만큼 연기 그리기
		for (int i = 1; i < blast_count; i++) {
			g.setColor(Util.randColor(255));
			double x0 = Util.rand(-20, 20);
			double y0 = Util.rand(-20, 20);
			double r0 = Util.rand(5, 30);
			g.fillOval((int) (x - x0 - r0 / 2), (int) (y - y0 - r0 / 2), (int) r0, (int) r0);
		}
	}

	// 바운딩박스 생성
	Rectangle getBBox() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), width, height);
	}

}

class Ufo extends GameObj {
	// private double dx; // 단위 이동 거리
	public static int N_SCENARIO = 6;
	public static int MOVE_DIST = 15;

	// 시나리오
	private int sc_x[] = { -100, 700, 700, 100, 100, 900 };
	private int sc_y[] = { 100, 100, 400, 150, 400, 50 };
	private int sc_i; // 다음 목표지점 인덱스

	// 생성자
	Ufo(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// ufo 발생
	void birth() {// alive상태
		state = ST_ALIVE;

		// 시작 위치 지정
		x = sc_x[0];
		y = sc_y[0];
		// 다음 목표지점 인덱스 초기화
		sc_i = 1;

		/*
		 * // 50% 확률로 시작 위치 결정 if (Util.prob100(50)) {// 50%확률 x = -40; dx =
		 * Util.rand(5, 10); } else { x = SpaceWar.FRAME_W + 40; dx =
		 * -Util.rand(5, 10); } y = Util.rand(40, SpaceWar.FRAME_H / 2);
		 */
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

		/*
		 * // ALIVE 상태에서는 좌우로 이동 if (state == ST_ALIVE || state == ST_TURN) { //
		 * 임의의 위치에서 방향 전환 double dir = Util.rand(SpaceWar.FRAME_W / 3,
		 * (SpaceWar.FRAME_W / 3) * 2);
		 * 
		 * if (x >= dir - Math.abs(dx) * 2 && x <= dir + Math.abs(dx) * 2 &&
		 * state != ST_TURN) { state = ST_TURN; } if (state != ST_TURN) {
		 * 
		 * x += dx; } else if (state == ST_TURN) { x -= dx; } if (x < -40 ||
		 * SpaceWar.FRAME_W + 40 < x) { state = ST_DEATH; } }
		 */

		// ALIVE 상태에서는 이동
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
				if (sc_i < N_SCENARIO - 1)
					sc_i++; // 다음 목표지점 설정
				else
					state = ST_DEATH; // 데드 상태 설정
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

	// 게임 프레임 수정
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

	private int state; // 게임 상태
	private int score; // 점수
	private int life; // 라이프
	private int ani_count; // 애니메이션 카운터,
	private int[] degree = new int[MAX_MISSILE];

	// 0~19 반복
	private int sc_count; // 시나리오 카운터

	SpaceWarComponent() {
		// 타이머 등록
		t = new Timer(TIME_SLICE, new TimerHandler());
		t.start();
		// 키 이벤트 등록
		this.addKeyListener(new KeyHandler());
		this.setFocusable(true);

		// 별 생성
		star = new Star[MAX_STAR];
		for (int i = 0; i < MAX_STAR; i++)
			star[i] = new Star();
		// 이미지 읽기
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
		// ufo 생성
		ufo = new Ufo[MAX_UFO];
		for (int i = 0; i < MAX_UFO; i++) {
			ufo[i] = new Ufo(imgUfo, 80, 40);
		}
		// 우주선 생성
		me = new MyShip(imgMyShip, 80, 80);

		// 폭탄 생성
		// bomb = new Bomb(imgBomb, 30, 30);
		bomb = new Bomb[MAX_BOMB];
		for (int i = 0; i < MAX_BOMB; i++)
			bomb[i] = new Bomb(imgBomb, 30, 30);

		// 미사일 생성
		// missile = new Missile(imgMissile, 20, 50);
		missile = new Missile[MAX_MISSILE];
		for (int i = 0; i < MAX_MISSILE; i++)
			missile[i] = new Missile(imgMissile, 15, 15);

		// 로켓 생성
		rocket = new Rocket[MAX_ROCKET];
		for (int i = 0; i < MAX_ROCKET; i++)
			rocket[i] = new Rocket(imgRocket, 30, 60);

		// 각도 초기화
		for (int i = 1; i < MAX_MISSILE; i++) {
			degree[i] = (i % 8) * 45;
		}

		// 게임 상태 초기화
		state = ST_TITLE;
		ani_count = 0;
		sc_count = 0;
	}

	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// 별 움직이기
			for (Star s : star)
				s.move();
			// ufo 생성 시나리오
			sc_count++;
			for (int i = 0; i < MAX_UFO; i++) {
				if (sc_count % 200 == (i + 1) * 10)
					ufo[i].birth();
			}
			// ufo 움직이기
			for (Ufo u : ufo) {
				u.move();
				// 폭탄 발사
				if (state == ST_GAME) // GAME 상태에서만 폭탄 투하
					for (Bomb b : bomb) {
						if (u.getState() == Ufo.ST_ALIVE && Util.prob100(30))
							b.shot(u.getX(), u.getY(), me.getX(), me.getY());
					}
			}

			// 우주선 동작 처리
			me.move();

			// ENDING 화면 우주선 폭발 처리
			ani_count = (ani_count + 1) % 20; // 0 .. 19 반복
			if (state == ST_ENDING)
				if (ani_count == 0)
					me.blast();

			// 폭탄 움직이기
			for (Bomb b : bomb) {
				b.move();
				// 폭탄 충돌처리
				if (b.getState() == Bomb.ST_ALIVE) {
					if (me.getState() == MyShip.ST_ALIVE) {
						if (me.getBBox().intersects(b.getBBox())) {
							me.blast();
							b.blast();
							life--; // 라이프 감소
							if (life == 0)
								state = ST_ENDING; // 게임 종료
						}
					}
				}
			}

			/////////////////// 신무기개발///////////////////////
			// 로켓 움직이기
			for (int i = 0; i < MAX_ROCKET; i++) {
				rocket[i].move();
			}

			// 로켓 충돌 처리
			for (int i = 0; i < MAX_ROCKET; i++) {
				if (rocket[i].getState() == Rocket.ST_ALIVE) {
					if (rocket[i].getY() < Util.rand(40, 400)) {
						rocket[i].blast();
						for (int j = i * 8 + 1; j <= (i * 8) + 8; j++) {
							missile[j].shot(rocket[i].getX(), rocket[i].getY());
						}
						// score += 10; // 점수 증가
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
								score += 10; // 점수 증가
								break;
							}
						}
					}
				}
			}

			// 전체 다시 그리기
			repaint();
		}
	}

	class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if (state == ST_TITLE) {
				if (code == KeyEvent.VK_SPACE) { // 게임 시작
					state = ST_GAME;
					score = 0;
					life = 3;
					me.startMyShip(); // 게임 시작
					for (Ufo u : ufo)
						u.death(); // ufo 초기화
					sc_count = 0;
				}
			} else if (state == ST_GAME) {

				if (code == KeyEvent.VK_LEFT)
					me.moveLeft(); // 왼쪽으로 이동
				else if (code == KeyEvent.VK_RIGHT)
					me.moveRight(); // 오른쪽으로 이동
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
					state = ST_TITLE; // 제목 화면으로 이동
				}
			}
			repaint();
		}
	}

	public void paintComponent(Graphics g) {
		// 검정색 우주 배경 그리기
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, SpaceWar.FRAME_W, SpaceWar.FRAME_H);
		// 구성요소 그리기
		for (Star s : star)
			s.draw(g); // 움직이는 별 그리기

		for (Bomb b : bomb)
			b.draw(g); // 폭탄 그리기

		for (Missile m : missile)
			m.draw(g); // 미사일 그리기

		for (Rocket r : rocket)
			r.draw(g); // 로켓 그리기

		for (Ufo u : ufo)
			u.draw(g); // ufo 그리기

		if (state != ST_TITLE) // 우주선 그리기
			me.draw(g); // 우주선 그리기

		// 상태별 문자 출력
		if (state == ST_TITLE) {
			int zoom = Math.abs(ani_count - 10); // 10 .. 0 .. 9 반복
			g.drawImage(imgTitle, 100 - zoom, 150 - zoom, 600 + zoom * 2, 100 + zoom * 2, null);
			if (ani_count < 10) { // 10 x 50msec = 500msec 주기
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
			if (ani_count < 10) // 10 x 50msec = 500msec 주기
				g.drawString("PRESS ENTER KEY", 230, 400);
		}
	}
}

class MyShip extends GameObj {
	// 생성자
	MyShip(Image img, int w, int h) {
		image = img;
		state = ST_ALIVE;
		x = SpaceWar.FRAME_W / 2;
		y = SpaceWar.FRAME_H - w;
		width = w;
		height = h;
	}

	// 왼쪽으로 이동
	void moveLeft() {
		if (x >= 70)
			x -= 20;
	}

	// 오른쪽으로 이동
	void moveRight() {
		if (x < SpaceWar.FRAME_W - 70)
			x += 20;
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_BLAST;
		blast_count = 30;
	}

	// 게임 시작
	void startMyShip() {
		state = ST_ALIVE;
		x = SpaceWar.FRAME_W / 2;
	}

	// 타이머에 의한 우주선 동작 처리
	void move() {
		// BLAST 상태에서는 count 시간 후 ALIVE로 설정
		if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_ALIVE;
		}
	}

	// 우주선 그리기
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
	private double dx, dy;// 폭탄의 단위 이동 거리

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
			dx = (mx - x) / 50;
			dy = (my - y) / 50;
		}
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_DEATH;
	}

	// 타이머에 의한 폭탄의 움직임 처리
	void move() {
		if (state == ST_ALIVE) {
			x += dx;
			y += dy;
			if (y < -40 || SpaceWar.FRAME_H + 40 < y)
				state = ST_DEATH;
		}
	}

	// 폭탄 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
	}
}

class Missile extends GameObj {
	private double dx, dy;// 폭탄의 단위 이동 거리

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
			dy = 0;
			dx = 0;
		}
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_DEATH;
	}

	// 유도미사일
	void move(double tx, double ty) {
		if (state == ST_ALIVE) {

			if (tx <= x) {// 타겟이 왼쪽
				dy -= 1;
				y += dy;
				dx = (x - tx) / 10;
				x -= dx;
			} else if (x < tx) {// 타겟이 오른쪽
				dy -= 1;
				y += dy;
				dx = (tx - x) / 10;
				x += dx;
			}
			if (y < -40 || SpaceWar.FRAME_H + 40 < y)// 화면 밖으로 나갈 경우
				state = ST_DEATH;
		}
	}

	// 신무기용
	void move(int i) {

		if (state == ST_ALIVE) {
			x -= dy * Math.sin(Math.toRadians(i));
			y -= dy * Math.cos(Math.toRadians(i));
			dy += 0.3;
			if (y < -40 || SpaceWar.FRAME_H + 40 < y || x < -40 || SpaceWar.FRAME_W + 40 < x)
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

class Rocket extends GameObj {
	private double dy;// 폭탄의 단위 이동 거리

	// 생성자
	Rocket(Image img, int w, int h) {
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
		state = ST_BLAST;
		blast_count = 30;
	}

	// 타이머에 의한 미사일의 움직임 처리
	void move() {
		if (state == ST_ALIVE) {
			dy -= 0.3;
			y += dy;
			if (y < -40 || SpaceWar.FRAME_H + 40 < y)// 화면 밖으로 나갈 경우
				state = ST_DEATH;
		}
	}

	// 미사일 그리기
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
