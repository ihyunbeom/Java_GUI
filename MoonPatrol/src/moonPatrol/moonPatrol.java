package moonPatrol;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.Timer;
//import javax.swing.border.LineBorder;

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
	public static int MAX_BOMB2 = 1;
	public static int MAX_MISSILE = 3;

	public static int ST_TITLE = 0;
	public static int ST_GAME = 1;
	public static int ST_ENDING = 2;
	public static int ST_LOAD = 3;
	public static int ST_SCORE = 4;

	private int state;
	private boolean t_up;
	private boolean t_ani;
	private int t_time;
	private int i_trap = 5;

	private MyTank tank;
	private Ground[] ground;
	private Star[] star;
	private Mount[] mount;
	private Mount2[] mount2;
	private Wheel[] wheel;
	private Ufo[] ufo;
	private Bomb[] bomb;
	private Bomb2 bomb2;
	private Missile[] missile;
	private Missile2[] missile2;
	private Shadow shadow;
	private Robot robot;

	private Timer t;
	private Timer t2;

	private double jumpSpeed = 0.2;// 속도
	private double moveSpeed = 0.5;
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
	private int robot_sc_count;
	private int bomb2_sc_count;
	private int cnt = 0;

	private boolean on_ufo = false;
	private boolean on_trap = false;
	private boolean on_shadow = false;
	private boolean on_robot = false;

	private int ckpoint = 0;
	private int score = 0;
	private double lab_time = 0;
	private double cr;
	private double score_ani = 0;
	private int sinario = 0;
	private int enemy_sina = 0;
	private int ufo_sina = 1;
	private int p = 4;
	private int blast_count = 10;
	private boolean trap_blast = false;

	Font font = new Font("HY목각파임B", 0, 25);
	Font font1 = new Font("a옛날목욕탕L", Font.ITALIC, 30);

	Image imgTank, imgUfo, imgBomb, imgBomb2, imgMissile, imgMissile2, imgRobot;
	Image imgMount, imgMount2;
	Image imgWheel;
	Image imgGr1, imgGr2, imgGr3, imgTrap, imgFire;
	Image[] imgShadow;
	Image imgM, imgO, imgN, imgP, imgA, imgT, imgR, imgL;
	Image imgpress1, imgpress2;

	JLabel la = new JLabel("POINT = " + (char) ('A' + ckpoint));
	JLabel la2 = new JLabel("Score = " + Integer.toString(score));
	JLabel la3 = new JLabel("TIME = " + Double.toString(lab_time));
	JLabel lA = new JLabel(" ");
	JLabel lB = new JLabel(" ");
	JLabel lC = new JLabel(" ");
	JLabel lD = new JLabel(" ");
	JLabel lE = new JLabel(" ");

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
			imgBomb2 = ImageIO.read(new File("images/bomb.png"));
			imgRobot = ImageIO.read(new File("images/robot.png"));
			imgShadow = new Image[2];
			for (int i = 0; i < imgShadow.length; i++)
				imgShadow[i] = ImageIO.read(new File("images/Shadow_" + i + ".png"));
			imgM = ImageIO.read(new File("images/M.png"));
			imgO = ImageIO.read(new File("images/O.png"));
			imgN = ImageIO.read(new File("images/N.png"));
			imgP = ImageIO.read(new File("images/P.png"));
			imgA = ImageIO.read(new File("images/A.png"));
			imgT = ImageIO.read(new File("images/T.png"));
			imgR = ImageIO.read(new File("images/R.png"));
			imgL = ImageIO.read(new File("images/L.png"));
			imgpress1 = ImageIO.read(new File("images/press1.png"));
			imgpress2 = ImageIO.read(new File("images/press2.png"));
		} catch (IOException e) {
			System.exit(-1);
			//System.out.println("errrrrrrrrrrrrrrrorrrrrrr");
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
		missile = new Missile[MAX_MISSILE];
		for (int i = 0; i < MAX_MISSILE; i++)
			missile[i] = new Missile(imgMissile, 40, 15);
		missile2 = new Missile2[MAX_MISSILE];
		for (int i = 0; i < MAX_MISSILE; i++)
			missile2[i] = new Missile2(imgMissile2, 15, 30);

		// Robot 생성
		robot = new Robot(imgRobot, 50, 40);

		bomb2 = new Bomb2(imgBomb2, 15, 15);

		// shadow 생성
		shadow = new Shadow(imgShadow[0], 50, 30);
		state = ST_TITLE;
		t_up = false;
		t_ani = false;
		t_time = 200;

		la.setLocation(0, -20);
		la.setSize(997, 100);
		la.setFont(font);
		la.setForeground(Color.RED);

		la2.setLocation(0, 20);
		la2.setSize(500, 100);
		la2.setFont(font);
		la2.setForeground(Color.WHITE);

		la3.setLocation(350, 0);
		la3.setSize(190, 100);
		la3.setFont(font);
		la3.setForeground(Color.RED);

	}

	public void handlepixels(Image img, int x, int y, int w, int h, int p) {
		int[] pixels = new int[w * h];
		int pixel[] = new int[w];
		int[][] pic = new int[w][h];

		PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("waiting for pixels");
			return;
		}
		if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
			System.err.println("image errored");
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
				if (state == ST_TITLE) {
					ck = 0;
					state = ST_LOAD;
				}
				if (tank.getState() == MyTank.ST_ALIVE && state == ST_GAME) {
					for (Missile m : missile) {
						if (m.getState() == Missile.ST_DEATH) {
							m.shot(tank.getX() + 40, tank.getY());
							break;
						}
					}
					for (Missile2 m2 : missile2) {
						if (m2.getState() == Missile2.ST_DEATH) {
							m2.shot(tank.getX() - 22, tank.getY() - 15);
							break;
						}
					}

				}
			}

		}
	}

	class TimerHandler2 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (state != ST_TITLE) {
				ck2++;
			}
			if (ck2 >= 10 && state == ST_GAME) {
				add(la);
				add(la2);
				add(la3);
				if (sinario == 0) {
					lA.setText("A");
					lB.setText("B");
					lC.setText("C");
					lD.setText("D");
					lE.setText("E");
				}
				lab_time += 0.05;
				cr +=0.15;
				
				enemy_sina++;
				la.setText("POINT = " + (char) ('A' + ckpoint));

				// ufo 생성 시나리오
				if (state == ST_GAME) {
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
					if (on_shadow == true) {
						// shadow 생성 시나리오
						shadow_sc_count++;
						for (int i = 0; i < MAX_SHADOW; i++) {
							if (shadow_sc_count % 250 == (i + 1) * 10)
								shadow.birth();
						}
						/// Shadow 움직이기
						shadow.move();
						if (shadow.getState() == Shadow.ST_ALIVE) {
							if (shadow.stop_count == 0)
								shadow.image = imgShadow[1];
							else
								shadow.image = imgShadow[0];
						} else if (shadow.getState() == Shadow.ST_STOP)
							shadow.image = imgShadow[0];
					}
					if (shadow.getState() == Shadow.ST_DEATH)
						on_shadow = false;

					// 미사일 충돌처리(ufo 방향 미사일)
					for (Missile2 m2 : missile2) {
						if (m2.getState() == Missile2.ST_ALIVE) {
							for (Ufo u : ufo) {
								if (u.getState() == Ufo.ST_ALIVE) {
									if (u.getBBox().intersects(m2.getBBox()) || m2.getBBox().intersects(u.getBBox())) {
										u.blast();
										m2.blast();
										score += 20;
										break;
									}
								}
							}
						}
					}
				}
				// shadow 충돌처리
				if (tank.getState() == MyTank.ST_ALIVE) {
					if (shadow.getState() == Shadow.ST_ALIVE) {
						
						if (tank.getBBox().intersects(shadow.getBBox())) {
							if (nowJumping == true) {
								shadow.blast();
								score += 50;
							} else {
								shadow.blast();
								tank.blast();
								if (tank.getState() == MyTank.ST_DEATH)
									state = ST_ENDING;
							}
						}
						
					}
				}

				// robot 충돌
				
				if (tank.getState() == MyTank.ST_ALIVE) {
					if (robot.getState() == Robot.ST_ALIVE) {
						if (tank.getBBox().intersects(robot.getBBox())) {
							tank.blast();
							//robot.blast();
							robot.state = Robot.ST_DEATH;
							if (tank.getState() == MyTank.ST_DEATH)
								state = ST_ENDING;
						}
					}
				}
				
				//bomb2.getState() == Bomb2.ST_ALIVE ||
				
				if (tank.getState() == MyTank.ST_ALIVE) {
					if (bomb2.getState() == Bomb2.ST_ALIVE) {
						if (tank.getBBox().intersects(bomb2.getBBox())) {
							tank.blast();
							//bomb2.blast();
							bomb2.state = Bomb2.ST_DEATH;
							if (tank.getState() == MyTank.ST_DEATH)
								state = ST_ENDING;
						}
					}
				}
				
				// trap 충돌
				
				if (tank.getState() == MyTank.ST_ALIVE) {
					if (on_trap == true) {
						if (tank.getBBox().intersects(ground[5].getBBox()) && tank.y > 480) {
							tank.blast();
							if (tank.getState() == MyTank.ST_DEATH)
								state = ST_ENDING;

						}
					}
				}
				
				tank.move();

				// 미사일 범위 지정(앞 방향 미사일)
				for (Missile m : missile) {
					if (m.getState() == Missile.ST_ALIVE) {
						if (m.getX() > tank.getX() + 200)
							m.blast();
					}
				}

				

				la3.setText("TIME = " + Integer.toString((int) lab_time));
				la2.setText("Score = " + Integer.toString(score));

				// enemy_sina
				//System.out.println("on ufo : " + on_ufo);
				//System.out.println("cnt : " + cnt);
				//System.out.println("ckpoint : " + ckpoint);
				////////////
				// 시나리오
				if (ckpoint % 4 == 0 && ck2 > 10 && cnt == 0 && on_ufo == false) {
					on_ufo = true;
					cnt = 1;

				}
				if (on_ufo == true && ckpoint % 4 == 0 && cnt == 1 && ck2 > 260) {
					on_ufo = false;
					ufo_sc_count = 0;
				}

				if (ckpoint % 4 == 0 && ck2 > 350 && cnt == 1 && on_ufo == false) {
					on_ufo = true;
					cnt = 2;
				}
				if (on_ufo == true && (ckpoint - 1) % 4 == 0 && cnt == 2 && ck2 > 100) {
					on_ufo = false;
					ufo_sc_count = 0;
				}

				if ((ckpoint - 1) % 4 == 0 && ck2 > 170 && cnt == 2 && on_ufo == false) {
					on_ufo = true;
					cnt = 3;
				}
				if (on_ufo == true && (ckpoint - 1) % 4 == 0 && cnt == 3 && ck2 > 420) {
					on_ufo = false;
					ufo_sc_count = 0;
				}

				/// 두번째
				if ((ckpoint - 2) % 4 == 0 && ck2 > 10 && cnt == 3 && on_ufo == false) {
					on_ufo = true;
					cnt = 4;
				}
				if (on_ufo == true && (ckpoint - 2) % 4 == 0 && cnt == 4 && ck2 > 260) {
					on_ufo = false;
					ufo_sc_count = 0;
				}

				if ((ckpoint - 2) % 4 == 0 && ck2 > 350 && cnt == 4 && on_ufo == false) {
					on_ufo = true;
					cnt = 5;
				}
				if (on_ufo == true && (ckpoint - 3) % 4 == 0 && cnt == 5 && ck2 > 100) {
					on_ufo = false;
					ufo_sc_count = 0;
				}

				if ((ckpoint - 3) % 4 == 0 && ck2 > 170 && cnt == 5 && on_ufo == false) {
					on_ufo = true;
					cnt = 0;
				}
				if (on_ufo == true && (ckpoint - 3) % 4 == 0 && cnt == 0 && ck2 > 420) {
					on_ufo = false;
					ufo_sc_count = 0;
				}

				///// trap C~E I~K M~O
				if ((ckpoint >= 2 && ckpoint <= 3) || (ckpoint >= 12) || (ckpoint >= 8 && ckpoint <= 9)) {
					if (on_trap == false && ground[5].x > 900)
						on_trap = true;
				} else if (ground[5].x < -50)
					on_trap = false;
				///// robot E~G I~M
				if ((ckpoint >= 4 && ckpoint <= 5) || (ckpoint >= 8 && ckpoint <= 11)) {
					on_robot = true;
				} else if(on_robot==true)
					on_robot = false;
				///// shadow G~I K~O
				if ((ckpoint >= 6 && ckpoint <= 7) || (ckpoint >= 10)) {
					on_shadow = true;
				} else if(on_shadow == true)
					on_shadow = false;

				///// ALL
				if ((ckpoint >= 14)) {
					on_robot = true;
				}

				////////////

				if (ck2 > 490) {/////////////// 490
					ckpoint++;
					score += 10;
					ck2 = 10;
					la.setText("POINT = " + (char) ('A' + ckpoint));
				}
				//System.out.println("ck2: " + ck2);
				if (ckpoint % p == 0 && ckpoint != 0 && ckpoint!=24) {
					state = ST_SCORE;
				}
				if(ckpoint == 24){
					state = ST_ENDING;
				}
			}
			if (state == ST_SCORE || state == ST_ENDING)

			{
				on_trap = false;
				la3.setFont(font1);
				la2.setFont(font1);
				la.setFont(font1);
				la.setText(" ");
				la2.setText(" ");
				la3.setText(" ");
				lA.setText(" ");
				lB.setText(" ");
				lC.setText(" ");
				lD.setText(" ");
				lE.setText(" ");
				if ((int) score_ani == 0) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME");

					score_ani += 0.1;
				} else if ((int) score_ani == 1) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO");

					score_ani += 0.1;
				} else if ((int) score_ani == 2) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH");

					score_ani += 0.1;
				} else if ((int) score_ani == 3) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : ");
					score_ani += 0.1;
				} else if ((int) score_ani == 4) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YO");

					score_ani += 0.1;
				} else if ((int) score_ani == 5) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR");

					score_ani += 0.1;
				} else if ((int) score_ani == 6) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR TIME : ");

					score_ani += 0.1;
				} else if ((int) score_ani == 7) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR TIME : " + (int) lab_time);

					la.setSize(700, 50);
					la.setLocation(50, 250);
					la.setText("YO");
					// la.setText("YOUR SCORE : " + score);
					score_ani += 0.1;
				} else if ((int) score_ani == 8) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR TIME : " + (int) lab_time);

					la.setSize(700, 50);
					la.setLocation(50, 250);
					la.setText("YOUR");
					// la.setText("YOUR SCORE : " + score);
					score_ani += 0.1;
				} else if ((int) score_ani == 9) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR TIME : " + (int) lab_time);

					la.setSize(700, 50);
					la.setLocation(50, 250);
					la.setText("YOUR SCORE : ");
					// la.setText("YOUR SCORE : " + score);
					score_ani += 0.1;
				} else if ((int) score_ani == 10) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR TIME : " + (int) lab_time);

					la.setSize(700, 50);
					la.setLocation(50, 250);
					la.setText("YOUR SCORE : " + score);
					// la.setText("YOUR SCORE : " + score);
					score_ani += 0.1;
				} else if ((int) score_ani >= 11) {
					la3.setSize(700, 50);
					la3.setLocation(50, 50);
					la3.setText("TIME TO REACH POINT : " + (char) ('A' + ckpoint));

					la2.setSize(700, 50);
					la2.setLocation(50, 150);
					la2.setText("YOUR TIME : " + (int) lab_time);

					la.setSize(700, 50);
					la.setLocation(50, 250);
					la.setText("YOUR SCORE : " + score);
					// la.setText("YOUR SCORE : " + score);
					score_ani += 0.1;
				}

				if ((int) score_ani == 15) {
					la.setLocation(0, -20);
					la.setSize(997, 100);
					la.setFont(font);
					la.setForeground(Color.RED);

					la2.setLocation(0, 20);
					la2.setSize(500, 100);
					la2.setFont(font);
					la2.setForeground(Color.WHITE);

					la3.setLocation(350, 0);
					la3.setSize(190, 100);
					la3.setFont(font);
					la3.setForeground(Color.RED);

					la3.setText("TIME = " + Integer.toString((int) lab_time));
					la2.setText("Score = " + Integer.toString(score));
					la.setText("POINT = " + (char) ('A' + ckpoint));
					sinario++;

					if (sinario == 1) {
						lA.setText("E");
						lB.setText("F");
						lC.setText("G");
						lD.setText("H");
						lE.setText("I");
					} else if (sinario == 2) {
						lA.setText("I");
						lB.setText("J");
						lC.setText("K");
						lD.setText("L");
						lE.setText("M");
					} else if (sinario == 3) {
						lA.setText("M");
						lB.setText("N");
						lC.setText("O");
						lD.setText("P");
						lE.setText("Q");
					} else if (sinario == 4) {
						lA.setText("Q");
						lB.setText("R");
						lC.setText("S");
						lD.setText("T");
						lE.setText("U");
					} else if (sinario == 5) {
						lA.setText("U");
						lB.setText("V");
						lC.setText("W");
						lD.setText("X");
						lE.setText("Y");
					}
					if (state == ST_SCORE) {
						cr = 0;
						ck = 0;
						ck2 = 0;
						score_ani = 0;
						p += 4;
						enemy_sina = 0;
						on_ufo = false;
						on_trap = false;
						on_shadow = false;
						on_robot = false;
						state = ST_LOAD;
					} else if (state == ST_ENDING) {
						cr = 0;
						ck = 0;
						ck2 = 0;
						score_ani = 0;
						p = 4;
						ckpoint = 0;
						score = 0;
						lab_time = 0;
						enemy_sina = 0;
						state = ST_TITLE;
						ufo_sc_count = 0;
						shadow_sc_count = 0;
						robot_sc_count = 0;
						bomb2_sc_count = 0;
						cnt = 0;

						on_ufo = false;
						on_trap = false;
						on_shadow = false;
						on_robot = false;
						sinario = 0;
						ufo_sina = 1;
						la.setText(" ");
						la2.setText(" ");
						la3.setText(" ");
						lA.setText(" ");
						lB.setText(" ");
						lC.setText(" ");
						lD.setText(" ");
						lE.setText(" ");
						for (Ufo u : ufo) {
							u.SCENARIO = 1;
							u.UFO_COUNT = 5;
						}
						Ufo.SCENARIO = 1;
						Ufo.UFO_COUNT = 5;
						trap_blast = false;
						t_ani=false;
						t_up = false;
						t_time = 200;

						tank.state = MyTank.ST_ALIVE;
					}

				}
			}

			repaint();
		}
	}

	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ck++;
			if (ck >= 50 && state == ST_GAME) {

				if (tank.getState() != MyTank.ST_BLAST) {
					for (Ground gr : ground)
						gr.move();

					for (Mount m : mount)
						m.move();

					for (Mount2 m2 : mount2)
						m2.move();
					for (Star s : star)
						s.move();
				}
				for (Missile m : missile)
					m.move();
				for (Missile2 m2 : missile2)
					m2.move();

				if (tank.state == MyTank.ST_DEATH)
					state = ST_ENDING;

				// 로봇 움직이기
				if (on_robot == true) {
					robot_sc_count++;
					if (robot_sc_count > 0) {
						if (ground[3].x > 1000) {
							robot.birth();
							robot.x = ground[3].x;
							robot.y = ground[3].y - 30;
							robot_sc_count = 0;
						}
					}
					robot.move();
					if (robot.getState() == Robot.ST_ALIVE) {

						bomb2_sc_count++;
						for (int i = 0; i < MAX_BOMB2; i++) {
							if (bomb2_sc_count % 100 == (i + 1) * 10)
								bomb2.shot(robot.getX(), robot.getY());
						}
					}
				}
				if(on_robot == false){
					robot.state = Robot.ST_DEATH;
				}

				// 폭탄 움직이기
				for (Bomb b : bomb)
					b.move();
				// 로봇 미사일 움직이기
				bomb2.move();
				// 폭탄 충돌처리
				for (int i = 0; i < MAX_MISSILE; i++) {
					for (Bomb b : bomb) {
						if (b.getState() == Bomb.ST_ALIVE) {
							
							if (tank.getState() == MyTank.ST_ALIVE) {
								if (tank.getBBox().intersects(b.getBBox())) {
									b.blast();
									tank.blast();
									for (Ufo u : ufo)
										u.state = Ufo.ST_DEATH;
									
								}
							}
							

							if (missile2[i].getState() == Missile.ST_ALIVE) {
								if (missile2[i].getBBox().intersects(b.getBBox())) {
									b.blast();
									missile2[i].state = Missile.ST_DEATH;
								}
							}

							for (int j = 0; j < MAX_GROUND; j++) {
								if (b.getBBox().intersects(ground[j].getBBox()))
									b.blast();
							}
						}
					}
				}

				// 로봇 충돌처리
				if (robot.getState() == Robot.ST_ALIVE) {
					
					if (robot.getBBox().intersects(tank.getBBox()))
						//robot.blast();
						robot.state = Robot.ST_DEATH;
						
				}

				// 로봇 미사일 충돌처리
				if (bomb2.getState() == Bomb2.ST_ALIVE) {
					
					if (tank.getState() == MyTank.ST_ALIVE) {
						if (tank.getBBox().intersects(bomb2.getBBox())) {
							//bomb2.blast();
							bomb2.state = Bomb2.ST_DEATH;
						}
					}
					

					if (bomb2.getX() < robot.getX() - 150 || bomb2.getX() > robot.getX() + 150)
						bomb2.blast();
					for (Missile m : missile) {
						if (m.getState() == Missile.ST_ALIVE) {
							if (bomb2.getBBox().intersects(m.getBBox())) {
								//bomb2.blast();
								bomb2.state = Bomb2.ST_DEATH;
								m.blast();
							}
						}
					}
				}

				for (Missile m : missile) {
					if (robot.getState() == Robot.ST_ALIVE && m.getState() == Missile.ST_ALIVE) {
						if (robot.getBBox().intersects(m.getBBox())) {
							//robot.blast();
							robot.state = Robot.ST_DEATH;
							m.blast();
							score += 30;
							bomb2.state = Bomb2.ST_DEATH;
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
					if (on_trap == true)
						i_trap = 6;
					else if (on_trap == false)
						i_trap = 5;
					for (int i = 0; i < i_trap; i++) {
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
						wheel[0].x += moveWidth + 0.12;
						wheel[1].x += moveWidth + 0.12;
						wheel[2].x += moveWidth + 0.12;
						// moveWidth += moveSpeed;

						c1 += 1;
						c2 += 1;
						c3 += 1;
						if (tank.x > original_X + 50) {
							rightMove = false;
							moveSpeed = 0.5;
							moveWidth = 0;
						}

					}
					if (leftMove == true) {
						moveWidth += moveSpeed;
						tank.x -= moveWidth;
						wheel[0].x -= moveWidth + 0.12;
						wheel[1].x -= moveWidth + 0.12;
						wheel[2].x -= moveWidth + 0.12;
						// moveWidth += moveSpeed;
						c1 -= 1;
						c2 -= 1;
						c3 -= 1;

						if (tank.x < original_X - 50) {
							leftMove = false;
							moveSpeed = 0.5;
							moveWidth = 0;
						}

					}

					if (c1 >= 0 && c2 >= 0 && c3 >= 0) {
						wheel[0].y = 505 - pixel1[c1];
						wheel[1].y = 505 - pixel2[c2];
						wheel[2].y = 505 - pixel3[c3];
						tank.y = 485 - pixel3[c2] * 0.7;
					}

				}

			

			}
			if (state == ST_SCORE) {

				tank.x = 500;
				wheel[0].x = 530;
				wheel[1].x = 500;
				wheel[2].x = 470;

				for (Ground gr : ground)
					gr.move();
				for (Mount m : mount)
					m.move();
				for (Mount2 m2 : mount2)
					m2.move();
				for (Star s : star)
					s.move();

				if (nowJumping == false) {

					for (int i = 0; i < i_trap; i++) {
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

					} // for()

					wheel[0].y = 505 - pixel1[c1];
					wheel[1].y = 505 - pixel2[c2];
					wheel[2].y = 505 - pixel3[c3];
					tank.y = 485 - pixel3[c2] * 0.7;

				}
				// System.out.println(ck);
				if (ck == t_time) {
					nowJumping = true;
					original_jumpHeight = jumpHeight;
					original_Y = tank.y;
				}
				if (ck > t_time + 300)
					ck = 400;

			

			} // if(SCORE)

			// TITLE
			if (state == ST_TITLE && ck > 50) {
				tank.x = 500;
				wheel[0].x = 530;
				wheel[1].x = 500;
				wheel[2].x = 470;
				if (t_up == false) {
					for (Ground gr : ground)
						gr.move();
					for (Mount m : mount)
						m.move();
					for (Mount2 m2 : mount2)
						m2.move();
					for (Star s : star)
						s.move();
				}
				if (nowJumping == true) {
					if (ck > t_time + 50 && ck < t_time + 140) {
						//System.out.println("waitting");
						t_up = true;

					} else {
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
				} else if (nowJumping == false) {

					for (int i = 0; i < i_trap; i++) {
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

					} // for()

					wheel[0].y = 505 - pixel1[c1];
					wheel[1].y = 505 - pixel2[c2];
					wheel[2].y = 505 - pixel3[c3];
					tank.y = 485 - pixel3[c2] * 0.7;

				}
				// System.out.println(ck);
				if (ck == t_time) {
					nowJumping = true;
					original_jumpHeight = jumpHeight;
					original_Y = tank.y;
				}
				if (ck > t_time + 300)
					ck = 400;

			} // if(TITLE)
			if (state == ST_LOAD) {
				if (ck > 50)
					state = ST_GAME;
			}
			repaint();
		}

	}

	public void paintComponent(Graphics g) {
		// 검정색 우주 배경 그리기
		Color c = new Color(255, 214, 102);
		Color c2 = new Color(0, 184, 70);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, moonPatrol.FRAME_W, moonPatrol.FRAME_H);

		if (state == ST_LOAD) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, moonPatrol.FRAME_W, moonPatrol.FRAME_H);

		} else if (state == ST_GAME) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
			g.setColor(c);
			g.fillRect(0, 500, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
			for (Star s : star)
				s.draw(g); // 움직이는 별 그리기
			for (Mount2 m2 : mount2)
				m2.draw(g);
			for (Mount m : mount)
				m.draw(g);
			for (Ground gr : ground)
				gr.draw(g);
			if (on_trap == true) {
				g.setColor(c2);
				g.fillRect((int) ground[5].x - 60, (int) ground[5].y, (int) ground[5].width - 80,
						(int) ground[5].height + 80);
				g.drawImage(imgFire, (int) ground[5].x - 60, 520, (int) ground[5].width - 80, 80, null);
				for (int i = 1; i < 5; i++) {
					g.setColor(Util.randColor(255));
					double x0 = Util.rand(-50, 50);
					double y0 = Util.rand(-50, 50);
					double r0 = Util.rand(5, 10);
					g.fillOval((int) ((int) ground[5].x - x0 - r0 / 2), (int) (520 - y0 - r0 / 2), (int) r0, (int) r0);
				}
			} else if (on_trap == false) {
				g.setColor(c);
				g.fillRect((int) ground[5].x - 70, (int) ground[5].y - 5, (int) ground[5].width - 60,
						(int) ground[5].height + 80);
			}

			for (Bomb b : bomb)
				b.draw(g); // 폭탄 그리기
			shadow.draw(g);
			for (Missile m : missile)
				m.draw(g);
			for (Missile2 m2 : missile2)
				m2.draw(g);
			tank.draw(g);
			if (tank.getState() != MyTank.ST_BLAST) {
				for (Wheel w : wheel)
					w.draw(g);
			}
			for (Ufo u : ufo)
				u.draw(g); // ufo 그리기
			bomb2.draw(g); // 폭탄 그리기
			robot.draw(g);
			g.setColor(new Color(47, 85, 151));
			g.fillRect(0, 0, 999, 100);
			g.setColor(new Color(143, 170, 220));
			
			g.fillRect(630, 15, 330, 60);
			g.setColor(Color.GRAY);
			g.fillRect(650, 50, 288, 10);
			
			//cr += 0.05;

			lA.setLocation(640, 20);
			lA.setSize(30, 30);
			lA.setFont(font);
			lA.setForeground(Color.WHITE);
			lB.setLocation(715, 20);
			lB.setSize(30, 30);
			lB.setFont(font);
			lB.setForeground(Color.WHITE);
			lC.setLocation(785, 20);
			lC.setSize(30, 30);
			lC.setFont(font);
			lC.setForeground(Color.WHITE);
			lD.setLocation(855, 20);
			lD.setSize(30, 30);
			lD.setFont(font);
			lD.setForeground(Color.WHITE);
			lE.setLocation(925, 20);
			lE.setSize(30, 30);
			lE.setFont(font);
			lE.setForeground(Color.WHITE);

			add(lA);
			add(lB);
			add(lC);
			add(lD);
			add(lE);

			if (cr < 300) {
				g.setColor(Color.GREEN);
				g.fillRect(650, 50, (int) (0 + cr), 10);
			}

		}

		// SCORE
		if (state == ST_SCORE) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
			g.setColor(c);
			g.fillRect(0, 500, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
			for (Star s : star)
				s.draw(g); // 움직이는 별 그리기
			for (Mount m : mount)
				m.draw(g);
			for (Ground gr : ground)
				gr.draw(g);
			g.setColor(c);
			g.fillRect((int) ground[5].x - 70, (int) ground[5].y - 5, (int) ground[5].width - 60,
					(int) ground[5].height + 80);
			tank.draw(g);
			for (Wheel w : wheel)
				w.draw(g);
		}

		// TITLE
		if (state == ST_TITLE && ck > 50) {

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
			g.setColor(c);
			g.fillRect(0, 500, moonPatrol.FRAME_W, moonPatrol.FRAME_H);
			for (Star s : star)
				s.draw(g); // 움직이는 별 그리기
			// for (Mount2 m2 : mount2)
			// m2.draw(g);
			for (Mount m : mount)
				m.draw(g);
			for (Ground gr : ground)
				gr.draw(g);
			g.setColor(c);
			if (on_trap == false) {
				g.fillRect((int) ground[5].x - 70, (int) ground[5].y - 5, (int) ground[5].width - 60,
						(int) ground[5].height + 80);
			}
			tank.draw(g);
			for (Wheel w : wheel)
				w.draw(g);
			if (t_up == true && t_ani == false) {
				if (ck > t_time + 50)
					g.drawImage(imgM, 190, 10, 300, 300, null);
				if (ck > t_time + 60)
					g.drawImage(imgO, 300, 10, 300, 300, null);
				if (ck > t_time + 70)
					g.drawImage(imgO, 400, 10, 300, 300, null);
				if (ck > t_time + 80)
					g.drawImage(imgN, 500, 10, 300, 300, null);
				if (ck > t_time + 90)
					g.drawImage(imgP, 100, 120, 300, 300, null);
				if (ck > t_time + 100)
					g.drawImage(imgA, 200, 120, 300, 300, null);
				if (ck > t_time + 110)
					g.drawImage(imgT, 300, 120, 300, 300, null);
				if (ck > t_time + 120)
					g.drawImage(imgR, 390, 120, 300, 300, null);
				if (ck > t_time + 130)
					g.drawImage(imgO, 490, 120, 300, 300, null);
				if (ck > t_time + 140)
					g.drawImage(imgL, 560, 120, 300, 300, null);
			}
			if (ck > t_time + 145) {
				t_up = false;
				t_ani = true;
				g.drawImage(imgM, 190, 10, 300, 300, null);
				g.drawImage(imgO, 300, 10, 300, 300, null);
				g.drawImage(imgO, 400, 10, 300, 300, null);
				g.drawImage(imgN, 500, 10, 300, 300, null);
				g.drawImage(imgP, 100, 120, 300, 300, null);
				g.drawImage(imgA, 200, 120, 300, 300, null);
				g.drawImage(imgT, 300, 120, 300, 300, null);
				g.drawImage(imgR, 390, 120, 300, 300, null);
				g.drawImage(imgO, 490, 120, 300, 300, null);
				g.drawImage(imgL, 560, 120, 300, 300, null);
				if (ck % 10 < 5)
					g.drawImage(imgpress1, 350, 310, 300, 100, null);
				else
					g.drawImage(imgpress2, 350, 310, 300, 100, null);
			}
		}

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

	void move() {
		if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// 뒤쪽으로 이동
	void moveLeft() {
		if (state == ST_ALIVE) {
			if (x >= 70) {
				x -= dx;
				// dx++;
			}
		}
	}

	// 앞쪽으로 이동
	void moveRight() {

		if (state == ST_ALIVE) {
			if (x < moonPatrol.FRAME_W - 70) {
				x += dx;
				// dx++;
			}
		}
	}

	void blast() {
		state = ST_BLAST;
		blast_count = 15;
	}

	void drawImage(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

	// 탱크 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST) {
			drawBlast(g);
		}
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
		dx = 1;
	}

	void drawImage(Graphics g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), width, height, null);
	}

	// 뒤쪽으로 이동
	void moveLeft() {
		if (state == ST_ALIVE) {
			if (x >= 70) {
				x -= dx;
				// dx++;
			}
		} else if (state == ST_BLAST)
			state = ST_DEATH;
	}

	// 앞쪽으로 이동
	void moveRight() {
		if (state == ST_ALIVE) {
			if (x < moonPatrol.FRAME_W - 70) {
				x += dx;
				// dx++;
			}
		} else if (state == ST_BLAST)
			state = ST_DEATH;
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
		else if (state == ST_BLAST)
			drawBlast(g);
	}
}

class Ufo extends GameObj {
	public static int SC_INDEX = 6;
	public static int MOVE_DIST = 15;
	public static int SCENARIO = 1;
	public static int UFO_COUNT = 5;

	// 시나리오
	private int sc_x1[] = { -100, 900, 900, 100, 100, 1100 };
	private int sc_y1[] = { 120, 120, 180, 180, 150, 150 };
	private int sc_x2[] = { -100, 300, 300, 600, 600, 1100 };
	private int sc_y2[] = { 120, 120, 150, 150, 180, 180 };
	private int sc_x3[] = { -100, 400, 200, 800, 600, 1100 };
	private int sc_y3[] = { 140, 140, 110, 110, 160, 160 };
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
		UFO_COUNT--;
	}

	void scenario() {
		state = ST_DEATH;
		UFO_COUNT--;
		if (UFO_COUNT == 0) {
			if (SCENARIO == 3)
				SCENARIO = 1;
			else
				SCENARIO++;

			UFO_COUNT = 5;
		}
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
					else
						scenario();
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
					else
						scenario();
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
					else
						scenario();
				}
			}
		}

		// BLAST 상태에서는 count 시간 후 DEATH로 설정
		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
		
		if(UFO_COUNT == 0){
	         if(SCENARIO == 3)
	            SCENARIO = 1;
	         else
	            SCENARIO++;
	         
	         UFO_COUNT = 5;
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

		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// 폭탄 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST) {
			drawBlast(g);
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
		state = ST_BLAST;
		blast_count = 15;
	}

	void move() {
		if (state == ST_ALIVE) {
			dx += 1;
			x += dx;
			if (moonPatrol.FRAME_W + 30 < x)// 화면 밖으로 나갈 경우
				state = ST_DEATH;
		}

		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// 미사일 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST)
			drawBlast(g);
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
		state = ST_BLAST;
		blast_count = 15;
	}

	void move() {
		if (state == ST_ALIVE) {
			dy += 0.1;
			y -= dy;
			if (0 > y)// 화면 밖으로 나갈 경우
				state = ST_DEATH;
		}

		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// 미사일 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST)
			drawBlast(g);
	}
}

class Shadow extends GameObj {
	public static int SC_INDEX = 3;
	public static int MOVE_DIST = 5;
	// 시나리오
	private int sc_x[] = { -100, 100, 1100 };
	private int sc_y[] = { 475, 475, 475 };
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

	void stop() {
		state = ST_STOP;
		stop_count = 50;
		MOVE_DIST = 10;
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
				if (sc_i < SC_INDEX - 1) {
					sc_i++;
					//System.out.println(sc_i);
					if (sc_i == 2)
						stop();
				}

				else {
					state = ST_DEATH; // 다음 목표지점 설정
					stop_count = 50;
				}
			}
		}

		// BLAST 상태에서는 count 시간 후 DEATH로 설정
		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0) {
				state = ST_DEATH;
				stop_count = 50;
			}
		}

		else if (state == ST_STOP) {
			stop_count--;
			if (stop_count == 0)
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

class Robot extends GameObj {
	// 생성자
	Robot(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// Robot 발생
	void birth() {
		state = ST_ALIVE;
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

	// 타이머에 의한 Robot의 움직임 처리
	void move() {
		// ALIVE 상태에서는 좌우로 이동
		if (state == ST_ALIVE) {
			x -= 4;
			if (x < -100)
				state = ST_DEATH;
		}
		// BLAST 상태에서는 count 시간 후 DEATH로 설정
		else if (state == ST_BLAST) {
			blast_count--;
			if (blast_count == 0)
				state = ST_DEATH;
		}
	}

	// Robot 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
		else if (state == ST_BLAST)
			drawBlast(g);
	}
}

class Bomb2 extends GameObj { // 로봇이쏨ㅋ
	boolean dir = false;

	Bomb2(Image img, int w, int h) {
		image = img;
		state = ST_DEATH;
		width = w;
		height = h;
	}

	// x, y 위치에서 mx, my 위치로 폭탄 발사
	void shot(double x, double y) {
		if (state == ST_DEATH) {
			state = ST_ALIVE;
			this.x = x;
			this.y = y;
		}
	}

	// 폭발 상태로 변경
	void blast() {
		state = ST_BLAST;
		blast_count = 15; // 폭탄 폭발을 위해 상태를 BLAST로 변경함.
	}

	// 타이머에 의한 폭탄의 움직임 처리
	void move() {
		if (state == ST_ALIVE) {
			x -= 10;
			if (this.x < -40 || this.x > moonPatrol.FRAME_W + 40)
				state = ST_DEATH;
		}

		else if (state == ST_BLAST)
			state = ST_DEATH;
	}

	// 폭탄 그리기
	void draw(Graphics g) {
		if (state == ST_ALIVE)
			drawImage(g);
	}
}