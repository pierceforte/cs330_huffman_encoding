import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class HuffmanDriver {
    public static void main(String[] args) {
        Map<Character, Double> input = new HashMap<>() {{
            put('_', 18.3); put('r', 4.8);  put('y', 1.6);
            put('e', 10.2); put('d', 3.5);  put('p', 1.6);
            put('t', 7.7);  put('l', 3.4);  put('b', 1.3);
            put('a', 6.8);  put('c', 2.6);  put('v', 0.9);
            put('o', 5.9);  put('u', 2.4);  put('k', 0.6);
            put('i', 5.8);  put('m', 2.1);  put('j', 0.2);
            put('n', 5.5);  put('w', 1.9);  put('x', 0.2);
            put('s', 5.1);  put('f', 1.8);  put('q', 0.1);
            put('h', 4.9);  put('g', 1.7);  put('z', 0.1);
        }};
        
        Huffman.Node encodingRoot = Huffman.encode(input);
        writeTreeFile(encodingRoot, "huffman_tree.txt");

        Map<Huffman.Node, String> encodingMap = new HashMap<>();
        Huffman.findEncodings(encodingRoot, "", encodingMap);
        printEncodings(encodingMap);

        System.out.printf("Expected bits per letter: %f\n", Huffman.expectedBitsPerLetter(encodingRoot, encodingMap));

        System.out.printf("Entropy: %f\n", Huffman.entropy(encodingMap.keySet()));
    }

    private static void writeTreeFile(Huffman.Node root, String filePath) {
        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            root.printTree(out);
            out.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void printEncodings(Map<Huffman.Node, String> encodingMap) {
        List<Huffman.Node> sortedNodes = new ArrayList<>(encodingMap.keySet());
        Collections.sort(sortedNodes, (a, b) -> encodingMap.get(a).length() - encodingMap.get(b).length());
        for (Huffman.Node node : sortedNodes) {
            System.out.println(node.data + ": " + encodingMap.get(node));
        }
    }
}