package com.ikhokha.techcheck.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.android.material.button.MaterialButton
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.databinding.FragmentReceiptBinding
import com.ikhokha.techcheck.adapter.ProductReceiptAdapter
import com.ikhokha.techcheck.viewModel.ReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ReceiptFragment : Fragment(){

    private lateinit var binding: FragmentReceiptBinding
    private val viewModel: ReceiptViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_receipt, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.receiptDateTime.text = viewModel.getDateAndTime()
        binding.receiptTotalDue.text = getFinalAmountDue()

        setupAdapter()

        binding.receiptGotoBarcode.setOnClickListener {
            gotoBarcodeFragment()
        }
        runBlocking {
            if (viewModel.getProducts().none { it.inBasket }) {
                fun setDisabled(materialButton: MaterialButton) {
                    materialButton.isClickable = false
                    materialButton.isEnabled = false
                }

                binding.receiptConfirm.run {
                    setDisabled(this)
                }

                binding.receiptCancel.run {
                    setDisabled(this)
                }


            } else {
                binding.receiptCancel.setOnClickListener {
                    cancellationDialog()
                }

                binding.receiptConfirm.setOnClickListener {
                    openConfirmOrderDialog()
                }
            }
        }
        binding.executePendingBindings()
    }

    private fun getFinalAmountDue() = requireContext().getString(R.string.price_text,viewModel.calculateTotal()).replace(",",".")
    private fun setupAdapter(){
        binding.receiptRecyclerview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.receiptRecyclerview.adapter = ProductReceiptAdapter(requireContext(),viewModel.getProducts())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openConfirmOrderDialog(){
        MaterialDialog(requireContext(),BottomSheet(LayoutMode.WRAP_CONTENT)).show{

            cornerRadius(16f)
            title(R.string.receipt_request)

            positiveButton(R.string.yes_text){
                shareOptions()
            }

            negativeButton(R.string.no_text){
                cleanUpAndGoToCart()
            }
        }

    }

    private fun cancellationDialog(){
        MaterialDialog(requireContext()).show {
            cornerRadius(16f)
            title(R.string.cancel_confirm_text)
            positiveButton(R.string.yes_text){
                cleanUpAndGoToCart()
            }

            negativeButton(R.string.no_text){
                cancel()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shareOptions(){
        val receiptString = StringBuilder()
            .append("${requireContext().getString(R.string.receipt_title)}\n")
            .append("${viewModel.getDateAndTime()}\n\n")
            .apply {
                viewModel.getProducts().forEach {
                    val totalAmount = it.price!!*it.quantity!!
                    append("${it.quantity}x${it.description} \t ${requireContext().getString(R.string.price_text,totalAmount)}\n")
                }
                append("\n${requireContext().getString(R.string.total_text)}\t ${requireContext().getString(R.string.price_text,viewModel.calculateTotal())}")
            }

        val shareReceiptIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putExtra(Intent.EXTRA_TEXT,receiptString.toString())
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareReceiptIntent,null))
        cleanUpAndGoToCart()
    }

    private fun cleanUpAndGoToCart(){
        viewModel.cleanUpData()
        gotoCartFragment()
    }
    private fun gotoBarcodeFragment(){
        findNavController().navigate(R.id.barcodeFragment,null,optionsNav())
    }

    private fun gotoCartFragment(){
        findNavController().navigate(R.id.cartFragment,null,optionsNav())
    }

    private fun optionsNav() = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

}