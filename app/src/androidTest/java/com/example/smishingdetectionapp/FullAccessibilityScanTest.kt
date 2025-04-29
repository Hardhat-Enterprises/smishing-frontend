package com.example.smishingdetectionapp

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Button
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullAccessibilityScanTest {

    companion object {
        private lateinit var logFile: File
    }

    private val scannedViews = mutableListOf<String>()
    private val accessibilityWarnings = mutableListOf<String>()
    private var currentActivityName: String = ""
    private var activityWarningsCount = 0
    private val contentDescriptionMap = mutableMapOf<String, MutableList<String>>() // new

    private fun writeLog(message: String) {
        FileOutputStream(logFile, true).bufferedWriter().use { writer ->
            writer.appendLine(message)
        }
    }

    private fun scanAllViews(root: View) {
        if (root.visibility == View.VISIBLE) {
            try {
                if (root.id != View.NO_ID) {
                    val viewName = try {
                        root.resources.getResourceEntryName(root.id)
                    } catch (e: Exception) {
                        "no_resource_name"
                    }
                    scannedViews.add(viewName)

                    // ImageView: check missing contentDescription
                    if (root is ImageView) {
                        val desc = root.contentDescription
                        if (desc.isNullOrEmpty()) {
                            val warning = "⚠️ Missing contentDescription: Activity=$currentActivityName, ViewID=$viewName"
                            accessibilityWarnings.add(warning)
                            activityWarningsCount++
                            writeLog("    $warning")
                        }
                    }

                    // Button: check missing text
                    if (root is Button) {
                        try {
                            val buttonText = root.text
                            if (buttonText.isNullOrEmpty()) {
                                val warning = "⚠️ Button missing text: Activity=$currentActivityName, ViewID=$viewName"
                                accessibilityWarnings.add(warning)
                                activityWarningsCount++
                                writeLog("    $warning")
                            }
                        } catch (e: Exception) {
                            val warning = "⚠️ Button text could not be retrieved: Activity=$currentActivityName, ViewID=$viewName"
                            accessibilityWarnings.add(warning)
                            activityWarningsCount++
                            writeLog("    $warning")
                        }
                    }

                    // EditText: check missing hint
                    if (root is EditText) {
                        val hintText = root.hint
                        if (hintText.isNullOrEmpty()) {
                            val warning = "🚨 [HIGH] Missing hint in EditText: Activity=$currentActivityName, ViewID=$viewName"
                            accessibilityWarnings.add(warning)
                            activityWarningsCount++
                            writeLog("    $warning")
                        }
                    }

                    // Clickable views missing description
                    if (root.isClickable && root !is Button && root !is ImageView) {
                        val desc = root.contentDescription
                        if (desc == null || desc.isNullOrEmpty()) {
                            val warning = "🚨 [HIGH] Clickable view missing description: Activity=$currentActivityName, ViewID=$viewName"
                            accessibilityWarnings.add(warning)
                            activityWarningsCount++
                            writeLog("    $warning")
                        }
                    }

                    // Small touch target
                    if (root.isClickable) {
                        val widthPx = root.width
                        val heightPx = root.height
                        val density = root.resources.displayMetrics.density
                        val widthDp = widthPx / density
                        val heightDp = heightPx / density
                        val minTouchTargetDp = 48

                        if (widthDp < minTouchTargetDp || heightDp < minTouchTargetDp) {
                            val warning = "🚨 [HIGH] Small touch target (<48dp): Activity=$currentActivityName, ViewID=$viewName, size=${widthDp.toInt()}dp x ${heightDp.toInt()}dp"
                            accessibilityWarnings.add(warning)
                            activityWarningsCount++
                            writeLog("    $warning")
                        }
                    }

                    // Track contentDescriptions for duplicates
                    val desc = try { root.contentDescription?.toString() } catch (e: Exception) { null }
                    if (!desc.isNullOrEmpty()) {
                        contentDescriptionMap.getOrPut(desc) { mutableListOf() }.add(viewName)
                    }
                }
            } catch (ignored: Exception) {
                // Ignore crashes
            }
        }

        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                scanAllViews(root.getChildAt(i))
            }
        }
    }

    private fun <T : Activity> scanActivity(activityClass: Class<T>) {
        try {
            ActivityScenario.launch(activityClass).use { scenario ->
                InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                scenario.onActivity { activity: T ->
                    val rootView = activity.window.decorView.rootView
                    currentActivityName = activityClass.simpleName
                    activityWarningsCount = 0
                    scannedViews.clear()
                    contentDescriptionMap.clear()

                    writeLog("\n==============================")
                    writeLog("🔵 Scanning Activity: $currentActivityName")
                    writeLog("==============================")

                    scanAllViews(rootView)

                    if (scannedViews.isEmpty()) {
                        writeLog("⚠️ No visible views found to scan.")
                    } else {
                        writeLog("✅ Features scanned in $currentActivityName:")
                        scannedViews.sorted().forEach { viewName ->
                            writeLog("    - $viewName")
                        }
                        writeLog("✅ Total scanned views: ${scannedViews.size}")
                    }

                    // Detect duplicate contentDescriptions
                    for ((desc, viewIds) in contentDescriptionMap) {
                        if (viewIds.size > 1) {
                            val warning = "⚠️ Duplicate contentDescription: \"$desc\" used in ${viewIds.size} views in $currentActivityName (${viewIds.joinToString(", ")})"
                            accessibilityWarnings.add(warning)
                            activityWarningsCount++
                            writeLog("    $warning")
                        }
                    }

                    if (activityWarningsCount > 0) {
                        writeLog("⚠️ Total accessibility warnings in $currentActivityName: $activityWarningsCount")
                    } else {
                        writeLog("✅ No accessibility warnings in $currentActivityName")
                    }

                    writeLog("✅ Completed scanning $currentActivityName\n")
                }
            }
        } catch (e: Exception) {
            writeLog("❌ Failed to scan ${activityClass.simpleName}: ${e.message}\n")
        }
    }

    @Test
    fun fullAccessibilityScan_MainActivities() {
        // AccessibilityChecks.enable() // (optional depending on your use case)

        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val downloadsFolder = File("/sdcard/Download/")
        if (!downloadsFolder.exists()) {
            downloadsFolder.mkdirs()
        }
        logFile = File(downloadsFolder, "AccessibilityScanLog_$timestamp.txt")
        logFile.createNewFile()

        writeLog("🔎 Accessibility Scan Started at $timestamp\n")

        scanActivity(MainActivity::class.java)
        scanActivity(SettingsActivity::class.java)
        scanActivity(AboutUsActivity::class.java)
        scanActivity(DebugActivity::class.java)
        scanActivity(EducationActivity::class.java)
        scanActivity(FeedbackActivity::class.java)
        scanActivity(HelpActivity::class.java)
        scanActivity(SignupActivity::class.java)
        scanActivity(NotificationActivity::class.java)
        scanActivity(SmishingRulesActivity::class.java)
        scanActivity(SmsActivity::class.java)
        scanActivity(ReportingActivity::class.java)
        scanActivity(NewsActivity::class.java)
        scanActivity(AboutMeActivity::class.java)
        scanActivity(TermsAndConditionsActivity::class.java)

        writeLog("\n🏁 Accessibility Scan Completed Successfully!")

        // insertSummarySection()  (still commented)
    }

    //    private fun insertSummarySection() {
    //        val originalContent = logFile.readText()
    //        val summaryBuilder = StringBuilder()
    //        summaryBuilder.appendLine("================================")
    //        summaryBuilder.appendLine("🔎 Accessibility Scan Summary")
    //        summaryBuilder.appendLine("================================")
    //
    //        if (accessibilityWarnings.isEmpty()) {
    //            summaryBuilder.appendLine("✅ No accessibility issues detected! Great job!")
    //        } else {
    //            summaryBuilder.appendLine("⚠️ Issues detected:")
    //            accessibilityWarnings.forEach { warning ->
    //                summaryBuilder.appendLine("    - $warning")
    //            }
    //        }
    //        summaryBuilder.appendLine("\n")
    //        FileOutputStream(logFile, false).bufferedWriter().use { writer ->
    //            writer.append(summaryBuilder.toString())
    //            writer.append(originalContent)
    //        }
    //    }
}
