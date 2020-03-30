package utils

import org.reflections.Reflections

import scala.jdk.CollectionConverters._
import scala.reflect._

object Inference {
  // https://github.com/mockito/mockito-scala/issues/117#issuecomment-499654664
  def getSimpleName(name: String) = {
    val withoutDollar = name.split("\\$").lastOption.getOrElse(name)
    val withoutDot =
      withoutDollar.split("\\.").lastOption.getOrElse(withoutDollar)
    withoutDot
  }

  def getSubtypesOf[C: ClassTag] = {
    def getSubtypesOf[C: ClassTag](packageName: String) = {

      val javaSet =
        new Reflections(packageName) // search for subclasses will be performed inside the 'model' package
          .getSubTypesOf(classTag[C].runtimeClass)
      javaSet.asScala.toSet
    }

    val packages = Set("model", "readside", "serialization", "cassandra")
    packages.flatMap { packageName =>
      getSubtypesOf[C](packageName)
    }
  }

  def instantiate[T](clazz: java.lang.Class[_])(args: AnyRef*): T = {
    val constructor = clazz.getConstructors()(0)
    constructor.newInstance(args: _*).asInstanceOf[T]
  }

  def methodOf[Trait: ClassTag, Expected](methodName: String): Set[Expected] = {
    val klasses = getSubtypesOf[Trait]

    klasses.map { klass =>
      klass
        .getDeclaredMethod(methodName)
        .invoke(klass.getDeclaredConstructor().newInstance())
        .asInstanceOf[Expected]
    }
  }

  def getSubtypesNames[C: ClassTag] =
    getSubtypesOf[C]
      .map(_.getName)
      .map {
        getSimpleName
      }

}
