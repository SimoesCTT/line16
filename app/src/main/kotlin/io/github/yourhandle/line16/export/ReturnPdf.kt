package io.github.yourhandle.line16.export

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField
import io.github.yourhandle.line16.domain.calc.TaxResult
import io.github.yourhandle.line16.domain.model.FilingStatus
import io.github.yourhandle.line16.domain.model.TaxReturn
import java.io.File
import java.io.FileOutputStream

object ReturnPdf {

    // ---- Field names of the IRS Form 1040 (2025 revision) ----
    // Verify against the actual PDF with PDFBox PDFDebugger if fields land empty.
    private const val F_FILING_SINGLE = "topmostSubform[0].Page1[0].FilingStatus[0].c1_01[0]"
    private const val F_FILING_MFJ = "topmostSubform[0].Page1[0].FilingStatus[0].c1_01[1]"
    private const val F_FILING_HOH = "topmostSubform[0].Page1[0].FilingStatus[0].c1_01[2]"

    private const val F_FIRST_NAME = "topmostSubform[0].Page1[0].f1_02[0]"
    private const val F_LAST_NAME = "topmostSubform[0].Page1[0].f1_03[0]"
    private const val F_MI = "topmostSubform[0].Page1[0].f1_02[1]"
    private const val F_SSN = "topmostSubform[0].Page1[0].YourSocial_ReadOrderControl[0].f1_04[0]"
    private const val F_SPOUSE_FIRST = "topmostSubform[0].Page1[0].f1_05[0]"
    private const val F_SPOUSE_LAST = "topmostSubform[0].Page1[0].f1_06[0]"
    private const val F_SPOUSE_MI = "topmostSubform[0].Page1[0].f1_05[1]"
    private const val F_SPOUSE_SSN = "topmostSubform[0].Page1[0].YourSocial_ReadOrderControl[0].f1_06[0]"
    private const val F_ADDRESS = "topmostSubform[0].Page1[0].f1_07[0]"
    private const val F_APT = "topmostSubform[0].Page1[0].f1_08[0]"
    private const val F_CITY = "topmostSubform[0].Page1[0].f1_09[0]"
    private const val F_STATE = "topmostSubform[0].Page1[0].f1_10[0]"
    private const val F_ZIP = "topmostSubform[0].Page1[0].f1_11[0]"

    private const val F_W2_WAGES = "topmostSubform[0].Page1[0].f1_47[0]"
    private const val F_TOTAL_INCOME = "topmostSubform[0].Page1[0].f1_73[0]"
    private const val F_AGI_P1 = "topmostSubform[0].Page1[0].f1_75[0]"
    private const val F_AGI_P2 = "topmostSubform[0].Page2[0].f2_01[0]"
    private const val F_STD_DEDUCTION = "topmostSubform[0].Page2[0].f2_02[0]"
    private const val F_TAXABLE_INCOME = "topmostSubform[0].Page2[0].f2_06[0]"
    private const val F_TAX = "topmostSubform[0].Page2[0].f2_08[0]"
    private const val F_TOTAL_TAX = "topmostSubform[0].Page2[0].f2_16[0]"
    private const val F_WITHHOLDING = "topmostSubform[0].Page2[0].f2_17[0]"

    fun write(context: Context, taxReturn: TaxReturn, result: TaxResult): File {
        PDFBoxResourceLoader.init(context.applicationContext)

        val assetStream = context.assets.open("f1040.pdf")
        val document = PDDocument.load(assetStream)
        val acroForm: PDAcroForm = document.documentCatalog.acroForm

        val tp = taxReturn.taxpayer

        set(acroForm, F_FIRST_NAME, tp.firstName)
        set(acroForm, F_MI, tp.middleInitial)
        set(acroForm, F_LAST_NAME, tp.lastName)
        set(acroForm, F_SSN, tp.ssn)

        when (taxReturn.status) {
            FilingStatus.SINGLE -> set(acroForm, F_FILING_SINGLE, "1")
            FilingStatus.MFJ -> {
                set(acroForm, F_FILING_MFJ, "1")
                set(acroForm, F_SPOUSE_FIRST, tp.spouseFirstName)
                set(acroForm, F_SPOUSE_MI, tp.spouseMiddleInitial)
                set(acroForm, F_SPOUSE_LAST, tp.spouseLastName)
                set(acroForm, F_SPOUSE_SSN, tp.spouseSsn)
            }
            FilingStatus.HOH -> set(acroForm, F_FILING_HOH, "1")
        }

        set(acroForm, F_ADDRESS, tp.address)
        set(acroForm, F_APT, tp.apt)
        set(acroForm, F_CITY, tp.city)
        set(acroForm, F_STATE, tp.state)
        set(acroForm, F_ZIP, tp.zip)

        if (taxReturn.totalWages > 0) set(acroForm, F_W2_WAGES, taxReturn.totalWages.toString())
        set(acroForm, F_TOTAL_INCOME, result.totalIncome.toString())
        set(acroForm, F_AGI_P1, result.agi.toString())
        set(acroForm, F_AGI_P2, result.agi.toString())
        set(acroForm, F_STD_DEDUCTION, result.standardDeduction.toString())
        set(acroForm, F_TAXABLE_INCOME, result.taxableIncome.toString())
        set(acroForm, F_TAX, result.federalIncomeTax.toString())
        set(acroForm, F_TOTAL_TAX, result.totalTax.toString())
        set(acroForm, F_WITHHOLDING, result.withholding.toString())

        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "line16-1040-filled.pdf")
        FileOutputStream(file).use { document.save(it) }

        document.close()
        assetStream.close()
        return file
    }

    private fun set(form: PDAcroForm, name: String, value: String) {
        val field: PDField? = form.getField(name)
        field?.setValue(value)
    }
}
