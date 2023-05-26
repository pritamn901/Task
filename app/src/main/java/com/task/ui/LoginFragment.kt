package com.task.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.task.R
import com.task.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    lateinit var binding:FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        span(" Privacy Policy",binding.tvTerms)
        binding.tvTerms.append(" and ")
        span("Terms of Use",binding.tvTerms)
        span(" Sign Up",binding.tvSignup)
    }

    fun span(text: String, textView: TextView) {
        // span item designs
        val span = Spannable.Factory.getInstance().newSpannable(text)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setOnLongClickListener { v: View? -> true }
        val cs: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLACK
                ds.isUnderlineText = true
            }

            override fun onClick(v: View) {

            }
        }
        span.setSpan(cs, 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.append(span)
    }

}