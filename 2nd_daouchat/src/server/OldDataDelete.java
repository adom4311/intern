package server;

import java.util.TimerTask;

import model.dao.ServerDAO;

public class OldDataDelete extends TimerTask {
	private ServerDAO sDao;
	public OldDataDelete(ServerBack serverBack) {
		sDao = new ServerDAO();
	}

	@Override
	public void run() {
		sDao.deleteolddata();
	}

}
