
import com.sun.xml.internal.ws.util.StringUtils;
import org.opencv.core.*;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.calib3d.Calib3d.calibrateCamera;
import static org.opencv.calib3d.Calib3d.drawChessboardCorners;
import static org.opencv.calib3d.Calib3d.findChessboardCorners;
import static org.opencv.core.CvType.CV_64FC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cornerSubPix;
import static org.opencv.imgproc.Imgproc.cvtColor;


/**
 * Created by Stetskevich Roman 2381.
 */
public class Main {

    private static final int BORDER_HEIGHT = 10;
    private static final int BORDER_WIDTH = 14;
    private static final double SQUARE_SIZE = 23.22;

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Size patternSize = new Size(BORDER_WIDTH, BORDER_HEIGHT);
        List<Mat> conersList = new ArrayList<Mat>();
        List<Mat> objectsList = new ArrayList<Mat>();
        for( Integer i = 1; i <= 15; i++) {
           Mat currentFrame;
            Mat m_grayImage = new Mat();

            currentFrame = imread(Main.class.getResource("PNG/"+i.toString()+".png").getPath());
            MatOfPoint2f corners = new MatOfPoint2f();
            boolean chessboardCorners = findChessboardCorners(currentFrame, patternSize, corners);
            if(chessboardCorners){
                TermCriteria termCriteria = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                cvtColor(currentFrame, m_grayImage, 6);
                cornerSubPix(m_grayImage, corners, new Size(11,11),
                        new Size(-1, -1), termCriteria);
                drawChessboardCorners(currentFrame, patternSize, corners, true);
                writeMatToFile(i.toString(), corners);
                imwrite(i.toString()+".png", currentFrame);
                conersList.add(corners);
                ArrayList<Point3> point3List = new ArrayList<Point3>();
                for(Point point : corners.toArray()){
                    point3List.add(new Point3(point));
                }
                objectsList.add(new MatOfPoint3f(point3List.toArray(new Point3[point3List.size()])));
            }
        }

        Mat intrinsicsMatrix = new Mat();
        Mat distortionCoefficients = new Mat();
        List<Mat> rotationVectors = new ArrayList<Mat>();
        List<Mat> translationVectors = new ArrayList<Mat>();
        double calibrateCamera = calibrateCamera(objectsList, conersList, patternSize, intrinsicsMatrix, distortionCoefficients, rotationVectors, translationVectors);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("output.txt"));
        fileOutputStream.write("Intrinsics:\n".getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for(int r = 0; r < intrinsicsMatrix.rows(); r++){
            for(int c = 0; c < intrinsicsMatrix.cols(); c++){
                stringBuilder.append(Arrays.toString(intrinsicsMatrix.get(r,c))).append(",");
            }
            if(r != intrinsicsMatrix.cols() -1)
                stringBuilder.append("\n");
        }
        fileOutputStream.write(stringBuilder.toString().getBytes());
        fileOutputStream.write("\nDistortion coefficients:\n".getBytes());
        stringBuilder = new StringBuilder();
        for(int r = 0; r < distortionCoefficients.rows(); r++){
            for(int c = 0; c < distortionCoefficients.cols(); c++){
                stringBuilder.append(Arrays.toString(distortionCoefficients.get(r,c))).append(",");
            }
            if(r != distortionCoefficients.cols() -1)
                stringBuilder.append("\n");
        }
        fileOutputStream.write(stringBuilder.toString().getBytes());

        fileOutputStream.write("\nRotation vectors:\n".getBytes());
        stringBuilder = new StringBuilder();
        for(Mat mat3 : rotationVectors) {
            for (int r = 0; r < mat3.rows(); r++) {
                for (int c = 0; c < mat3.cols(); c++) {
                    stringBuilder.append(Arrays.toString(mat3.get(r, c))).append(",");
                }
                if (r != mat3.cols() - 1)
                    stringBuilder.append("\n");
            }
            stringBuilder.append("\n\n");
        }
        fileOutputStream.write(stringBuilder.toString().getBytes());
        fileOutputStream.write("\nTranslation vectors:\n".getBytes());
        stringBuilder = new StringBuilder();
        for(Mat mat4 : translationVectors) {
            for (int r = 0; r < mat4.rows(); r++) {
                for (int c = 0; c < mat4.cols(); c++) {
                    stringBuilder.append(Arrays.toString(mat4.get(r, c))).append(",");
                }
                if (r != mat4.cols() - 1)
                    stringBuilder.append("\n");
            }
            stringBuilder.append("\n\n");
        }
        fileOutputStream.write(stringBuilder.toString().getBytes());

    }

    private static void writeMatToFile(String numberFile, Mat mat){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File("chess_0"+numberFile+".txt"));
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for(int r = 0; r < mat.rows(); r++){
                for(int c = 0; c < mat.cols(); c++){
                    stringBuilder.append(i++).append(" ").append(Arrays.toString(mat.get(r,c))).append(",");
                }
                if(r != mat.cols() -1)
                    stringBuilder.append("\n");
            }
            fileOutputStream.write(stringBuilder.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
