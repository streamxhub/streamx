package com.streamxhub.streamx.test.stream

import com.streamxhub.streamx.common.util.ConfigUtils
import com.streamxhub.streamx.flink.core.java.wrapper.HBaseQuery
import com.streamxhub.streamx.flink.core.scala.request.HBaseRequest
import com.streamxhub.streamx.flink.core.scala.source.HBaseSource
import com.streamxhub.streamx.flink.core.scala.{FlinkStreaming, StreamingContext}
import org.apache.flink.api.scala._
import org.apache.hadoop.hbase.CellUtil
import org.apache.hadoop.hbase.client.{Get, Scan}
import org.apache.hadoop.hbase.util.Bytes

import java.util

object HBaseSourceApp extends FlinkStreaming {

  override def handle(context: StreamingContext): Unit = {

    implicit val conf = ConfigUtils.getHBaseConfig(context.parameter.toMap)

    val id = new HBaseSource(context).getDataStream[String](query => {
      Thread.sleep(10)
      if (query == null) {
        new HBaseQuery("person", new Scan())
      } else {
        //TODO 从上一条记录中获取便宜量,决定下次查询的条件...
        new HBaseQuery("person", new Scan())
      }
    }, r => new String(r.getRow))


    HBaseRequest(id).requestOrdered(x => {
      new HBaseQuery("person", new Get(x.getBytes()))
    }, (a,r) => {
      val map = new util.HashMap[String, String]()
      val cellScanner = r.cellScanner()
      while (cellScanner.advance()) {
        val cell = cellScanner.current()
        val q = Bytes.toString(CellUtil.cloneQualifier(cell))
        val (name, v) = q.split("_") match {
          case Array(_type, name) =>
            _type match {
              case "i" => name -> Bytes.toInt(CellUtil.cloneValue(cell))
              case "s" => name -> Bytes.toString(CellUtil.cloneValue(cell))
              case "d" => name -> Bytes.toDouble(CellUtil.cloneValue(cell))
              case "f" => name -> Bytes.toFloat(CellUtil.cloneValue(cell))
            }
          case _ =>
        }
        map.put(name.toString, v.toString)
      }
      map.toString
    }).print("Async")


  }

}
