package utils

/*
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

/**
 * This is an updated version with enhancements made by Daniel Migowski,
 * Andre Bogus, and David Koelle
 *
 * To convert to use Templates (Java 1.5+):
 *   - Change "implements Comparator" to "implements Comparator<String>"
 *   - Change "compare(Object o1, Object o2)" to "compare(String s1, String s2)"
 *   - Remove the type checking and casting in compare().
 *
 * To use this class:
 *   Use the static "sort" method from the java.util.Collections class:
 *   Collections.sort(your list, new AlphanumComparator());
 */

/**
 * These are methods derived from the original java implementation of the alphanum comparator. See above for details and
 * licensing.
 */

public class AlphaNum { companion object s {
    public fun compare(a: String, b: String): Int {
        var aMarker = 0
        var bMarker = 0
        val aLength = a.length()
        val bLength = b.length()

        while (aMarker < aLength && bMarker < bLength) {
            val aChunk = getChunk(a, aLength, aMarker)
            aMarker += aChunk.length()

            val bChunk = getChunk(b, bLength, bMarker)
            bMarker += bChunk.length()

            var result = 0
            if (isDigit(aChunk.charAt(0)) && isDigit(bChunk.charAt(0))) {
                var aChunkLength = aChunk.length()
                result = aChunkLength - bChunk.length()
                if (result == 0) {
                    for (i in 0..(aChunkLength - 1)) {
                        result = aChunk.charAt(i) - bChunk.charAt(i)
                        if (result != 0) {
                            return result
                        }
                    }
                }
            } else {
                result = aChunk.compareTo(bChunk)
            }

            if (result != 0) {
                return result
            }
        }
        return aLength - bLength
    }

    private fun isDigit(ch: Char): Boolean {
        return ch >= '0' && ch <= '9'
    }

    private fun getChunk(s: String, slength: Int, _marker: Int): String {
        var marker = _marker
        val chunk = StringBuilder()
        var c = s.charAt(marker)
        chunk.append(c)
        marker++

        val wasDigit = isDigit(c)
        while (marker < slength) {
            c = s.charAt(marker)
            if (isDigit(c) != wasDigit) break
            chunk.append(c)
            marker++
        }
        return chunk.toString()
    }
}}