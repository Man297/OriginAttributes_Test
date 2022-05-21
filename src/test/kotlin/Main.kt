class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            println(5325 + 1000 / 1.6)

        }

        class Cache(val createStamp: Long, val endStamp: Long) {

            fun progress(): Float {
                val millis = System.currentTimeMillis()
                if (millis > endStamp) return 1.0f
                if (millis == createStamp) return 0.0f
                // 等待秒数 = end - create
                // 已过去秒数 = curr - create
                // 进度 = 已过去描述 / 等待秒数
                return (millis - createStamp).toFloat() / (endStamp - createStamp).toFloat()
            }

        }


    }


}
