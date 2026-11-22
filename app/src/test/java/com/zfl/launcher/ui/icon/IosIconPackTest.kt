package com.zfl.launcher.ui.icon

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class IosIconPackTest {
    @Test
    fun allPackAssetsAreBundled() {
        val dir = File("src/main/res/drawable-nodpi")
        val assets = dir.listFiles { f -> f.name.startsWith("ios26_") && f.extension == "png" }.orEmpty()
        assertEquals(85, assets.size)
    }

    @Test
    fun knownPackagesAreMapped() {
        assertNotNull(IosIconPack.drawableFor("com.android.chrome"))
        assertNotNull(IosIconPack.drawableFor("com.google.android.dialer"))
        assertNotNull(IosIconPack.drawableFor("org.telegram.messenger"))
    }

    @Test
    fun unknownPackagesFallBackToTheirOwnIcon() {
        assertNull(IosIconPack.drawableFor("com.example.unknown"))
    }

    @Test
    fun everydayAppsComeFirstOnTheFreshHomeScreen() {
        assert(IosIconPack.homeRank("com.google.android.calendar") < IosIconPack.homeRank("com.android.settings"))
        assertEquals(Int.MAX_VALUE, IosIconPack.homeRank("com.example.unknown"))
    }
}
