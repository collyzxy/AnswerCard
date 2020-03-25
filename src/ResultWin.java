import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class ResultWin {

	public ResultWin(Vector<Object> result) {
		// TODO Auto-generated constructor stub
		JFrame jf=new JFrame("批改结果");
		
		String[] colName={"学号","成绩"};
		JPanel jp=new JPanel(new BorderLayout());
		
		System.out.println("size"+result.size());
		Object[][] rowData=new Object[result.size()/2][2];
		for(int i=0;i<result.size();i++){
			rowData[i/2][i%2]=result.elementAt(i);
			System.out.println(result.elementAt(i));
		}

		JTable jt=new JTable(rowData, colName);
		DefaultTableCellRenderer r=new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JLabel.CENTER);
		jt.setDefaultRenderer(Object.class, r);
		JScrollPane sp=new JScrollPane(jt);
		jp.add(jt.getTableHeader(), BorderLayout.NORTH);
		jp.add(jt, BorderLayout.CENTER);
		
		jf.getContentPane().add(sp, BorderLayout.CENTER);
		jf.setContentPane(jp);
		jf.pack();
		jf.setSize(1000, 600);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}
}
