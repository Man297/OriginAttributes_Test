package ac.github.oa.internal.core.item.script.hoop.ternary


object CompareSymbolManager {

    val symbols = arrayListOf(
        CompareSymbolEQ, CompareSymbolGE, CompareSymbolGT, CompareSymbolLE, CompareSymbolLT, CompareSymbolNE
    )

//    @Awake(LifeCycle.ENABLE)
//    fun load() {
//        runningClasses.forEach {
//            info(it)
//            if (CompareSymbol::class.java.isAssignableFrom(it)) {
//                val any = it.getInstance()?.get() ?: return
//                symbols += any as CompareSymbol
//            }
//        }
//    }

}