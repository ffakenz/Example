export SEED_NODES="akka://ExampleSystem@0.0.0.0:2551"
export CLUSTER_PORT=2552
export MANAGEMENT_PORT=8552
kill $(lsof -t -i:2552) > /dev/null 2>&1
sbt 'poc/runMain Node'
