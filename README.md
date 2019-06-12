**CHIMERA Inbound**

Inboud for CHIMERA project. 

Scan a directory to take documents, send them to another directory, and publish messages to an AMQP messaging system.

**Docker**

You can use Docker to play with the project :

```
./gradlew docker
```

You can put additional external config in `docker/application-docker.yml`

This build a Docker image : `com.tetragramato/chimera-inbound:latest`

To launch the project, you need an image of Rabbit MQ : `rabbitmq:3-management`

***Docker lauch***

Launch Rabbit MQ on a dedicated Network :

```
docker run
-p 15672:15672 -p 5672:5672
--name rabbit
--network chimera
rabbitmq:3-management 
```

Launch `Chimera-inbound` in the same Network :

You need to provide the path to an `inbound` and an `outbound` folder of your system.

```
docker run
-v /your/path/to/inboud:/directoryIn
-v /your/path/to/outbound:/directoryOut
--name chimera
--network chimera
com.tetragramato/chimera-inbound:latest 
```

You can also simply use the docker-compose to launch the complete stack, present in the docker folder :

You need to provide the path to an `inbound` and an `outbound` folder of your system in the `docker-compose.yml`.

```
docker-compose up
```

***Tests***

After launch, you can put a .txt or .pdf file in the `inbound` folder of your system, and check in the Rabbit admin (http://localhost:15672) that the file is in the queue.