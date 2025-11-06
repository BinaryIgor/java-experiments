package com.igor101.algo;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class BinaryTree {
    public static void main(String[] args) {
        var map = new LinkedHashMap<String, Integer>();
        map.put("2", 22);

        var tree = new Tree(5);

        tree.insert(2);
        tree.insert(10);
        tree.insert(1);
        tree.insert(101);
        tree.insert(0);

        tree.traverse();

        System.out.println();
//        System.out.println(tree);
    }

    static class Tree {

        final int value;
        Tree left;
        Tree right;

        Tree(int value) {
            this.value = value;
        }

        void insert(int value) {
            if (value < this.value) {
                if (left == null) {
                    this.left = leaf(value);
                } else {
                    this.left.insert(value);
                }
            } else if (value > this.value) {
                if (right == null) {
                    this.right = leaf(value);
                } else {
                    this.right.insert(value);
                }
            } else {
                throw new IllegalArgumentException("Duplicates are not allowed!");
            }
        }

        public static Tree leaf(int value) {
            return new Tree(value);
        }

        public void traverse() {
            traverse(this);
        }

        private void traverse(Tree tree) {
            if (tree != null) {
                traverse(tree.left);
                System.out.println(tree.value);
                traverse(tree.right);
            }
        }

        @Override
        public String toString() {
            return "Tree, value: " + value + ", left: " + left + ", right: " + right;
        }
    }
}
