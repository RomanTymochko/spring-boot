package com.example.springboot;

import java.util.function.UnaryOperator;

public class NeuralNetwork {

    private double learningRate;
    private NNLayer[] nnLayers;
    private UnaryOperator<Double> activation;
    private UnaryOperator<Double> derivative;

//    Constructor
    public NeuralNetwork(double learningRate, UnaryOperator<Double> activation, UnaryOperator<Double> derivative,  int... sizes) {
        this.learningRate = learningRate;
        this.activation = activation;
        this.derivative = derivative;
        nnLayers = new NNLayer[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            int nextSize = 0;
            if(i < sizes.length - 1) nextSize = sizes[i + 1];
            nnLayers[i] = new NNLayer(sizes[i], nextSize);
            for (int j = 0; j < sizes[i]; j++) {
//                Fill the weights by random numbers
                nnLayers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0; k < nextSize; k++) {
                    nnLayers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

//    Method that takes inputs and calculate outputs
    public double[] feedForward(double[] inputs) {
        System.arraycopy(inputs, 0, nnLayers[0].neurons, 0, inputs.length);
        for (int i = 1; i < nnLayers.length; i++)  {
            NNLayer l = nnLayers[i - 1];
            NNLayer l1 = nnLayers[i];
            for (int j = 0; j < l1.size; j++) {
                l1.neurons[j] = 0;
                for (int k = 0; k < l.size; k++) {
                    l1.neurons[j] += l.neurons[k] * l.weights[k][j];
                }
                l1.neurons[j] += l1.biases[j];
                l1.neurons[j] = activation.apply(l1.neurons[j]);
            }
        }
        return nnLayers[nnLayers.length - 1].neurons;
    }

//    Learning method
    public void backpropagation(double[] targets) {
        double[] errors = new double[nnLayers[nnLayers.length - 1].size];
        for (int i = 0; i < nnLayers[nnLayers.length - 1].size; i++) {
            errors[i] = targets[i] - nnLayers[nnLayers.length - 1].neurons[i];
        }
        for (int k = nnLayers.length - 2; k >= 0; k--) {
            NNLayer l = nnLayers[k]; NNLayer l1 = nnLayers[k + 1];
            double[] errorsNext = new double[l.size]; double[] gradients = new double[l1.size];
            for (int i = 0; i < l1.size; i++) {
                gradients[i] = errors[i] * derivative.apply(nnLayers[k + 1].neurons[i]); gradients[i] *= learningRate;
            }
            double[][] deltas = new double[l1.size][l.size];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    deltas[i][j] = gradients[i] * l.neurons[j];
                }
            }
            for (int i = 0; i < l.size; i++) {
                errorsNext[i] = 0;
                for (int j = 0; j < l1.size; j++) {
                    errorsNext[i] += l.weights[i][j] * errors[j];
                }
            }
            errors = new double[l.size];
            System.arraycopy(errorsNext, 0, errors, 0, l.size);
            double[][] weightsNew = new double[l.weights.length][l.weights[0].length];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    weightsNew[j][i] = l.weights[j][i] + deltas[i][j];
                }
            }
            l.weights = weightsNew;
            for (int i = 0; i < l1.size; i++) {
                l1.biases[i] += gradients[i];
            }
        }
    }
}