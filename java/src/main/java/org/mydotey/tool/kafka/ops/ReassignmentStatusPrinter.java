package org.mydotey.tool.kafka.ops;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mydotey.java.io.file.FileExtension;
import org.mydotey.tool.kafka.ops.Assignments;
import org.mydotey.tool.kafka.ops.Assignments.Status;
import org.mydotey.tool.kafka.ops.Clients;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author koqizhao
 *
 * Dec 10, 2018
 */
public class ReassignmentStatusPrinter {

    private static final String KEY_FILE = "file";

    private static ArgumentParser _argumentParser = ArgumentParsers
            .newFor(ReassignmentStatusPrinter.class.getSimpleName()).build();

    static {
        _argumentParser.addArgument("-bs", "--" + Clients.KEY_BOOTSTRAP_SERVERS).required(true);
        _argumentParser.addArgument("-zk", "--" + Clients.KEY_ZK_CONNECT).required(true);
        _argumentParser.addArgument("-f", "--" + KEY_FILE).required(true);
    }

    public static void main(String[] args) throws Exception {
        Namespace ns;
        try {
            ns = _argumentParser.parseArgs(args);
        } catch (HelpScreenException e) {
            return;
        }

        Properties properties = new Properties();
        String bootstrapServers = ns.get(Clients.KEY_BOOTSTRAP_SERVERS);
        properties.put(Clients.KEY_BOOTSTRAP_SERVERS, bootstrapServers);
        String zkConnect = ns.get(Clients.KEY_ZK_CONNECT);
        properties.put(Clients.KEY_ZK_CONNECT, zkConnect);
        String file = ns.get(KEY_FILE);
        System.out.printf("arguments:\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\n", Clients.KEY_BOOTSTRAP_SERVERS,
                bootstrapServers, Clients.KEY_ZK_CONNECT, zkConnect, KEY_FILE, file);
        String assignmentJson = FileExtension.readFileContent(Paths.get(file));

        try (Clients clients = new Clients(properties)) {
            Assignments assignments = new Assignments(clients);
            Map<String, Map<Integer, List<Integer>>> assignmentsMap = assignments.fromJson(assignmentJson);
            System.out.printf("\nassignments: \n%s\n\n", assignments.toJson(assignmentsMap));
            Map<String, Map<Integer, Status>> assignmentsStatus = assignments.verifyAssignment(assignmentsMap);
            System.out.printf("assignments status: \n", assignmentsMap);
            assignmentsStatus.forEach((t, ps) -> {
                ps.forEach((p, s) -> {
                    System.out.printf("\ttopic: %s, partition: %s, status: %s\n", t, p, s);
                });
            });
            System.out.println();
        }
    }

}
