/*
 * Copyright (C) 2025 Jacob Wysko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package org.wysko.midis2jam2.starter

import com.jme3.app.SimpleApplication
import com.jme3.system.AppSettings
import org.wysko.midis2jam2.starter.configuration.*
import java.awt.GraphicsEnvironment
import javax.imageio.ImageIO

internal fun SimpleApplication.applyConfigurations(configurations: Collection<Configuration>) {
    setSettings(AppSettings(false).apply {
        copyFrom(DEFAULT_JME_SETTINGS)
        applyResolution(configurations)
    })
    setDisplayStatView(false)
    setDisplayFps(false)
    isPauseOnLostFocus = false
    isShowSettings = false
}

private fun AppSettings.applyResolution(configurations: Collection<Configuration>) = when {
    configurations.find<SettingsConfiguration>().isFullscreen -> {
        isFullscreen = true
        with(screenResolution()) {
            this@applyResolution.width = width
            this@applyResolution.height = height
        }
    }

    else -> {
        isFullscreen = false
        with(configurations.find<GraphicsConfiguration>()) {
            when (windowResolution) {
                is Resolution.DefaultResolution ->
                    with(preferredResolution()) {
                        this@applyResolution.width = width
                        this@applyResolution.height = height
                    }

                is Resolution.CustomResolution ->
                    with(windowResolution) {
                        this@applyResolution.width = width
                        this@applyResolution.height = height
                    }
            }
        }
    }
}

internal fun screenResolution(): Resolution.CustomResolution =
    with(GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode) {
        Resolution.CustomResolution(width, height)
    }

internal fun preferredResolution(): Resolution.CustomResolution =
    with(screenResolution()) {
        Resolution.CustomResolution((width * 0.95).toInt(), (height * 0.85).toInt())
    }

private val DEFAULT_JME_SETTINGS = AppSettings(true).apply {
    frameRate = 120
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayModes.firstOrNull()?.let {
        frequency = 120
    }
    isVSync = false
    isResizable = false
    isGammaCorrection = false
    icons =
        arrayOf("/ico/icon16.png", "/ico/icon32.png", "/ico/icon128.png", "/ico/icon256.png").map {
            ImageIO.read(this::class.java.getResource(it))
        }.toTypedArray()
    title = "midis2jam2"
    audioRenderer = null
    centerWindow = true
}
