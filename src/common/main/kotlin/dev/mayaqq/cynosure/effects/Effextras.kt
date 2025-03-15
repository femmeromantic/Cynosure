package dev.mayaqq.cynosure.effects

import net.minecraft.world.effect.MobEffect

/**
 * A collection of extra properties for MobEffects.
 */
public object Effextras {
    /**
     * Any Effect in this set will not call onRemove on status effect update.
     */
    public var updateless: MutableSet<MobEffect> = mutableSetOf()

    public fun isUpdateless(effect: MobEffect): Boolean = updateless.contains(effect)
    public fun setUpdateless(effect: MobEffect): Boolean = updateless.add(effect)
}

public fun MobEffect.isUpdateless(): Boolean = Effextras.updateless.contains(this)
public fun MobEffect.setUpdateless() = Effextras.updateless.add(this)