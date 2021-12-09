import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.*;

public class VectorQuant {


    ArrayList<AverageVector> codeBooks;
    Vector<Vector<Double>> avOld=new Vector<>();
    Vector<Vector<Double>> avNew=new Vector<>();

    public VectorQuant(int n) {
        codeBooks = new ArrayList<>(n);
    }

    public Vector<Double> average(Vector<Vector<Integer>> v) {
        Vector<Double> sum = new Vector<>();
        for (int i = 0; i < v.get(0).size(); i++) {
            sum.add(0.0);
        }

        for (Vector<Integer> vector : v) {
            for (int j = 0; j < vector.size(); j++) {
                ;
                sum.setElementAt(sum.get(j) + vector.get(j), j);
            }

        }
        //System.out.println(sum);
        for (int j = 0; j < sum.size(); j++) {
            sum.set(j, sum.get(j) / v.size());
        }
        //System.out.println("Average: "+sum);
        //System.out.println("==============");
        return sum;
    }

    public Vector<Integer> getFloor(Vector<Double> s) {
        Vector<Integer> floor = new Vector<>();
        for (Double aDouble : s) {
            floor.add((int) Math.floor(aDouble));
        }
        //System.out.println(floor);
        return floor;
    }

    public Vector<Integer> getCeiling(Vector<Double> s) {
        Vector<Integer> ceiling = new Vector<>();
        for (Double aDouble : s) {
            ceiling.add((int) Math.ceil(aDouble));
        }
        //System.out.println(ceiling);
        return ceiling;

    }

    public void associate(ArrayList<AverageVector> c, Vector<Vector<Integer>> imageVectors) {

        Vector<Vector<Double>> av=new Vector<>();

        ArrayList<Integer> associate = new ArrayList<>();

        for (Vector<Integer> image : imageVectors) {

            for (AverageVector vector : c) {
                int x = 0;
                for (int j = 0; j < vector.getAverageVector().size(); j++) {

                    x += Math.pow(vector.getAverageVector().get(j) - image.get(j), 2);

                }
                associate.add(x);
            }

            int index = associate.indexOf(Collections.min(associate));
            c.get(index).setAssociated(image);
            associate.clear();
        }

        Vector<Double> temp=new Vector<>();
        int k=0;
        for (AverageVector vector : c) {

                if(vector.getAssociated().isEmpty())
                {
                    vector.setAssociated(vector.getAverageVector());
                    for (int i = 0; i < vector.getAverageVector().size(); i++) {

                        temp.add((double)vector.getAverageVector().get(i));

                    }
                    av.add(temp);
                }
                else av.add(average(vector.getAssociated()));
        }

        if(!avOld.equals(av))
        {
            avOld=av;

            if(codeBooks.size()<4)
            {
                codeBooks.clear();
                split(av,imageVectors);
            }
            if(codeBooks.size()==4)
            {
                for (AverageVector vector : c) {
                    vector.getAssociated().clear();
                    vector.getAverageVector().clear();
                }

                for (int i = 0; i < c.size(); i++) {
                    c.get(i).setAverageVector(getCeiling(avOld.get(i)));
                }
                codeBooks=c;

                associate(codeBooks,imageVectors);
            }
            
        }


    }

    public void split(Vector<Vector<Double>> v,Vector<Vector<Integer>> image) {

        for (Vector<Double> doubles : v) {

            AverageVector avC = new AverageVector(getCeiling(doubles));
            AverageVector avF = new AverageVector(getFloor(doubles));
            codeBooks.add(avF);
            codeBooks.add(avC);
            //associate(codeBooks, image);
        }
        associate(codeBooks, image);
    }

    public void compress() throws IOException {
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
//        Vector<Vector<Integer>> Vectors = new Vector<>();
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
    }

    public void decompress() {

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


    public static void main(String[] args) throws IOException {

        //4-> codebook size
        VectorQuant obj = new VectorQuant(4);

        int[][] imgArr = {{1, 2, 7, 9, 4, 11}, {3, 4, 6, 6, 12, 12}, {4, 9, 15, 14, 9, 9}, {10, 10, 20, 18, 8, 8}, {4, 3, 17, 16, 1, 4}, {4, 5, 18, 18, 5, 6}};
        Vector<Vector<Integer>> Vectors = new Vector<>();

        for (int i = 0; i < 6; i += 2) {
            for (int j = 0; j < 6; j += 2) {
                Vectors.add(new Vector<>());
                for (int x = i; x < i + 2; x++) {
                    for (int y = j; y < j + 2; y++) {
                        Vectors.lastElement().add(imgArr[x][y]);
                    }
                }
            }
        }

        Vector<Vector<Double>> Average=new Vector<>();
        Average.add(obj.average(Vectors));
        obj.split(Average,Vectors);
        System.out.println(obj.avOld);
        System.out.println("----------------------");
        for (int i = 0; i < obj.codeBooks.size(); i++) {
            System.out.println("----------------------");
            System.out.println(obj.codeBooks.get(i).getAverageVector());
            System.out.println(obj.codeBooks.get(i).getAssociated());
        }



    }
}
