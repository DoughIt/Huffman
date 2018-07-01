package Logical;

import java.util.*;

public class HuffmanTree {
    static class Node implements Comparable<Node>{
        public void setChildren(Node leftChild, Node rightChild) {
            this.leftChild = leftChild;
            this.leftChild.childType = "0";
            this.rightChild = rightChild;
            this.rightChild.childType = "1";
        }

        public Node getLeftChild() {
            return leftChild;
        }

        public Node getRightChild() {
            return rightChild;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(byte value) {
            this.value = value;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getChildType() {
            return childType;
        }

        public boolean isLeaf() {
            return this.leftChild == null && this.rightChild == null;
        }

        @Override
        public int compareTo(Node node) {
            if (this.count < node.count)
                return -1;
            else return 1;
        }

        private int value;
        private int count;
        private Node leftChild;
        private Node rightChild;
        private String childType = "";  //0代表左节点，1代表右节点

        public Node(int count) {
            this.count = count;
        }

        public Node(int value, int count) {
            this.leftChild = null;
            this.rightChild = null;
            this.value = value;
            this.count = count;

        }

    }

    //按字符出现次数生成优先队列
    public PriorityQueue<Node> toQueue(int[] chars) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 0) {
                Node node = new Node(i, chars[i]);
                queue.add(node);
            }
        }
//        }
        return queue;
    }

    //生成哈夫曼树
    public Node getHuffmanTree(PriorityQueue<Node> queue) {
        while (queue.size() > 1) {
            Node leftChild = queue.poll();
            Node rightChild = queue.poll();
            Node parent = new Node(leftChild.count + rightChild.count);
            parent.setChildren(leftChild, rightChild);
            queue.add(parent);
        }
        if (queue.size() > 0)
            return queue.poll();
        else return null;
    }

}