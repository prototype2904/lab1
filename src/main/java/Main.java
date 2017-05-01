import org.bytedeco.javacpp.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.CV_64FC1;
import static org.bytedeco.javacpp.opencv_core.CV_8UC3;

/**
 * Created by Stetskevich Roman 2381.
 */
public class Main {


    public static void main(String[] args) throws IOException {

        List<opencv_core.Mat> imagePoints = new ArrayList<opencv_core.Mat>();
        for( Integer i = 1; i <= 15; i++) {
            BufferedImage image = ImageIO.read(Main.class.getResource("PNG/"+i.toString()+".png"));
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();



            opencv_core.Mat currentFrame = new opencv_core.Mat(0,0,CV_8UC3);
            opencv_core.Mat m_cameraMatrix = new opencv_core.Mat(3, 3, CV_64FC1);

            opencv_core.Mat m_distortionCoefficients = new opencv_core.Mat(5, 1, CV_64FC1);
            opencv_core.Mat m_R = new opencv_core.Mat();
            opencv_core.Mat m_T = new opencv_core.Mat();
            opencv_core.Mat m_grayImage = new opencv_core.Mat();
            List<List<opencv_core.Point3f>> m_realPoints;
            Toolkit toolkit= Toolkit.getDefaultToolkit();
            opencv_core.Size patternSize = new opencv_core.Size(7, 7);
            BytePointer bytePointer = new BytePointer(pixels);
            currentFrame = opencv_imgcodecs.imread(bytePointer);
            currentFrame.put(bytePointer);
            System.out.println(currentFrame.toString());
            opencv_core.Mat corners = new opencv_core.Mat();
            boolean chessboardCorners = opencv_calib3d.findChessboardCorners(currentFrame, patternSize, corners);
            if(chessboardCorners){
                opencv_core.TermCriteria termCriteria = new opencv_core.TermCriteria(opencv_core.TermCriteria.EPS | opencv_core.TermCriteria.MAX_ITER, 30, 0.1);
                opencv_imgproc.cvtColor(currentFrame, m_grayImage, opencv_imgproc.CV_BGR2GRAY);
                opencv_imgproc.cornerSubPix(m_grayImage, corners, new opencv_core.Size(11,11),
                        new opencv_core.Size(-1, -1), new opencv_core.TermCriteria(opencv_core.TermCriteria.MAX_ITER, 30, 0.1));
                opencv_calib3d.drawChessboardCorners(currentFrame, patternSize, corners, true);
                imagePoints.add(corners);
            }


        }
    }
//
//    public static void main(String[] args) {
//        for (Integer i = 1; i <= 15; i++) {
//            BufferedImage image = null;
//            try {
//                image = ImageIO.read(Main.class.getResource("PNG/"+i.toString()+".png"));
//            } catch (IOException e) {
//            }
//            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//
//        }
//
//    }
}
