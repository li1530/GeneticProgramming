import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import javax.swing.SwingUtilities;

import utilities.GeneticOperators;
import utilities.Settings;
import data.GPTree;
import data.OutputData;
import data.TrainingData;

/**
 * @author Ou Li and Vishal Yelisetti
 * 2015/05/03
 */

public class GeneticProgrammingMain {

    public final static OutputData output = new OutputData();

    /**
     *
     * @param args main args
     */
	public static void main(String[] args) {
		GeneticProgrammingMain gpMain = new GeneticProgrammingMain();
		try {
            gpMain.process();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred.");
        }


	}
	
	public void process() throws Exception {
        TrainingData.generateInitialTrainingData(-15D, 70);
	    // Initialize

	    ArrayList<TrainingData> trainingData = TrainingData.getTrainingData();

        showGUI();
	    
        OutputData output = GeneticProgrammingMain.output;
        
        ArrayList<GPTree> initialPopulation = getInitialPopulation(trainingData);
        
        // Sort population according to fitness, in descending order
        Collections.sort(initialPopulation);
        
        // Get current time in milliseconds
        long startTime = getCurrentTime();
        output.setStartTime(startTime);
        
        GPTree currentMaxFitnessTree = getCurrentMaxFitnessTree(initialPopulation);
        initializeOutputData(output, initialPopulation, currentMaxFitnessTree);
        output.loadDashboard();
       
        // Generate the best fit solution
        ArrayList<GPTree> population = initialPopulation;
        while (!done(startTime, currentMaxFitnessTree) && output.getGenerationCount() < Settings.getMaxGeneration()) {  
            if (Settings.regenerateTrees(output.getGenerationCount())) {
                population = getInitialPopulation(trainingData);
                Collections.sort(initialPopulation);
            }
            
            output.setCurrentTime(getCurrentTime());
            output.displayResults();
            output.updateDashboard();
            
            if (Settings.trace()) {
                output.displayPopulation(population);
            }
            
            ArrayList<GPTree> nextGenPopulation = GeneticOperators.selection(population);
            
            if (Settings.trace()) {
                System.out.println("[Trace] Display selected population:");
                output.displayPopulation(nextGenPopulation);
            }
            
            ArrayList<GPTree> children = GeneticOperators.crossoverTrees(population);
            
            nextGenPopulation.addAll(children);
            
            GeneticOperators.mutateTrees(nextGenPopulation);
            
            output.incrementGenerationCount();
            output.addPopulationSizeInGeneration(nextGenPopulation.size());
                
            performFitnesEvaluation(nextGenPopulation);
                
            Collections.sort(nextGenPopulation);
            
            currentMaxFitnessTree = getCurrentMaxFitnessTree(nextGenPopulation);
            output.addFittestTreeInGeneration(currentMaxFitnessTree);
                 
            population = nextGenPopulation;
        }
        
        output.setCurrentTime(getCurrentTime());
        output.displayPopulation(population);
        output.displayFinalResults();
        output.recordXYGraph();
        output.recordFinalPopulationFitness(population);
	}

    private void showGUI() {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                GeneticProgrammingMain.output.createAndShowDashboard();
            }
        });
    }

    public void initializeOutputData(OutputData output, ArrayList<GPTree> initialPopulation, GPTree currentMaxFitnessTree) throws Exception {
        output.incrementGenerationCount();
        output.addFittestTreeInGeneration(currentMaxFitnessTree);
        output.addPopulationSizeInGeneration(initialPopulation.size());
        output.recordInitialPopulationFitness(initialPopulation);
    }

    private void performFitnesEvaluation(ArrayList<GPTree> nextGenPopulation)throws Exception {
        for (GPTree gpTree : nextGenPopulation) {
            double fitness = GPTree.evaluateFitness(TrainingData.getTrainingData(), gpTree);
            gpTree.setFitness(fitness);
        }
    }

    private GPTree getCurrentMaxFitnessTree(ArrayList<GPTree> population) {
        GPTree currentMaxFitnessTree = population.get(0);
        return currentMaxFitnessTree;
    }

    private boolean done(long startTime, GPTree gpTree) throws Exception {
        if (bestSolutionFound(gpTree) || executionTimeExceeded(startTime)) {
            if (Settings.trace()) {
                System.out.println("[Trace:done()] *** Done! ***");
            }
            return true;
        } else {
            if (Settings.trace()) {
                System.out.println("[Trace:done()] Not done");
            }
            return false;
        }
    }

    private boolean bestSolutionFound(GPTree currentMaxFitnessTree) throws Exception {
        boolean found = false;
        if (currentMaxFitnessTree.getFitness() <= Settings.getFitnessThreshold()) {
            found = true;
            
            if (Settings.trace()) {
                System.out.println("[Trace:bestSolutionFound()] *** Best fit solution found! ***");
            }
        }
        
        if (Settings.trace()) {
            System.out.println("[Trace} currentMaxFitness: " + currentMaxFitnessTree.getFitness());
            System.out.println("[Trace} fitnessThreshold : " + Settings.getFitnessThreshold());
        }
        
        return found;
    }
	
    private boolean executionTimeExceeded(long startTime) throws Exception {
        long currentTime = new Date().getTime();
        long runDuration = currentTime - startTime;
        
        if (Settings.trace()) {
            System.out.println("[Trace] StartTime   : " + startTime);
            System.out.println("[Trace] CurrentTime : " + currentTime);
            System.out.println("[Trace] Run Duration: " + runDuration);
            System.out.println("[Trace] Max Duration: " + Settings.maxExecutionTime());
        }
        
        if (runDuration > Settings.maxExecutionTime()) {
            if (Settings.trace()) {
                System.out.println("[Trace] Run Duration: " + runDuration);
                System.out.println("[Trace] Max Duration: " + Settings.maxExecutionTime());
                System.out.println("[Trace:bestSolutionFound()] *** Max exection time exceeded! ***");
            }
            
            return true;
        } else {
            if (Settings.trace()) {
                System.out.println("[Trace] Run Duration: " + runDuration);
                System.out.println("[Trace] Max Duration: " + Settings.maxExecutionTime());
            }

            return false;
        }  
    }

    private long getCurrentTime() {
        return new Date().getTime();
    }

    private ArrayList<GPTree> getInitialPopulation(ArrayList<TrainingData> trainingData) {
        int size = 0;
        
        try {
            Properties settings = Settings.getSettings();
            
            String prop = settings.getProperty(Settings.PROP_POPULATION_SIZE);
            
            size = Integer.parseInt(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ArrayList<GPTree> population = new ArrayList<GPTree>(size);
        try {
            for (int i = 0; i < size; i++) {
                GPTree gpTree = GPTree.createGeneticProgrammingTree(trainingData);
                
                population.add(gpTree);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return population;
    }

}
