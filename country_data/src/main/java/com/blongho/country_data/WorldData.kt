/*
 * MIT License
 *
 * Copyright (c) 2019 Bernard Che Longho
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.blongho.country_data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * @file WorldData.java
 * @author Bernard Che Longho (blongho)
 * @brief A class to load all the flags and countries in a map
 * <br></br> This eases the access of flag when the country
 * alpha2 or alpha3  or the numeric codes are known<br></br> This class is accessible only to the
 * package
 * @since 2019-11-15 Refactored class and removes many unnecessary variables.
 * @since 2020-02-29 Changes classname from WorldBuilder to WorldData. *Builder is misleading since
 * this class does not follow th Builder pattern
 */
internal class WorldData private constructor(ctx: Context) {
    /**
     * Load the countries and their flags in a Map container
     * <br></br>
     * Each country is flag is mapped with the country alpha2 and alpha3 codes
     */
    private suspend fun loadAllData(context: Context) {
        var countryFlag: Int
        val countries = getCountries(context)
        for (country in countries) {
            // do was not allowed as a drawable so was renamed to dominican
            countryFlag =
                    context.resources.getIdentifier(
                            "lang_${country.alpha3.toLowerCase()}",
                            "drawable",
                            context.packageName
                    )
            country.flagResource = countryFlag
            countryFlagMap[country.alpha3] = countryFlag
            if (country.alpha3.equals("xxx", ignoreCase = true)) {
                universe = country
                countryFlagMap[universe!!.alpha3] = globe()
            }
        }
    }

    /**
     * Read all countries from file
     */
    private fun getCountries(context: Context): Array<Country> {
        val values = AssetsReader.readFromAssets(context,
                R.raw.com_blongho_country_data_countries)
        val gson = Gson()
        return gson.fromJson(values, Array<Country>::class.java)
    }

    companion object {
        const val CURRENT_VERSION = "1.5.1-beta"
        private var instance: WorldData? = null
        private val countryFlagMap: MutableMap<String, Int> = HashMap()
        private var universe: Country? = null

        /**
         * Get an instance of this class<br></br> This is a thread-safe singleton of the class. <br></br> Once
         * called, all the flag resources are loaded and all countries are assigned their flags. Calling
         * this more than once has not benefit.
         *
         * @param ctx The application context (getApplicationContext())
         * @return An instance of this class
         */
        @JvmStatic
        fun getInstance(ctx: Context): WorldData? {
            if (instance != null) {
                return instance
            }
            synchronized(WorldData::class.java) {
                if (instance == null) {
                    instance = WorldData(ctx)
                }
            }
            return instance
        }

        /* package */
        @JvmStatic
        fun countries(): List<String> {
            return ArrayList(countryFlagMap.keys)
        }

        /**
         * The Image of the globe
         *
         * @return The globe as a drawable resource
         */
        @JvmStatic
        fun globe(): Int {
            return R.drawable.lang_globe
        }

        /**
         * Get the flag of a country using any of the country attributes
         *
         * @param countryIdentifier (alpha2, alpha3, country name, or numeric code)
         * @return flag resource
         */
        @JvmStatic
        fun flagFromCountry(countryIdentifier: String): Int {
            if (countryIdentifier.equals("xx", ignoreCase = true)
                    || countryIdentifier.equals("XXX", ignoreCase = true)
                    || countryIdentifier.equals("world", ignoreCase = true)
                    || countryIdentifier.equals("globe", ignoreCase = true)) {
                return globe()
            }
            val identifier = countryIdentifier.replace("-", "_")
            val country = countryFlagMap[identifier]
            return country ?: globe()
        }
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadAllData(ctx)
        }
    }
}