package com.example.springboot;

public class NNLayer {

    public int size;
    public double[] neurons;
    public double[] biases;
    public double[][] weights;

    public NNLayer(int size, int nextSize) {
        this.size = size;
        neurons = new double[size];
        biases = new double[size];
        weights = new double[size][nextSize];
    }
}