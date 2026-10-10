package io.github.yourhandle.line16.export

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckBox
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDRadioButton
import io.github.yourhandle.line16.domain.calc.TaxResult
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.TaxReturn
import java.io.File
import java.io.FileOutputStream

object ReturnPdf {

    // ---- Field names verified against the 2025 IRS 1040 fillable PDF ----
    // Page 1, top-of-form (in the PDF's own read order)
    private const val F_NAME_PRIMARY = "topmostSubform[0].Page1[0].f1_01[0]"   // first name + MI
    private const val F_LAST_PRIMARY = "topmostSubform[0].Page1[0].f1_02[0]"
    private const val F_SSN_PRIMARY  = "topmostSubform[0].Page1[0].f1_03[0]"
    private const val F_NAME_SPOUSE  = "topmostSubform[0].Page1[0].f1_04[0]"
    private const val F_LAST_SPOUSE  = "topmostSubform[0].Page1[0].f1_05[0]"
    private const val F_SSN_SPOUSE   = "topmostSubform[0].Page1[0].f1_06[0]"

    private const val F_ADDR = "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_20[0]"
    private const val F_APT  = "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_21[0]"
    private const val F_CITY = "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_22[0]"
    private const val F_STATE= "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_23[0]"
    private const val F_ZIP  = "topmostSubform[0].Page1[0].Address_ReadOrder[0].f1_24[0]"

    // Filing-status checkboxes
    private const val C_SINGLE = "topmostSubform[0].Page1[0].c1_1[0]"
    private const val C_MFJ    = "topmostSubform[0].Page1[0].c1_2[0]"
    private const val C_MFS    = "topmostSubform[0].Page1[0].c1_3[0]"
    private const val C_HOH    = "topmostSubform[0].Page1[0].c1_4[0]"

    // Page 1, income block
    private const val F_LINE1A = "topmostSubform[0].Page1[0].f1_47[0]"  // W-2 wages
    private const val F_LINE1Z = "topmostSubform[0].Page1[0].f1_55[0]"  // total of 1a-1h
    private const val F_LINE9  = "topmostSubform[0].Page1[0].f1_63[0]"  // total income
    private const val F_LINE11A= "topmostSubform[0].Page1[0].f1_65[0]"  // AGI

    // Page 2, tax block
    private const val F_LINE11B= "topmostSubform[0].Page2[0].f2_01[0]"  // AGI (repeat)
    private const val F_LINE12E= "topmostSubform[0].Page2[0].f2_06[0]"  // standard deduction
    private const val F_LINE15 = "topmostSubform[0].Page2[0].f2_09[0]"  // taxable income
    private const val F_LINE16 = "topmostSubform[0].Page2[0].f2_10[0]"  // tax
    private const val F_LINE24 = "topmostSubform[0].Page2[0].f2_18[0]"  // total tax
    private const val F_LINE25A= "topmostSubform[0].Page2[0].f2_19[0]"  // federal withholding
    private const val F_LINE34 = "topmostSubform[0].Page2[0].f2_28[0]"  // overpaid (refund)
    private const val F_LINE37 = "topmostSubform[0].Page2[0].f2_31[0]"  // amount you owe

    fun write(context: Context, taxReturn: TaxReturn, result: TaxResult): File {
        PDFBoxResourceLoader.init(context.applicationContext)

        val assetStream = io.github.yourhandle.line16.data.forms.FormUpdater.openForm(context, io.github.yourhandle.line16.domain.year.TaxYear2025.YEAR)
        val document = PDDocument.load(assetStream)
        val form: PDAcroForm = document.documentCatalog.acroForm

        val tp = taxReturn.taxpayer

        // --- Identity ---
        setText(form, F_NAME_PRIMARY, listOf(tp.firstName, tp.middleInitial)
            .filter { it.isNotBlank() }.joinToString(" "))
        setText(form, F_LAST_PRIMARY, tp.lastName)
        setText(form, F_SSN_PRIMARY, tp.ssn)

        if (taxReturn.status == FilingStatus.MFJ) {
            setText(form, F_NAME_SPOUSE, listOf(tp.spouseFirstName, tp.spouseMiddleInitial)
                .filter { it.isNotBlank() }.joinToString(" "))
            setText(form, F_LAST_SPOUSE, tp.spouseLastName)
            setText(form, F_SSN_SPOUSE, tp.spouseSsn)
        }

        // --- Address ---
        setText(form, F_ADDR, tp.address)
        setText(form, F_APT, tp.apt)
        setText(form, F_CITY, tp.city)
        setText(form, F_STATE, tp.state)
        setText(form, F_ZIP, tp.zip)

        // --- Filing status (checkbox) ---
        check(form, C_SINGLE, taxReturn.status == FilingStatus.SINGLE)
        check(form, C_MFJ,    taxReturn.status == FilingStatus.MFJ)
        check(form, C_HOH,    taxReturn.status == FilingStatus.HOH)

        // --- Income ---
        if (taxReturn.totalWages > 0) {
            setText(form, F_LINE1A, taxReturn.totalWages.toString())
            setText(form, F_LINE1Z, taxReturn.totalWages.toString())
        }
        setText(form, F_LINE9,  result.totalIncome.toString())
        setText(form, F_LINE11A, result.agi.toString())

        // --- Tax (page 2) ---
        setText(form, F_LINE11B, result.agi.toString())
        setText(form, F_LINE12E, result.standardDeduction.toString())
        setText(form, F_LINE15,  result.taxableIncome.toString())
        setText(form, F_LINE16,  result.federalIncomeTax.toString())
        setText(form, F_LINE24,  result.totalTax.toString())
        setText(form, F_LINE25A, result.withholding.toString())

        if (result.refundOrOwed >= 0) {
            setText(form, F_LINE34, result.refundOrOwed.toString())
        } else {
            setText(form, F_LINE37, (-result.refundOrOwed).toString())
        }

        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "line16-1040-filled.pdf")
        FileOutputStream(file).use { document.save(it) }

        document.close()
        assetStream.close()
        return file
    }

    private fun setText(form: PDAcroForm, name: String, value: String) {
        if (value.isBlank()) return
        val field: PDField? = form.getField(name)
        field?.setValue(value)
    }

    private fun check(form: PDAcroForm, name: String, on: Boolean) {
        if (!on) return
        val field = form.getField(name)
        when (field) {
            is PDCheckBox -> field.check()
            is PDRadioButton -> field.setValue("1")
            else -> field?.setValue("1")
        }
    }
}
