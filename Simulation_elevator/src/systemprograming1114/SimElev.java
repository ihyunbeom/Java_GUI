package systemprograming1114;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//========================================
// ���������� ī �ϵ���� Ŭ����
//========================================

class Car {
	// ī ����
	public static final int CAR_WIDTH = 140;	// ī ���� ũ��
	public static final int CAR_HEIGHT = 120;	// ī ���� ũ��
	public static final int CAR_SPEED = 10;		// �ӵ� 1m/sec = 10cm/100msec

	// ī ���� ��
	public static final int CS_STOP = 0;		// ����
	public static final int CS_OPENING = 1;		// �� ���� ��
	public static final int CS_OPENED = 2;		// �� ���� ����
	public static final int CS_CLOSING = 3;		// �� �ݴ� ��
	public static final int CS_MOVING = 4;		// �̵� ��

	// ī �̵� ���� ��
	public static final int DIR_UP = 0;			// ����
	public static final int DIR_DOWN = 1;		// ����

	// ���� ���� ���
	public static final int OPEN_TIME = 3;		// ������ ���ð� 3��
	public static final int OPEN_COUNT = 30;	// ������ ���ð� ī��Ʈ

	private int	state;			// ī ���� ����
	private int	direction;		// ī �̵� ����

	private int	cur_floor;		// ������, 0 = 1��
	private int	dest_floor;		// ������
	private int	cur_height;		// ī ����, �ٴ� ����
	private int	dest_height;	// ���� ����

	private int open_rate;		// ���� ���� ����, 0 = ������, 100 = ������
	private int open_timer;		// ������ �ð� Ÿ�̸�

	private String name;		// ���������� �̸�
	private int	bx, by;			// paint ���� ��ġ

	private String s_state[] = { "STOP", "OPENING", "OPENED", "CLOSING", "MOVING" };
	private String s_dir[]   = { "UP", "DOWN" };

	int getState()		{ return state;			}
	int getDirection()	{ return direction;		}
	int getCurFloor()	{ return cur_floor;		}

	void setDirection(int dir) { direction = dir;	}

	// Car ������, �ʱⰪ ����
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

	// Car �̵� ���
	boolean moveTo(int floor) {
		// ���� �� ���� ���¶��, moveTo ���� ����
		if (state == CS_OPENING ||
				state == CS_OPENED ||
				state == CS_CLOSING) {
			return false;
		}

		// ����, ������, ���� ���� ����
		state = CS_MOVING;
		dest_floor = floor;
		dest_height = SimElev.FLOOR_H*floor;

		// �̵� ���� ����, (���� ���� ���� == ���� ���� ��� ��ȭ����)
		if (cur_floor < dest_floor)
			direction = DIR_UP;
		else if (cur_floor > dest_floor)
			direction = DIR_DOWN;

		// ����� �޽���
		System.out.printf("moveTo(%dF => %dF) %s\n",
				cur_floor + 1, dest_floor + 1, s_dir[direction]);

		return true;	// moveTo ���� ���� 
	}

	// �� ���� ���
	void open() {
		if (state == CS_STOP || state == CS_CLOSING)
			state = CS_OPENING;
	}

	// �� �ݱ� ���
	void close() {
		if (state == CS_OPENED) {
			open_timer = 0;
			state = CS_CLOSING;
		}
	}

	// Ÿ�̸ӿ� ���� �ݺ� �۾�, ���������� �̵� ó��
	void action(SimElev	se) {
		switch (state) {

		// ���� ����
		case CS_STOP:
			break;

			// ���� ����, open_rate�� 10�� ������Ű��
			//         100�� �Ǹ�, ���� ���·� ����
		case CS_OPENING:
			if (open_rate < 100)
				open_rate += 10;
			if (open_rate == 100) {
				state = CS_OPENED;
				open_timer = OPEN_COUNT;		
				// onDoorOpened �ڵ鷯 ȣ��
				se.onDoorOpened(this, cur_floor);	
			}
			break;

			// ���� ����, open_timer�� 1�� ���ҽ�Ű�ٰ�
			//         0�� �Ǹ�, �ݴ� ���·� ����
		case CS_OPENED:
			if (open_timer > 0)
				open_timer--;
			if (open_timer == 0)
				state = CS_CLOSING;
			break;

			// �ݴ� ����, open_rate�� 10�� ���ҽ�Ű��
			//         0�� �Ǹ�, ���� ���·� ����
		case CS_CLOSING:
			if (open_rate > 0)
				open_rate -= 10;
			if (open_rate == 0) {
				state = CS_STOP;
				// onDoorClosed �ڵ鷯 ȣ��
				se.onDoorClosed(this, cur_floor);	
			}
			break;    		

			// �̵� ����
		case CS_MOVING:
			// cur_height �̵�
			if (cur_height < dest_height)
				cur_height += CAR_SPEED;
			else if (cur_height > dest_height)
				cur_height -= CAR_SPEED;

			// ������ ���
			if (cur_height % SimElev.FLOOR_H == 0)
				cur_floor = cur_height/SimElev.FLOOR_H;

			// ���� ���̿� �����ϸ� ����
			if (cur_height == dest_height) {
				state = CS_OPENING;
				// onStopCar �̺�Ʈ �ڵ鷯 ȣ��
				se.onStopCar(this, cur_floor);
			}
			break;
		}    	
	}

	// paintComponent�� ���� ȣ��
	// (bx, by)��ġ�� ī �׸���, (tx, ty)��ġ�� ���� ǥ��
	void draw(Graphics g, int tx, int ty) {

		int center_x = bx + 100;
		int height_px = cur_height*SimElev.FRAME_DH/SimElev.BD_H;
		int Car_top = SimElev.FRAME_DH - height_px - CAR_HEIGHT;

		// ī �׸���
		g.setColor(Color.GREEN);
		g.fillRect(center_x - 2, by, 4, Car_top);
		g.fillRect(center_x - CAR_WIDTH/2, by + Car_top, CAR_WIDTH, CAR_HEIGHT);

		// ���� �׸���
		for (int i = 0; i < SimElev.FLOOR_SIZE; i++) {
			int d_x = bx + 50;
			int d_bot = SimElev.FRAME_DH - i*SimElev.FRAME_DH/SimElev.FLOOR_SIZE;
			int d_top = d_bot - 100;
			int r_h = SimElev.FRAME_DH/SimElev.FLOOR_SIZE;
			int close_rate;

			// ���� ���� ���, close_rate ���
			if (i == cur_floor)
				close_rate = (100 - open_rate)/2;
			else
				close_rate = 50;

			// ���� �׸���
			g.setColor(Color.WHITE);
			g.fillRect(d_x, by + d_top, close_rate, 100);
			g.fillRect(d_x + 100 - close_rate, by + d_top, close_rate, 100);
			g.setColor(Color.BLACK);
			g.drawRect(d_x, by + d_top, close_rate, 100);
			g.drawRect(d_x + 100 - close_rate, by + d_top, close_rate, 100);

			// ������ �׸���
			g.drawRect(d_x, by + d_top, 100, 100);
			g.drawRect(bx, by + d_bot - r_h, 200, r_h);
		}

		// ī ���� ���
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
// ���������� ī ���� ��ư Ŭ����
//========================================

class CarButton {
	public static final int BTN_WIDTH = 80;		// ȭ�鿡 ��Ÿ���� ��ư ���� ũ��
	public static final int BTN_HEIGHT = 30;	// ȭ�鿡 ��Ÿ���� ��ư ���� ũ��
	public static final int BTN_NEXT = 40;		// ���� ��ư�� ���� ��ġ ����

	private	JCheckBox	jbtn_floor[];			// ī�� �� ���� ��ư
	private	JButton		jbtn_open;				// ī�� Open ��ư
	private	JButton		jbtn_close;				// ī�� Close ��ư
	private int			bx, by;					// paint ���� ��ġ

	private boolean		event_skip_flag;		// event ȣ�� ��� �÷���

	boolean	getButton(int floor) { return jbtn_floor[floor].isSelected(); }

	void resetButton(int floor) {
		// ���α׷��� ���� �����̹Ƿ� �̺�Ʈ �߻����� �ʵ��� ��
		if (jbtn_floor[floor].isSelected() == true) {
			event_skip_flag = true;
			jbtn_floor[floor].setSelected(false);
		}
	}

	// CarButton ������, ��ư ���� �� ��ġ ����
	CarButton(SimElev se, int bx, int by)
	{
		CarButton cbtn = this;
		int	btn_top = (SimElev.FRAME_DH -(SimElev.FLOOR_SIZE + 2)*BTN_NEXT)/2;

		// �� ���� ��ư ����
		jbtn_floor = new JCheckBox[SimElev.FLOOR_SIZE];

		for (int i = SimElev.FLOOR_SIZE - 1; i >= 0 ; i--) {
			jbtn_floor[i] = new JCheckBox(" Floor " + (i + 1));
			jbtn_floor[i].setLocation(bx + 10, btn_top);
			jbtn_floor[i].setSize(BTN_WIDTH, BTN_HEIGHT);	
			
			// onFloorButton �̺�Ʈ �ڵ鷯 ���
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

		// Open ��ư ����
		jbtn_open = new JButton("OPEN");
		jbtn_open.setLocation(bx + 10, btn_top);
		jbtn_open.setSize(BTN_WIDTH, BTN_HEIGHT);		
		
		// onOpenButton �̺�Ʈ �ڵ鷯 �߰�
		jbtn_open.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				se.onOpenButton(cbtn);
			}
		});
		se.add(jbtn_open);
		btn_top += BTN_NEXT;

		// Close ��ư ����
		jbtn_close = new JButton("CLOSE");
		jbtn_close.setLocation(bx + 10, btn_top);
		jbtn_close.setSize(BTN_WIDTH, BTN_HEIGHT);		
		
		// onCloseButton �̺�Ʈ �ڵ鷯 �߰�
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

	// paintComponent�� ���� ȣ��, (bx, by)��ġ�� �׸���
	void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect(bx, by, 100, SimElev.FRAME_DH);
	}
}

//========================================
// ���� Ȧ��ư Ŭ����
//========================================

class HallButton {
	public static final int BTN_WIDTH = 80;		// ȭ�鿡 ��Ÿ���� ��ư ���� ũ��
	public static final int BTN_HEIGHT = 20;	// ȭ�鿡 ��Ÿ���� ��ư ���� ũ��
	public static final int BTN_NEXT = 30;		// ���� ��ư�� ���� ��ġ ����

	private	JCheckBox 	jbtn_up[];				// ���� Up ��ư
	private	JCheckBox 	jbtn_down[];			// ���� Down ��ư
	private int			bx, by;					// paint ���� ��ġ

	private boolean		event_skip_flag;		// event ȣ�� ��� �÷���

	boolean	getUpButton(int floor)		{ return jbtn_up[floor].isSelected();	}
	boolean	getDownButton(int floor)	{ return jbtn_down[floor].isSelected();	}

	void resetUpButton(int floor) {
		// ���α׷��� ���� �����̹Ƿ� �̺�Ʈ �߻����� �ʵ��� ��
		if (jbtn_up[floor].isSelected() == true) {
			event_skip_flag = true;
			jbtn_up[floor].setSelected(false);
		}
	}

	void resetDownButton(int floor) {
		// ���α׷��� ���� �����̹Ƿ� �̺�Ʈ �߻����� �ʵ��� ��
		if (jbtn_down[floor].isSelected() == true) {
			event_skip_flag = true;
			jbtn_down[floor].setSelected(false);
		}
	}

	// HallButton ������, ��ư ���� �� ��ġ ����
	HallButton(SimElev se, int bx, int by) {
		HallButton	hbtn = this;
		jbtn_up = new JCheckBox[SimElev.FLOOR_SIZE];
		jbtn_down = new JCheckBox[SimElev.FLOOR_SIZE];

		for (int i = 0; i < SimElev.FLOOR_SIZE; i++) {
			int d_top = SimElev.FRAME_DH - 
					i*SimElev.FRAME_DH/SimElev.FLOOR_SIZE - 80;

			// Up ��ư ����
			jbtn_up[i] = new JCheckBox("Up " + (i + 1));

			// �� ������ �ƴϸ� Up ��ư �ʱ�ȭ
			if (i < SimElev.FLOOR_SIZE - 1)	{
				jbtn_up[i].setLocation(bx + 10, d_top);
				jbtn_up[i].setSize(BTN_WIDTH, BTN_HEIGHT);			

				// onUpButton �̺�Ʈ �ڵ鷯 ���
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

			// Down ��ư ����
			jbtn_down[i] = new JCheckBox("Down " + (i + 1));

			// �� �Ʒ����� �ƴϸ�  Down ��ư �ʱ�ȭ
			if (i > 0) {	
				jbtn_down[i].setLocation(bx + 10, d_top + BTN_NEXT);
				jbtn_down[i].setSize(BTN_WIDTH, BTN_HEIGHT);

				// onDownButton �̺�Ʈ �ڵ鷯 ���
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

	// paintComponent�� ���� ȣ��, (bx, by)��ġ�� ���� �׸���
	void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect(bx, by, 100, SimElev.FRAME_DH);
	}
}

//========================================
// ���������� �ùķ��̼� Ŭ����
//========================================

@SuppressWarnings("serial")
class SimElev extends JPanel {
	public static final int FRAME_W = 1220;		// ������ �� = 
	// 10 + (100 + 200)*3 + 100 + 200 + 10 = 1220
	public static final int FRAME_H = 720;		// ������ ����
	public static final int FRAME_M = 10;		// ������ ����
	public static final int FRAME_DH = FRAME_H - FRAME_M*2;	// ������ ���� ���� ����

	public static final int FLOOR_H = 400;		// ���� 4m = 400cm
	public static final int FLOOR_SIZE = 5;		// �ǹ� ���� = 5��
	public static final int BD_H = FLOOR_H*FLOOR_SIZE;	// �ǹ� ����

	public static final int TIME_SLICE = 100;	// action ���� �ֱ� 100 msec

	private Timer	t;			// Timer ��ü
	private int		tick_cnt;	// Timer ȣ�� Ƚ�� ī����

	private Car			car[];	// ī ��ü
	private CarButton	cbtn[];	// ī��ư ��ü
	private HallButton	hbtn;	// Ȧ��ư ��ü
	private SimElev		simel;	// SimElev ��ü, this

	// SimElev ������, �� ���������� ������Ʈ ����
	SimElev() {
		setLayout(null);	// ���̾ƿ� ����, ���� ��ġ�� ������Ʈ ����

		// Ÿ�̸� ���
		t = new Timer(TIME_SLICE, new TimerHandler());
		t.start();
		tick_cnt = 0;

		// ���������� ������Ʈ ����
		car = new Car[3];
		cbtn = new CarButton[3];	
		cbtn[0] = new CarButton(this,     FRAME_M, FRAME_M);		// ī��ư ��ü ����
		car[0]  = new Car("<Elevator 1>", FRAME_M + 100, FRAME_M);	// ī ��ü ����
		cbtn[1] = new CarButton(this,     FRAME_M + 300, FRAME_M);	// ī��ư ��ü ����
		car[1]  = new Car("<Elevator 2>", FRAME_M + 400, FRAME_M);	// ī ��ü ����
		cbtn[2] = new CarButton(this,     FRAME_M + 600, FRAME_M);	// ī��ư ��ü ����
		car[2]  = new Car("<Elevator 3>", FRAME_M + 700, FRAME_M);	// ī ��ü ����
		hbtn    = new HallButton(this,    FRAME_M + 900, FRAME_M);	// Ȧ��ư ��ü ����
		simel   = this;
	}

	// Timer handler, �� ���������� ������Ʈ ���� ó��
	class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// ī��Ʈ ����
			tick_cnt++;

			// ���������� �ϵ���� ����
			car[0].action(simel);
			car[1].action(simel);
			car[2].action(simel);

			// ��ü �ٽ� �׸���
			repaint();
		}
	}

	// paintComponent, �� ���������� ������Ʈ �׸���
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int bx = FRAME_M + 1000;
		int by = FRAME_M;

		g.drawString("Tick Counter = " + tick_cnt, bx + 10, by + 20);

		// �� ���������� ������Ʈ  �׸���
		cbtn[0].draw(g);
		car[0].draw(g,  FRAME_M + 1000, FRAME_M + 30);
		cbtn[1].draw(g);
		car[1].draw(g,  FRAME_M + 1000, FRAME_M + 180);
		cbtn[2].draw(g);
		car[2].draw(g,  FRAME_M + 1000, FRAME_M + 330);
		hbtn.draw(g);
	}

	// main �޼ҵ�
	public static void main(String[] arg) {
		JFrame f = new JFrame("SimElevator v1.0 - Single Mode");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new SimElev());
		f.setSize(FRAME_W + 16, FRAME_H + 40);
		f.setVisible(true);
	}

	//========================================
	// ���������� ��Ʈ�� �̺�Ʈ �ڵ鷯 ����
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
	
	// Up �������� ������ �˻�
	int findUpDest(Car c) {
		CarButton cb = getCarButton(c);	// ī ��ư
		int cur = c.getCurFloor();		// ���� ��

		// �����ִ� ���¿��� ������ ��ư�̳� �������� Up Ȧ��ư Ȯ��
		if (c.getState() != Car.CS_MOVING) {
			if (cb.getButton(cur) == true || hbtn.getUpButton(cur) == true)
				return cur;
		}
		// ���� ������ Up ���⿡ �����ִ� ���� ����� ����ư �Ǵ� Up Ȧ��ư �˻�
		for (int i = cur + 1; i < FLOOR_SIZE; i++) {
			if (cb.getButton(i) == true || hbtn.getUpButton(i) == true)
				return i;
		}
		// Up ���⿡ �����ִ� ���� �� Down Ȧ��ư �˻�
		for (int i = FLOOR_SIZE - 1; i > cur; i--) {
			if (hbtn.getDownButton(i) == true)
				return i;
		}
		// �����ִ� ���¿��� �������� Down Ȧ��ư Ȯ��
		if (c.getState() != Car.CS_MOVING) {
			if (hbtn.getDownButton(cur) == true) {
				c.setDirection(Car.DIR_DOWN);	// ���� ��ȯ
				return cur;
			}
		}
		return -1;	// �˻� ����
	}

	// Down �������� ������ �˻�
	int findDownDest(Car c) {
		CarButton cb = getCarButton(c);	// ī ��ư
		int cur = c.getCurFloor();		// ���� ��

		// �����ִ� ���¿��� ������ ��ư�̳� �������� Down Ȧ��ư Ȯ��
		if (c.getState() != Car.CS_MOVING) {
			if (cb.getButton(cur) == true || hbtn.getDownButton(cur) == true)
				return cur;
		}
		// ���� ������ Down ���⿡ �����ִ� ���� ����� ����ư �Ǵ� Down Ȧ��ư �˻�
		for (int i = cur - 1; i >= 0; i--) {
			if (cb.getButton(i) == true || hbtn.getDownButton(i) == true)
				return i;
		}
		// Down ���⿡ �����ִ� ���� �� Up Ȧ��ư �˻�
		for (int i = 0; i < cur; i++) {
			if (hbtn.getUpButton(i) == true)
				return i;
		}
		// �����ִ� ���¿��� �������� Up Ȧ��ư Ȯ��
		if (c.getState() != Car.CS_MOVING) {
			if (hbtn.getUpButton(cur) == true) {
				c.setDirection(Car.DIR_UP);	// ���� ��ȯ
				return cur;
			}
		}
		return -1;	// �˻� ����
	}

	// ���ο� ������ �˻�
	int findNewDest(Car c) {
		int dir = c.getDirection();	// �̵� ����
		int new_dest = -1;

		// ���� �̵� ���϶�
		if (dir == Car.DIR_UP) {
			new_dest = findUpDest(c);
			if (new_dest == -1)
				new_dest = findDownDest(c);
		}
		// �Ʒ��� �̵� ���϶�
		else if (dir == Car.DIR_DOWN) {
			new_dest = findDownDest(c);
			if (new_dest == -1)
				new_dest = findUpDest(c);    		
		}

		return new_dest;
	}

	// ī�� �������� �����Ͽ� ���� ��� ȣ��
	void onStopCar(Car c, int floor) {
		System.out.printf("onStopCar(%dF)\n", floor + 1);

		// ������ ����ư ����
		CarButton cb = getCarButton(c);	// ī ��ư
		cb.resetButton(floor);

		// ������ �̵����� Ȧ��ư ���
		int dir = c.getDirection();
		if (dir == Car.DIR_UP)
			hbtn.resetUpButton(floor);
		else // dir == Car.DIR_DOWN
			hbtn.resetDownButton(floor);

		// �� �������� �˻��Ͽ� �̵� ������ �ٲ���� Ȯ��
		findNewDest(c);
		int new_dir = c.getDirection();

		// �̵� ������ �ٲ��, �ٲ� ���� Ȧ��ư ���
		if (dir != new_dir) {
			if (new_dir == Car.DIR_UP)
				hbtn.resetUpButton(floor);
			else // new_dir == Car.DIR_DOWN
				hbtn.resetDownButton(floor);
		}
	}

	// ��� ���� ��� ȣ��
	void onDoorOpened(Car c, int floor) {
		System.out.printf("onDoorOpened(%dF)\n", floor + 1);		

		// �̺�Ʈ ó�� . . .
	}

	// ��� ���� ��� ȣ��
	void onDoorClosed(Car c, int floor) {
		System.out.printf("onDoorClosed(%dF)\n", floor + 1);

		// �� ������ �˻�
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}

	// ī ������ Open ��ư�� ���� ��� ȣ��
	void onOpenButton(CarButton cb) {
		System.out.printf("onOpenButton()\n");

		// �̺�Ʈ ó��
		Car c = getCar(cb);
		c.open();
	}

	// ī ������ Close ��ư�� ���� ��� ȣ��
	void onCloseButton(CarButton cb) {
		System.out.printf("onCloseButton()\n");

		// �̺�Ʈ ó��
		Car c = getCar(cb);
		c.close();
	}

	// ī ������ �� ��ư�� ���� ��� ȣ��
	void onFloorButton(CarButton cb, int floor, boolean sel) {
		System.out.printf("onFloorButton(%dF, %b)\n", floor + 1, sel);

		// �� ������ �˻�
		Car c = getCar(cb);
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}

	// ������ Up ��ư�� ���� ��� ȣ��
	void onUpButton(HallButton hb, int floor, boolean sel) {
		System.out.printf("onUpButton(%dF, %b)\n", floor + 1, sel);

		// �� ������ �˻�
		Car c = car[0];	// �ӽ�
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}

	// ������ Down ��ư�� ���� ��� ȣ��
	void onDownButton(HallButton hb, int floor, boolean sel) {
		System.out.printf("onDownButton(%dF, %b)\n", floor + 1, sel);

		// �� ������ �˻�
		Car c = car[0];	// �ӽ�
		int new_dest = findNewDest(c);
		if (new_dest != -1)
			c.moveTo(new_dest);
	}
}