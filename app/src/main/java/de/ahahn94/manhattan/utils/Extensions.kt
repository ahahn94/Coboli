package de.ahahn94.manhattan.utils

/*
Collection of extension functions to existing classes.
 */

/**
 * Return the String if it is not null,
 * else return an empty String.
 */
infix fun String?.replaceNull(with : String) : String {
    return this ?: with
}