import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.*;

public class VectorQuant {


    ArrayList<AverageVector> codeBooks;
    int bookSize;
    Vector<Vector<Double>> avOld=new Vector<>();

    public VectorQuant(int n) {

        codeBooks = new ArrayList<>(n);
        bookSize=n;
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

            if(codeBooks.size()<bookSize)
            {
                codeBooks.clear();
                split(av,imageVectors);
            }
            if(codeBooks.size()==bookSize)
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
        }
        associate(codeBooks, image);
    }

    public void compress(int vectorSize) throws IOException {
        // MATRIX FROM IMAGE
        File file = new File("dog.JPG");
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] imgArr = new int[width][height];
        Raster raster = img.getData();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imgArr[i][j] = raster.getSample(i, j, 0);
            }
        }


        //Resize image
        int resizedHeight=height;
        int resizedWidth=width;
        if(height % vectorSize!=0)
        {
            resizedHeight=((height / vectorSize) + 1) * vectorSize;
        }
        if(width % vectorSize!=0)
        {
            resizedWidth=( (width  /  vectorSize) + 1) * vectorSize;
        }
        System.out.println(resizedWidth);
        System.out.println(resizedHeight);
        int[][] resizedImage = new int[resizedWidth][resizedHeight];
        for (int i = 0; i < resizedWidth; i++) {
            int x = i;
            if (i >= width) {
                x = width - 1;
            }
            for (int j = 0; j < resizedHeight; j++) {
                int y = j ;
                if(j>=height)
                {
                    y=height-1;
                }
                resizedImage[i][j] = imgArr[x][y];
            }
        }
        Vector<Vector<Integer>> vectors = new Vector<>();
         //Vectors of image
        for (int i = 0; i < resizedWidth; i+= vectorSize) {
            for (int j = 0; j < resizedHeight; j+= vectorSize) {
                vectors.add(new Vector<>());
                for (int x = i; x < i + vectorSize; x++) {
                    for (int y = j; y < j + vectorSize; y++) {
                        vectors.lastElement().add(resizedImage[x][y]);
                    }
                }
            }
        }
        Vector<Vector<Double>> Average=new Vector<>();
        Average.add(average(vectors));
        split(Average,vectors);
        System.out.println(avOld);
        System.out.println("----------------------");
        for (int i = 0; i < codeBooks.size(); i++) {
            System.out.println("----------------------");
            System.out.println(codeBooks.get(i).getAverageVector());
            System.out.println(codeBooks.get(i).getAssociated());
        }

        int codeLength= (int )Math.ceil(Math.log(codeBooks.size()) / Math.log(2));
        for (int i = 0; i <codeBooks.size(); i++) {
            String b = "";
            String code = Integer.toBinaryString(i);
            if(code.length() != codeLength){
                for(int j = 0; j < (codeLength - code.length()); j++){
                    b+="0";
                }
                code = b + code;
            }
            codeBooks.get(i).setCode(code);
            System.out.println(codeBooks.get(i).getCode());
        }
        boolean present=false;
        FileWriter codeOutput=new FileWriter("Compressed.txt",true);
        for (AverageVector v: codeBooks) {
            codeOutput.append(v.getCode()+" "+v.getAverageVector()+"_");
        }
        codeOutput.append("\n");
        for (Vector<Integer> vector : vectors) {

            for (int j = 0; j < codeBooks.size(); j++) {

                present = codeBooks.get(j).getAssociated().contains(vector);
                if (present) {
                    codeOutput.append(codeBooks.get(j).getCode());
                    break;
                }
            }

        }
        //System.out.println(vectors);
        codeOutput.close();
    }

    public void decompress() throws IOException {

        File com= new File("Compressed.txt");
        Scanner scan = new Scanner(com);
        String codeBook="";
        String imageCode;
        while (scan.hasNextLine()) {
            codeBook += scan.nextLine();
        }
        System.out.println("---------------------");
        System.out.println(codeBook);
        String[] s = codeBook.split("_");
        imageCode=s[s.length-1];
        System.out.println("---------------------");
        System.out.println("imageCode: "+imageCode);

        ArrayList<String> label=new ArrayList<>();
        Vector<Vector<Integer>> imageVector=new Vector<>();
        Vector<Integer> ints= new Vector<>();
        for (int i = 0; i < s.length-1; i++) {
            label.add(s[i].substring(0,s[i].indexOf(" ")));
        }

        for (int i = 0; i < s.length-1; i++) {

            String n=s[i].substring(s[i].indexOf("[")+1,s[i].length()-1);
            String [] num =n.split(", ");
            for(String k : num) {
                ints.add(Integer.parseInt(k));
            }
            imageVector.add(ints);
            ints= new Vector<>();

        }

        System.out.println("Code: "+label);
        System.out.println("imageVector: "+imageVector);


         //GET IMAGE FROM MATRIX
//        BufferedImage image= new BufferedImage(240, 216,BufferedImage.TYPE_BYTE_INDEXED);
//        System.out.println(240);
//        System.out.println(216);
//        for(int i=0; i<imageVector.size(); i++) {
//            for(int j=0; j<imageVector.get(i).size(); j++) {
//                int a =imageVector.get(i).get(j);
//                Color newColor = new Color(a,a,a);
//                image.setRGB(i,j,newColor.getRGB());
//            }
//        }
//        File output = new File("GrayScale.jpg");
//        ImageIO.write(image, "jpg", output);

    }


    public static void main(String[] args) throws IOException {

        //4-> codebook size
        Scanner input=new Scanner(System.in);
        System.out.print("Enter Code book size: ");
        int bookSize= input.nextInt();
        System.out.print("Enter vector size: ");
        int vectorSize= input.nextInt();
        VectorQuant obj = new VectorQuant(bookSize);

        obj.compress((int)Math.sqrt(vectorSize));
        obj.decompress();

//        int[][] imgArr = {{1, 2, 7, 9, 4, 11}, {3, 4, 6, 6, 12, 12}, {4, 9, 15, 14, 9, 9}, {10, 10, 20, 18, 8, 8}, {4, 3, 17, 16, 1, 4}, {4, 5, 18, 18, 5, 6}};
//        Vector<Vector<Integer>> vectors = new Vector<>();
//
//        for (int i = 0; i < 6; i += 2) {
//            for (int j = 0; j < 6; j += 2) {
//                vectors.add(new Vector<>());
//                for (int x = i; x < i + 2; x++) {
//                    for (int y = j; y < j + 2; y++) {
//                        vectors.lastElement().add(imgArr[x][y]);
//                    }
//                }
//            }
//        }

//        Vector<Vector<Double>> Average=new Vector<>();
//        Average.add(obj.average(vectors));
//        obj.split(Average,vectors);
//        System.out.println(obj.avOld);
//        System.out.println("----------------------");
//        for (int i = 0; i < obj.codeBooks.size(); i++) {
//            System.out.println("----------------------");
//            System.out.println(obj.codeBooks.get(i).getAverageVector());
//            System.out.println(obj.codeBooks.get(i).getAssociated());
//        }
//
//        int codeLength= (int )Math.ceil(Math.log(obj.codeBooks.size()) / Math.log(2));
//        for (int i = 0; i < obj.codeBooks.size(); i++) {
//            String b = "";
//            String code = Integer.toBinaryString(i);
//            if(code.length() != codeLength){
//                for(int j = 0; j < (codeLength - code.length()); j++){
//                    b+="0";
//                }
//                code = b + code;
//            }
//            obj.codeBooks.get(i).setCode(code);
//            System.out.println(obj.codeBooks.get(i).getCode());
//        }
//        boolean present=false;
//        FileWriter codeOutput=new FileWriter("Compressed.txt",true);
//        for (AverageVector v: obj.codeBooks) {
//            codeOutput.append(v.getCode()+" "+v.getAverageVector()+"_");
//        }
//        codeOutput.append("\n");
//        for (Vector<Integer> vector : vectors) {
//
//            for (int j = 0; j < obj.codeBooks.size(); j++) {
//
//                present = obj.codeBooks.get(j).getAssociated().contains(vector);
//                if (present) {
//                    codeOutput.append(obj.codeBooks.get(j).getCode());
//                    break;
//                }
//            }
//
//        }
//        //System.out.println(vectors);
//        codeOutput.close();

        // Decompress
//        File com= new File("Compressed.txt");
//        Scanner scan = new Scanner(com);
//        String codeBook="";
//        String imageCode;
//        while (scan.hasNextLine()) {
//            codeBook += scan.nextLine();
//        }
//        System.out.println("---------------------");
//        System.out.println(codeBook);
//        String[] s = codeBook.split("_");
//        imageCode=s[s.length-1];
//        System.out.println("---------------------");
//        System.out.println("imageCode: "+imageCode);
//
//        ArrayList<String> label=new ArrayList<>();
//        Vector<Vector<Integer>> imageVector=new Vector<>();
//        Vector<Integer> ints= new Vector<>();
//        for (int i = 0; i < s.length-1; i++) {
//            label.add(s[i].substring(0,s[i].indexOf(" ")));
//        }
//
//        for (int i = 0; i < s.length-1; i++) {
//
//            String n=s[i].substring(s[i].indexOf("[")+1,s[i].length()-1);
//            String [] num =n.split(", ");
//            for(String k : num) {
//                ints.add(Integer.parseInt(k));
//            }
//            imageVector.add(ints);
//            ints= new Vector<>();
//
//        }
//
//        System.out.println("Code: "+label);
//        System.out.println("imageVector: "+imageVector);

    }
}
