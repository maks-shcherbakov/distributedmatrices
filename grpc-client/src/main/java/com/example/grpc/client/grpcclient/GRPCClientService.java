package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File; 
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;

import com.example.grpc.client.model.FileUploadResponse;

@Service
public class GRPCClientService {

        private String fileName;
        private String uploadFilePath;
        private String contentType;
        private File dest;

        @Value("${matrix.symbols}")
        private String matrixSymbols;

        public String ping() {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();        
                PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel);        
                PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());   

                channel.shutdown();        
                return helloResponse.getPong();
        }

        public FileUploadResponse fileUpload(@RequestParam("file") MultipartFile file){

                fileName = file.getOriginalFilename();
                uploadFilePath = "/home/ubuntu/grpcNew/Files";
                contentType = file.getContentType();
                dest = new File(uploadFilePath + '/' + fileName);

                if (!dest.getParentFile().exists())  dest.getParentFile().mkdirs();
                    

                try { file.transferTo(dest); }
                catch (Exception e) { return new FileUploadResponse(fileName, contentType, "File is not provided, please add a file!!! " + e.getMessage()); }

                // Get matrices from file 
                String matrixA_temp = txt2String(dest).split(matrixSymbols)[0];
                String matrixB_temp = txt2String(dest).split(matrixSymbols)[1];

                // Convert each string matrix to int[][]] matrix
                int[][] matrixA = convertToMatrix(matrixA_temp);
                int[][] matrixB = convertToMatrix(matrixB_temp);

                // If not square matrix
                if(matrixA.length != matrixA[0].length || matrixB.length != matrixB[0].length){
                        String data  = "Matrix A: " + matrixA.length  + "x" + matrixA[0].length;
                               data += "  Matrix B: " + matrixB.length  + "x" + matrixB[0].length;
                        return new FileUploadResponse(fileName, contentType, "Rows and Columns of the Matrices should be equal size!!! " + data);
                }
                // If not even number rows and col
                if(matrixA.length % 4 !=0 || matrixB.length % 4 !=0 ){
                        String data  = "Matrix A: " + matrixA.length  + "x" + matrixA[0].length;
                               data += "  Matrix B: " + matrixB.length  + "x" + matrixB[0].length;
                        return new FileUploadResponse(fileName, contentType, "Accepted Matrices: nxn where n%4=0!!! " + data);
                }
                grpc(matrixA, matrixB);
                return new FileUploadResponse(fileName, contentType, "File Successfully Uploaded");
        }

        public void grpc(int[][]a, int[][]b){
 
                ManagedChannel channel1 = ManagedChannelBuilder.forAddress("172.31.94.130", 9090).usePlaintext().build();  
                ManagedChannel channel2 = ManagedChannelBuilder.forAddress("172.31.81.112", 9090).usePlaintext().build();  
                ManagedChannel channel3 = ManagedChannelBuilder.forAddress("172.31.94.231", 9090).usePlaintext().build();  
                ManagedChannel channel4 = ManagedChannelBuilder.forAddress("172.31.92.51", 9090).usePlaintext().build();  
                ManagedChannel channel5 = ManagedChannelBuilder.forAddress("172.31.81.107", 9090).usePlaintext().build();  
                ManagedChannel channel6 = ManagedChannelBuilder.forAddress("172.31.93.184", 9090).usePlaintext().build();  
                ManagedChannel channel7 = ManagedChannelBuilder.forAddress("172.31.88.43", 9090).usePlaintext().build();  
                ManagedChannel channel8 = ManagedChannelBuilder.forAddress("172.31.93.187", 9090).usePlaintext().build();  
  
                MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub3 = MatrixServiceGrpc.newBlockingStub(channel3);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub4 = MatrixServiceGrpc.newBlockingStub(channel4);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub5 = MatrixServiceGrpc.newBlockingStub(channel5);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub6 = MatrixServiceGrpc.newBlockingStub(channel6);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub7 = MatrixServiceGrpc.newBlockingStub(channel7);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub8 = MatrixServiceGrpc.newBlockingStub(channel8);

                ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubss = new ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub>();
                stubss.add(stub1);
                stubss.add(stub2);
                stubss.add(stub3);
                stubss.add(stub4);
                stubss.add(stub5);
                stubss.add(stub6);
                stubss.add(stub7);
                stubss.add(stub8);

                int stubs_index = 0;

                int N = a.length;

                int c[][] = new int[N][N];

                for (int i = 0; i < N; i++) { // row
                        for (int j = 0; j < N; j++) { // col
                            for (int k = 0; k < N; k++) {
                                
                                MatrixReply temp=stubss.get(stubs_index).multiplyBlock(MatrixRequest.newBuilder().setA(a[i][k]).setB(b[k][j]).build());
                                if(stubs_index == 2) stubs_index = 0;
                                else stubs_index++;
                                MatrixReply temp2=stubss.get(stubs_index).addBlock(MatrixRequest.newBuilder().setA(c[i][j]).setB(temp.getC()).build());
                                c[i][j] = temp2.getC();
                                if(stubs_index == 2) stubs_index = 0;
                                else stubs_index++;
                            }
                        }
                    }

                
                    for (int i = 0; i < a.length; i++) {
                        for (int j = 0; j < a[0].length; j++) {
                            System.out.print(c[i][j] + " ");
                        }
                        System.out.println("");
                    }
            
                channel.shutdown();

                
        }

        private static double footPrint(MatrixServiceGrpc.MatrixServiceBlockingStub stub, int a, int b){

        }

        public static int[][] convertToMatrix(String m){
                String[] data = m.split(";");
                String row_col[] = data[0].split(",");
                // Get rows and data into int var. 
                int row = Integer.parseInt(row_col[0].replaceAll("[\\n\\t ]", ""));
                int col = Integer.parseInt(row_col[1].replaceAll("[\\n\\t ]", ""));

                
 
                String[] matrixData_temp = data[1].split(" ");
                int[][] matrix = new int[row][col];
                int temp_matrix_index = 0; 
                 
                for(int i = 0; i < row; i++){
                        for(int j = 0; j < col; j++){
                                matrix[i][j] = Integer.parseInt(matrixData_temp[temp_matrix_index].replaceAll("[\\n\\t ]", ""));
                                temp_matrix_index++;
                        }
                }
                return matrix;
        }


        public static String txt2String(File file) {
                StringBuilder result = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String s = null;
                    while ((s = br.readLine()) != null) {
                        result.append(System.lineSeparator() + s);
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result.toString();
        }



}

