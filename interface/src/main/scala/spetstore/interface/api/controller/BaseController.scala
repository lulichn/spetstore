package spetstore.interface.api.controller

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive1, ExceptionHandler, RejectionHandler }
import com.github.j5ik2o.dddbase.AggregateNotFoundException
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import monix.execution.Scheduler
import spetstore.interface.api.model.{ ErrorResponseBody, ResolveUserAccountResponseJson }
import spetstore.interface.api.rejection.MalformedPathRejection

trait BaseController {

  protected def exceptionHandler: ExceptionHandler =
    ExceptionHandler({
      case ex: AggregateNotFoundException =>
        complete(
          (NotFound, ResolveUserAccountResponseJson(Left(ErrorResponseBody(ex.getMessage, "0404"))))
        )
    })

  protected def rejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder().handle {
        case r: MalformedPathRejection =>
          complete((BadRequest, ResolveUserAccountResponseJson(Left(ErrorResponseBody(r.errorMsg, "0400")))))
      }.result()

  protected def extractScheduler: Directive1[Scheduler] = extractActorSystem.tmap { s =>
    Scheduler(s._1.dispatcher)
  }

}
