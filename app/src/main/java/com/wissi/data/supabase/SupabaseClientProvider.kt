package com.wissi.data.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    private const val SUPABASE_URL =
        "https://nupdkgpfgzbhllfsqjgz.supabase.co"

    private const val SUPABASE_KEY =
        "sb_publishable_z3T6HpNAFARnOYxz_juiFw_upv0FNWo"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {

        // Base de datos
        install(Postgrest)



        // Storage
        install(Storage)
    }
}
