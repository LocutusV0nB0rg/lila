package lila.irwin

import reactivemongo.api.bson._

import lila.db.dsl._
import lila.db.BSON

object BSONHandlers {

  import IrwinReport._

  implicit private val MoveReportBSONHandler = new BSON[MoveReport] {

    private val activation = "a"
    private val rank       = "r"
    private val ambiguity  = "m"
    private val odds       = "o"
    private val loss       = "l"

    def reads(r: BSON.Reader) =
      MoveReport(
        activation = r intD activation,
        rank = r intO rank,
        ambiguity = r intD ambiguity,
        odds = r intD odds,
        loss = r intD loss
      )

    def writes(w: BSON.Writer, o: MoveReport) =
      BSONDocument(
        activation -> w.intO(o.activation),
        rank       -> o.rank.flatMap(w.intO),
        ambiguity  -> w.intO(o.ambiguity),
        odds       -> w.intO(o.odds),
        loss       -> w.intO(o.loss)
      )
  }

  implicit private val GameReportBSONHandler = Macros.handler[GameReport]
  // private implicit val PvBSONHandler = nullableHandler[Int, BSONInteger]
  // private implicit val ReporterIdBSONHandler = stringIsoHandler[ReporterId](ReporterId.reporterIdIso)
  implicit val ReportBSONHandler = Macros.handler[IrwinReport]

  import KaladinUser.{ Requester, Response }
  implicit private val KaladinRequesterBSONHandler = quickHandler[Requester](
    {
      case BSONString("TournamentLeader") => Requester.TournamentLeader
      case BSONString("TopOnline")        => Requester.TopOnline
      case BSONString("Report")           => Requester.Report
      case BSONString(modId)              => Requester.Mod(modId)
    },
    {
      case Requester.Mod(modId) => BSONString(modId)
      case other                => BSONString(other.name)
    }
  )
  implicit val KaladinResponseBSONHandler = Macros.handler[Response]
  implicit val KaladinUserBSONHandler     = Macros.handler[KaladinUser]
}
