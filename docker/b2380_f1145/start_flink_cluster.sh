docker network remove tfn
docker network create tfn
docker run -d --rm --name=jm --network tfn --publish 8081:8081 --env FLINK_PROPERTIES="jobmanager.rpc.address: jm" flink:1.13.6-scala_2.12 jobmanager
docker run -d --rm --name=tm1 --network tfn --env FLINK_PROPERTIES="jobmanager.rpc.address: jm" flink:1.13.6-scala_2.12 taskmanager
docker run -d --rm --name=tm2 --network tfn --env FLINK_PROPERTIES="jobmanager.rpc.address: jm" flink:1.13.6-scala_2.12 taskmanager
docker run -d --rm --name=tm3 --network tfn --env FLINK_PROPERTIES="jobmanager.rpc.address: jm" flink:1.13.6-scala_2.12 taskmanager