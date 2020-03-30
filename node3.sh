export SEED_NODES="akka://ExampleSystem@0.0.0.0:2551"
export CLUSTER_PORT=2553
export MANAGEMENT_PORT=8553
kill $(lsof -t -i:2553) > /dev/null 2>&1
sbt 'poc/runMain Node'
