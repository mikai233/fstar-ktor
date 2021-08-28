@file:Suppress("DuplicatedCode", "unused")

package com.mikai233.service

import com.mikai233.orm.DB
import com.mikai233.orm.Score
import com.mikai233.orm.Scores
import com.mikai233.tool.asyncIO
import org.ktorm.dsl.*
import org.ktorm.entity.*

/**
 * @author mikai233
 * @email dreamfever2017@yahoo.com
 * @date 2021/8/27
 */

class ScoreService {
    suspend fun getScoreById(id: Int) = DB.asyncIO {
        scores.find { it.id eq id }
    }

    suspend fun getScoresByStudentNumber(number: String) = DB.asyncIO {
        scores.filter { it.studentNumber eq number }.toList()
    }

    suspend fun getScoresByClassNumber(number: String) = DB.asyncIO {
        scores.filter { it.studentNumber like "$number%" }.toList()
    }

    suspend fun getScoresByPage(page: Int, size: Int) = DB.asyncIO {
        require(page > 0) { "page: $page must > 0" }
        require(size >= 0) { "size: $size must >= 0" }
        scores.drop((page - 1) * size).take(size).toList()
    }

    suspend fun addScore(score: Score) = DB.asyncIO {
        database.insert(Scores) {
            with(score) {
                set(Scores.studentNumber, studentNumber)
                set(Scores.no, no)
                set(Scores.semester, semester)
                set(Scores.scoreNo, scoreNo)
                set(Scores.name, name)
                set(Scores.score, this.score)
                set(Scores.credit, credit)
                set(Scores.period, period)
                set(Scores.evaluationMode, evaluationMode)
                set(Scores.courseProperty, courseProperty)
                set(Scores.courseNature, courseNature)
                set(Scores.alternativeCourseNumber, alternativeCourseNumber)
                set(Scores.alternativeCourseName, alternativeCourseName)
                set(Scores.scoreFlag, scoreFlag)
            }
        }
    }

    suspend fun addScores(scores: List<Score>) = DB.asyncIO {
        database.batchInsert(Scores) {
            scores.forEach { score ->
                item {
                    with(score) {
                        set(Scores.studentNumber, studentNumber)
                        set(Scores.no, no)
                        set(Scores.semester, semester)
                        set(Scores.scoreNo, scoreNo)
                        set(Scores.name, name)
                        set(Scores.score, this.score)
                        set(Scores.credit, credit)
                        set(Scores.period, period)
                        set(Scores.evaluationMode, evaluationMode)
                        set(Scores.courseProperty, courseProperty)
                        set(Scores.courseNature, courseNature)
                        set(Scores.alternativeCourseNumber, alternativeCourseNumber)
                        set(Scores.alternativeCourseName, alternativeCourseName)
                        set(Scores.scoreFlag, scoreFlag)
                    }
                }
            }
        }
    }

    suspend fun updateScore(score: Score) = DB.asyncIO {
        database.update(Scores) {
            where { (it.studentNumber eq score.studentNumber) and (it.scoreNo eq score.scoreNo) }
            with(score) {
                set(Scores.no, no)
                set(Scores.semester, semester)
                set(Scores.name, name)
                set(Scores.score, this.score)
                set(Scores.credit, credit)
                set(Scores.period, period)
                set(Scores.evaluationMode, evaluationMode)
                set(Scores.courseProperty, courseProperty)
                set(Scores.courseNature, courseNature)
                set(Scores.alternativeCourseNumber, alternativeCourseNumber)
                set(Scores.alternativeCourseName, alternativeCourseName)
                set(Scores.scoreFlag, scoreFlag)
            }
        }
    }

    suspend fun deleteScoreById(id: Int) = DB.asyncIO {
        database.delete(Scores) {
            it.id eq id
        }
    }

    suspend fun deleteScoresByStudentNumber(number: String) = DB.asyncIO {
        database.delete(Scores) {
            it.studentNumber eq number
        }
    }

    suspend fun deleteAllScores() = DB.asyncIO {
        database.deleteAll(Scores)
    }
}