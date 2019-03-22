# Kafka Ops Tools

A few Kafka ops tools.

目录：

- [Download](#download)
- [Script Arguments](#script-arguments)
- [broker-data-transfer.sh](#broker-data-transfersh)
- [topic-partition-transfer.sh](#topic-partition-transfersh)
- [reassignment-status-printer.sh](#reassignment-status-printersh)

## Download

All tools are in folder ./bin.

## Script Arguments

-h, --help             show this help message and exit

-bs BOOTSTRAP.SERVERS, --bootstrap.servers BOOTSTRAP.SERVERS, like: 192.168.56.101:9092[,192.168.56.102:9092]

-zk ZOOKEEPER.CONNECT, --zookeeper.connect ZOOKEEPER.CONNECT, like: 192.168.56.101:2181[,192.168.56.102:2181]

-f FROM, --from FROM, the from broker id, like: 1

-t TOS, --tos TOS, the to brokers ids, like: 2[,3, ...]

-a {generate,execute}, --action {generate,execute}, like: generate

--file FILE, save the reassignments to a json file, like: reassignments.json

-ibl INTER-BROKER-LIMIT, --inter-broker-limit INTER-BROKER-LIMIT, transfer speed limit, -1 for unlimited, like: 2000000000

## broker-data-transfer.sh

Transfer data (1 topic or multiple topics or all topics) from 1 broker to anothers (1 or multiple brokers).

usage:

```sh
./broker-data-transfer.sh -h
usage: BrokerDataTransfer [-h] -bs BOOTSTRAP.SERVERS -zk ZOOKEEPER.CONNECT
                          -f FROM -t TOS [-a {generate,execute}]
                          [--file FILE] [-ibl INTER-BROKER-LIMIT]
                          [topic [topic ...]]

positional arguments:
  topic

named arguments:
  -h, --help             show this help message and exit
  -bs BOOTSTRAP.SERVERS, --bootstrap.servers BOOTSTRAP.SERVERS
  -zk ZOOKEEPER.CONNECT, --zookeeper.connect ZOOKEEPER.CONNECT
  -f FROM, --from FROM
  -t TOS, --tos TOS
  -a {generate,execute}, --action {generate,execute}
  --file FILE
  -ibl INTER-BROKER-LIMIT, --inter-broker-limit INTER-BROKER-LIMIT
```

If no topic provided, transfer all the topics on the -f broker.

generate reassignment:

```sh
./broker-data-transfer.sh -bs 192.168.56.101:9092,192.168.56.102:9092 -zk 192.168.56.101:2181,192.168.56.102:2181 -f 1 -t 2,3 -file reassignments.json -a generate -ibl -1 test-topic1
```

execute reassignment:

```sh
./broker-data-transfer.sh -bs 192.168.56.101:9092,192.168.56.102:9092 -zk 192.168.56.101:2181,192.168.56.102:2181 -f 1 -t 2,3 -file reassignments.json -a execute -ibl -1 test-topic1
```

## topic-partition-transfer.sh

Transfer some partitions (1 or multiple or all) of 1 topic from 1 broker to anothers (1 or multiple brokers).

usage:

```sh
./topic-partition-transfer.sh -h
usage: TopicPartitionTransfer [-h] -bs BOOTSTRAP.SERVERS
                              -zk ZOOKEEPER.CONNECT -f FROM -t TOS
                              [-a {generate,execute}] [--file FILE]
                              [-ibl INTER-BROKER-LIMIT] --topic TOPIC
                              [partition [partition ...]]

positional arguments:
  partition

named arguments:
  -h, --help             show this help message and exit
  -bs BOOTSTRAP.SERVERS, --bootstrap.servers BOOTSTRAP.SERVERS
  -zk ZOOKEEPER.CONNECT, --zookeeper.connect ZOOKEEPER.CONNECT
  -f FROM, --from FROM
  -t TOS, --tos TOS
  -a {generate,execute}, --action {generate,execute}
  --file FILE
  -ibl INTER-BROKER-LIMIT, --inter-broker-limit INTER-BROKER-LIMIT
  --topic TOPIC
```

If no partiton provided, transfer all the partitions on the -f broker.

generate reassignment:

```sh
./topic-partition-transfer.sh -bs 192.168.56.101:9092,192.168.56.102:9092 -zk 192.168.56.101:2181,192.168.56.102:2181 -f 1 -t 2,3 -file reassignments.json -a generate -ibl -1 -topic test-topic1 1 2 3
```

execute reassignment:

```sh
./topic-partition-transfer.sh -bs 192.168.56.101:9092,192.168.56.102:9092 -zk 192.168.56.101:2181,192.168.56.102:2181 -f 1 -t 2,3 -file reassignments.json -a execute -ibl -1 -topic test-topic1 1 2 3
```

## reassignment-status-printer.sh

Show the reassignment process.

usage:

```sh
./reassignment-status-printer.sh -h
usage: ReassignmentStatusPrinter
       [-h] -bs BOOTSTRAP.SERVERS -zk ZOOKEEPER.CONNECT -f FILE

named arguments:
  -h, --help             show this help message and exit
  -bs BOOTSTRAP.SERVERS, --bootstrap.servers BOOTSTRAP.SERVERS
  -zk ZOOKEEPER.CONNECT, --zookeeper.connect ZOOKEEPER.CONNECT
  -f FILE, --file FILE
```

example:

```sh
./reassignment-status-printer.sh -bs 192.168.56.101:9092,192.168.56.102:9092 -zk 192.168.56.101:2181,192.168.56.102:2181 -f reassignments.json
```
