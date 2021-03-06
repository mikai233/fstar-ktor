package com.mikai233.client

import com.mikai233.orm.Course
import com.mikai233.orm.Score
import io.ktor.client.call.*
import io.ktor.client.statement.*
import org.jsoup.Jsoup
import java.nio.charset.Charset

/**
 * @author mikai233(dairch)
 * @date 2021/9/27
 */

fun String.parseCourse(): Pair<List<Course>, String> {
    val document = Jsoup.parse(this)
    val courses = mutableListOf<Course>()
    val trs = document.select("#kbtable tr")
    val remark = trs.last()?.text() ?: ""
    trs.dropLast(1).drop(1).forEachIndexed { rowIndex, tr ->
        tr.select("td .kbcontent").forEachIndexed { columnIndex, element ->
            courseParser(element.html(), rowIndex + 1, columnIndex + 1).let {
                courses.addAll(it)
            }
        }
    }
    return courses to remark
}

fun String.parseScore(alternative: Boolean = false): List<Score> {
    val document = Jsoup.parse(this)
    val scores = mutableListOf<Score>()
    val trs = document.select("#dataList tr")
    if (trs.isNotEmpty()) {
        trs.drop(1).forEach { element ->
            element.select("td").map { it.text() }.takeIf { it.isNotEmpty() }?.let { attrs ->
                if (!alternative)
                    Score(
                        id = -1,
                        studentNumber = "",
                        no = attrs[0],
                        semester = attrs[1],
                        scoreNo = attrs[2],
                        name = attrs[3],
                        score = attrs[4],
                        credit = attrs[5],
                        period = attrs[6],
                        evaluationMode = attrs[7],
                        courseProperty = attrs[8],
                        courseNature = attrs[9],
                        alternativeCourseNumber = attrs[10],
                        alternativeCourseName = attrs[11],
                        scoreFlag = attrs[12],
                    ).let {
                        scores.add(it)
                    }
                else
                    Score(
                        id = -1,
                        studentNumber = "",
                        no = attrs[0],
                        semester = attrs[1],
                        scoreNo = attrs[2],
                        name = attrs[3],
                        score = attrs[4],
                        credit = attrs[5],
                        period = "",
                        evaluationMode = attrs[6],
                        courseProperty = attrs[7],
                        courseNature = "",
                        alternativeCourseNumber = "",
                        alternativeCourseName = "",
                        scoreFlag = "",
                    ).let {
                        scores.add(it)
                    }
            }
        }
    }
    return scores
}

fun courseParser(courseInfo: String, row: Int, column: Int): List<Course> {
    val courses = mutableListOf<Course>()
    courseInfo.replace("\n", "").split(Regex("-{5,}<br>")).forEach { info ->
        val regex = Regex("(.*?)<br>(.*?)<br><.*?>(.*?)</font>.*?\">(.*?)\\(???\\)")
        val roomRegex = Regex("<font title=\"??????\">(.*?)</font>")
        val room = roomRegex.find(info)?.groupValues?.get(1) ?: ""
        regex.findAll(info).forEach { detail ->
            val groupValues = detail.groupValues
            Course(
                id = groupValues[1],
                name = groupValues[2],
                classRoom = room,
                week = weekParser(groupValues[4]),
                teacher = groupValues[3],
                row = row,
                column = column,
                rowSpan = 2,
            ).let {
                courses.add(it)
            }
        }
    }
    return courses
}

fun weekParser(rawWeek: String): List<Int> {
    val weeks = mutableSetOf<Int>()
    rawWeek.split(",").forEach { eachPart ->
        if (eachPart.indexOf('-') != -1) {
            val (begin, end) = eachPart.split("-")
            for (week in begin.toInt()..end.toInt()) {
                weeks.add(week)
            }
        } else {
            weeks.add(eachPart.toInt())
        }
    }
    return weeks.toList()
}

fun String.parseSportScore(): List<Map<String, String>> {
    val document = Jsoup.parse(this).also {
        it.charset(Charset.forName("gbk"))
    }
    val scores = mutableListOf<Map<String, String>>()
    val scoreElements = document.select("#autonumber1")[1].select("tr").drop(1)
    scoreElements.forEach { scoreElement ->
        val scoreAttrs = scoreElement.select("td")
        val attrs = mutableListOf<String>()
        scoreAttrs.forEachIndexed { index, element ->
            if (index != 7) {
                attrs.add(element.text().trim().replace("\u3000", ""))
            } else {
                val remark = element.select("input").attr("onclick")
                attrs.add(remark)
            }
            val details = attrs[7].split("\n").dropLast(1).toMutableList()
            details[0] = details[0].substring(10)
            val detailString = details.joinToString("\n") { it.trim() }
            mapOf(
                "semester" to details[0],
                "special" to details[1],
                "time" to details[2],
                "teacher" to details[3],
                "name" to details[4],
                "score" to details[5],
                "evaluate" to details[6],
                "detail" to detailString,
                "remark" to details[8],
            ).let {
                scores.add(it)
            }
        }
    }
    return scores
}

fun String.parseSportClub() {
    val document = Jsoup.parse(this).also {
        it.charset(Charset.forName("gbk"))
    }
    val (trs, remark) = with(document.select("table tr")) {
        subList(1, size - 1) to (last()?.text() ?: "")
    }
    val regex = Regex("<td>1</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td>")
}

suspend fun HttpResponse.parseVpn2LoginParameter(): HashMap<String, String> {
    val document = Jsoup.parse(receive<String>())
    val parameters = hashMapOf<String, String>()
    val form = document.select("#fm1")
    parameters["loginUrl"] = request.url.toString()
    form.select("input").forEach { element ->
        parameters[element.attr("name")] = element.attr("value")
    }
    return parameters
}
