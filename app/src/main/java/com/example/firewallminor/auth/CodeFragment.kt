package com.example.firewallminor.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.firewallminor.ActivityMain
import com.example.firewallminor.R
import com.example.firewallminor.auth.PhoneViewModel.Companion.PHONE
import com.example.firewallminor.databinding.FragmentCodeBinding
import com.example.firewallminor.utils.BaseBindingFragment
import com.example.firewallminor.utils.underLine
import org.koin.androidx.viewmodel.ext.android.viewModel

class CodeFragment :
    BaseBindingFragment<FragmentCodeBinding, CodeViewModel>(R.layout.fragment_code) {
    override val vm: CodeViewModel by viewModel()
    private var timer: CountDownTimer? = null

    override fun initViews(savedInstanceState: Bundle?) {
        binding.run {
            viewModel = vm
            resetTimer()
            vm.init(resources, arguments?.getString(PHONE) ?: "", requireActivity())
            sendCodeAgain.setOnClickListener {
                if (vm.clickable.get()) {
                    resetTimer()
                    vm.sendAgain()
                    vm.code.set("")
                }
            }
            vm.end.observe(viewLifecycleOwner) {
                authCodeHint.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_grey
                    )
                )
                val fullHint = getString(R.string.auth_code_hint)
                val result = SpannableString(fullHint)
                result.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.transparent
                        )
                    ), 0, it, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                authCodeHint.text = result
            }

            vm.openMainActivity.observe(viewLifecycleOwner) {
                requireActivity().startActivity(Intent(requireActivity(), ActivityMain::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun resetTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                vm.timeLeft.set(
                    SpannableString.valueOf(
                        getString(
                            R.string.resend_after_n_sec,
                            (millisUntilFinished / 1000) + 1
                        )
                    )
                )
                vm.clickable.set(false)
            }

            override fun onFinish() {
                vm.timeLeft.set(getString(R.string.send_again).underLine())
                vm.clickable.set(true)
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }
}