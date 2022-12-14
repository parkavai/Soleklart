package com.example.soleklart.ui.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.soleklart.R
import com.example.soleklart.databinding.BottomSheetBinding
import com.example.soleklart.ui.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Bottomsheet fragment handling bottom sheet state & displayed data.
 * Listens for state change in shared Viewmodel & updates data in own viewmodel & recycler view.
 */
class BottomSheetFragment : Fragment() {
    private lateinit var binding: BottomSheetBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: BottomSheetViewModel
    private val bottomSheetView by lazy { binding.bottomSheet }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(inflater, container, false)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        viewModel = BottomSheetViewModel(sharedViewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBottomSheetVisibility(false)
        observeState()
        showData()
    }

    /**
     * Observes shared viewmodel data fetching state,
     * sets intermediate loading bar visibility according to state
     * and makes bottom sheet viewmodel update strings with new data
     */
    private fun observeState() {
        sharedViewModel.state.observe(viewLifecycleOwner) {
            if (it == "loading") {
                binding.indeterminateBar.visibility = View.VISIBLE
            } else if (it == "finished") {
                viewModel.loadDataOutput()
                binding.indeterminateBar.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Observes output data from bottomsheet viewmodel & populates adapter with data.
     * Also shows toast if data list is empty
     */
    private fun showData() {
        viewModel.outData.observe(viewLifecycleOwner) {
            setBottomSheetVisibility(true)
            binding.recyclerView.adapter = DataOutputAdapter(it, this.requireContext())
            // if no data show toast
            if (it.isEmpty()) {
                showToast(
                    bottomSheetView.context.applicationContext.getString(R.string.Error_noData)
                )
            }
            //binding.dataTextView.text = it
        }
    }

    /**
     * Makes bottom sheet visible/popup or invisible/popdown
     * with values true of false
     */
    private fun setBottomSheetVisibility(isVisible: Boolean) {
        Log.d("Bottom sheet visibility", true.toString())
        val updatedState =
            if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }

    /**
     * show toast containing string message
     */
    private fun showToast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}

