package spl.lae;

import java.io.IOException;

import parser.ComputationNode;
import parser.InputParser;
import parser.OutputWriter;

public class Main {

    public static void main(String[] args) throws IOException {
        // TODO: main
        if(args.length < 3)return;
        InputParser parser = new InputParser();
        LinearAlgebraEngine lae = new LinearAlgebraEngine(Integer.parseInt(args[0]));
        try {
            ComputationNode root = parser.parse(args[1]);
            ComputationNode ans = lae.run(root);
            OutputWriter.write(ans.getMatrix(), args[2]);
            System.out.println(lae.getWorkerReport());
        } catch (Exception e) {
          try{
            OutputWriter.write(e.getMessage(), args[2]);
          }catch (IOException ignored){
            System.out.println("IO Exeption occured");
          }
        }
    }
}
