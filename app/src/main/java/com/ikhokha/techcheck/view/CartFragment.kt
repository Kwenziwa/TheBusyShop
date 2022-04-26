package com.ikhokha.techcheck.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.ikhokha.techcheck.R
import com.ikhokha.techcheck.databinding.FragmentCartBinding
import com.ikhokha.techcheck.model.Product
import com.ikhokha.techcheck.viewModel.CartViewModel
import com.ikhokha.techcheck.adapter.ProductCartAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CartFragment : Fragment(){

    private val viewModel: CartViewModel by viewModels()
    private lateinit var binding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_cart, container, false)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        binding.addProductsButton.setOnClickListener {
            gotoBarcode(findNavController())
        }

        binding.checkoutButton.setOnClickListener {
            gotoCheckout(findNavController())
        }

    }

    private fun setupView(){
        if(viewModel.cartIsEmpty()){
            showEmptyView()
        }
        else{
            setupAdapter()
            showItemsView()
        }

    }

    private fun showEmptyView(){
        binding.cartRecyclerview.visibility = View.GONE
        binding.checkoutButton.visibility = View.GONE

        binding.addProductsButton.visibility = View.VISIBLE
        binding.emptyCartText.visibility = View.VISIBLE
        binding.emptyCardImg.visibility = View.VISIBLE
    }

    private fun showItemsView(){
        binding.cartRecyclerview.visibility = View.VISIBLE
        binding.checkoutButton.visibility = View.VISIBLE
        binding.addProductsButton.visibility = View.VISIBLE

        binding.emptyCartText.visibility = View.GONE
        binding.emptyCardImg.visibility = View.GONE
    }

    private fun gotoCheckout(navController: NavController){
        navController.navigate(R.id.receiptFragment,null,optionsNav())
    }

    private fun gotoBarcode(navController: NavController){
        navController.navigate(R.id.barcodeFragment,null,optionsNav())
    }

    private fun optionsNav() = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    private fun setupAdapter(){
        binding.cartRecyclerview.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        val adapter = ProductCartAdapter(
            requireContext(),
            viewModel.getCartProducts(),
            ::showProductDetails
        )
        binding.cartRecyclerview.adapter = adapter
    }

    private var _selectedProduct: Product? = null
    private fun showProductDetails(product: Product){
        _selectedProduct = product
        openDialogForSelectedProduct()
    }

    private fun openDialogForSelectedProduct(){
        MaterialDialog(requireContext()).show {
            val customView = customView(R.layout.product_view_more)

            Glide.with(requireContext())
                .load(_selectedProduct!!.imageUrl)
                .placeholder(R.drawable.load)
                .error(R.drawable.error)
                .into(customView.findViewById<AppCompatImageView>(R.id.product_view_more_image))


            customView.findViewById<AppCompatTextView>(R.id.product_view_more_description).run {
                text = _selectedProduct!!.description
            }
            customView.findViewById<AppCompatTextView>(R.id.product_view_more_price).run{
                text = getString(R.string.price_text,_selectedProduct!!.price).replace(",",".")
            }
            customView.findViewById<AppCompatTextView>(R.id.product_view_more_quantity).run {
                text = getString(R.string.quantity_text,_selectedProduct!!.quantity)
            }
            customView.findViewById<MaterialButton>(R.id.product_view_more_button_close).setOnClickListener {
                cancel()
            }

        }
    }

}