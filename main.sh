export SEED_NODES="akka://ExampleSystem@0.0.0.0:2551"
export CLUSTER_PORT=2554
export MANAGEMENT_PORT=8554
kill $(lsof -t -i:2554) > /dev/null 2>&1
sbt 'poc/runMain Main'
