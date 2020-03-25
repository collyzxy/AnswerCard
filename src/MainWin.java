import static org.opencv.imgproc.Imgproc.MORPH_RECT;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

import javax.swing.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MainWin {

	static void show_win() throws Exception {
		JFrame jf_img=new JFrame("答题卡");
		jf_img.setLayout(new GridLayout(3, 1));
		
		//open dir
		JPanel jp_text=new JPanel();
		JTextField jtf=new JTextField(30);
		JButton jb_text=new JButton("    答    案    ");
		jp_text.add(jtf);
		jp_text.add(jb_text);
		
		JPanel jp_dir=new JPanel();
		JTextField jtf_dir=new JTextField(30);
		JButton jb_dir=new JButton("打开文件夹");
		jp_dir.add(jtf_dir);
		jp_dir.add(jb_dir);
		
		JPanel jp_bt=new JPanel();
		JButton jb_ok=new JButton("确认");
		jp_bt.add(jb_ok);
		
		Vector<Object> result=new Vector<Object>();
		
		Vector<Ques> data=new Vector<Ques>();
		
		jb_text.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showDialog(new JLabel(), "选择");
				File result=jfc.getSelectedFile();
				try {
					BufferedReader bf=new BufferedReader(new FileReader(result));
					String fn=result.getAbsoluteFile().toString();
					jtf.setText(fn);
					
					if(result.exists()&&result.isFile()){
						if(fn.substring(fn.lastIndexOf('.')+1).equals("txt")){
							String textLine=new String();
							while((textLine=bf.readLine())!=null){
								String[] temp=textLine.split(" ");
								int no=Integer.parseInt(temp[0]);
								String answer=temp[1];
								float point=Float.parseFloat(temp[2]);
								System.out.println(no+","+answer+","+point);
								Ques q=new Ques(no,answer);
								q.point=point;
								data.add(q);
							}
						}
					}
					bf.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		jb_dir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jfc=new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showDialog(new JLabel(), "选择");
				File dir=jfc.getSelectedFile();
				String fn=dir.getAbsoluteFile().toString();
				jtf_dir.setText(fn);
				if(dir.exists()&&dir.isDirectory()){
					result.clear();
					String[] files=dir.list();
//					System.out.println(fn);
					for(String file:files){
						int flag=file.lastIndexOf('.');
						if(!(file.substring(flag+1).equals("gif")||file.substring(flag+1).equals("jpg")||file.substring(flag+1).equals("png")))continue;
						String img_route=fn+"\\"+file;
//						System.out.println(img_route);
						
						Mat img=Imgcodecs.imread(img_route);
						Mat srcImage2 = new Mat();
				        Mat srcImage3 = new Mat();
				        Mat srcImage4 = new Mat();
				        Mat srcImage5 = new Mat();

				        //图片变成灰度图片
				        Imgproc.cvtColor(img, srcImage2, Imgproc.COLOR_RGB2GRAY);
				        //图片二值化
				        Imgproc.adaptiveThreshold(srcImage2, srcImage3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 255, 1);
				        //确定腐蚀和膨胀核的大小
				        Mat element = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 6));
				        //腐蚀操作
				        Imgproc.erode(srcImage3, srcImage4, element);
				        //膨胀操作
				        Imgproc.dilate(srcImage4, srcImage5, element);
				        
				        Vector<Ques> answer=OpenCv.get_answer(srcImage4);
				        String sno=OpenCv.get_sno(srcImage4);
//				        System.out.println(sno+","+answer.size());
				        float score=0;
				        
				        for(int i=0;i<answer.size();i++){
				        	Ques temp=check(answer.elementAt(i), data);
				        	answer.set(i, temp);
				        	score+=temp.point;
				        }
//				        System.out.println(sno+","+score);
				        result.add(sno); result.add(score);
					}
				}
			}
		});
		
		jb_ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new ResultWin(result);
			}
		});
		
		jf_img.getContentPane().add(jp_text);
		jf_img.getContentPane().add(jp_dir);
		jf_img.getContentPane().add(jp_bt);
		jf_img.setSize(500, 150);
		jf_img.setVisible(true);
		jf_img.setExtendedState(JFrame.NORMAL);
		jf_img.setLocationRelativeTo(null);
	}
	
	public static Ques check(Ques q,Vector<Ques> data){
		for(int i=0;i<data.size();i++){
			Ques temp=data.elementAt(i);
			if(temp.no==q.no){
				if(temp.answer.equals(q.answer))
					q.point=data.elementAt(i).point;
				else if(temp.answer.length()>1){
					for(int j=0;j<temp.answer.length();j++){
						if(q.answer.indexOf(temp.answer.charAt(j))!=-1)
							q.point=1;
					}
					for(int j=0;j<q.answer.length();j++){
						if(temp.answer.indexOf(q.answer.charAt(j))==-1)
							q.point=0;
					}
				}
//				System.out.println(q.no+","+q.point);
				return q;
			}
		}
		return q;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		show_win();
	}

}
