package com.igor101.algo;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class BalancedBinaryTree {
    public static void main(String[] args) {
        var left = new Tree("L");
        left.left = new Tree("LL");
        left.left.left = new Tree("LLL");
        left.right = new Tree("LR");

        var right = new Tree("R");
        right.right = new Tree("RR");
        right.left = new Tree("RL");

        var root = new Tree("Root");
        root.left = left;
        root.right = right;

        System.out.println(isBalanced(root));
    }

    static boolean isBalanced(Tree root) {
        return dfs(root).balanced();
    }

    static IsBalancedAndHeightResult dfs(Tree root) {
        if (root == null) {
            return new IsBalancedAndHeightResult(true, 0);
        }
        System.out.println("Root: " + root);
        var left = dfs(root.left);
        var right = dfs(root.right);
        var balanced = left.balanced() && right.balanced() && abs(left.height() - right.height()) <= 1;

        return new IsBalancedAndHeightResult(balanced, 1 + max(left.height(), right.height()));
    }

    static class Tree {
        final String name;
        Tree left;
        Tree right;

        Tree(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Tree: " + name;
        }
    }

    record IsBalancedAndHeightResult(boolean balanced, int height) {
    }
}
