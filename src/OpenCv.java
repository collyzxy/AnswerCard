
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;

public class OpenCv {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String []args) {
        String sheet = "img//A4.jpg";

        //A4 ��ֵ�����ͺ����ɵ�ͼƬ·��
        String results = "img//result.jpg";
        String msg = rowsAndCols(sheet, results);
        System.out.println(msg);
    }

    public static void Canny(String oriImg, int threshold) {
        //װ��ͼƬ
        Mat img = Imgcodecs.imread(oriImg);
        Mat srcImage2 = new Mat();
        Mat srcImage3 = new Mat();
        Mat srcImage4 = new Mat();
        Mat srcImage5 = new Mat();

        //ͼƬ��ɻҶ�ͼƬ
        Imgproc.cvtColor(img, srcImage2, Imgproc.COLOR_RGB2GRAY);
        //ͼƬ��ֵ��
        Imgproc.adaptiveThreshold(srcImage2, srcImage3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 255, 1);
        //ȷ����ʴ�����ͺ˵Ĵ�С
        Mat element = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 6));
        //��ʴ����
        Imgproc.erode(srcImage3, srcImage4, element);
        //���Ͳ���
        Imgproc.dilate(srcImage4, srcImage5, element);
    }

    public static Vector<Ques> get_answer(Mat srcImage){
    	//�𰸵�ROI����
        Mat imag_ch1 = srcImage.submat(new Rect(200, 1065, 1930, 2210));
        
        //ʶ����������
        Vector<MatOfPoint> chapter1 = new Vector<>();
        Imgproc.findContours(imag_ch1, chapter1, new Mat(), 2, 3);
        Mat result = new Mat(imag_ch1.size(), CV_8U, new Scalar(255));
        Imgproc.drawContours(result, chapter1, -1, new Scalar(0), 2);
        
        Imgcodecs.imwrite("img//result.jpg", result);
        
        //newһ�� ���μ��� ����װ ����
        List<RectComp> RectCompList = new ArrayList<>();
        for (int i = 0; i < chapter1.size(); i++) {
            Rect rm = Imgproc.boundingRect(chapter1.get(i));
            RectComp ti = new RectComp(rm);
            //��������������� 50 - 80 ��Χ�ڵ�����װ�����μ���
            if (ti.rm.width > 60 && ti.rm.width < 85) {
                RectCompList.add(ti);
            }
        }
        
        //newһ�� map �����洢���⿨����Ĵ� (A\B\C\D)
        TreeMap<Integer, String> listenAnswer = new TreeMap<>();
        //�� X�� ��listenAnswer��������
        RectCompList.sort((o1, o2) -> {
            if (o1.rm.x > o2.rm.x) {
                return 1;
            }
            if (o1.rm.x == o2.rm.x) {
                return 0;
            }
            if (o1.rm.x < o2.rm.x) {
                return -1;
            }
            return -1;
        });

        //���� Y�� ȷ����ѡ��� (A\B\C\D)
        for (RectComp rc : RectCompList) {
        	flag:
            for (int h = 0; h < 7; h++) {
            	for(int w = 0; w < 4 ; w++){
            		for(int i=0;i<5;i++){
            			String answer=new String();
            			if(rc.rm.contains(new Point(55 + (80*i) + (500 * w), 115 + (320 * h))))
            				answer+='A';
            			if(rc.rm.contains(new Point(55 + (80*i) + (500 * w), 165 + (320 * h))))
            				answer+='B';
            			if(rc.rm.contains(new Point(55 + (80*i) + (500 * w), 220 + (320 * h))))
            				answer+='C';
            			if(rc.rm.contains(new Point(55 + (80*i) + (500 * w), 275 + (320 * h))))
            				answer+='D';
            			if(!answer.isEmpty() && !listenAnswer.containsKey(i + 1 + (20 * h) + (5 * w))){
            				listenAnswer.put(i + 1 + (20 * h) + (5 * w), answer);
            				break flag;
            			}else if(!answer.isEmpty() && listenAnswer.containsKey(i + 1 + (20 * h) + (5 * w))){
            				answer+=listenAnswer.get(i + 1 + (20 * h) + (5 * w));
            				listenAnswer.put(i + 1 + (20 * h) + (5 * w), answer);
            				break flag;
            			}
            		}
            	}
            }
        }
        
        Vector<Ques> answer=new Vector<>();
        Iterator iter = listenAnswer.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            Ques q=new Ques((Integer)key,(String)val);
            answer.add(q);
            System.out.println("��" + key + "��,ѡ��:" + val);
        }
        
        return answer;
    }
    
    public static String get_sno(Mat srcImage){
    	//׼��֤�ŵ�ROI����
        Mat imag_ch2 = srcImage.submat(new Rect(1370, 470, 750, 525));
        
        Vector<MatOfPoint> chapter2 = new Vector<>();
        Imgproc.findContours(imag_ch2, chapter2, new Mat(), 2, 3);
        Mat num = new Mat(imag_ch2.size(), CV_8U, new Scalar(255));
        Imgproc.drawContours(num, chapter2, -1, new Scalar(0), 2);

        Imgcodecs.imwrite("img//num.jpg", num);
        
        List<RectComp> RectCompList_num = new ArrayList<>();
        for (int i = 0; i < chapter2.size(); i++) {
            Rect rm = Imgproc.boundingRect(chapter2.get(i));
            RectComp ti = new RectComp(rm);
            if (ti.rm.width > 60 && ti.rm.width < 85) {
                RectCompList_num.add(ti);
            }
        }
        
        TreeMap<Integer, Integer> listenNum = new TreeMap<>();
        //�� X�� ��listenAnswer��������
        RectCompList_num.sort((o1, o2) -> {
            if (o1.rm.x > o2.rm.x) {
                return 1;
            }
            if (o1.rm.x == o2.rm.x) {
                return 0;
            }
            if (o1.rm.x < o2.rm.x) {
                return -1;
            }
            return -1;
        });
        
        for(RectComp rc : RectCompList_num){
        	for (int h = 0; h < 10; h++) {
        		for (int w = 0; w < 9; w++) {
        			if (rc.rm.contains(new Point(55 + (80 * w), 25 + (53 * h)))) {
        				listenNum.put(1 + w, h);
                    }
                }
        	}
        }
        
        String sno=new String();
        Iterator iter_num = listenNum.entrySet().iterator();
        while (iter_num.hasNext()) {
            Map.Entry entry = (Map.Entry) iter_num.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            sno+=val;
        }
        System.out.println(sno);
        
        return sno;
    }
    
    public static String rowsAndCols(String oriImg, String dstImg) {
        String msg = "";

        Canny(oriImg, 50);

        Mat mat = Imgcodecs.imread(dstImg);
        msg += "\n����:" + mat.rows();
        msg += "\n����:" + mat.cols();
        msg += "\nheight:" + mat.height();
        msg += "\nwidth:" + mat.width();
        msg += "\nelemSide:" + mat.elemSize();
        //CvType contourSeq = null;

        return msg;
    }
}
