export SEED_NODES="akka://ExampleSystem@0.0.0.0:2551"
export CLUSTER_PORT=2551
export MANAGEMENT_PORT=8551
kill $(lsof -t -i:2551) > /dev/null 2>&1
sbt 'poc/runMain Node'
