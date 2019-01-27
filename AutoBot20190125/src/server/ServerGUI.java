package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ServerGUI extends JFrame {
	JLabel lblNewLabel_1;
	private JPanel contentPane;
	private JTable table;
	String columnName[] = {"번호", "유저ID", "상태"};
	JTable jt;
	JScrollPane scrollPane;

	public ServerGUI(ServerBack serverBack) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 616);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 33, 410, 500);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel = new JLabel("총 사용자 수 ");
		lblNewLabel.setBounds(12, 8, 80, 15);
		contentPane.add(lblNewLabel);
		
		lblNewLabel_1 = new JLabel("N");
		lblNewLabel_1.setBounds(106, 8, 250, 15);
		contentPane.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("데이터 갱신");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userStatus(serverBack.getCurrentClientMap());
			}
		});
		btnNewButton.setBounds(123, 545, 180, 23);
		contentPane.add(btnNewButton);
		setVisible(true);
	}
	
	public void userStatus(Map<String, ObjectOutputStream> map) {
		synchronized (map) {
			System.out.println("맵크기" + map.size());
			int i = 0;
			int login = 0;
			int non = 0;
			Object rowData[][] = new Object[map.size()][3];
			SortedSet<String> keys = new TreeSet<String>(map.keySet());
			for (String s : keys) { 
				rowData[i][0] = i+1;
				rowData[i][1] = s;
				if(!s.substring(0, 2).equals("GM")) {
					rowData[i][2] = "로그인 중";
					login++;
				}else {
					rowData[i][2] = "연결 중";
					non++;
				}
				i++;
			}
		

			lblNewLabel_1.setText(String.valueOf(i) + "- 로그인 : " + String.valueOf(login) + ", 비로그인 : "+ String.valueOf(non));
			
			// 내용 수정 불가 시작 //
	        DefaultTableModel mod = new DefaultTableModel(rowData, columnName) {
	        public boolean isCellEditable(int rowIndex, int mColIndex) {
	                return false;
	            }
	        };
	      
	
			jt = new JTable(mod); // 친구 찾기 테이블
			scrollPane.setViewportView(jt);
		}
	}
}
