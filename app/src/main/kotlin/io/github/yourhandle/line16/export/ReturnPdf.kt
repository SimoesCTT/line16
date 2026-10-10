package io.github.yourhandle.line16.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import io.github.yourhandle.line16.domain.calc.TaxResult
import io.github.yourhandle.line16.domain.model.TaxReturn
import io.github.yourhandle.line16.domain.year.TaxYear2026
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReturnPdf {

    private const val PAGE_W = 612   // 8.5" * 72
    private const val PAGE_H = 792   // 11" * 72
    private const val MARGIN = 54    // 0.75"

    fun write(context: Context, taxReturn: TaxReturn, result: TaxResult): File {
        val doc = PdfDocument()
        val page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
        val c = page.canvas

        val title = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
        }
        val subtitle = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
        }
        val section = Paint().apply {
            color = Color.BLACK
            textSize = 13f
            isFakeBoldText = true
        }
        val body = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }
        val mono = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            typeface = android.graphics.Typeface.MONOSPACE
        }
        val rule = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.5f
        }

        var y = MARGIN.toFloat() + 20f

        c.drawText("Line 16", MARGIN.toFloat(), y, title)
        y += 18f
        c.drawText("US Federal Tax Summary — Tax Year ${TaxYear2026.YEAR}", MARGIN.toFloat(), y, subtitle)
        y += 14f
        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        c.drawText("Generated $stamp  ·  Not tax advice  ·  All data stays on device",
            MARGIN.toFloat(), y, subtitle)
        y += 24f

        c.drawLine(MARGIN.toFloat(), y, (PAGE_W - MARGIN).toFloat(), y, rule)
        y += 20f

        // ---- Filing info ----
        c.drawText("Filing information", MARGIN.toFloat(), y, section)
        y += 18f
        c.drawText("Filing status:", MARGIN.toFloat(), y, body)
        c.drawText(taxReturn.status.name, MARGIN.toFloat() + 110f, y, body)
        y += 16f
        c.drawText("W-2 count:", MARGIN.toFloat(), y, body)
        c.drawText(taxReturn.w2s.size.toString(), MARGIN.toFloat() + 110f, y, body)
        y += 16f
        if (taxReturn.businessMiles > 0) {
            c.drawText("Business miles:", MARGIN.toFloat(), y, body)
            c.drawText(taxReturn.businessMiles.toString(), MARGIN.toFloat() + 110f, y, body)
            y += 16f
        }
        if (taxReturn.homeOfficeSqFt > 0) {
            c.drawText("Home office (sq ft):", MARGIN.toFloat(), y, body)
            c.drawText(taxReturn.homeOfficeSqFt.toString(), MARGIN.toFloat() + 110f, y, body)
            y += 16f
        }
        y += 8f

        // ---- Line-by-line 1040 ----
        c.drawText("Line-by-line", MARGIN.toFloat(), y, section)
        y += 6f
        c.drawLine(MARGIN.toFloat(), y, (PAGE_W - MARGIN).toFloat(), y, rule)
        y += 16f

        val colForm = MARGIN.toFloat()
        val colLine = MARGIN.toFloat() + 60f
        val colLabel = MARGIN.toFloat() + 120f
        val colValueRight = (PAGE_W - MARGIN).toFloat()

        c.drawText("Form", colForm, y, body)
        c.drawText("Line", colLine, y, body)
        c.drawText("Description", colLabel, y, body)
        c.drawText("Amount", colValueRight - 60f, y, body)
        y += 4f
        c.drawLine(MARGIN.toFloat(), y, (PAGE_W - MARGIN).toFloat(), y, rule)
        y += 16f

        // Only print lines with a non-zero value, plus the always-show lines.
        val alwaysShow = setOf(
            "Total income", "Adjusted gross income", "Standard deduction",
            "Taxable income", "Federal income tax", "Total tax",
            "Federal withholding", "Refund", "Amount owed",
        )
        val visible = result.lines.filter { it.value != 0L || it.label in alwaysShow }
        for (item in visible) {
            c.drawText(item.form, colForm, y, body)
            c.drawText(item.line, colLine, y, body)
            c.drawText(item.label, colLabel, y, body)
            val text = "$" + "%,d".format(item.value)
            val w = mono.measureText(text)
            c.drawText(text, colValueRight - w, y, mono)
            y += 16f
        }

        y += 6f
        c.drawLine(MARGIN.toFloat(), y, (PAGE_W - MARGIN).toFloat(), y, rule)
        y += 20f

        // ---- Rates ----
        c.drawText("Rates", MARGIN.toFloat(), y, section)
        y += 18f
        c.drawText("Effective rate:", MARGIN.toFloat(), y, body)
        c.drawText("%.2f%%".format(result.effectiveRate * 100), MARGIN.toFloat() + 110f, y, body)
        y += 16f
        c.drawText("Marginal rate:", MARGIN.toFloat(), y, body)
        c.drawText("%.0f%%".format(result.marginalRate * 100), MARGIN.toFloat() + 110f, y, body)
        y += 30f

        // ---- Footer ----
        c.drawLine(MARGIN.toFloat(), y, (PAGE_W - MARGIN).toFloat(), y, rule)
        y += 16f
        c.drawText(
            "Line 16 computes federal income tax and self-employment tax under 2026 rules. " +
            "It does not file, does not give advice, and is not a substitute for a CPA.",
            MARGIN.toFloat(), y, subtitle
        )

        doc.finishPage(page)

        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "line16-summary-${TaxYear2026.YEAR}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }
}
