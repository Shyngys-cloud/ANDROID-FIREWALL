package com.example.firewallminor.auth

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.firewallminor.R
import com.example.firewallminor.databinding.FragmentPhoneBinding
import com.example.firewallminor.utils.BaseBindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhoneFragment :
    BaseBindingFragment<FragmentPhoneBinding, PhoneViewModel>(R.layout.fragment_phone) {
    override val vm: PhoneViewModel by viewModel()

    override fun initViews(savedInstanceState: Bundle?) {
        binding.run {
            viewModel = vm
            vm.init(resources, requireActivity())
            vm.end.observe(viewLifecycleOwner) {
                authPhoneHint.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_grey
                    )
                )
                val fullHint: String = getString(R.string.auth_phone_hint)
                val result = SpannableString(fullHint)
                result.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.transparent
                        )
                    ), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                authPhoneHint.text = result
            }
        }
    }
}