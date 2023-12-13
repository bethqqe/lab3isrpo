package com.dualanguages.translate

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder

class TranslationPlugin : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project != null && editor != null) {
            val selectedText = getSelectedText(editor)

            if (selectedText.isNotEmpty()) {
                translateAndOpenBrowser(project, selectedText)
            } else {
                showError(project, "No text selected", "Custom Error Title")
            }
        }
    }

    private fun getSelectedText(editor: Editor): String {
        val selectionModel: SelectionModel = editor.selectionModel
        return if (selectionModel.hasSelection()) {
            selectionModel.selectedText ?: ""
        } else {
            ""
        }
    }

    private fun translateAndOpenBrowser(project: Project, textToTranslate: String) {
        try {
            val encodedText = URLEncoder.encode(textToTranslate, "UTF-8")
            val translationUrl = "https://www.deepl.com/translator#en/ru/$encodedText"
            openBrowser(translationUrl)
        } catch (e: Exception) {
            showError(project, "Error translating text", "Custom Error Title")
        }
    }

    private fun openBrowser(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            showError(null, "Desktop browsing not supported", "Custom Error Title")
        }
    }

    private fun showError(project: Project?, message: String, title: String) {
        ApplicationManager.getApplication().invokeLater({
            Messages.showErrorDialog(project, message, title)
        }, ModalityState.NON_MODAL)
    }
}