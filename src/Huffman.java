// Mariam Amr Mohamed ID: 20190520
// Norhan Abdelkader Ali ID: 20190600

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import javax.swing.*;

class Node {

    int freq;
    int codeLength;
    String code;
    char character;
    Node leftChild;
    Node rightChild;
}


class ImplementComparator implements Comparator<Node> {
    public int compare(Node firstNode, Node secondNode) {
        return firstNode.freq - secondNode.freq;
    }
}


public class Huffman {

    static String writeInFile = "";
    static ArrayList<Node> nodes = new ArrayList<Node>();

    public static void buildTree(Node root, String code) throws IOException {

        if (root.leftChild == null && root.rightChild == null) {
            root.code = code;
            System.out.print(root.character + " " + code + ", ");
            writeInFile = writeInFile + root.character + " " + code + ", ";
            root.codeLength = code.length();
            if (root.character != '0') nodes.add(root);
            return;
        }

        buildTree(root.leftChild, code + "0");
        buildTree(root.rightChild, code + "1");
    }

    public boolean compression() throws IOException {
        File read = new File("input.txt");
        if (read.exists()) {
            String word = "";
            Scanner scan = new Scanner(read);
            while (scan.hasNextLine()) {
                word += scan.nextLine();
            }

            Vector<Character> charArray = new Vector<Character>();
            Vector<Integer> count = new Vector<Integer>();
            Vector<Integer> charfreq = new Vector<Integer>();
            Node root = null;

            char string[] = word.toCharArray();

            for (int i = 0; i < word.length(); i++) {
                int cnt = 1;
                for (int j = i + 1; j < word.length(); j++) {
                    if (string[i] == string[j]) {

                        cnt++;

                        string[j] = '0';
                    }
                }
                count.add(cnt);
            }


            for (int i = 0; i < word.length(); i++) {
                if (string[i] != '0') {
                    charArray.add(string[i]);
                    charfreq.add(count.get(i));
                }

            }
            int sumFreq = 0;
            for (int i = 0; i < charfreq.size(); i++) {
                sumFreq += charfreq.get(i);
                System.out.println("char: " + charArray.get(i) + " FREQ: " + charfreq.get(i));
            }

            PriorityQueue<Node> queue = new PriorityQueue<Node>(charArray.size(), new ImplementComparator());

            for (int i = 0; i < charArray.size(); i++) {
                Node huffnode = new Node();

                huffnode.character = charArray.get(i);
                huffnode.freq = charfreq.get(i);
                huffnode.leftChild = null;
                huffnode.rightChild = null;

                queue.add(huffnode);
            }

            while (queue.size() > 1) {

                Node smallLeft = queue.poll();

                Node smallRight = queue.poll();

                Node newNode = new Node();
                newNode.freq = smallLeft.freq + smallRight.freq;

                newNode.character = '0';
                newNode.leftChild = smallLeft;
                newNode.rightChild = smallRight;
                root = newNode;

                queue.add(root);
            }


            System.out.println("=================================");
            System.out.print("Dictionary: ");
            buildTree(root, "");
            System.out.println("\n=================================");

            double compressedSize = 0;
            for (Node node : nodes) {
                compressedSize += (node.freq * node.codeLength);
            }

            double numBits = Math.round((Math.log(charArray.size()) / Math.log(2)));
            double originalSize = numBits * sumFreq;

            System.out.println("Compression Ratio: ");
            System.out.println(originalSize / compressedSize);

            FileWriter writeDict = new FileWriter("Dictionary.txt");
            writeDict.write(writeInFile.substring(0, writeInFile.length() - 2));
            writeDict.close();
            FileWriter compressed = new FileWriter("Compressed.txt");
            for (int j = 0; j < word.length(); j++) {
                for (Node node : nodes) {
                    if (node.character == word.charAt(j)) {
                        compressed.append(node.code);
                    }

                }

            }
            compressed.close();
        }
        return read.exists();
    }

    public boolean decompression() throws IOException {
        File comp = new File("Compressed.txt");
        File dict = new File("Dictionary.txt");
        if (comp.exists() && dict.exists()) {
            String code = "", charCode = "";
            Scanner scan = new Scanner(comp);
            Scanner scan2 = new Scanner(dict);
            while (scan.hasNextLine()) {
                code += scan.nextLine();
            }
            while (scan2.hasNextLine()) {
                charCode += scan2.nextLine();
            }
            String[] s = charCode.split(", ");

            int i = 0;
            String decoded = "", curr = "";
            while (i < code.length()) {

                curr += code.charAt(i);

                for (String value : s) {

                    if (value.substring(1).equals(" " + curr)) {
                        decoded += value.charAt(value.indexOf(" " + curr) - 1);
                        curr = "";
                    }

                }
                i++;

            }
            FileWriter decompressed = new FileWriter("Decompressed.txt");
            decompressed.write(decoded);
            System.out.println("Original String: " + decoded);

            decompressed.close();
        }
        return comp.exists() && dict.exists();
    }

    public static void main(String[] args) throws IOException {

        Huffman obj = new Huffman();
        JFrame huffFrame = new JFrame("Huffman Compression/Decompression");
        JButton com = new JButton("Compress");
        JButton decom = new JButton("Decompress");
        JButton exit = new JButton("Exit");
        JLabel enter=new JLabel("Click your choice");
        JLabel error=new JLabel("Compressed file not found");
        JLabel error2=new JLabel("Input file not found");

        enter.setBounds(145, 60, 150, 40);
        com.setBounds(130, 100, 130, 40);
        decom.setBounds(130, 150, 130, 40);
        exit.setBounds(130, 200, 130, 40);
        error.setBounds(130, 240, 150, 40);
        error2.setBounds(130, 240, 150, 40);

        error.setVisible(false);
        error2.setVisible(false);

        huffFrame.add(com);
        huffFrame.add(decom);
        huffFrame.add(exit);
        huffFrame.add(enter);
        huffFrame.add(error);
        huffFrame.add(error2);

        com.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    error2.setVisible(!obj.compression());
                    error.setVisible(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        decom.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    error.setVisible(!obj.decompression());
                    error2.setVisible(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        huffFrame.setSize(400, 400);
        huffFrame.setLayout(null);
        huffFrame.setVisible(true);
        huffFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}