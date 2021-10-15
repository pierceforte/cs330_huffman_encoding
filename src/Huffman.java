import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.lang.Math;

public class Huffman {

    static class Node {
        public static final char INTERNAL_NODE_DATA = '#';

        public char data;
        public double freq;
        public long timestamp;  

    	public Node left;
    	public Node right;

        public Node(char data, double freq, long timestamp) {
            this.data = data;
            this.freq = freq;
            this.timestamp = timestamp;
        }

        public int compare(Node b) {
            // prefer lower freq then prefer lower timestamp
            if(freq > b.freq){
                return 1;
            } else if (b.freq > freq){
                return -1;
            }
            if (timestamp > b.timestamp) {
                return -1;
            } else if (b.timestamp > timestamp) {
                return 1;
            }
            return 0;
        }

        public String toString() {
            String dataStr = "" + data;
            if (data == '_') {
                dataStr = "blank";
            }
            return dataStr + "(" + freqToString() + ")";
        }

        public String freqToString() {
            return String.format("%.2f", freq);
        }
        

        /**
         * From https://stackoverflow.com/a/19484210
         * Modified to add 0 and 1 along left and right edges, respectively.
         */
        public void printTree(OutputStreamWriter out) throws IOException {
            if (right != null) {
                right.printTree(out, true, "");
            }
            printNodeValue(out);
            if (left != null) {
                left.printTree(out, false, "");
            }
        }
        private void printNodeValue(OutputStreamWriter out) throws IOException {
            out.write(toString());
            out.write('\n');
        }
        // use string and not stringbuffer on purpose as we need to change the indent at each recursion
        private void printTree(OutputStreamWriter out, boolean isRight, String indent) throws IOException {
            if (right != null) {
                right.printTree(out, true, indent + (isRight ? "        " : " |      "));
            }
            out.write(indent);

            int binaryValue = 0;

            if (isRight) {
                binaryValue = 1;
                out.write(" /");
            } else {
                out.write(" \\");
            }
            out.write("--" + binaryValue + "-- ");
            printNodeValue(out);
            if (left != null) {
                left.printTree(out, false, indent + (isRight ? " |      " : "        "));
            }
        }
    }

    /**
     * Generate Huffman encoding tree from map of elements and their corresponding frequency
     */
    public static Node encode(Map<Character, Double> freqs){

        Set<Node> nodes = new HashSet<>();
        for (char c : freqs.keySet()) {
            Node cur = new Node(c, freqs.get(c), System.nanoTime());
            nodes.add(cur);
        }

        int n = freqs.size();

        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> a.compare(b));

        queue.addAll(nodes);
        for (int k = 1; k < n; k++) {
            Node i = queue.remove();
            Node j = queue.remove();

            double rootFreq = i.freq + j.freq;
            Node cur = new Node(Node.INTERNAL_NODE_DATA, rootFreq, System.nanoTime());
            
            cur.left = i;
            cur.right = j;
            queue.add(cur);
        }
        return queue.remove();
    }

    public static double expectedBitsPerLetter(Node root, Map<Node, String> encodingMap) {
        double sumOfFreqs = 0;
        double sumOfFreqByBits = 0;
        for (Node node : encodingMap.keySet()) {
            sumOfFreqs += node.freq;

            double bits = encodingMap.get(node).length();
            sumOfFreqByBits += node.freq * bits;
        }

        return sumOfFreqByBits/sumOfFreqs;
    }

    public static void findEncodings(Node root, String encoding, Map<Node, String> map) {
        if (root == null) {
            return;
        }
        if (root.data != Node.INTERNAL_NODE_DATA) {
            map.put(root, encoding);
            return;
        }
        findEncodings(root.left, encoding + "0", map);
        findEncodings(root.right, encoding + "1", map);
    }

    public static double entropy(Set<Node> nodes) {
        double sumOfFreqs = 0;
        for (Node node : nodes) {
            sumOfFreqs += node.freq;
        }

        double entropy = 0;
        for (Node node : nodes) {
            entropy += (node.freq/sumOfFreqs) * log2(sumOfFreqs/node.freq);
        }

        return entropy;
    }

    public static double log2(double N)
    {
        // calculate log2 N indirectly
        // using log() method
        double result = Math.log(N) / Math.log(2);
  
        return result;
    }
}
