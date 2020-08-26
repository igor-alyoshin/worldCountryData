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

package com.blongho.country_data;

import androidx.annotation.DrawableRes;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Bernard Che Longho (blongho)
 * @file Country.java
 * @brief A country is represented by the name, the 2 letter representation, the 3 letter
 * representation The Country data were gotten from the sister project by same author from
 * https://github.com/blongho/countries A sample entry of the file is { "id": "020", "alpha2": "AD",
 * "alpha3": "AND", "name": "Andorra", "capital": "Andorra la Vella", "area": "468.0", "population":
 * "84,000", "continent": "EU" }
 *
 * <p>The user should not be able to create a new Country as in real life,
 * countries are not just created. </p>
 * @since 2020-02-29
 **/

public class Country {

  private final String id;        // The country's ISO 3166-1 numeric id
  private final String alpha3;    // The country's ISO 3166 alpha3 id
  @DrawableRes
  private int flagResource; // The image resource that represent the
  // country flag

  /**
   * @param id The numeric code of the country
   * @param alpha3 The country's ISO 3166 alpha3 id
   * @param flagResource The country flag
   */
  Country(String id, String alpha3, @DrawableRes int flagResource) {
    this.id = id;
    this.alpha3 = alpha3;
    this.flagResource = flagResource;
  }

  /**
   * Unique id for each Country
   *
   * @return The country's ISO 3166-1 numeric id
   */
  public final int getId() {
    return Integer.parseInt(id);
  }

  /**
   * Get the alpha3 of the country
   *
   * @return The ISO 3166 alpha3 id of the country
   */
  public final String getAlpha3() {
    return alpha3;
  }

  /**
   * Get the image resouce of the country
   *
   * @return The R.drawable.id representing the flag of the country
   */
  public final int getFlagResource() {
    return flagResource;
  }

  /* package */
  void setFlagResource(@DrawableRes final int flagResource) {
    this.flagResource = flagResource;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Country country = (Country) o;
    return flagResource == country.flagResource &&
            Objects.equals(id, country.id) &&
            Objects.equals(alpha3, country.alpha3);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, alpha3, flagResource);
  }

  @Override
  public String toString() {
    return "Country{" +
        "id='" + id + '\'' +
        ", alpha3='" + alpha3 + '\'' +
        '}';
  }
}