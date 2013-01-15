package sabre.util

import com.typesafe.config.ConfigFactory
import java.io.File
import scala.io.Source

object ParseConfig {
  val config = ConfigFactory.parseFile(new File("sabre.conf")).resolve()
}
