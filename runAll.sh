docker-compose -f ./assets/docker-compose.yml down -v
docker-compose -f ./assets/docker-compose.yml up -d cassandra


sh node1.sh &
sh node2.sh &
sh node3.sh &

sleep 100

sh main.sh &
