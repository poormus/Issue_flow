package com.mussrose.issueflower.ui.main.fragments.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.DialogReportBinding
import com.mussrose.issueflower.ui.snackBar

class DialogReportIssue : DialogFragment(R.layout.dialog_report) {


    private var onReportClicked: ((String) -> Unit)? = null
    fun setOnReportClickListener(listener: (String) -> Unit) {
        onReportClicked = listener
    }

    lateinit var binding: DialogReportBinding
    private var reportReason: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogReportBinding.bind(view)

        binding.rb1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rb2.isChecked = false
                binding.rb3.isChecked = false
                binding.rb4.isChecked = false
                reportReason = binding.rb1.text.toString()
                binding.btReportIssue.isEnabled = true
            }
        }
        binding.rb2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rb1.isChecked = false
                binding.rb3.isChecked = false
                binding.rb4.isChecked = false
                reportReason = binding.rb2.text.toString()
                binding.btReportIssue.isEnabled = true
            }
        }
        binding.rb3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rb1.isChecked = false
                binding.rb2.isChecked = false
                binding.rb4.isChecked = false
                reportReason = binding.rb3.text.toString()
                binding.btReportIssue.isEnabled = true
            }
        }
        binding.rb4.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.rb1.isChecked = false
                binding.rb2.isChecked = false
                binding.rb3.isChecked = false
                reportReason = binding.rb4.text.toString()
                binding.btReportIssue.isEnabled = true
            }
        }

        binding.btReportIssue.setOnClickListener {
            //save report to firebase here
            dialog?.dismiss()

        }
    }
}