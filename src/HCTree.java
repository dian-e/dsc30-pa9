/*
 * Name: Diane Li
 * PID: A15773774
 */

import java.io.*;
import java.util.Stack;
import java.util.PriorityQueue;

/**
 * The Huffman Coding Tree
 * @author Diane Li
 * @since 03/09/2021
 */
public class HCTree {
    // alphabet size of extended ASCII
    private static final int NUM_CHARS = 256;
    // number of bits in a bytef
    private static final int BYTE_BITS = 8;

    // the root of HCTree
    private HCNode root;
    // the leaves of HCTree that contain all the symbols
    private HCNode[] leaves = new HCNode[NUM_CHARS];

    /**
     * The Huffman Coding Node
     */
    protected class HCNode implements Comparable<HCNode> {

        byte symbol; // the symbol contained in this HCNode
        int freq; // the frequency of this symbol
        HCNode c0, c1, parent; // c0 is the '0' child, c1 is the '1' child

        /**
         * Initialize a HCNode with given parameters
         * @param symbol the symbol contained in this HCNode
         * @param freq   the frequency of this symbol
         */
        HCNode(byte symbol, int freq) {
            this.symbol = symbol;
            this.freq = freq;
        }

        /**
         * Getter for symbol
         * @return the symbol contained in this HCNode
         */
        byte getSymbol() {
            return this.symbol;
        }

        /**
         * Setter for symbol
         * @param symbol the given symbol
         */
        void setSymbol(byte symbol) {
            this.symbol = symbol;
        }

        /**
         * Getter for freq
         * @return the frequency of this symbol
         */
        int getFreq() {
            return this.freq;
        }

        /**
         * Setter for freq
         * @param freq the given frequency
         */
        void setFreq(int freq) {
            this.freq = freq;
        }

        /**
         * Getter for '0' child of this HCNode
         * @return '0' child of this HCNode
         */
        HCNode getC0() {
            return c0;
        }

        /**
         * Setter for '0' child of this HCNode
         * @param c0 the given '0' child HCNode
         */
        void setC0(HCNode c0) {
            this.c0 = c0;
        }

        /**
         * Getter for '1' child of this HCNode
         * @return '1' child of this HCNode
         */
        HCNode getC1() {
            return c1;
        }

        /**
         * Setter for '1' child of this HCNode
         * @param c1 the given '1' child HCNode
         */
        void setC1(HCNode c1) {
            this.c1 = c1;
        }

        /**
         * Getter for parent of this HCNode
         * @return parent of this HCNode
         */
        HCNode getParent() {
            return parent;
        }

        /**
         * Setter for parent of this HCNode
         * @param parent the given parent HCNode
         */
        void setParent(HCNode parent) {
            this.parent = parent;
        }

        /**
         * Check if the HCNode is leaf (has no children)
         * @return if it's leaf, return true. Otherwise, return false.
         */
        boolean isLeaf() { return (this.c0 == null && this.c1 == null); }

        /**
         * String representation
         * @return string representation
         */
        public String toString() {
            return "Symbol: " + this.symbol + "; Freq: " + this.freq;
        }

        /**
         * Compare two nodes
         * @param o node to compare
         * @return int positive if this node is greater
         */
        public int compareTo(HCNode o) {
            // compare frequencies, priority given to smaller frequency
            if (this.freq < o.getFreq()) { return -1; }
            else if (this.freq > o.getFreq()) { return 1; }

            // then compare ascii values if necessary, priority to symbols with smaller ascii value
            else if (this.symbol < o.getSymbol()) { return -1; }
            else if (this.symbol > o.getSymbol()) { return 1; }
            else { return 0; }
        }
    }

    /**
     * Returns the root node
     * @return root node
     */
    public HCNode getRoot() { return root; }

    /**
     * Sets the root node
     * @param root node to set
     */
    public void setRoot(HCNode root) { this.root = root; }

    /**
     * A method that takes an frequency of each byte and creates HCNodes for leaves,
     * adds them to a priority queue, and builds the HCTree
     * @param freq int array of size 256 tracking the frequency of each byte
     */
    public void buildTree(int[] freq) {
        // priority queue to initially store leaf nodes
        PriorityQueue<HCNode> hcNodePriorityQueue = new PriorityQueue<>();
        // create leaf nodes and populate leaves array
        HCNode leaf;
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) {
                leaf = new HCNode((byte) i, freq[i]);
                this.leaves[i] = leaf;
                hcNodePriorityQueue.add(leaf);
            }
        }

        // build HCTree by iteratively removing 2 nodes, summing for parent frequency, etc
        // stop when there is only one node left in the queue, the root
        HCNode child0, child1, parent;
        while (hcNodePriorityQueue.size() > 1) {
            child0 = hcNodePriorityQueue.poll();
            child1 = hcNodePriorityQueue.poll();
            // create parent node with convention of child0's symbol, sum of children's frequency
            parent = new HCNode(child0.getSymbol(), child0.getFreq() + child1.getFreq());
            // sets children & parent node relationships, adds parent into queue
            parent.setC0(child0);
            parent.setC1(child1);
            child0.setParent(parent);
            child1.setParent(parent);
            hcNodePriorityQueue.add(parent);
        }
        setRoot(hcNodePriorityQueue.poll());
    }

    /**
     * A method that takes a given symbol and uses the HCTree built to find its encoding bits and
     * writes them to the given BitOutputStream, encoding exactly one symbol
     * @param symbol to be encoded in bits based on the HCTree
     * @param out stream to write the bits of the given symbol
     * @throws IOException if the BitOutputStream is invalid
     */
    public void encode(byte symbol, BitOutputStream out) throws IOException {
        int ascii = symbol & 0xff;
        HCNode curr = leaves[ascii];
        HCNode currParent;
        // stack to store bits traversed
        Stack<Integer> bits = new Stack<>();

        // traverses tree from leaf up to root, collecting bits on that path
        while (curr.getParent() != null) {
            currParent = curr.getParent();
            // adds 0 or 1 depending on which child curr is
            if (currParent.getC0() == curr) { bits.push(0); }
            else { bits.push(1); }
            curr = currParent;
        }

        // reverses the bits to get the encoding
        while (!bits.isEmpty()) { out.writeBit(bits.pop()); }
    }

    /**
     * A method that decodes the bits and returns a byte that represents the symbol encoded.
     * Each call reads the bit representation of exactly one symbol and returns the decoded symbol
     * @param in bits to be decoded into a byte
     * @return byte that decodes the given bits
     * @throws IOException if the BitInputStream is invalid
     */
    public byte decode(BitInputStream in) throws IOException {
        HCNode curr = this.root;
        while (!curr.isLeaf()) {
            int bit = in.readBit();
            if (bit == 0) { curr = curr.getC0(); }
            else { curr = curr.getC1(); }
        }
        return curr.getSymbol();
    }

    /**
     * A method that takes a node and output stream and uses recursive pre-oder traversing to
     * "print out" the structure of the HCTree in bits
     * @param node to begin traversing from
     * @param out stream to print out structure of HCTree
     * @throws IOException if the BitOutputStream is invalid
     */
    public void encodeHCTree(HCNode node, BitOutputStream out) throws IOException {
        if (node.isLeaf()) {
            out.writeBit(1);
            out.writeByte(node.getSymbol());
        } else {
            out.writeBit(0);
            encodeHCTree(node.getC0(), out);
            encodeHCTree(node.getC1(), out);
        }
    }

    /**
     * A method that builds the original HCTree from the header "printed" in bits when encoding the
     * HCTree. Recursively keeps getting new c0 and c1 and connecting them using a new parent node
     * @param in
     * @return
     * @throws IOException the BitInputStream is invalid
     */
    public HCNode decodeHCTree(BitInputStream in) throws IOException {
        // if the bit represents a leaf node
        // read the next byte, make it a node, put it in the leaves array, and return the node
        if (in.readBit() == 1) {
            byte leaf = in.readByte();
            HCNode node = new HCNode(leaf, 0);
            this.leaves[leaf & 0xff] = node;
            return node;
        } else {
            // otherwise, recurse on both children
            // make a parent node for the children, set parent and children nodes, return parent
            HCNode child0 = decodeHCTree(in);
            HCNode child1 = decodeHCTree(in);
            HCNode parent = new HCNode(child0.getSymbol(), 0);
            parent.setC0(child0);
            parent.setC1(child1);
            child0.setParent(parent);
            child1.setParent(parent);
            return parent;
        }
    }

}
