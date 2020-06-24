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
        getCurrencies(context)
        for (country in countries) {
            // do was not allowed as a drawable so was renamed to dominican
            countryFlag = if (country.alpha2.equals("do", ignoreCase = true)) {
                R.drawable.dominican
            } else {
                val resource = "drawable/" + country.alpha2.toLowerCase()
                context.resources
                        .getIdentifier(resource, null, context.packageName)
            }
            country.flagResource = countryFlag
            country.currency = currencyMap[country.alpha2.toLowerCase()]
            countryFlagMap[country.alpha2] = countryFlag
            if (country.alpha2.equals("xx", ignoreCase = true)) {
                universe = country
                countryFlagMap[universe!!.alpha2] = globe()
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

    /**
     * Load the currencies from com_blongho_country_data_currencies.json
     */
    private fun getCurrencies(context: Context) {
        val currencyArray = AssetsReader.readFromAssets(context,
                R.raw.com_blongho_country_data_currencies)
        val gson = Gson()
        val currencies = gson.fromJson(currencyArray, Array<Currency>::class.java)
        for (currency in currencies) {
            currencyMap[currency.country.toLowerCase()] = currency
        }
    }

    companion object {
        const val CURRENT_VERSION = "1.5.1-beta"
        private val currencyMap: MutableMap<String, Currency> = HashMap() // {alpha2, Currency}
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
        fun currencies(): List<Currency> {
            return ArrayList(currencyMap.values)
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
            return R.drawable.globe
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
            val country = countryFlagMap[countryIdentifier]
            return country ?: globe()
        }

        @JvmStatic
        fun flagsFromCountries(vararg countryIdentifiers: String): IntArray {
            val result = IntArray(countryIdentifiers.size)
            for (i in 0 until countryIdentifiers.size) {
                result[i] = flagFromCountry(countryIdentifiers[i])
            }
            return result
        }
    }

    init {
        GlobalScope.launch(Dispatchers.IO) {
            loadAllData(ctx)
        }
    }
}