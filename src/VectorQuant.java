import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;

public class VectorQuant {


    ArrayList<AverageVector> codeBooks;
    int bookSize;
    Vector<Vector<Double>> avOld=new Vector<>();

    public VectorQuant() {

        codeBooks = new ArrayList<>();
    }
    public void setBookSize(int n)
    {
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
        for (int j = 0; j < sum.size(); j++) {
            sum.set(j, sum.get(j) / v.size());
        }

        return sum;
    }

    public Vector<Integer> getFloor(Vector<Double> s) {
        Vector<Integer> floor = new Vector<>();
        for (Double aDouble : s) {
            floor.add((int) Math.floor(aDouble));
        }
        return floor;
    }

    public Vector<Integer> getCeiling(Vector<Double> s) {
        Vector<Integer> ceiling = new Vector<>();
        for (Double aDouble : s) {
            ceiling.add((int) Math.ceil(aDouble));
        }
        return ceiling;

    }

    public void associate(ArrayList<AverageVector> c, Vector<Vector<Integer>> imageVectors) {

        Vector<Vector<Double>> av=new Vector<>();

        ArrayList<Integer> associate = new ArrayList<>();


        for (Vector<Integer> image : imageVectors) {
            for (AverageVector vector : c) {
                int x = 0;

                for (int j = 0; j < vector.getAverageVector().size(); j++) {
                    if(j < image.size()) x += Math.pow(vector.getAverageVector().get(j) - image.get(j), 2);

                }
                associate.add(x);
            }

            int index = associate.indexOf(Collections.min(associate));
            c.get(index).setAssociated(image);
            associate.clear();
        }

        Vector<Double> temp=new Vector<>();
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

    public boolean compress(int vectorSize) throws IOException {
        // MATRIX FROM IMAGE
        File file = new File("dogColor.jpg");
        if (file.exists()) {
            BufferedImage img = ImageIO.read(file);
            int width = img.getWidth();
            int height = img.getHeight();
            int[][] imgArr = new int[height][width];
            Raster raster = img.getData();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    imgArr[j][i] = raster.getSample(i, j, 0);
                }
            }


            //Resize image
            int resizedHeight = height;
            int resizedWidth = width;
            if (height % vectorSize != 0) {
                resizedHeight = ((height / vectorSize) + 1) * vectorSize;
            }
            if (width % vectorSize != 0) {
                resizedWidth = ((width / vectorSize) + 1) * vectorSize;
            }
            System.out.println(resizedWidth);
            System.out.println(resizedHeight);
            int[][] resizedImage = new int[resizedHeight][resizedWidth];
            for (int i = 0; i < resizedWidth; i++) {
                int x = i;
                if (i >= width) {
                    x = width - 1;
                }
                for (int j = 0; j < resizedHeight; j++) {
                    int y = j;
                    if (j >= height) {
                        y = height - 1;
                    }
                    resizedImage[j][i] = imgArr[y][x];
                }
            }
            Vector<Vector<Integer>> vectors = new Vector<>();
            //Vectors of image
            for (int i = 0; i < resizedHeight; i += vectorSize) {
                for (int j = 0; j < resizedWidth; j += vectorSize) {
                    vectors.add(new Vector<>());
                    for (int x = i; x < i + vectorSize; x++) {
                        for (int y = j; y < j + vectorSize; y++) {
                            vectors.lastElement().add(resizedImage[x][y]);
                        }
                    }
                }
            }
            Vector<Vector<Double>> Average = new Vector<>();
            Average.add(average(vectors));
            split(Average, vectors);

            int codeLength = (int) Math.ceil(Math.log(codeBooks.size()) / Math.log(2));
            for (int i = 0; i < codeBooks.size(); i++) {
                String b = "";
                String code = Integer.toBinaryString(i);
                if (code.length() != codeLength) {
                    for (int j = 0; j < (codeLength - code.length()); j++) {
                        b += "0";
                    }
                    code = b + code;
                }
                codeBooks.get(i).setCode(code);

            }
            boolean present = false;
            File f = new File("Compressed.txt");
            if (f.exists()) f.delete();
            FileWriter codeOutput = new FileWriter("Compressed.txt", true);
            for (AverageVector v : codeBooks) {
                codeOutput.append(v.getCode() + " " + v.getAverageVector() + "_");
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
            codeOutput.append("_" + resizedHeight + "_" + resizedWidth);

            codeOutput.close();
        }
        return file.exists();
    }

    public boolean decompress() throws IOException {

        File com= new File("Compressed.txt");
        if (com.exists()) {
            Scanner scan = new Scanner(com);
            String codeBook = "";
            String imageCode;
            while (scan.hasNextLine()) {
                codeBook += scan.nextLine();
            }

            String[] s = codeBook.split("_");
            imageCode = s[s.length - 3];

            ArrayList<String> label = new ArrayList<>();
            Vector<Vector<Integer>> codeBookVector = new Vector<>();
            Vector<Integer> ints = new Vector<>();

            for (int i = 0; i < s.length - 3; i++) {
                label.add(s[i].substring(0, s[i].indexOf(" ")));
            }

            for (int i = 0; i < s.length - 3; i++) {

                String n = s[i].substring(s[i].indexOf("[") + 1, s[i].length() - 1);
                String[] num = n.split(", ");
                for (String k : num) {
                    ints.add(Integer.parseInt(k));
                }
                codeBookVector.add(ints);
                ints = new Vector<>();

            }

            Vector<Vector<Integer>> imageVector = new Vector<>();

            int length = label.get(0).length();

            for (int i = 0; i < imageCode.length(); i += length) {

                for (int j = 0; j < label.size(); j++) {

                    if (label.get(j).equals(imageCode.substring(i, i + length))) {
                        imageVector.add(codeBookVector.get(j));
                        break;
                    }
                }

            }

            int height = Integer.parseInt(s[s.length - 2]);
            int width = Integer.parseInt(s[s.length - 1]);

            int m = 0;
            int j = 0;
            int vectorSize = codeBookVector.get(0).size();
            Vector<Integer> v1 = new Vector<>();
            Vector<Vector<Integer>> v2 = new Vector<>();
            for (int i = 0; (i < imageVector.size()); i += (width / Math.sqrt(vectorSize))) {
                for (int l = 0; l < Math.sqrt(imageVector.get(i).size()); l++) {
                    m = j;
                    for (int k = i; k < i + (width / Math.sqrt(vectorSize)); k++) {

                        for (j = m; j < m + Math.sqrt(imageVector.get(i).size()); j++) {

                            v1.add(imageVector.get(k).get(j));
                        }

                    }
                    v2.add(v1);
                    v1 = new Vector<>();

                }
                j = 0;
            }
            System.out.println(v2.size());

            /////////////////////////////////////////////////

            //GET IMAGE FROM MATRIX
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);

            for (int i = 0; i < height; i++) {
                for (int x = 0; x < width; x++) {
                    int a = v2.get(i).get(x);
                    Color newColor = new Color(a, a, a);
                    image.setRGB(x, i, newColor.getRGB());
                }
            }
            File output = new File("DecompressedImage.jpg");
            ImageIO.write(image, "jpg", output);
        }
        return com.exists();
    }


    public static void main(String[] args) throws IOException {

        //Scanner input=new Scanner(System.in);
        //System.out.print("Enter Code book size: ");
        //int bookSize1;
       // System.out.print("Enter vector size: ");
        //int vectorSize= input.nextInt();
        VectorQuant obj = new VectorQuant();
        //2*2 = 4 (vector size)
        //obj.compress((int)Math.sqrt(vectorSize));
        //obj.decompress();

        JFrame vectorFrame = new JFrame("Vector Quantization Compression/Decompression");
        JButton com = new JButton("Compress");
        JButton decom = new JButton("Decompress");
        JButton exit = new JButton("Exit");
        JLabel enter=new JLabel("Click your choice");
        JLabel error=new JLabel("Compressed file not found");
        JLabel error2=new JLabel("Input file not found");

        // Create a file chooser
         final JFileChooser fc  = new JFileChooser();
        // In response to a button click:
        //int result = fc.showOpenDialog(null);

        enter.setBounds(145, 60, 150, 40);
        com.setBounds(130, 100, 130, 40);
        decom.setBounds(130, 150, 130, 40);
        exit.setBounds(130, 200, 130, 40);
        error.setBounds(130, 240, 150, 40);
        error2.setBounds(130, 240, 150, 40);

        error.setVisible(false);
        error2.setVisible(false);

        vectorFrame.add(com);
        vectorFrame.add(decom);
        vectorFrame.add(exit);
        vectorFrame.add(enter);
        vectorFrame.add(error);
        vectorFrame.add(error2);

        // if (JFileChooser.APPROVE_OPTION == result){
        //     File file = chooser.getSelectedFile();
        //     MessageDigest digest = MessageDigest.getInstance("MD5");
        //     try (InputStream is = new FileInputStream(file)) {
        //         DigestInputStream dis = new DigestInputStream(new BufferedInputStream(is), digest);
        //         while (dis.read() != -1){}
        //     }
        //     //JOptionPane.showMessageDialog(null, Hex.encodeHexString(digest.digest()));
        // }
    //     fc.addActionListener(new ActionListener(){
    //     public void actionPerformed(ActionEvent e) {
    //         //Handle open button action.
    //         if (e.getSource() == openButton) {
    //             int returnVal = fc.showOpenDialog(FileChooserDemo.this);
        
    //             if (returnVal == JFileChooser.APPROVE_OPTION) {
    //                 File file = fc.getSelectedFile();
    //                 //This is where a real application would open the file.
    //                 log.append("Opening: " + file.getName() + "." + newline);
    //             } else {
    //                 log.append("Open command cancelled by user." + newline);
    //             }
    //        }
    //     }
    // });
    //OpenFileAction o = new OpenFileAction(vectorFrame, fc);
    String filename = File.separator+"tmp";
    JFileChooser fc1 = new JFileChooser(new File(filename));

    // Show open dialog; this method does not return until the dialog is closed
    fc1.showOpenDialog(vectorFrame);
    File selFile = fc1.getSelectedFile();

    
        com.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JTextField bookSizeTxt = new JTextField("Code book size");
                JTextField vectorSizeHeight = new JTextField("Vector Height");
                JTextField vectorSizeWidth = new JTextField("Vector Width");
                JButton b = new JButton("Enter");

                vectorFrame.add(bookSizeTxt);
                vectorFrame.add(vectorSizeHeight);
                vectorFrame.add(vectorSizeWidth);
                vectorFrame.add(b);

                bookSizeTxt.setBounds(130, 250, 130, 40);
                vectorSizeHeight.setBounds(130, 350, 130, 40);
                vectorSizeWidth.setBounds(130, 300, 130, 40);
                b.setBounds(130, 400, 130, 40);


                b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                    {
                        int bookSize1,vectorSize;
                        bookSize1 = Integer.parseInt(bookSizeTxt.getText());
                        obj.setBookSize(bookSize1);
                        vectorSize = Integer.parseInt(vectorSizeHeight.getText())*Integer.parseInt(vectorSizeWidth.getText());
                        try {
                            error2.setVisible(!obj.compress((int)Math.sqrt(vectorSize)));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        error.setVisible(false);
                        bookSizeTxt.setVisible(false);
                        vectorSizeWidth.setVisible(false);
                        vectorSizeHeight.setVisible(false);
                        b.setVisible(false);
                        
                    }});
                    com.setEnabled(false);
            }
        });

        decom.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    error.setVisible(!obj.decompress());
                    error2.setVisible(false);
                    ImageIcon grayImg= new ImageIcon("DecompressedImage.jpg");
                    JLabel grayImgLabel = new JLabel(grayImg);
                    JFrame imgFrame = new JFrame("DecompressedImage");
                    imgFrame.add(grayImgLabel);
                    imgFrame.setSize(grayImg.getIconWidth()+50, grayImg.getIconHeight()+50);
                    imgFrame.setLayout(new  FlowLayout());
                    imgFrame.setVisible(true);
                    imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    vectorFrame.setVisible(false);
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                decom.setEnabled(false);
            }
        });

        exit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        vectorFrame.setSize(400, 500);
        vectorFrame.setLayout(null);
        vectorFrame.setVisible(true);
        vectorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
class OpenFileAction extends AbstractAction {
    JFrame frame;
    JFileChooser chooser;
    File file;

    OpenFileAction(JFrame frame, JFileChooser chooser) {
        super("Open...");
        this.chooser = chooser;
        this.frame = frame;
    }

    public void actionPerformed(ActionEvent evt) {
        // Show dialog; this method does not return until dialog is closed
        chooser.showOpenDialog(frame);

        // Get the selected file
        file = chooser.getSelectedFile();    
    }
    public File getFile(){
        return file;
    }
};