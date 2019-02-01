package org.mydotey.tool.kafka.ops;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.mydotey.java.io.file.FileExtension;
import org.mydotey.tool.kafka.ops.Assignments;
import org.mydotey.tool.kafka.ops.Brokers;
import org.mydotey.tool.kafka.ops.Clients;

import com.google.common.collect.Sets;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author koqizhao
 *
 * Dec 10, 2018
 */
public class BrokerDataTransfer {

    private static final String KEY_FROM = "from";
    private static final String KEY_TOS = "tos";
    private static final String KEY_ACTION = "action";
    private static final String KEY_FILE = "file";
    private static final String KEY_INTER_BROKER_LIMIT = "inter-broker-limit";
    private static final String KEY_TOPIC = "topic";

    private static final String ACTION_GENERATE = "generate";
    private static final String ACTION_EXECUTE = "execute";

    private static ArgumentParser _argumentParser = ArgumentParsers.newFor(BrokerDataTransfer.class.getSimpleName())
            .build();

    static {
        _argumentParser.addArgument("-bs", "--" + Clients.KEY_BOOTSTRAP_SERVERS).required(true);
        _argumentParser.addArgument("-zk", "--" + Clients.KEY_ZK_CONNECT).required(true);
        _argumentParser.addArgument("-f", "--" + KEY_FROM).type(Integer.class).required(true);
        _argumentParser.addArgument("-t", "--" + KEY_TOS).type(ArgumentTypes.LIST_INTEGER).required(true);
        _argumentParser.addArgument("-a", "--" + KEY_ACTION).choices(ACTION_GENERATE, ACTION_EXECUTE)
                .setDefault(ACTION_GENERATE);
        _argumentParser.addArgument("--" + KEY_FILE).setDefault("assignments.json");
        _argumentParser.addArgument("-ibl", "--" + KEY_INTER_BROKER_LIMIT).type(Long.class).dest(KEY_INTER_BROKER_LIMIT)
                .setDefault(Assignments.DEFAULT_REASSIGN_THROTTLE_LIMIT);
        _argumentParser.addArgument(KEY_TOPIC).nargs("*");
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
        int from = ns.getInt(KEY_FROM);
        List<Integer> tos = ns.getList(KEY_TOS);
        String action = ns.get(KEY_ACTION);
        String file = ns.get(KEY_FILE);
        long interBrokerLimit = ns.get(KEY_INTER_BROKER_LIMIT);
        Set<String> topics = ns.getList(KEY_TOPIC) == null ? null : Sets.newHashSet(ns.getList(KEY_TOPIC));
        System.out.printf(
                "arguments:\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\t%s: %s\n\n",
                Clients.KEY_BOOTSTRAP_SERVERS, bootstrapServers, Clients.KEY_ZK_CONNECT, zkConnect, KEY_FROM, from,
                KEY_TOS, tos, KEY_ACTION, action, KEY_FILE, file, KEY_INTER_BROKER_LIMIT, interBrokerLimit, KEY_TOPIC,
                topics);

        try (Clients clients = new Clients(properties)) {
            Brokers brokers = new Brokers(clients);
            Assignments assignments = new Assignments(clients);
            if (topics == null || topics.isEmpty())
                topics = brokers.getTopics(from);

            Map<String, Map<Integer, List<Integer>>> currentAssignmentsMap = brokers.getAssignments(from, topics);
            Map<String, Map<Integer, List<Integer>>> newAssignmentsMap = brokers.generateAssignmentsForTransfer(from,
                    tos, topics);
            String currentAssignmentsJson = assignments.toJson(currentAssignmentsMap);
            String newAssignmentsJson = assignments.toJson(newAssignmentsMap);
            System.out.printf("\ncurrent assignments: \n%s\n\nnew assignments:\n%s\n\n", currentAssignmentsJson,
                    newAssignmentsJson);

            String fileName = file.endsWith(".json") ? file.substring(0, file.length() - ".json".length()) : file;
            String oldFileName = fileName + ".old.json";
            String newFileName = fileName + ".new.json";
            FileExtension.writeFileContent(Paths.get(oldFileName), currentAssignmentsJson);
            FileExtension.writeFileContent(Paths.get(newFileName), newAssignmentsJson);
            System.out.printf("saved assignments to files: \n\t%s\n\t%s\n\n", oldFileName, newFileName);

            switch (action) {
                case ACTION_EXECUTE:
                    assignments.reassign(newAssignmentsMap, interBrokerLimit,
                            Assignments.DEFAULT_REASSIGN_THROTTLE_LIMIT, Assignments.DEFAULT_REASSIGN_TIMEOUT);
                    System.out.println();
                    break;
                default:
                    break;
            }
        }
    }

}
