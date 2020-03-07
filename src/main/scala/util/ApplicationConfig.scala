package util

import com.typesafe.config.ConfigFactory

object ApplicationConfig {
  val config = ConfigFactory.load("application.conf")


  val apiURL = config.getString("api.url")
  val apiKey = config.getString("api.key")
  val apiUriMovieDiscover = config.getString("api.uri.movie.discover")
  val apiUriTvDiscover = config.getString("api.uri.tv.discover")
  val apiUriMovieCredit = config.getString("api.uri.movie.cast")
  val apiUriTveCredit = config.getString("api.uri.tv.cast")
}

