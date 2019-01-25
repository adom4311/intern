package server.sangwoo;

import java.io.IOException;

import server.Receiver;

public class Timer extends Thread{
	private Long acc_time;
	private boolean signupflag;
	public boolean isSignupflag() {
		return signupflag;
	}
	public void setSignupflag(boolean signupflag) {
		this.signupflag = signupflag;
	}
	Receiver receiver;
	public Timer(Long acc_time, Receiver receiver) {
		this.acc_time = acc_time;
		this.receiver=receiver;
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
		}
		
	}
	
	

}
