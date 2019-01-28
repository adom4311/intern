package server.sangwoo;

import java.io.IOException;

import server.Receiver;

public class Timer extends Thread{
	private Long acc_time;
	private Long ev_time;
	public Long getEv_time() {
		return ev_time;
	}
	public void setEv_time(Long ev_time) {
		this.ev_time = ev_time;
	}
	private boolean signupflag;
	public boolean isSignupflag() {
		return signupflag;
	}
	public void setSignupflag(boolean signupflag) {
		this.signupflag = signupflag;
	}
	Receiver receiver;
	public Timer(Long acc_time, Receiver receiver, Long ev_time) {
		this.acc_time = acc_time;
		this.receiver=receiver;
		this.ev_time = ev_time;
		signupflag=false;
	}
	@Override
	public void run() {
		while(receiver.getSocket()!=null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Long current = System.currentTimeMillis();
			if(current-acc_time>5000L) {
				if(signupflag==false) {
					try {
						receiver.getSocket().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if(current-ev_time>600000L) {
				try {
					receiver.getSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	

}
