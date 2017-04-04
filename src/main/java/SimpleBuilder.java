import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class SimpleBuilder {

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            String inputFile = args[0];
            int repetitions = Integer.parseInt(args[1]);
            String outputFile = args[2];

            if (repetitions < 1) {
                System.out.println("invalid number of repetitions. exiting.");
                System.exit(0);
            }

            String fullInput = Utils.readFile(new File(inputFile), Charset.defaultCharset());
            String lines[] = Common.splitFullInputIntoLines(fullInput);

            // find simulator name
            String ns = Common.findSimulatorName(lines);
            System.out.println("ns name: " + ns);

            // get node lines
            List<String> nodeLines = Common.getNodeLines(lines, ns);

            // get node names
            List<String> nodes = Common.getNodeNames(nodeLines);
            System.out.println("nodes: " + nodes);

            if (nodes.contains("joint")) {
                System.out.println("joint node found.");
            } else {
                System.out.println("joint node not found. exiting.");
                System.exit(0);
            }

            // get make-lan lines
            List<String> lanLines = Common.getMakeLanLines(lines, ns);

            // generate lans
            List<Lan> lans = Common.generateLans(lanLines, nodes);

            // get set os lines
            List<String> osLines = Common.getOsLines(lines, nodes);

            // get os mapping
            Map<String, String> osMap = Common.getOsMap(osLines);
            System.out.println("Os map: " + osMap);

            System.out.println("\n\n\n---Output---");
            String output = Common.getPreamble(ns);

            // define nodes
            for (int i = 1; i <= repetitions; i++) {
                for (String node : nodes) {
                    output+= "set " + node + Common.separator(i) + " [$ns node]\n";
                }
                output+= "# ---\n";
            }

            // define lans
            for (int i = 1; i <= repetitions; i++) {
                for (Lan lan : lans) {
                    output+= "set " + lan.name + Common.separator(i) + " [$" + ns + " make-lan \"";
                    for (int j = 0; j < lan.clients.size() - 1; j++) {
                        output+= "$" + lan.clients.get(j) + Common.separator(i) + " ";
                    }
                    output+= "$" + lan.clients.get(lan.clients.size() - 1) + Common.separator(i) + "\" ";
                    output+= lan.speed + " " + lan.delay + "]\n";
                }
                output+= "# ---\n";
            }
            output+= "set jointLan [$" + ns + " make-lan \"";
            for (int i = 1; i < repetitions; i++) {
                output+= "$joint" + Common.separator(i) + " ";
            }
            output+= "$joint" + Common.separator(repetitions) + "\" 10Gb 0ms]\n";
            output+= "# ---\n";

            // define os
            if (osMap.size() > 0) {
                for (int i = 1; i <= repetitions; i++) {
                    for (Object o : osMap.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        output += "tb-set-node-os " + pair.getKey() + Common.separator(i) + " " + pair.getValue() + "\n";
                    }
                    output+= "# ---\n";
                }
            }

            output+= Common.getEpilogue(ns);

            System.out.println(output);
            Utils.writeFile(outputFile, output);
        } else {
            System.out.println("Invalid number of arguments.");
            System.out.println("3 arguments required: <input file> <number of repetitions> <output file>");
        }
    }

}
