package com.beeftechlabs.androidwallet.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.beeftechlabs.androidwallet.databinding.MainFragmentBinding

class MainWalletFragment : Fragment() {

    companion object {
        fun newInstance(uri: String?) = MainWalletFragment().apply {
            arguments = Bundle().apply {
                putString(URI, uri)
            }
        }

        private const val URI = "uri"
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainWalletViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainWalletViewModel::class.java]

        arguments?.getString(URI)?.let { uri ->
            viewModel.onWcUriReceived(uri)
        }

        viewModel.sessionRequest.observe(viewLifecycleOwner) { appName ->
            MaterialDialog(requireContext()).show {
                title(text = appName)
                message(text = "Do you want to approve the Session?")
                positiveButton(text = "Approve") {
                    viewModel.approveSession()
                }
                negativeButton(text = "Reject") {
                    viewModel.rejectSession()
                }
            }
        }
    }

}