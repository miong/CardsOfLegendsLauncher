package com.bubul.col.launcher.core

import java.nio.file.Paths

fun startTmpLauncher()
{
    Runtime.getRuntime().exec(Paths.get("../tmp/launcher/CardsOfLegendsLauncher.exe").toAbsolutePath().toString())
}

fun startLauncherFromTmp()
{
    Runtime.getRuntime().exec(Paths.get("../../launcher/CardsOfLegendsLauncher.exe").toAbsolutePath().toString())
}

fun startGame()
{
    Runtime.getRuntime().exec(Paths.get("../game/CardsOfLegends.exe").toAbsolutePath().toString())
}