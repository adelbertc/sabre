package sabre.util

import com.typesafe.config.ConfigFactory
import java.io.File

object ParseConfig {
  val config = ConfigFactory.parseFile(new File("sabre.conf")).resolve()
}
