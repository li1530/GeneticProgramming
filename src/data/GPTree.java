package data;

import utilities.Settings;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.Properties;

public class GPTree extends Tree implements Comparable<GPTree> {

    private double fitness;

    private GPTree(Node root, double fitness) {
        super(root);
        setFitness(fitness);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public static ArrayList<GPTree> getGeneticTreePopulation(int size) throws Exception {
        ArrayList<GPTree> population = new ArrayList<GPTree>(size);
        boolean singletonExists = false;
        int i = 0;
        while (i < size) {
            GPTree gpTree = GPTree.createGeneticProgrammingTree(TrainingData.getTrainingData());

            if (gpTree.depth() == 1) {
                if (singletonExists) {
                    continue;
                } else {
                    if (gpTree.exists(new OperandNode(OperandNode.OPERAND_X))) {
                        population.add(gpTree);
                        i++;

                        singletonExists = true;

                        if (Settings.trace()) {
                            Utilities.printTreeNode(gpTree.getRoot());
                        }
                    }
                }
            } else {
                if (gpTree.exists(new OperandNode(OperandNode.OPERAND_X))) {
                    population.add(gpTree);
                    i++;

                    if (Settings.trace()) {
                        Utilities.printTreeNode(gpTree.getRoot());
                    }
                } else {
                    continue;
                }
            }
        }

        return population;
    }

    public static GPTree createGeneticProgrammingTree(ArrayList<TrainingData> trainingDataList) throws Exception {
        Properties settings = Settings.getSettings();

        String prop = settings.getProperty(Settings.PROP_MAX_DEPTH);

        int maxDepth = Integer.parseInt(prop);

        return createGeneticProgrammingTree(trainingDataList, maxDepth);
    }

    public static GPTree createGeneticProgrammingTree(ArrayList<TrainingData> trainingDataList, int maxDepth) throws Exception {
        Tree tree = generateInitialTree(maxDepth);

        double fitness = evaluateFitness(trainingDataList, tree);

        return new GPTree(tree.getRoot(), fitness);
    }
    
    public static GPTree copy(GPTree tree) throws Exception {
        Tree copyTree = Tree.copy(tree);
        
        return new GPTree(copyTree.getRoot(), tree.getFitness());
    }

    public static double evaluateFitness(ArrayList<TrainingData> trainingDataList, Tree tree) throws Exception {
        double fitness = 0;

        for (TrainingData trainingData : trainingDataList) {
            double evalData = tree.evaluate(trainingData.inputData);

            if (Settings.trace()) {
                System.out.println("Evaluate tree(" + trainingData.inputData + "): " + evalData);
            }

            fitness = fitness + Math.abs(trainingData.outputData - evalData);

            if (Settings.trace()) {
                System.out.println("Tree Fitness : Math.abs(" + trainingData.outputData + " - " + evalData + ") = " + fitness);
            }
        }
        return fitness;
    }

    public static void updateFitness(GPTree tree, ArrayList<TrainingData> trainingDataList) throws Exception {

        if (tree != null && trainingDataList.size() > 0) {
            double fitness = evaluateFitness(trainingDataList, tree);
            
            tree.setFitness(fitness);
        }
    }

    public static Tree generateInitialTree(int maxDepth) throws Exception {
        Tree tree = null;
        do {
            tree = Tree.generateTree(maxDepth);
        } while (Double.isNaN(tree.evaluate(0)) || Double.isInfinite(tree.evaluate(0)));

        if (Settings.trace()) {
            System.out.println("Evaluate for x=0: " + tree.evaluate(0));
        }

        return tree;
    }

    public int compareTo(GPTree gpTree) {
        double delta = this.fitness - gpTree.getFitness();

        if (delta > 0) {
            return 1;
        } else if (delta == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}
