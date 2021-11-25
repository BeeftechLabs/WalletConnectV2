package com.beeftechlabs.androiddapp.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.beeftechlabs.androiddapp.databinding.MainFragmentBinding
import com.google.android.material.snackbar.Snackbar

class MainDappFragment : Fragment() {

    companion object {
        fun newInstance() = MainDappFragment()
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainDappViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainDappViewModel::class.java]

        binding.connect.setOnClickListener {
            viewModel.connectToWallet()
        }

        viewModel.uri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(uri)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context?.startActivity(this)
                }
            }
        }

        viewModel.sessionApproved.observe(viewLifecycleOwner) { (approved, accounts) ->
            Snackbar.make(binding.root, if (approved) "Session approved for $accounts" else "Session rejected", Snackbar.LENGTH_INDEFINITE).show()
        }
    }
}