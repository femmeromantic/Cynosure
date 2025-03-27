package dev.mayaqq.cynosure.effects

import com.google.common.collect.Sets
import dev.mayaqq.cynosure.utils.mapBacked
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
import net.minecraft.world.effect.MobEffect
import java.util.WeakHashMap

/**
 * A collection of extra properties for MobEffects.
 */
public object Effextras {
    /**
     * Any Effect in this set will not call onRemove on status effect update.
     */
    internal val updateless: MutableSet<MobEffect> = ReferenceOpenHashSet()

    public fun isUpdateless(effect: MobEffect): Boolean = updateless.contains(effect)
    public fun setUpdateless(effect: MobEffect): Boolean = updateless.add(effect)
    public fun removeUpdateless(effect: MobEffect): Boolean = updateless.remove(effect)
}

public var MobEffect.updateless: Boolean by mapBacked(false)