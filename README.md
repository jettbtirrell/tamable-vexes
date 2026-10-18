# Tamable Vexes

A Fabric mod for Minecraft 26.3 that lets you tame vexes and keep them as loyal, battle ready companions.

## Taming

Right-click a vex with a **Totem of Undying**. The totem is consumed and the vex becomes yours:

- Its life timer is removed, so it no longer fades away, and it won't despawn.
- Max health becomes **70** (fully healed on taming).
- It has a different texture when tamed, with a relatively happier looking brow!

## Commands

Right-click your tamed vex with an empty hand (or anything that isn't one of the items below) to make it **sit** or stand. Only the owner can do this. A sitting vex hovers upright with its arms crossed, holding its sword blade-down in front of it. Taking damage makes it stand up again.

| Item | Effect |
| --- | --- |
| Glowstone Dust | Heals 10 HP (anyone can feed it, and it makes no sound or particles, like feeding a wolf). At full health it isn't used up and the click sits or stands the vex instead. |
| Any sword | Owner only: swaps the vex's sword, dropping the old one |
| Lead / Name Tag | Work as normal |

## Swords

A tamed vex fights with whatever sword it holds, and you can swap it at any time by right-clicking with a different one.

- **Enchanted swords work.** Sharpness, Fire Aspect, Knockback and the other damage enchantments apply to the vex's attacks, since it attacks with the vanilla mob attack code.
- **Durability doesn't go down.** Mobs never wear down the weapon they hold, so the sword stays in the condition you gave it.
- **It's always dropped.** A tamed vex drops its sword whenever it dies, whoever or whatever killed it, with its enchantments and durability intact.
- **The old sword comes back to you.** Swapping tosses the previous sword to the owner instead of deleting it.
- Damage is the vex's base 4 plus the sword's bonus. A netherite sword makes it hit for 11 (5.5 hearts) before armor and difficulty.

## Behavior

A tamed vex behaves like a tamed wolf:

- **Follows** you, and teleports to you when you get more than 12 blocks away. It won't while sitting, leashed, riding, or if you're a spectator.
- **Fights** whatever hurts you and whatever you attack, and retaliates against anything that hurts it. Other tamed vexes of yours join in. It won't fight while sitting.
- **Spares your other pets:** it never attacks you, other tamed vexes, tamed wolves of yours, tamed cats/parrots and other tameables, tamed horses, creepers, ghasts, armor stands, or players you can't harm.
- **Panics** from fire and other environmental damage.
- Joins your **team** and counts you and your allies as allies.
- Is ignored by iron golems, snow golems, shulkers and conduits, and doesn't stop you from sleeping.
- Sends you its **death message** if it dies.
- Is slightly quieter than a wild vex: wolf volume with a slightly lower pitch.
- **Won't despawn**, even on Peaceful, and can be leashed.
- Its wings **flap slower** the closer it gets to death (below 50% health).

## Compatibility

The renderer changes are done with mixins on the vanilla vex renderer rather than by replacing it, so it should coexist with other mods that retexture vexes. Fabric API is required, on both client and server.

## Building

```
./gradlew build      # jar in build/libs
./gradlew runClient  # launch the game
```

Requires JDK 25.

Tip: run `./gradlew genSources` to populate the Minecraft sources for reference.
