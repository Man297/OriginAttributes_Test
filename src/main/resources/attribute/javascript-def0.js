/**
 * 处理器
 * @param memory DamageMemory 伤害处理 UpdateMemory 更新处理
 * @param data 数据
 *
 * API(api)
 *  fun tell(entity,message) 打印到文本框
 *  fun info(message) 打印到控制台
 *  fun chance(value) : boolean 产生概率 值要求0-1内的数
 *  fun getData(context,entity,attributeName) : Data 取属性 context = memory
 *
 * Data :
 *  fun get(index) 取值
 *  fun random() 取0下标1下标中间随机值
 *
 * DamageMember
 *
 *  val attacker 攻击者
 *  val injured 被攻击者
 *  var damage 伤害
 *  val totalDamage 总伤害
 *  val event : Event
 *  fun getDamageSources() : List 数据来源
 *
 *
 *  Event:
 *    val cause 伤害来源
 *
 */

function handler(memory, data) {
    api.info("Hello javascript attribute.")
}
