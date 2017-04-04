import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class HierarchicalBuilder {

    public static void main(String[] args) throws IOException {
        if (args.length == 4) {
            String inputFile = args[0];
            int levels = Integer.parseInt(args[1]);
            int repetitions = Integer.parseInt(args[2]);
            String outputFile = args[3];

            if (levels < 1 || repetitions < 1) {
                System.out.println("invalid number of levels or repetitions. exiting.");
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
            for (int i = 1; i <= Math.pow(repetitions, levels); i++) {
                for (String node : nodes) {
                    output += "set " + node + Common.separator(levels + 1, i) + " [$ns node]\n";
                }
                output += "# ---\n";
            }

            // define joint nodes
            for (int currentLevel = levels - 1; currentLevel >= 0; currentLevel--) {
                for (int i = 1; i <= Math.pow(repetitions, currentLevel - 1); i++) {
                    for (int j = 1; j <= repetitions; j++) {
                        output += "set jointNode" + Common.separator(currentLevel + 1, (repetitions * (i - 1)) + j)
                                + " [$ns node]\n";
                    }
                }
                if (currentLevel == 0) {
                    output += "set jointNode" + Common.separator(1, 1) + " [$ns node]\n";
                }
                output+= "# ---\n";
            }

            // define base level lans
            for (int i = 1; i <= Math.pow(repetitions, levels); i++) {
                for (Lan lan : lans) {
                    output += "set " + lan.name + Common.separator(levels + 1, i) + " [$" + ns + " make-lan \"";
                    for (int k = 0; k < lan.clients.size() - 1; k++) {
                        output += "$" + lan.clients.get(k) + Common.separator(levels + 1, i) + " ";
                    }
                    output += "$" + lan.clients.get(lan.clients.size() - 1) + Common.separator(levels + 1, i) + "\" ";
                    output += lan.speed + " " + lan.delay + "]\n";
                }
                output += "# ---\n";
            }

            // define joint lans
            for (int currentLevel = levels - 1; currentLevel >= 0; currentLevel--) {
                for (int i = 1; i <= Math.pow(repetitions, currentLevel); i++) {
                    output+= "set jointLan" + Common.separator(currentLevel + 1, i) + " [$" + ns + " make-lan \"";
                    output+= "$jointNode" + Common.separator(currentLevel + 1, i) + " ";
                    for (int j = 1; j < repetitions; j++) {
                        if (currentLevel == levels - 1) {
                            output+= "$joint";
                        } else {
                            output+= "$jointNode";
                        }
                        output+= Common.separator(currentLevel + 2, (repetitions * (i - 1)) + j) + " ";
                    }
                    if (currentLevel == levels - 1) {
                        output+= "$joint";
                    } else {
                        output+= "$jointNode";
                    }
                    output+= Common.separator(currentLevel + 2, repetitions * i) + "\" 10Gb 0ms]\n";
                }
                output+= "# ---\n";
            }

            // define os
            if (osMap.size() > 0) {
                for (int i = 1; i <= Math.pow(repetitions, levels); i++) {
                    for (Object o : osMap.entrySet()) {
                        Map.Entry pair = (Map.Entry) o;
                        output += "tb-set-node-os " + pair.getKey() + Common.separator(levels + 1, i) + " " +
                                pair.getValue() + "\n";
                    }
                    output += "# ---\n";
                }
            }

            output+= Common.getEpilogue(ns);

            System.out.println(output);
            Utils.writeFile(outputFile, output);
        } else {
            System.out.println("Invalid number of arguments.");
            System.out.println("4 arguments required: <input file> <number of levels> " +
                    "<number of repetitions per level> <output file>");
        }
    }

}
