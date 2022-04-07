# KafkaTools

This exemplary tool provides an ability to run kafka diagnostics.

## How to use

This projects uses xerial pack plugin to build

    sbt pack

    cd ./app/target/pack/bin

## Run as consumer
You can select to run project either as consumer or producer (in case of simulating real time data stream).
Running kafkatools in consumer mode will subscribe to the topic and consume messages for 1 minute

    ./kafkatools --bootstrap-server localhost:9092 --topic sometopic

## Run as producer
Running kafkatools in producer mode will publish random string messages into the topic for 10 seconds

    ./kafkatools --bootstrap-server localhost:9092 --topic sometopic --producer

## Local kafka instance
Use docker-compose to run local kafka instance. The broken will be available at **localhost:9092**
    
    docker-compose up
 
## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
