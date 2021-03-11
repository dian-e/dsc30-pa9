import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

public class HCTreeTester {

    HCTree tree1, tree2, tree3;
    int[] arr1, arr2, arr3;

    /**
     * Tests encode() and decode().
     * @param tree HCTree to test
     * @param input the byte to reconstruct
     * @return whether the encode-decode can reconstruct the input byte
     * @throws IOException from stream
     */
    private static boolean testByte(HCTree tree, byte input) throws IOException {

        // build out-stream
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        BitOutputStream bitOut = new BitOutputStream(dataOut);

        // encode byte
        tree.encode(input, bitOut);

        // send data from out-stream to in-stream
        bitOut.flush();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        DataInputStream dataIn = new DataInputStream(byteIn);
        BitInputStream bitIn = new BitInputStream(dataIn);

        // decode byte and compare with input
        boolean result = (input == tree.decode(bitIn));

        // close streams
        dataOut.close();
        byteOut.close();
        dataIn.close();
        byteIn.close();
        return result;
    }

    /**
     * Checks if `expected` and `actual` have the same structure,
     * regardless of the instance variables on the nodes.
     * @param expected the root of the expected tree
     * @param actual the root of the actual tree
     * @return whether they share the same structure
     */
    private static boolean sameTreeStructure(HCTree.HCNode expected, HCTree.HCNode actual) {
        if (expected == null && actual == null) return true;
        if (expected == null || actual == null) return false;
        return sameTreeStructure(expected.c0, actual.c0)
                && sameTreeStructure(expected.c1, actual.c1);
    }

    /**
     * Tests encodeHCTree() and decodeHCTree().
     * @param tree HCTree to test
     * @return whether the encode-decode can reconstruct the tree
     * @throws IOException from stream
     */
    private static boolean testTree(HCTree tree) throws IOException {
        // build out-stream
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        BitOutputStream bitOut = new BitOutputStream(dataOut);

        // encode tree
        tree.encodeHCTree(tree.getRoot(), bitOut);

        // send data from out-stream to in-stream
        bitOut.flush();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        DataInputStream dataIn = new DataInputStream(byteIn);
        BitInputStream bitIn = new BitInputStream(dataIn);

        // decode tree and compare with input
        HCTree treeOut = new HCTree();
        treeOut.setRoot(treeOut.decodeHCTree(bitIn));
        boolean result = sameTreeStructure(tree.getRoot(), treeOut.getRoot());

        // close streams
        dataOut.close();
        byteOut.close();
        dataIn.close();
        byteIn.close();
        return result;
    }

    @Before
    public void setUp(){
        tree1 = new HCTree();
        tree2 = new HCTree();
        tree3 = new HCTree();

        arr1 = new int[256];
        arr1[10] = 1; // \n
        arr1[97] = 17; // a
        arr1[98] = 8; // b
        arr1[99] = 7; // c
        arr1[100] = 14; // d
        arr1[101] = 9; // e
        arr1[102] = 1; // f

        arr2 = new int[256];
        arr2[10] = 1; // \n
        arr2[48] = 6; // 0
        arr2[49] = 1; // 1
        arr2[50] = 1; // 2
        arr2[51] = 1; // 3
        arr2[52] = 2; // 4
        arr2[56] = 1; // 8
        arr2[97] = 1; // a
        arr2[98] = 1; // b
        arr2[99] = 6; // c
        arr2[100] = 6; // d
        arr2[115] = 6; // s

        arr3 = new int[256];
        arr3[97] = 5; // a
        arr3[98] = 9; // b
        arr3[99] = 12; // c
        arr3[100] = 13; // d
        arr3[101] = 16; // e
        arr3[102] = 45; // f

        tree1.buildTree(arr1);
        tree2.buildTree(arr2);
        tree3.buildTree(arr3);
    }

//    @Test
//    public void testGetRoot() throws IOException {
//        assertEquals(new HCTree.HCNode['e', 57], tree1.getRoot());
//
//    }

    @Test
    public void testEncodeDecode() throws IOException {
        assertTrue(testByte(tree1, (byte) '\n'));
        assertTrue(testByte(tree1, (byte) 'a'));
        assertTrue(testByte(tree1, (byte) 'b'));
        assertTrue(testByte(tree1, (byte) 'c'));
        assertTrue(testByte(tree1, (byte) 'd'));
        assertTrue(testByte(tree1, (byte) 'e'));
        assertTrue(testByte(tree1, (byte) 'f'));

        assertTrue(testByte(tree2, (byte) '8'));
        assertTrue(testByte(tree2, (byte) 's'));
        assertTrue(testByte(tree2, (byte) '\n'));
        assertTrue(testByte(tree2, (byte) '0'));

        assertTrue(testByte(tree3, (byte) 'f'));
        assertTrue(testByte(tree3, (byte) 'd'));
        assertTrue(testByte(tree3, (byte) 'a'));
        assertTrue(testByte(tree3, (byte) 'e'));
    }

    @Test
    public void testEncodeDecodeHCTree() throws IOException {
        assertTrue(testTree(tree1));
        assertTrue(testTree(tree2));
        assertTrue(testTree(tree3));
    }

}
