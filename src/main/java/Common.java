import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {

    private static final String SEPARATOR = "ooo";

    public static String separator(int i) {
        return SEPARATOR + i;
    }

    public static String separator(int i, int j) {
        return SEPARATOR + i + SEPARATOR + j;
    }

    public static String[] splitFullInputIntoLines(String fullInput) {
        String oldLines[] = fullInput.split("[\\r\\n]+");
        String lines[] = new String[oldLines.length];
        for (int i = 0; i < oldLines.length; i++) {
            lines[i] = oldLines[i].trim();
        }
        return lines;
    }

    public static String findSimulatorName(String[] lines) {
        String ns = null;
        for (String line : lines) {
            if (line.startsWith("set") && line.endsWith("[new Simulator]")) {
                ns = line.split(" ")[1];
                break;
            }
        }
        return ns;
    }

    public static List<String> getNodeLines(String[] lines, String ns) {
        List<String> nodeLines = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("set") && line.endsWith("[$" + ns + " node]")) {
                nodeLines.add(line);
            }
        }
        return nodeLines;
    }

    public static List<String> getNodeNames(List<String> nodeLines) {
        List<String> nodes = new ArrayList<>();
        for (String nodeLine : nodeLines) {
            String node = nodeLine.split(" ")[1];
            if (nodes.contains(node)) {
                System.out.println("Repeated node: " + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public static List<String> getMakeLanLines(String[] lines, String ns) {
        List<String> lanLines = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("set") && line.contains("[$" + ns + " make-lan")) {
                lanLines.add(line);
            }
        }
        return lanLines;
    }

    public static List<String> getOsLines(String[] lines, List<String> nodes) {
        List<String> osLines = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("tb-set-node-os")) {
                String[] lineSplit = line.split(" ");
                if (nodes.contains(lineSplit[1].substring(1))) {
                    osLines.add(line);
                } else {
                    System.out.println("Cannot find node which os has been specified for.");
                }
            }
        }
        return osLines;
    }

    public static Map<String, String> getOsMap(List<String> osLines) {
        Map<String, String> osMap = new HashMap<>();
        for (String osLine : osLines) {
            String[] osSplit = osLine.split(" ");
            if (osSplit.length == 3) {
                osMap.put(osSplit[1], osSplit[2]);
            } else {
                System.out.println("Invalid os line.");
            }
        }
        return osMap;
    }

    public static List<Lan> generateLans(List<String> lanLines, List<String> nodes) {
        List<Lan> lans = new ArrayList<>();
        for (String lanLine : lanLines) {
            String name = lanLine.split(" ")[1];
            System.out.println("Lan name: " + name);
            String lanDetails = lanLine.substring(lanLine.indexOf("[") + 1, lanLine.length() - 1);
            String lanClientsString = lanDetails.substring(lanDetails.indexOf("\"") + 1, lanDetails.lastIndexOf("\""));
            String[] lanClientsSplit = lanClientsString.split(" ");
            List<String> lanClients = new ArrayList<>();
            for (String lanClient : lanClientsSplit) {
                lanClient = lanClient.substring(1);
                if (nodes.contains(lanClient)) {
                    if (lanClients.contains(lanClient)) {
                        System.out.println("Repeated client: " + lanClient);
                    } else {
                        lanClients.add(lanClient);
                    }
                } else {
                    System.out.println("Client has not been specified above.");
                }
            }
            System.out.println("Lan clients: " + lanClients);
            String[] lanSplit = lanDetails.split(" ");
            String lanSpeed = lanSplit[lanSplit.length - 2];
            System.out.println("Lan speed: " + lanSpeed);
            String lanDelay = lanSplit[lanSplit.length - 1];
            System.out.println("Lan delay: " + lanDelay);
            lans.add(new Lan(name, lanClients, lanSpeed, lanDelay));
        }
        return lans;
    }

    public static String getPreamble(String ns) {
        String output = "";
        output+= "# ---\n";
        output+= "set " + ns + " [new Simulator]\n";
        output+= "source tb_compat.tcl\n";
        output+= "# ---\n";
        return output;
    }

    public static String getEpilogue(String ns) {
        String output = "";
        output+= "$" + ns + " rtproto Static\n";
        output+= "$" + ns + " run\n";
        output+= "# ---\n";
        return output;
    }

}
