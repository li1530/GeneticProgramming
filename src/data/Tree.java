package data;

import utilities.NodeFactory;
import utilities.Settings;

import java.util.ArrayList;

public class Tree {
    Node root = null;

	public Tree(Node root) {
        setRoot(root);
    }	
    
    public Node getRoot() {
        return root;
    }
    
    public void setRoot(Node root) {
        this.root = root;
        root.setParent(null);
    }
    
    public boolean isEmpty() {
        return root == null;
    }
    
    public int depth() throws Exception {
        return depth(root);
    }
    
    private int depth(Node root) throws Exception {
        if (root == null)
            return 0;
        else
            return root.depth();
    }    

    public String postOrderPrint() {
        String treeEquation = "";
        if (root != null) {
           treeEquation += root.postOrderPrint();
            System.out.println("");
        } else {
            System.out.println("Tree is empty!");
        }
        return treeEquation;
    }
    
    public void inOrderPrint() {
        if (root != null) {
            root.inOrderPrint();
            System.out.println("");
        } else {
            System.out.println("Tree is empty!");
        }
    }
    
    public String getExpression() {
        if (root != null) {
            String s = root.getExpression();
            return s;
        } else {
            return "";
        }
    }
    
    public double evaluate(double xval) throws Exception {
        return root.evaluate(xval);
    }
    
    public ArrayList<Node> getAllNodes() throws Exception {
        ArrayList<Node> nodeList = new ArrayList<Node>(this.depth());
        
        nodeList = this.getRoot().getNodeList(nodeList);
        
        return nodeList;
    }
    
    public ArrayList<Node> getOperandNodes() throws Exception {
        ArrayList<Node> nodeList = new ArrayList<Node>(this.depth());
        
        nodeList = this.getRoot().getOperandNodeList(nodeList);
        
        return nodeList;
    }
    
    public ArrayList<Node> getOperatorNodes() throws Exception {
        ArrayList<Node> nodeList = new ArrayList<Node>(this.depth());
        
        nodeList = this.getRoot().getOperatorNodeList(nodeList);
        
        return nodeList;
    }
    
    public boolean exists(Node node) throws Exception {
        ArrayList<Node> nodeList = getAllNodes();
        
        if (Settings.debug()) {
            System.out.print("Nodes(");
            for (Node n : nodeList) {
                System.out.print(" " + n.getDataItem() + " ");
            }   
            System.out.println(")");
        }
        
        for (Node n : nodeList) {
            if (node.getDataItem().equals(n.getDataItem())) {
                if (Settings.trace()) {
                    System.out.println("equals(" + node.getDataItem() + "," + n.getDataItem() +")");
                }
                return true;
            }
        }
        
        return false;
    }
    
    public void replace(Node node1, Node node2) throws Exception {
        if (root != null) {
            if (root == node1) {
                setRoot(node2);
            } else {
                Node parentNode1 = node1.getParent();
                if (parentNode1 != null) {
                    if (parentNode1.getLeftChild() != null && parentNode1.getLeftChild() == node1) {
                        parentNode1.setLeftChild(node2);
                    } else if (parentNode1.getRightChild() != null && parentNode1.getRightChild() == node1) {
                        parentNode1.setRightChild(node2);
                    }
                }
            }
        }
    }
    
    public static void swap(Tree treeOne, Node nodeOne, Tree treeTwo, Node nodeTwo) throws Exception {
        Node parentNodeOne = nodeOne.getParent();
        Node parentNodeTwo = nodeTwo.getParent();
        
        if (parentNodeOne != null) {
            if (parentNodeOne.getLeftChild() != null && parentNodeOne.getLeftChild() == nodeOne) {
                parentNodeOne.setLeftChild(nodeTwo);
            } else {
                if (parentNodeOne.getRightChild() != null && parentNodeOne.getRightChild() == nodeOne)
                parentNodeOne.setRightChild(nodeTwo);
            }
        } else {
            treeOne.setRoot(nodeTwo);
        }
        
        if (parentNodeTwo != null) {
            if (parentNodeTwo.getLeftChild() != null && parentNodeTwo.getLeftChild() == nodeTwo) {
                parentNodeTwo.setLeftChild(nodeOne);
            } else {
                if (parentNodeTwo.getRightChild() != null && parentNodeTwo.getRightChild() == nodeTwo) {
                    parentNodeTwo.setRightChild(nodeOne);
                }
            }
        } else {
            treeTwo.setRoot(nodeOne);
        }
    }
    
    public static Tree generateTree(int maxDepth) throws Exception{
        double p = Math.random();
        
        Node root = null;
        
        if (p <= 0.5) {
             root = generateFull(maxDepth);
        } else {
            root = generateGrow(maxDepth);
        }
        
        return new Tree(root);
    }
    
    public static Tree copy(Tree tree) throws Exception {
        Node root = tree.getRoot();
        
        if (root == null) {
            return new Tree(null);
        } else {
            return new Tree(root.copy());
        }
    }
    
    private static Node generateFull(int maxDepth) throws Exception {
        Node root;
        
        if (maxDepth > 1) {
            root = NodeFactory.getNode();  
        } else {
            root = NodeFactory.getTerminal();  
        }
        
        for(int i = 0; i < root.numChildNodes(); i++)  {
            if (i == 0) {
                root.setLeftChild(generateFull(maxDepth - 1));
            } else {
                root.setRightChild(generateFull(maxDepth - 1));
            }
        }
           
        return root;  
    }
    
    private static Node generateGrow(int maxDepth) throws Exception {
        Node root;
        
        if (maxDepth > 1) {
            root = NodeFactory.getFunction();  
        } else {
            root = NodeFactory.getTerminal();  
        }
        
        for(int i = 0; i < root.numChildNodes(); i++)  {
            if (i == 0) {
                root.setLeftChild(generateGrow(maxDepth - 1));
            } else {
                root.setRightChild(generateGrow(maxDepth - 1));
            }
        }
           
        return root;  
    }
}
