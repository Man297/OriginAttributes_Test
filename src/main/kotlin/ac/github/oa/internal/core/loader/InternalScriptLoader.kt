package ac.github.oa.internal.core.loader

import ac.github.oa.internal.core.script.InternalScript
import ac.github.oa.internal.core.item.ItemGenerator
import ac.github.oa.internal.core.item.ItemPlant
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.util.function.Supplier

@Awake(LifeCycle.INIT)
object InternalScriptLoader : Injector.Classes {

    override val lifeCycle: LifeCycle
        get() = LifeCycle.INIT
    override val priority: Byte
        get() = 1

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        val any = instance.get()
        if (InternalScript::class.isInstance(any)) {
            (any as InternalScript<*>).register()
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {}
}

@Awake(LifeCycle.INIT)
object ItemGeneratorLoader : Injector.Classes {

    override val lifeCycle: LifeCycle
        get() = LifeCycle.INIT
    override val priority: Byte
        get() = 1

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        val any = instance.get()
        if (ItemGenerator::class.isInstance(any)) {
            val generator = any as ItemGenerator
            ItemPlant.generators.add(generator)
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {}
}