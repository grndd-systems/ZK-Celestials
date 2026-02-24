package com.grnddsystems.celestials.util

import kotlinx.coroutines.sync.Mutex

/**
 * Global mutex for Noir native library calls.
 * The native library does not support concurrent proof generation.
 */
object NoirLock {
    val mutex = Mutex()
}
