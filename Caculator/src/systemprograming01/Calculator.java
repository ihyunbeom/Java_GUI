package systemprograming01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Calculator extends JFrame {
	public String number = "0";
	JLabel la = new JLabel(number);

	Calculator(){
		setTitle("현범이의 계산기");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container con = getContentPane();
		con.setLayout(null);
		Font font = new Font("고딕",0,50);
		
		la.setBorder(new LineBorder(Color.BLUE, 1));
		la.setLocation(10, 10);
		la.setSize(350, 80);
		la.setFont(font);
		la.setHorizontalAlignment(JLabel.RIGHT);
		
		JButton btC = new JButton("C");
		btC.setSize(80, 80);
		btC.setLocation(10,100);
		btC.setFont(font);
		btC.addActionListener(new MyActionListener());
		
		JButton btX = new JButton("X");
		btX.setSize(80, 80);
		btX.setLocation(100,100);
		btX.setFont(font);
		btX.addActionListener(new MyActionListener());
		
		JButton btD = new JButton("/");
		btD.setSize(80, 80);
		btD.setLocation(190,100);
		btD.setFont(font);
		btD.addActionListener(new MyActionListener());
		
		JButton btM = new JButton("-");
		btM.setSize(80, 80);
		btM.setLocation(280,100);
		btM.setFont(font);
		btM.addActionListener(new MyActionListener());
		
		JButton bt1 = new JButton("1");
		bt1.setSize(80, 80);
		bt1.setLocation(10,370);
		bt1.setFont(font);
		bt1.setBackground(Color.WHITE);
		bt1.addActionListener(new MyActionListener());
		
		JButton bt2 = new JButton("2");
		bt2.setSize(80, 80);
		bt2.setLocation(100,370);
		bt2.setFont(font);
		bt2.setBackground(Color.WHITE);
		bt2.addActionListener(new MyActionListener());
		
		JButton bt3 = new JButton("3");
		bt3.setSize(80, 80);
		bt3.setLocation(190,370);
		bt3.setFont(font);
		bt3.setBackground(Color.WHITE);
		bt3.addActionListener(new MyActionListener());
		
		JButton bt4 = new JButton("4");
		bt4.setSize(80, 80);
		bt4.setLocation(10,280);
		bt4.setFont(font);
		bt4.setBackground(Color.WHITE);
		bt4.addActionListener(new MyActionListener());
		
		JButton bt5 = new JButton("5");
		bt5.setSize(80, 80);
		bt5.setLocation(100,280);
		bt5.setFont(font);
		bt5.setBackground(Color.WHITE);
		bt5.addActionListener(new MyActionListener());
		
		JButton bt6 = new JButton("6");
		bt6.setSize(80, 80);
		bt6.setLocation(190,280);
		bt6.setFont(font);
		bt6.setBackground(Color.WHITE);
		bt6.addActionListener(new MyActionListener());
		
		JButton bt7 = new JButton("7");
		bt7.setSize(80, 80);
		bt7.setLocation(10,190);
		bt7.setFont(font);
		bt7.setBackground(Color.WHITE);
		bt7.addActionListener(new MyActionListener());
		
		JButton bt8 = new JButton("8");
		bt8.setSize(80, 80);
		bt8.setLocation(100,190);
		bt8.setFont(font);
		bt8.setBackground(Color.WHITE);
		bt8.addActionListener(new MyActionListener());
		
		JButton bt9 = new JButton("9");
		bt9.setSize(80, 80);
		bt9.setLocation(190,190);
		bt9.setFont(font);
		bt9.setBackground(Color.WHITE);
		bt9.addActionListener(new MyActionListener());
		
		JButton btP = new JButton("+");
		btP.setSize(80, 170);
		btP.setLocation(280,190);
		btP.setFont(font);
		btP.addActionListener(new MyActionListener());
		
		JButton btE = new JButton("=");
		btE.setSize(80, 170);
		btE.setLocation(280,370);
		btE.setFont(font);
		btE.addActionListener(new MyActionListener());
		
		JButton bt0 = new JButton("0");
		bt0.setSize(260,80);
		bt0.setLocation(10,460);
		bt0.setFont(font);
		bt0.setBackground(Color.WHITE);
		bt0.addActionListener(new MyActionListener());
			
		con.add(la);
		con.add(btC);
		con.add(btX);
		con.add(btD);
		con.add(btM);
		con.add(bt7);
		con.add(bt8);
		con.add(bt9);
		con.add(bt4);
		con.add(bt5);
		con.add(bt6);
		con.add(bt1);
		con.add(bt2);
		con.add(bt3);
		con.add(btP);
		con.add(btE);
		con.add(bt0);
		
		setSize(385,600);
		setVisible(true);
		
	}
	private int chipher = 10;
	private int cur = 0;
	private int pre = 0;
	private int out = 0;
	private String op;
	
	private class  MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			
			if(btn.getText().equals("1")){
				cur = cur*chipher + 1;
				out = cur;
				la.setText( Integer.toString(out));
							
			}else if(btn.getText().equals("2")){
				cur = cur*chipher + 2;
				out = cur;
				la.setText( Integer.toString(out));
							
			}else if(btn.getText().equals("3")){
				cur = cur*chipher + 3;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("4")){
				cur = cur*chipher + 4;
				out = cur;
				la.setText( Integer.toString(out));
	
			}else if(btn.getText().equals("5")){
				cur = cur*chipher + 5;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("6")){
				cur = cur*chipher + 6;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("7")){
				cur = cur*chipher + 7;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("8")){
				cur = cur*chipher + 8;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("9")){
				cur = cur*chipher + 9;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("0")){
				cur = cur*chipher + 0;
				out = cur;
				la.setText( Integer.toString(out));
			
			}else if(btn.getText().equals("+")){
				pre = cur;
				cur = 0;
				op = "+";
			}
			else if(btn.getText().equals("/")){
				pre = cur;
				cur = 0;
				op = "/";
			}else if(btn.getText().equals("X")){
				pre = cur;
				cur = 0;
				op = "*";
			}else if(btn.getText().equals("-")){
				pre = cur;
				cur = 0;
				op = "-";
			}else if(btn.getText().equals("C")){
				pre = 0;
				cur = 0;
				out = 0;
				op = null;
				la.setText("0");
				//System.out.println("Initialize");
				
			}else if(btn.getText().equals("=")){
				switch(op){
					case "+" :
						out = pre + cur;
						cur = out;
						la.setText(Integer.toString(out));
						break;
					case "-" :
						out = pre - cur;
						cur = out;
						la.setText(Integer.toString(out));
						break;
					case "*" :
						out = pre * cur;
						cur = out;
						la.setText(Integer.toString(out));
						break;
					case "/" :
						out = pre / cur;
						cur = out;
						la.setText(Integer.toString(out));
						break;
				}
				
			}
			System.out.println("Pre=" + pre + " Cur=" + cur + " Out=" + out + " Op=" + op);				
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Calculator();

	}
}

