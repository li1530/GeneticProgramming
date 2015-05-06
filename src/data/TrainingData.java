package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class TrainingData {
    
    public final static String TRAINING_FILE_NAME = "trainingdata.txt";
    private static ArrayList<TrainingData> trainingDataList = null;
    
    double inputData;
    double outputData;
    
    public TrainingData(double inputData, double outputData) {
        this.inputData = inputData;
        this.outputData = outputData;
    }
    
    public double inputData() {
        return inputData;
    }
    
    public double outputData() {
        return outputData;
    }
    
    public static void generateInitialTrainingData(double minValue, int dataSetSize) throws Exception {
        
        FileOutputStream outputStream = new FileOutputStream(TRAINING_FILE_NAME);
        
        DataOutputStream out = new DataOutputStream(outputStream);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        
        for (int i = 0; i < dataSetSize; i++) {
            double x = minValue; 
            double y = (2*(x * x) - 3)/3;
            bw.write("" + x + "," + y + "\n");
            
            minValue++;
        }
        
        bw.flush();
        bw.close();
    }
    
    public static void generateInitialTrainingDataOriginal(double minValue, int dataSetSize) throws Exception {
        
        FileOutputStream outputStream = new FileOutputStream("trainingdata_orig.txt");
        
        DataOutputStream out = new DataOutputStream(outputStream);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        
        for (int i = 0; i < dataSetSize; i++) {
            double x = minValue; 
            double y = (2*(x * x) - 3)/3;
            
            bw.write("" + x + "," + y + "\n");
            
            minValue++;
        }
        
        bw.flush();
        bw.close();
    }
    
    public static void generateInitialTrainingDataOptional(double minValue, int dataSetSize) throws Exception {
        
        FileOutputStream outputStream = new FileOutputStream("trainingdata_opt.txt");
        
        DataOutputStream out = new DataOutputStream(outputStream);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        
        for (int i = 0; i < dataSetSize; i++) {
            double x = minValue; 
            double y = (((x * x * x)) -1)/2;
            
            bw.write("" + x + "," + y + "\n");
            
            minValue++;
        }
        
        bw.flush();
        bw.close();
    }
    
    public static ArrayList<TrainingData> getTrainingData() throws Exception {
        if (trainingDataList == null) {
            trainingDataList = readTrainingData(TRAINING_FILE_NAME);
        }
        
        return trainingDataList;
    }
    
    private static ArrayList<TrainingData> readTrainingData(String fileName) throws Exception {
        ArrayList<TrainingData> trainingDataList = new ArrayList<TrainingData>();

        FileInputStream inputStream = new FileInputStream(fileName);
        
        DataInputStream in = new DataInputStream(inputStream);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        
        String strLine;
        
        while ((strLine = br.readLine()) != null) {
            String[] strArr = strLine.split(",");
            
            double inputData = Double.parseDouble(strArr[0]);
            
            double outputData = Double.parseDouble(strArr[1]);

            TrainingData trainingData = new TrainingData(inputData, outputData);
            
            trainingDataList.add(trainingData);
        }
        
        in.close();

        return trainingDataList;
    }
}
