import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.*;

public class VectorQuant {

    public Vector<Float> average(Vector<Vector<Integer>> v)
    {
        Vector<Float> sum=new Vector<>();
        for (int i = 0; i < v.get(0).size(); i++) {
            sum.add((float) 0);
        }
        
        for (Vector<Integer> vector : v) {
            for (int j = 0; j < vector.size(); j++) {;
                sum.setElementAt(sum.get(j)+vector.get(j),j);
            }

        }
        System.out.println(sum);
        for (int j = 0; j <  sum.size(); j++) {
            sum.set(j,sum.get(j)/v.size());
        }
        System.out.println(sum);
        return sum;
    }
    public static void main(String[] args) throws IOException {
        // MATRIX FROM IMAGE
//        File file = new File("dog.JPG");
//        BufferedImage img = ImageIO.read(file);
//        int width = img.getWidth();
//        int height = img.getHeight();
//        int[][] imgArr = new int[width][height];
//        Raster raster = img.getData();
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                imgArr[i][j] = raster.getSample(i, j, 0);
//            }
//        }
//Create Array Of Vectors
        VectorQuant obj= new VectorQuant();
        int[][] imgArr={{1, 2, 7, 9, 4, 11},{3,4,6,6,12,12},{4,9,15,14,9,9},{10,10,20,18,8,8},{4,3,17,16,1,4},{4,5,18,18,5,6}};
        Vector<Vector<Integer>> Vectors = new Vector<>();


        //Resize image
//        int resizedHeight=height;
//        int resizedWidth=width;
//        if(height % 20!=0)
//        {
//            resizedHeight=((height / 20) + 1) * 20;
//        }
//        if(width % 20!=0)
//        {
//            resizedWidth=( (width  /  20) + 1) * 20;
//        }
//
//        int[][] resizedImage = new int[resizedWidth][resizedHeight];
//        for (int i = 0; i < resizedWidth; i++) {
//            int x = i;
//            if (i >= width) {
//                x = width - 1;
//            }
//            for (int j = 0; j < resizedHeight; j++) {
//                int y = j ;
//                if(j>=height)
//                {
//                    y=height-1;
//                }
//                resizedImage[i][j] = imgArr[x][y];
//            }
//        }
//
//         //Vectors of image
//        for (int i = 0; i < resizedWidth; i+= 20) {
//            for (int j = 0; j < resizedHeight; j+= 20) {
//                Vectors.add(new Vector<>());
//                for (int x = i; x < i + 20; x++) {
//                    for (int y = j; y < j + 20; y++) {
//                        Vectors.lastElement().add(resizedImage[x][y]);
//                    }
//                }
//            }
//        }


        for (int i = 0; i < 6; i+= 2) {
            for (int j = 0; j < 6; j+= 2) {
                Vectors.add(new Vector<>());
                for (int x = i; x < i + 2; x++) {
                    for (int y = j; y < j + 2; y++) {
                        Vectors.lastElement().add(imgArr[x][y]);
                    }
                }
            }
        }

        obj.average(Vectors);

        //System.out.println(Vectors);


        // GET IMAGE FROM MATRIX
//        BufferedImage image= new BufferedImage(width, height,BufferedImage.TYPE_BYTE_INDEXED);
//        System.out.println(width);
//        System.out.println(height);
//        for(int i=0; i<width; i++) {
//            for(int j=0; j<height; j++) {
//                int a =imgArr[i][j];
//                Color newColor = new Color(a,a,a);
//                image.setRGB(i,j,newColor.getRGB());
//            }
//        }
//        File output = new File("GrayScale.jpg");
//        ImageIO.write(image, "jpg", output);
    }
}
