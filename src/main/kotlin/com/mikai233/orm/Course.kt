package com.mikai233.orm

/**
 * @author mikai233(dairch)
 * @date 2021/9/27
 */

data class Course(
    val id:String,
    val name: String,
    val classRoom: String,
    val week: List<Int>,
    val row: Int,
    val rowSpan: Int,
    val column: Int,
    val teacher: String
)