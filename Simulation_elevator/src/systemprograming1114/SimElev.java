package systemprograming1114;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//========================================
// 엘리베이터 카 하드웨어 클래스
//========================================

class Car {
	// 카 정보
	public static final int CAR_WIDTH = 140;	// 카 가로 크기
	public static final int CAR_HEIGHT = 120;	// 카 세로 크기
	public static final int CAR_SPEED = 10;		// 속도 1m/sec = 10cm/100msec

	// 카 상태 값
	public static final int CS_STOP = 0;		// 정지
	public static final int CS_OPENING = 1;		// 문 여는 중
	public static final int CS_OPENED = 2;		// 문 열린 상태
	public static final int CS_CLOSING = 3;		// 문 닫는 중
	public static final int CS_MOVING = 4;		// 이동 중

	// 카 이동 방향 값
	public static final int DIR_UP = 0;			// 상행
	public static final int DIR_DOWN = 1;		// 하행

	// 도어 관련 상수
	public static final int OPEN_TIME = 3;		// 문열림 대기시간 3초
	public static final int OPEN_COUNT = 30;	// 문열림 대기시간 카운트

	private int	state;			// 카 현재 상태
	private int	direction;		// 카 이동 방향

	private int	cur_floor;		// 현재층, 0 = 1층
	private int	dest_floor;		// 도착층
	private int	cur_height;		// 카 높이, 바닥 기준
	private int	dest_height;	// 도착 높이

	private int open_rate;		// 도어 열린 정도, 0 = 닫혔음, 100 = 열렸음
	private int open_timer;		// 문열림 시간 타이머

	private String name;		// 엘리베이터 이름
	private int	bx, by;			// paint 기준 위치

	private String s_state[] = { "STOP", "OPENING", "OPENED", "CLOSING", "MOVING" };
	private String s_dir[]   = { "UP", "DOWN" };

	int getState()		{ return state;			}
	int getDirection()	{ return direction;		}
	int getCurFloor()	{ return cur_floor;		}

	void setDirection(int dir) { direction = dir;	}

	// Car 생성자, 초기값 설정
	Car(String name, int bx, int by) {
		state = CS_STOP;
		direction = DIR_UP;

		cur_floor = 0;
		dest_floor = 0;
		cur_height = 0;
		dest_height = 0;

		open_rate = 0;
		open_timer = 0;

		this.name = name;
		this.bx = bx;
		this.by = by;
	}

	// Car 이동 명령
	boolean moveTo(int floor) {
		// 현재 문 열린 상태라면, moveTo 지정 실패
		if (state == CS_OPENING ||
				state == CS_OPENED ||
				state == CS_CLOSING) {
			return false;
		}

		// 상태, 도착층, 도착 높이 설정
		state = CS_MOVING;
		dest_floor = floor;
		dest_height = SimElev.FLOOR_H*floor;

		// 이동 방향 설정, (만일 현재 높이 == 도착 높이 라면 변화없음)
		if (cur_floor < dest_floor)
			direction = DIR_UP;
		else if (cur_floor > dest_floor)
			direction = DIR_DOWN;

		// 디버그 메시지
		System.out.printf("moveTo(%dF => %dF) %s\n",
				cur_floor + 1, dest_floor + 1, s_dir[direction]);

		return true;	// moveTo 지정 성공 
	}

	// 문 열기 명령
	void open() {
		if (state == CS_STOP || state == CS_CLOSING)
			state = CS_OPENING;
	}

	// 문 닫기 명령
	void close() {
		if (state == CS_OPENED) {
			open_timer = 0;
			state = CS_CLOSING;
		}
	}

	// 타이머에 의한 반복 작업, 엘리베이터 이동 처리
	void action(SimElev	se) {
		switch (state) {

		// 정지 상태
		case CS_STOP:
			break;

			// 여는 상태, open_rate를 10씩 증가시키다
			//         100이 되면, 열린 상태로 전이
		case CS_OPENING:
			if (open_rate < 100)
				open_rate += 10;
			if (open_rate == 100) {
				state = CS_OPENED;
				open_timer = OPEN_COUNT;		
				// onDoorOpened 핸들러 호출
				se.onDoorOpened(this, cur_floor);	
			}
			break;

			// 열린 상태, open_timer를 1씩 감소시키다가
			//         0이 되면, 닫는 상태로 전이
		case CS_OPENED:
			if (open_timer > 0)
				open_timer--;
			if (open_timer == 0)
				state = CS_CLOSING;
			break;

			// 닫는 상태, open_rate를 10씩 감소시키다
			//         0이 되면, 닫힌 상태로 전이
		case CS_CLOSING:
			if (open_rate > 0)
				open_rate -= 10;
			if (open_rate == 0) {
				state = CS_STOP;
				// onDoorClosed 핸들러 호출
				se.onDoorClosed(this, cur_floor);	
			}
			break;    		

			// 이동 상태
		case CS_MOVING:
			// cur_height 이동
			if (cur_height < dest_height)
				cur_height += CAR_SPEED;
			else if (cur_height > dest_height)
				cur_height -= CAR_SPEED;

			// 현재층 계산
			if (cur_height % SimElev.FLOOR_H == 0)
				cur_floor = cur_height/SimElev.FLOOR_H;

			// 도착 높이에 도달하면 정지
			if (cur_height == dest_height) {
				state = CS_OPENING;
				// onStopCar 이벤트 핸들러 호출
				se.onStopCar(this, cur_floor);
			}
			break;
		}    	
	}

	// paintComponent에 의해 호출
	// (bx, by)위치에 카 그리기, (tx, ty)위치에 상태 표시
	void draw(Graphics g, int tx, int ty) {

		int center_x = bx + 100;
		int height_px = cur_height*SimElev.FRAME_DH/SimElev.BD_H;
		int Car_top = SimElev.FRAME_DH - height_px - CAR_HEIGHT;

		// 카 그리기
		g.setColor(Color.GREEN);
		g.fillRect(center_x - 2, by, 4, Car_top);
		g.fillRect(center_x - CAR_WIDTH/2, by + Car_top, CAR_WIDTH, CAR_HEIGHT);

		// 도어 그리기
		for (int i = 0; i < SimElev.FLOOR_SIZE; i++) {
			int d_x = bx + 50;
			int d_bot = SimElev.FRAME_DH - i*SimElev.FRAME_DH/SimElev.FLOOR_SIZE;
			int d_top = d_bot - 100;
			int r_h = SimElev.FRAME_DH/SimElev.FLOOR_SIZE;
			int close_rate;

			// 현재 층인 경우, close_rate 계산
			if (i == cur_floor)
				close_rate = (100 - open_rate)/2;
			else
				close_rate = 50;

			// 도어 그리기
			g.setColor(Color.WHITE);
			g.fillRect(d_x, by + d_top, close_rate, 100);
			g.fillRect(d_x + 100 - close_rate, by + d_top, close_rate, 100);
			g.setColor(Color.BLACK);
			g.drawRect(d_x, by + d_top, close_rate, 100);
			g.drawRect(d_x + 100 - close_rate, by + d_top, close_rate, 100);

			// 프레임 그리기
			g.drawRect(d_x, by + d_top, 100, 100);
			g.drawRect(bx, by + d_bot - r_h, 200, r_h);
		}

		// 카 상태 출력
		g.setColor(Color.BLACK);
		g.drawString("Name = " + name,
				tx + 10, ty + 20);
		g.drawString("State = " + s_state[state],
				tx + 10, ty + 40);
		g.drawString("Floor = " + (cur_floor + 1) + "F (" + cur_floor + ")",
				tx + 10, ty + 60);
		g.drawString("Destination = " + (dest_floor + 1) + "F (" + dest_floor + ")",
				tx + 10, ty + 80);
		g.drawString("Direction = " + s_dir[direction],
				tx + 10, ty + 100);
		g.drawString("Height = " + cur_height + " cm",
				tx + 10, ty + 120);
		g.drawString("Open Timer = " + open_timer,
				tx + 10, ty + 140);
	}
}

//========================================
// 엘리베이터 카 내부 버튼 클래스
//========================================

class CarButton {
	public static final int BTN_WIDTH = 80;		// 화면에 나타나는 버튼 가로 크기
	public static final int BTN_HEIGHT = 30;	// 화면에 나타나는 버튼 세로 크기
	public static final int BTN_NEXT = 40;		// 다음 버튼의 세로 위치 차이

	private	JCheckBox	jbtn_floor[];			// 카의 층 선택 버튼
	private	JButton		jbtn_open;				// 카의 Open 버튼
	private	JButton		jbtn_close;				// 카의 Close 버튼
	private int			bx, by;					// paint 기준 위치

	private boolean		event_skip_flag;		// event 호출 취소 플래그

	boolean	getButton(int floor) { return jbtn_floor[floor].isSelected(); }

	void resetButton(int floor) {
		// 프로그램에 의한 해제이므로 이벤트 발생하지 않도록 함
		if (jbtn_floor[floor].isSelected() == true) {
			event_skip_flag = true;
			jbtn_floor[floor].setSelected(false);
		}
	}

	// CarButton 생성자, 버튼 생성 및 위치 지정
	CarButton(SimElev se, int bx, int by)
	{
		CarButton cbtn = this;
		int	btn_top = (SimElev.FRAME_DH -(SimElev.FLOOR_SIZE + 2)*BTN_NEXT)/2;

		// 층 선택 버튼 생성
		jbtn_floor = new JCheckBox[SimElev.FLOOR_SIZE];

		for (int i = SimElev.FLOOR_SIZE - 1; i >= 0 ; i--) {
			jbtn_floor[i] = new JCheckBox(" Floor " + (i + 1));
			jbtn_floor[i].setLocation(bx + 10, btn_top);
			jbtn_floor[i].setSize(BTN_WIDTH, BTN_HEIGHT);	
			
			// onFloorButton 이벤트 핸들러 등록
			jbtn_floor[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					JCheckBox cb = (JCheckBox)e.getSource();
					int floor = cb.getText().charAt(7) - '1';
					if (event_skip_flag == true)
						event_skip_flag = false;
					else
						se.onFloorButton(cbtn, floor, cb.isSelected());
				}
			});
			se.add(jbtn_floor[i]);
			btn_top += BTN_NEXT;
		}

		// Open 버튼 생성
		jbtn_open = new JButton("OPEN");
		jbtn_open.setLocation(bx + 10, btn_top);
		jbtn_open.setSize(BTN_WIDTH, BTN_HEIGHT);		
		
		// onOpenButton 이벤트 핸들러 추가
		jbtn_open.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				se.onOpenButton(cbtn);
			}
		});
		se.add(jbtn_open);
		btn_top += BTN_NEXT;

		// Close 버튼 생성
		jbtn_close = new JButton("CLOSE");
		jbtn_close.setLocation(bx + 10, btn_top);
		jbtn_close.setSize(BTN_WIDTH, BTN_HEIGHT);		
		
		// onCloseButton 이벤트 핸들러 추가
		jbtn_close.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				se.onCloseButton(cbtn);
			}
		});    	
		se.add(jbtn_close);

		this.bx = bx;
		this.by = by;

		event_skip_flag = false;
	}

	// paintComponent에 의해 호출, (bx, by)위치에 그리기
	void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect(bx, by, 100, SimElev.FRAME_DH);
	}
}

//========================================
// 층별 홀버튼 클래스
//========================================

class HallButton {
	public static final int BTN_WIDTH = 80;		// 화면에 나타나는 버튼 가로 크기
	public static final int BTN_HEIGHT = 20;	// 화면에 나타나는 버튼 세로 크기
	public static final int BTN_NEXT = 30;		// 다음 버튼의 세로 위치 차이

	private	JCheckBox 	jbtn_up[];				// 층별 Up 버튼
	private	JCheckBox 	jbtn_down[];			// 층별 Down 버튼
	private int			bx, by;					// paint 기준 위치

	private boolean		event_skip_flag;		// event 호출 취소 플래그

	boolean	getUpButton(int floor)		{ return jbtn_up[floor].isSelected();	}
	boolean	getDownButton(int floor)	{ return jbtn_down[floor].isSelected();	}

	void resetUpButton(int floor) {
		// 프로그램에 의한 해제이므로 이벤트 발생하지 않도록 함
		if (jbtn_up[floor].isSelected() == true) {
			event_skip_flag = true;
			jbtn_up[floor].setSelected(false);
		}
	}

	void resetDownButton(int floor) {
		// 프로그램에 의한 해제이므로 이벤트 발생하지 않도록 함
		if (jbtn_down[floor].isSelected() == true) {
			event_skip_flag = true;
			jbtn_down[floor].setSelected(false);
		}
	}

	// HallButton 생성자, 버튼 생성 및 위치 지정
	HallButton(SimElev se, int bx, int by) {
		HallButton	hbtn = this;
		jbtn_up = new JCheckBox[SimElev.FLOOR_SIZE];
		jbtn_down = new JCheckBox[SimElev.FLOOR_SIZE];

		for (int i = 0; i < SimElev.FLOOR_SIZE; i++) {
			int d_top = SimElev.FRAME_DH - 
					i*SimElev.FRAME_DH/SimElev.FLOOR_SIZE - 80;

			// Up 버튼 생성
			jbtn_up[i] = new JCheckBox("Up " + (i + 1));

			// 맨 윗층이 아니면 Up 버튼 초기화
			if (i < SimElev.FLOOR_SIZE - 1)	{
				jbtn_up[i].setLocation(bx + 10, d_top);
				jbtn_up[i].setSize(BTN_WIDTH, BTN_HEIGHT);			

				// onUpButton 이벤트 핸들러 등록
				jbtn_up[i].addItemListener(new ItemListener() {	
					public void itemStateChanged(ItemEvent e) {
						JCheckBox cb = (JCheckBox)e.getSource();
						int floor = cb.getText().charAt(3) - '1';
						if (event_skip_flag == true)
							event_skip_flag = false;
						else
							se.onUpButton(hbtn, floor, cb.isSelected());
					}
				});        	
				se.add(jbtn_up[i]);
			}

			// Down 버튼 생성
			jbtn_down[i] = new JCheckBox("Down " + (i + 1));

			// 맨 아래층이 아니면  Down 버튼 초기화
			if (i > 0) {	
				jbtn_down[i].setLocation(bx + 10, d_top + BTN_NEXT);
				jbtn_down[i].setSize(BTN_WIDTH, BTN_HEIGHT);

				// onDownButton 이벤트 핸들러 등록
				jbtn_down[i].addItemListener(new ItemListener() {	
					public void itemStateChanged(ItemEvent e) {
						JCheckBox cb = (JCheckBox)e.getSource();
						int floor = cb.getText().charAt(5) - '1';
						if (event_skip_flag == true)
							event_skip_flag = false;
						else
							se.onDownButton(hbtn, floor, cb.isSelected());
					}
				});	    
				se.add(jbtn_down[i]);
			}
		}

		this.bx = bx;
		this.by = by;

		event_skip_flag = false;
	}

	// paintComponent에 의해 호출, (bx, by)위치에 도어 그리기
	void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect(bx, by, 100, SimElev.FRAME_DH);
	}
}

//========================================
// 엘리베이터 시뮬레이션 클래스
//========================================

@SuppressWarnings("serial")
class SimElev extends JPanel {
	public static final int FRAME_W = 1220;		// 프레임 폭 = 
	// 10 + (100 + 200)*3 + 100 + 200 + 10 = 1220
	public static final int FRAME_H = 720;		// 프레임 높이
	public static final int FRAME_M = 10;		// 프레임 마진
	public static final int FRAME_DH = FRAME_H - FRAME_M*2;	// 프레임 마진 제외 높이

	public static final int FLOOR_H = 400;		// 층고 4m = 400cm
	public static final int FLOOR_SIZE = 5;		// 건물 층수 = 5층
	public static final int BD_H = FLOOR_H*FLOOR_SIZE;	// 건물 높이

	public static final int TIME_SLICE = 100;	// action 실행 주기 100 msec

	private Timer	t;			// Timer 객체
	private int		tick_cnt;	// Timer 호출 횟수 카운터

	private Car			car[];	// 카 객체
	private CarButton	cbtn[];	// 카버튼 객체
	private HallButton	hbtn;	// 홀버튼 객체
	private SimElev		simel;	// SimElev 객체, this

	// SimElev 생성자, 각 엘리베이터 컴포넌트 생성
	SimElev() {
		setLayout(null);	// 레이아웃 제거, 절대 위치로 컴포넌트 삽입

		// 타이머 등록
		t = new Timer(TIME_SLICE, new TimerHandler());
		t.start();
		tick_cnt = 0;

		// 엘리베이터 컴포넌트 생성
		car = new Car[3];
		cbtn = new CarButton[3];	
		cbtn[0] = new CarButton(this,     FRAME_M, FRAME_M);		// 카버튼 객체 생성
		car[0]  = new Car("<Elevator 1>", FRAME_M + 100, FRAME_M);	// 카 객체 생성
		cbtn[1] = new CarButton(this,     FRAME_M + 300, FRAME_M);	// 카버튼 객체 생성
		car[1]  = new Car("<Elevator 2>", FRAME_M + 400, FRAME_M);	// 카 객체 생성
		cbtn[2] = new CarButton(this,     FRAME_M + 600, FRAME_M);	// 카버튼 객체 생성
		car[2]  = new Car("<Elevator 3>", FRAME_M + 700, FRAME_M);	// 카 객체 생성
		hbtn    = new HallButton(this,    FRAME_M + 900, FRAME_M);	// 홀버튼 객체 생성
		simel   = this;
	}

	// Timer handler, 각 엘리베이터 컴포넌트 동작 처리
	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// 카운트 갱신
			tick_cnt++;

			// 엘리베이터 하드웨어 동작
			car[0].action(simel);
			car[1].action(simel);
			car[2].action(simel);

			// 전체 다시 그리기
			repaint();
		}
	}

	// paintComponent, 각 엘리베이터 컴포넌트 그리기
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int bx = FRAME_M + 1000;
		int by = FRAME_M;

		g.drawString("Tick Counter = " + tick_cnt, bx + 10, by + 20);

		// 각 엘리베이터 컴포넌트  그리기
		cbtn[0].draw(g);
		car[0].draw(g,  FRAME_M + 1000, FRAME_M + 30);
		cbtn[1].draw(g);
		car[1].draw(g,  FRAME_M + 1000, FRAME_M + 180);
		cbtn[2].draw(g);
		car[2].draw(g,  FRAME_M + 1000, FRAME_M + 330);
		hbtn.draw(g);
	}

	// main 메소드
	public static void main(String[] arg) {
		JFrame f = new JFrame("SimElevator v1.0 - Single Mode");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new SimElev());
		f.setSize(FRAME_W + 16, FRAME_H + 40);
		f.setVisible(true);
	}

	//========================================
	// 엘리베이터 컨트롤 이벤트 핸들러 모음
	//========================================

	Car getCar(CarButton cb) {
		for (int i = 0; i < 3; i++)
			if (cb == cbtn[i]) return car[i];
		return car[0];	// dummy code
	}

	CarButton getCarButton(Car c) {
		for (int i = 0; i < 3; i++)
			if (c == car[i]) return cbtn[i];
		return cbtn[0];	// dummy code
	}
	
	// Up 방향으로 도착층 검색
	int findUpDest(Car c) {
		CarButton cb = getCarButton(c);	// 카 버튼
		int cur = c.getCurFloor();		// 현재 층

		// 멈취있는 상태에서 현재층 버튼이나 현재층의 Up 홀버튼 확인
		if (c.getState() != Car.CS_MOVING) {
			if (cb.getButton(cur) == true || hbtn.getUpButton(cur) == true)
				return cur;
		}
		// 현재 층에서 Up 방향에 눌려있는 가장 가까운 층버튼 또는 Up 홀버튼 검색
		for (int i = cur + 1; i < FLOOR_SIZE; i++) {
			if (cb.getButton(i) == true || hbtn.getUpButton(i) == true)
				return i;
		}
		// Up 방향에 눌려있는 가장 먼 Down 홀버튼 검색
		for (int i = FLOOR_SIZE - 1; i > cur; i--) {
			if (hbtn.getDownButton(i) == true)
				return i;
		}
		// 멈취있는 상태에서 현재층의 Down 홀버튼 확인
		if (c.getState() != Car.CS_MOVING) {
			if (hbtn.getDownButton(cur) == true) {
				c.setDirection(Car.DIR_DOWN);	// 방향 전환
				return cur;
			}
		}
		return -1;	// 검색 실패
	}

	// Down 방향으로 도착층 검색
	int findDownDest(Car c) {
		CarButton cb = getCarButton(c);	// 카 버튼
		int cur = c.getCurFloor();		// 현재 층

		// 멈취있는 상태에서 현재층 버튼이나 현재층의 Down 홀버튼 확인
		if (c.getState() != Car.CS_MOVING) {
			if (cb.getButton(cur) == true || hbtn.getDownButton(cur) == true)
				return cur;
		}
		// 현재 층에서 Down 방향에 눌려있는 가장 가까운 층버튼 또는 Down 홀버튼 검색
		for (int i = cur - 1; i >= 0; i--) {
			if (cb.getButton(i) == true || hbtn.getDownButton(i) == true)
				return i;
		}
		// Down 방향에 눌려있는 가장 먼 Up 홀버튼 검색
		for (int i = 0; i < cur; i++) {
			if (hbtn.getUpButton(i) == true)
				return i;
		}
		// 멈취있는 상태에서 현재층의 Up 홀버튼 확인
		if (c.getState() != Car.CS_MOVING) {
			if (hbtn.getUpButton(cur) == true) {
				c.setDirection(Car.DIR_UP);	// 방향 전환
				return cur;
			}
		}
		return -1;	// 검색 실패
	}

	// 새로운 도착층 검색
	int findNewDest(Car c) {
		int dir = c.getDirection();	// 이동 방향
		int new_dest = -1;

		// 위로 이동 중일때
		if (dir == Car.DIR_UP) {
			new_dest = findUpDest(c);
			if (new_dest == -1)
				new_dest = findDownDest(c);
		}
		// 아래로 이동 중일때
		else if (dir == Car.DIR_DOWN) {
			new_dest = findDownDest(c);
			if (new_dest == -1)
				new_dest = findUpDest(c);    		
		}

		return new_dest;
	}

	// 카가 도착층에 도착하여 멈춘 경우 호출
	void onStopCar(Car c, int floor) {
		System.out.printf("onStopCar(%dF)\n", floor + 1);

		// 도착층 층버튼 해제
		CarButton cb = getCarButton(c);	// 카 버튼
		cb.resetButton(floor);

		// 도착층 이동방향 홀버튼 취소
		int dir = c.getDirection();
		if (dir == Car.DIR_UP)
			hbtn.resetUpButton(floor);
		else // dir == Car.DIR_DOWN
			hbtn.resetDownButton(floor);

		// 새 도착층을 검색하여 이동 방향이 바뀌는지 확인
		findNewDest(c);
		int new_dir = c.getDirection();

		// 이동 방향이 바뀌면, 바뀐 방향 홀버튼 취소
		if (dir != new_dir) {
			if (new_dir == Car.DIR_UP)
				hbtn.resetUpButton(floor);
			else // new_dir == Car.DIR_DOWN
				hbtn.resetDownButton(floor);
		}
	}

	// 도어가 열린 경우 호출
	void onDoorOpened(Car c, int floor) {
		System.out.printf("onDoorOpened(%dF)\n", floor + 1);		

		// 이벤트 처리 . . .
	}

	// 도어가 닫힌 경우 호출
	void onDoorClosed(Car c, int floor) {
		System.out.printf("onDoorClosed(%dF)\n", floor + 1);

		// 새 도착층 검색
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}

	// 카 내부의 Open 버튼이 눌린 경우 호출
	void onOpenButton(CarButton cb) {
		System.out.printf("onOpenButton()\n");

		// 이벤트 처리
		Car c = getCar(cb);
		c.open();
	}

	// 카 내부의 Close 버튼이 눌린 경우 호출
	void onCloseButton(CarButton cb) {
		System.out.printf("onCloseButton()\n");

		// 이벤트 처리
		Car c = getCar(cb);
		c.close();
	}

	// 카 내부의 층 버튼이 눌린 경우 호출
	void onFloorButton(CarButton cb, int floor, boolean sel) {
		System.out.printf("onFloorButton(%dF, %b)\n", floor + 1, sel);

		// 새 도착층 검색
		Car c = getCar(cb);
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}

	// 도어의 Up 버튼이 눌린 경우 호출
	void onUpButton(HallButton hb, int floor, boolean sel) {
		System.out.printf("onUpButton(%dF, %b)\n", floor + 1, sel);

		// 새 도착층 검색
		Car c = car[0];	// 임시
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}

	// 도어의 Down 버튼이 눌린 경우 호출
	void onDownButton(HallButton hb, int floor, boolean sel) {
		System.out.printf("onDownButton(%dF, %b)\n", floor + 1, sel);

		// 새 도착층 검색
		Car c = car[0];	// 임시
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}
}