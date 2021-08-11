package ac.github.oa.util

import ac.github.oa.OriginAttribute
import org.bukkit.entity.Player
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Supplier
import kotlin.collections.HashMap

object TimeUtil {
    val sdf = SimpleDateFormatUtils()
    private val timeMap: MutableMap<String, Int> = HashMap()

    /**
     * 添加冷却时间
     *
     * @param player Player
     * @param name   String
     * @param times  int
     */
    fun add(player: Player, name: String, times: Int) {
        timeMap[player.name + ":" + name] =
            (System.currentTimeMillis() / 1000).toString().toInt() + times
    }

    /**
     * 获取冷却时间
     *
     * @param player Player
     * @param name   String
     * @return
     */
    private operator fun get(player: Player, name: String): Int {
        val time = timeMap[player.name + ":" + name]
        return if (time != null) time - (System.currentTimeMillis() / 1000).toString().toInt() else -1
    }

    /**
     * 检测是否还在冷却内
     *
     * @param player Player
     * @param name   String
     * @return Boolean
     */
    fun `is`(player: Player, name: String): Boolean {
        if (TimeUtil[player, name] > 0) {
            return true
        }
        timeMap.remove(player.getName() + ":" + name)
        return false
    }

    private fun getThisWeekMonday(date: Date?): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.setTime(date ?: Date())
        // 获得当前日期是一个星期的第几天
        val dayWeek: Int = cal.get(Calendar.DAY_OF_WEEK)
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY)
        // 获得当前日期是一个星期的第几天
        val day: Int = cal.get(Calendar.DAY_OF_WEEK)
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day)
        return cal.getTime()
    }

    /**
     * 获取准确的时间
     *
     * @param date   Date / null
     * @param day    星期几 / null
     * @param hour   小时 / -1
     * @param minute 分钟 / -1
     * @return Date
     */
    fun getDayTime(date: Date?, day: Int?, hour: Int, minute: Int): Date {
        var date = date
        date = date ?: Date()
        val cal: Calendar = Calendar.getInstance()
        if (day != null) {
            cal.setTime(getThisWeekMonday(date))
            cal.add(Calendar.DATE, day - 1)
        } else {
            cal.setTime(date)
        }
        if (hour > -1) {
            cal.set(Calendar.HOUR_OF_DAY, hour)
        }
        if (minute > -1) {
            cal.set(Calendar.MINUTE, minute)
        }
        cal.set(Calendar.SECOND, 0)
        return cal.getTime()
    }

    class SimpleDateFormatUtils {
        private val threadLocal: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial(
            Supplier { SimpleDateFormat(OriginAttribute.config.getString("options.time-format")) })

        @Throws(ParseException::class)
        fun parseFormDate(dateStr: String?): Date {
            return threadLocal.get().parse(dateStr)
        }

        @Throws(ParseException::class)
        fun parseFormLong(dateStr: String?): Long {
            return threadLocal.get().parse(dateStr).getTime()
        }

        fun format(date: Long?): String {
            return threadLocal.get().format(date)
        }

        fun format(date: Date?): String {
            return threadLocal.get().format(date)
        }

        fun reload() {
            threadLocal.remove()
        }
    }
}