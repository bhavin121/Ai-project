package com.code_n_droid.dwell.simannel;

import android.util.Pair;

import com.code_n_droid.dwell.CustomerDetail;
import com.code_n_droid.dwell.LatLong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TspSimAnnealing {

    private List<Integer> bestPath;
    private ArrayList<Integer> currPath;
    private float T;
    private int N;
    private List<CustomerDetail> dataPoints;
    private float bestFit = Float.POSITIVE_INFINITY;
    private float currentFitness = 0;
    private ArrayList<Integer> nodes;
    private int startNode = 0;

    public TspSimAnnealing(List<CustomerDetail> dataPoints, LatLong deliveryBoyCurrLocation){
        init(dataPoints , (float) Math.sqrt(dataPoints.size()), deliveryBoyCurrLocation);
    }

    public TspSimAnnealing(List<CustomerDetail> dataPoints , float initialTemp, LatLong deliveryBoyCurrLocation){
        init(dataPoints , initialTemp, deliveryBoyCurrLocation);
    }

    private void init(List<CustomerDetail> dataPoints, float initialTemp, LatLong deliveryBoyCurrLocation){
        this.dataPoints = dataPoints;
        this.T = initialTemp;
        this.N = dataPoints.size();
        float cost = Float.POSITIVE_INFINITY;
        nodes = new ArrayList<>();

        // Find start point which is nearest to the delivery boy's
        // location
        for(int i=0;i<dataPoints.size();i++){
            float currCost = cost(dataPoints.get(i).getCustomerAddress().getLatLong(), deliveryBoyCurrLocation);
            if(cost > currCost){
                cost = currCost;
                startNode = i;
            }

            nodes.add(i);
        }

        // Start Computing Solution Using Simulated Annealing
        start();
    }

    private float cost(CustomerDetail c1, CustomerDetail c2){
        LatLong cord1 = c1.getCustomerAddress().getLatLong();
        LatLong cord2 = c2.getCustomerAddress().getLatLong();

        return cost(cord1, cord2);
    }

    private float cost(LatLong cord1, LatLong cord2){
        return (float) (
                Math.pow((Double.parseDouble(cord1.getLatitude()) - (Double.parseDouble(cord2.getLatitude()))), 2)
                        + Math.pow((Double.parseDouble(cord1.getLongitude()) - (Double.parseDouble(cord2.getLongitude()))), 2)
        );
    }

    private float fitness(ArrayList<Integer> path){
        float fit = 0;

        for(int i=0;i<path.size()-1;i++){
            fit += cost(dataPoints.get(path.get(i)) , dataPoints.get(path.get(i + 1)));
        }

        return fit;
    }

    private float probAccept(float fitness){
        return (float) Math.exp(-Math.abs(fitness - currentFitness) / T);
    }

    private void acceptPath(ArrayList<Integer> path){
        float fitness = fitness(path);

        if(fitness < currentFitness){
            currentFitness = fitness;
            currPath = path;

            if(fitness < bestFit){
                bestFit = fitness;
                bestPath = path;
            }
        } else if(Math.random() < probAccept(fitness)){
            currentFitness = fitness;
            currPath = path;
        }
    }

    private Pair<Float, ArrayList<Integer>> initialPath(){
        int currNode = startNode;
        ArrayList<Integer> path = new ArrayList<>();
        path.add(currNode);

        HashSet<Integer> nodesLeft = new HashSet<>(nodes);
        nodesLeft.remove(currNode);

        while(!nodesLeft.isEmpty()){
            currNode = min(nodesLeft, currNode);
            path.add(currNode);
            nodesLeft.remove(currNode);
        }

        float fit = fitness(path);
        if(fit < bestFit){
            bestFit = fit;
            bestPath = path;
        }

        return new Pair<>(fit, path);
    }

    private int min(HashSet<Integer> nodesLeft, int currNode){
        int m = Integer.MAX_VALUE;
        float minCost = Float.POSITIVE_INFINITY;

        for(int nl:nodesLeft){
            float cost = cost(dataPoints.get(nl) , dataPoints.get(currNode));
            if(cost < minCost){
                minCost = cost;
                m = nl;
            }
        }

        return m;
    }

    private void start(){
        Pair<Float, ArrayList<Integer>> initialPath = initialPath();
        currPath = initialPath.second;
        currentFitness = initialPath.first;

        float stopTemp = 1e-10f;
        float alpha = 0.999f;

        while(T > stopTemp ){
            // Neighbourhood Generation
            // Method - 1
            ArrayList<Integer> path = new ArrayList<>(currPath);
            int start = ThreadLocalRandom.current().nextInt(1, N);
            int length = ThreadLocalRandom.current().nextInt(2, N);
            Collections.reverse(path.subList(start, Math.min(start+length, N-1)));

            // Method - 2
//            int a = ThreadLocalRandom.current().nextInt(1, N);
//            int b = ThreadLocalRandom.current().nextInt(1, N);
//
//            int tempA = path.get(a);
//            int tempB = path.get(b);
//            path.set(b, tempA);
//            path.set(a, tempB);

            acceptPath(path);
            T = T* alpha; // Decrease the temperature
        }
    }

    public List<CustomerDetail> bestPath(){
        List<CustomerDetail> bestPathCD = new ArrayList<>();
        int rank = 1;
        for(int i:bestPath){
            dataPoints.get(i).getCustomerAddress().getLatLong().setRank(rank++);
            bestPathCD.add(dataPoints.get(i));
        }

        return bestPathCD;
    }
}
