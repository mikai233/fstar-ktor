package com.mikai233.orm

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/27
 */

data class Score(
    val id: Int,
    val studentNumber: String,
    val no: String,
    val semester: String,
    val scoreNo: String,
    val name: String,
    val score: String,
    val credit: String,
    val period: String,
    val evaluationMode: String,
    val courseProperty: String,
    val courseNature: String,
    val alternativeCourseNumber: String,
    val alternativeCourseName: String,
    val scoreFlag: String
)

object Scores : BaseTable<Score>("score") {
    val id = int("id").primaryKey()
    val studentNumber = varchar("student_number")
    val no = varchar("no")
    val semester = varchar("semester")
    val scoreNo = varchar("score_no")
    val name = varchar("name")
    val score = varchar("score")
    val credit = varchar("credit")
    val period = varchar("period")
    val evaluationMode = varchar("evaluation_mode")
    val courseProperty = varchar("course_property")
    val courseNature = varchar("course_nature")
    val alternativeCourseNumber = varchar("alternative_course_number")
    val alternativeCourseName = varchar("alternative_course_name")
    val scoreFlag = varchar("score_flag")
    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) =
        Score(
            id = row[id] ?: 0,
            studentNumber = row[studentNumber].orEmpty(),
            no = row[no].orEmpty(),
            semester = row[semester].orEmpty(),
            scoreNo = row[scoreNo].orEmpty(),
            name = row[name].orEmpty(),
            score = row[score].orEmpty(),
            credit = row[credit].orEmpty(),
            period = row[period].orEmpty(),
            evaluationMode = row[evaluationMode].orEmpty(),
            courseProperty = row[courseProperty].orEmpty(),
            courseNature = row[courseNature].orEmpty(),
            alternativeCourseNumber = row[alternativeCourseNumber].orEmpty(),
            alternativeCourseName = row[alternativeCourseName].orEmpty(),
            scoreFlag = row[scoreFlag].orEmpty()
        )
}