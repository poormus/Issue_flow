package com.mussrose.issueflower.ui.main.fragments.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.mussrose.issueflower.R




class DialogAddTag : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val addLabelEditText = LayoutInflater.from(
            requireContext()
        ).inflate(
            R.layout.edit_text_tag,
            requireActivity().findViewById(R.id.clCreateIssue),
            false
        ) as TextInputLayout
        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_baseline_label)
            .setTitle("Tag")
            .setMessage("What is your tag")
            .setView(addLabelEditText)
            .setPositiveButton("Add"){_,_ ->
                val label=addLabelEditText.findViewById<EditText>(R.id.etAddTag).text.toString()
                positiveListener?.let {yes->

                    if (label.isEmpty()){
                        return@setPositiveButton
                    }
                        yes(label)
                }
            }
            .setNegativeButton("cancel"){dialog,_ ->
                dialog.cancel()
            }
            .create()
    }
}