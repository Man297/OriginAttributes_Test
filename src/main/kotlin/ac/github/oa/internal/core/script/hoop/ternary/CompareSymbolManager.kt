package ac.github.oa.internal.core.script.hoop.ternary

import ac.github.oa.internal.core.script.hoop.ternary.CompareSymbolGE


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