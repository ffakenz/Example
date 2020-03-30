import sbt._

object Dependencies {
  // Versions
  lazy val scalaVersion = "2.13.1"
  private lazy val akkaVersion = "2.6.1"

  // Resolvers
  lazy val commonResolvers = Seq(
    Resolver sonatypeRepo "public",
    Resolver typesafeRepo "releases",
    Resolver.bintrayRepo("tanukkii007", "maven"),
    // for Embedded Kafka 2.4.0
    Resolver.bintrayRepo("seglo", "maven"),
    // the library is available in Bintray repository
    "dnvriend" at "http://dl.bintray.com/dnvriend/maven"
  )

  // Modules
  trait Module {
    def modules: Seq[ModuleID]
  }

  object Akka extends Module {
    val akkaHttpVersion = "10.1.11"
    val akkaManagementVersion = "1.0.3"

    private def akkaModule(name: String) = "com.typesafe.akka" %% name % akkaVersion
    private lazy val SBR = "com.github.TanUkkii007" %% "akka-cluster-custom-downing" % "0.0.13"
    private lazy val akkaStreamKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.4"

    override def modules: Seq[ModuleID] =
      akkaModule("akka-cluster-tools") ::
      akkaModule("akka-remote") ::
      akkaModule("akka-discovery") ::
      akkaModule("akka-persistence-query") ::
      akkaModule("akka-actor") ::
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion ::
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion ::
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion ::
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion ::
      "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion ::
      "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion ::
      "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion ::
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion ::
      SBR :: akkaStreamKafka ::
      Nil
  }

  object Cassandra extends Module {
    lazy val AkkaPersistenceCassandraVersion = "0.101"
    private def akkaPersistenceCassandraModule(name: String) =
      "com.typesafe.akka" %% name % AkkaPersistenceCassandraVersion

    override def modules: Seq[sbt.ModuleID] =
      akkaPersistenceCassandraModule("akka-persistence-cassandra") ::
      akkaPersistenceCassandraModule("akka-persistence-cassandra-launcher") ::
      Nil
  }

  object Utils extends Module {
    private lazy val logbackVersion = "1.2.3"

    private lazy val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
    private lazy val logbackEncoder = "net.logstash.logback" % "logstash-logback-encoder" % "5.3"
    private lazy val kryo = "io.altoo" %% "akka-kryo-serialization" % "1.1.0" //"com.twitter" %% "chill-akka" % kryoVersion
    private lazy val commonsIO = "commons-io" % "commons-io" % "2.6"
    private lazy val reflections = "org.reflections" % "reflections" % "0.9.10"
    private lazy val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"
    private lazy val heikoseeberger = "de.heikoseeberger" %% "akka-http-play-json" % "1.30.0"
    private lazy val scribe = "com.outr" %% "scribe" % "2.7.10"
    private lazy val scribeLogstash = "com.outr" %% "scribe-logstash" % "2.7.10"

    override def modules: Seq[ModuleID] =
      logback ::
      logbackEncoder ::
      kryo ::
      commonsIO ::
      reflections ::
      shapeless ::
      heikoseeberger ::
      scribe ::
      scribeLogstash ::
      Nil
  }

  // Projects
  lazy val mainDeps = Akka.modules ++ Cassandra.modules ++ Utils.modules
  lazy val testDeps = Seq.empty[ModuleID]
}

trait Dependencies {
  val scalaVersionUsed = Dependencies.scalaVersion
  val commonResolvers = Dependencies.commonResolvers
  val mainDeps = Dependencies.mainDeps
  val testDeps = Dependencies.testDeps
}
