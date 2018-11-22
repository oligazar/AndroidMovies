package us.kostenko.architecturecomponentstmdb.common.test

import android.content.Context
import android.content.SharedPreferences
import us.kostenko.architecturecomponentstmdb.common.test.Rank.SEVEN
import us.kostenko.architecturecomponentstmdb.common.test.Suit.DIAMONDS
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// delegates
// observable property

abstract class ObservableProperty<T>(initialValue: T): ReadWriteProperty<Any?, T> {
    private var value = initialValue

    /**
     * If the callback returns `true` the value of the property is being set to the new value
     * and if the callback returns `false` the new value is declared.
     */

    protected open fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = true

    protected open fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {}

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val oldValue = this.value
        if (!beforeChange(property, oldValue, value)) { return }
        this.value = value
        afterChange(property, oldValue, value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}

// SharedPreferences delegate
abstract class SharedPrefDelegate<T>(context: Context, val key: String, val defaultValue: T) {
    val prefs: SharedPreferences by lazy { context.getSharedPreferences("prefName", Context.MODE_PRIVATE) }

    /**
     * If the callback returns `true` the value of the property is being set to the new value
     * and if the callback returns `false` the new value is declared.
     */

    protected open fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = true

    protected open fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {}

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = with(prefs)  {
        when (defaultValue) {
            is Boolean -> getBoolean(key, defaultValue)
            is Int -> getInt(key, defaultValue)
            is String -> getString(key, defaultValue)
            else -> throw IllegalArgumentException()
        }
    } as T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = with(prefs.edit()) {
        when (value) {
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            is String -> putString(key, value)
            else -> throw IllegalArgumentException()
        }.apply()
    }
}

// infix function

enum class Suit {
    HEARTS, SPADES, CLUBS, DIAMONDS
}

enum class Rank {
    TWO, THREE, FOUR, FIVE,
    SIX, SEVEN, EIGHT, NINE,
    TEN, JACK, QUEEN, KING, ACE;

    infix fun of(suit: Suit) = Card(this, suit)
}

data class Card(val rank: Rank, val suit: Suit)

// usage
val card = SEVEN of DIAMONDS
